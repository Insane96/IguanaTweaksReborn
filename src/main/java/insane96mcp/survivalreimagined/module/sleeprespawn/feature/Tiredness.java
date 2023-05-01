package insane96mcp.survivalreimagined.module.sleeprespawn.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.sleeprespawn.data.EnergyBoostItem;
import insane96mcp.survivalreimagined.network.NetworkHandler;
import insane96mcp.survivalreimagined.network.message.MessageTirednessSync;
import insane96mcp.survivalreimagined.setup.SRMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkDirection;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Tiredness", description = "Prevents sleeping if the player is not tired. Tiredness is gained by gaining exhaustion. Allows you to sleep during daytime if too tired. Energy Boost Items are controlled via json in this feature's folder")
@LoadFeature(module = Modules.Ids.SLEEP_RESPAWN)
public class Tiredness extends SRFeature {
	public static final String TIREDNESS_TAG = SurvivalReimagined.RESOURCE_PREFIX + "tiredness";

	public static final String NOT_TIRED = "survivalreimagined.not_tired";
	public static final String TIRED_ENOUGH = "survivalreimagined.tired_enough";
	public static final String TOO_TIRED = "survivalreimagined.too_tired";

	public static final List<EnergyBoostItem> ENERGY_BOOST_ITEMS_DEFAULT = new ArrayList<>(Arrays.asList(
			new EnergyBoostItem(IdTagMatcher.Type.TAG, "survivalreimagined:energy_boost"),
			new EnergyBoostItem(IdTagMatcher.Type.ID, "farmersdelight:hot_cocoa", 80, 0)
	));
	public static final List<EnergyBoostItem> energyBoostItems = new ArrayList<>();

	@Config(min = 0d, max = 128d)
	@Label(name = "Tiredness gained multiplier", description = "Multiply the tiredness gained by this value. Normally you gain tiredness equal to the exhaustion gained. 'Effective Hunger' doesn't affect the exhaustion gained.")
	public static Double tirednessGainMultiplier = 1d;
	@Config
	@Label(name = "Prevent Spawn Point", description = "If true the player will not set the spawn point if he/she can't sleep.")
	public static Boolean shouldPreventSpawnPoint = false;
	@Config(min = 0d)
	@Label(name = "Tiredness to sleep", description = "Tiredness required to be able to sleep.")
	public static Double tirednessToSleep = 300d;
	@Config(min = 0d)
	@Label(name = "Tiredness for effect", description = "Tiredness required to get the Tired effect.")
	public static Double tirednessToEffect = 350d;
	@Config(min = 0d)
	@Label(name = "Tiredness per level", description = "Every this Tiredness above 'Tiredness for effect' will add a new level of Tired.")
	public static Double tirednessPerLevel = 50d;
	@Config
	@Label(name = "Show Tiredness Bar", description = "If true the tiredness bar will be shown.")
	public static Boolean showTirednessBar = false;
	@Config(min = 0d)
	@Label(name = "Default Energy Boost Duration Multiplier", description = "By default if omitted in the json, food items will give 1 second of Energy Boost per effective nourishment (hunger + saturation) of the food. This multiplies the duration of the effect")
	public static Double defaultEnergyBoostDurationMultiplier = 5d;
	//Vigour
	@Config(min = 0)
	@Label(name = "Vigour.Duration", description = "Duration (in seconds) of the Vigour effect on wake up")
	public static Integer vigourDuration = 1200;
	@Config(min = 0)
	@Label(name = "Vigour.Penalty", description = "How many seconds per tiredness above 'Tiredness for effect' will be removed from the effect duration on apply?")
	public static Integer vigourPenalty = 20;
	@Config(min = 0)
	@Label(name = "Vigour.Amplifier", description = "Amplifier (effect level) of Vigour effect on wake up. (Note 0 = Level I, 1 = II, ...)")
	public static Integer vigourAmplifier = 0;

	public Tiredness(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		JSON_CONFIGS.add(new JsonConfig<>("energy_boost_items.json", energyBoostItems, ENERGY_BOOST_ITEMS_DEFAULT, EnergyBoostItem.LIST_TYPE));
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (!this.isEnabled()
				|| event.player.level.isClientSide
				|| event.phase == TickEvent.Phase.START)
			return;

		ServerPlayer serverPlayer = (ServerPlayer) event.player;
		CompoundTag persistentData = serverPlayer.getPersistentData();
		float tiredness = persistentData.getFloat(TIREDNESS_TAG);
		applyTired(tiredness, serverPlayer);
		tickEnergyBoostEffect(tiredness, serverPlayer, persistentData);
	}

	private void tickEnergyBoostEffect(float tiredness, ServerPlayer player, CompoundTag persistentData) {
		if (!player.hasEffect(SRMobEffects.ENERGY_BOOST.get()))
			return;

		//noinspection ConstantConditions
		int effectLevel = player.getEffect(SRMobEffects.ENERGY_BOOST.get()).getAmplifier() + 1;
		float newTiredness = Math.max(tiredness - (0.01f * effectLevel), 0);
		persistentData.putFloat(TIREDNESS_TAG, newTiredness);

		if (player.tickCount % 20 == 0) {
			Object msg = new MessageTirednessSync(newTiredness);
			NetworkHandler.CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
		}
	}

	private void applyTired(float tiredness, ServerPlayer player) {
		if (tiredness >= tirednessToEffect && player.tickCount % 20 == 0) {
			if (!player.hasEffect(SRMobEffects.TIRED.get()))
				player.displayClientMessage(Component.translatable(TOO_TIRED), false);
			player.addEffect(new MobEffectInstance(SRMobEffects.TIRED.get(), 25, Math.min((int) ((tiredness - tirednessToEffect) / tirednessPerLevel), 4), true, false, true));
		}
	}

	@SubscribeEvent
	public void onItemFinishUse(LivingEntityUseItemEvent.Finish event) {
		if (!this.isEnabled()
				|| !event.getItem().isEdible())
			return;
		for (EnergyBoostItem energyBoostItem : energyBoostItems) {
			energyBoostItem.tryApply((Player) event.getEntity(), event.getItem());
		}
	}

	public static void onFoodExhaustion(Player player, float amount) {
		if (!isEnabled(Tiredness.class)
				|| player.level.isClientSide)
			return;

		ServerPlayer serverPlayer = (ServerPlayer) player;

		CompoundTag persistentData = serverPlayer.getPersistentData();
		float tiredness = persistentData.getFloat(TIREDNESS_TAG);
		float newTiredness = tiredness + (amount * tirednessGainMultiplier.floatValue());
		persistentData.putFloat(TIREDNESS_TAG, newTiredness);
		if (tiredness < tirednessToSleep && newTiredness >= tirednessToSleep) {
			serverPlayer.displayClientMessage(Component.translatable(TIRED_ENOUGH), false);
		}

		Object msg = new MessageTirednessSync(newTiredness);
		NetworkHandler.CHANNEL.sendTo(msg, serverPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onTiredBreakSpeed(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled()
				|| !event.getEntity().hasEffect(SRMobEffects.TIRED.get()))
			return;

		//noinspection ConstantConditions
		int level = event.getEntity().getEffect(SRMobEffects.TIRED.get()).getAmplifier() + 1;
		event.setNewSpeed(event.getNewSpeed() * (1 - (level * 0.05f)));
	}

	@SubscribeEvent
	public void notTiredToSleep(PlayerSleepInBedEvent event) {
		if (!this.isEnabled()
				|| event.getResultStatus() != null
				|| event.getEntity().level.isClientSide)
			return;

		ServerPlayer player = (ServerPlayer) event.getEntity();

		if (player.getPersistentData().getFloat(TIREDNESS_TAG) < tirednessToSleep) {
			event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
			player.displayClientMessage(Component.translatable(NOT_TIRED), true);
			if (!shouldPreventSpawnPoint)
				player.setRespawnPosition(player.level.dimension(), event.getPos(), player.getYRot(), false, true);
		}
		else if (player.getPersistentData().getFloat(TIREDNESS_TAG) > tirednessToEffect) {
			event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
			player.startSleeping(event.getPos());
			((ServerLevel)player.level).updateSleepingPlayerList();
			if (!shouldPreventSpawnPoint)
				player.setRespawnPosition(player.level.dimension(), event.getPos(), player.getYRot(), false, true);
		}
	}

	//Run after Sleeping Effects
	@SubscribeEvent(priority = EventPriority.LOW)
	public void resetTirednessOnWakeUp(SleepFinishedTimeEvent event) {
		if (!this.isEnabled())
			return;
		event.getLevel().players().stream().filter(LivingEntity::isSleeping).toList().forEach((player) -> {
			float tirednessOnWakeUp = Mth.clamp(player.getPersistentData().getFloat(TIREDNESS_TAG) - tirednessToEffect.floatValue(), 0, Float.MAX_VALUE);
			int duration = (int) (vigourDuration - (tirednessOnWakeUp * vigourPenalty));
			if (duration > 0)
				player.addEffect(new MobEffectInstance(SRMobEffects.VIGOUR.get(), duration * 20, vigourAmplifier, false, false));
			player.getPersistentData().putFloat(TIREDNESS_TAG, tirednessOnWakeUp);
		});
	}

	@SubscribeEvent
	public void allowSleepAtDay(SleepingTimeCheckEvent event) {
		if (!canSleepDuringDay(event.getEntity()))
			return;
		event.setResult(Event.Result.ALLOW);
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean canSleepDuringDay(Player player) {
		return isEnabled(Tiredness.class)
				&& player.getPersistentData().getFloat(TIREDNESS_TAG) > tirednessToEffect;
	}

	@SubscribeEvent
	public void onServerStarted(ServerStartedEvent event) {
		if (!this.isEnabled())
			return;

		event.getServer().getGameRules().getRule(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE).set(1, event.getServer());
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onRenderer(RenderGuiOverlayEvent.Post event) {

	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerGui(RegisterGuiOverlaysEvent event) {
		event.registerAbove(VanillaGuiOverlay.FOOD_LEVEL.id(), "tiredness", (gui, mStack, partialTicks, screenWidth, screenHeight) -> {
			if (!showTirednessBar)
				return;
			assert Minecraft.getInstance().player != null : "Minecraft.getInstance().player is null";
			boolean isMounted = Minecraft.getInstance().player.getVehicle() instanceof LivingEntity;
			if (isEnabled(Tiredness.class) && !isMounted && !Minecraft.getInstance().options.hideGui && gui.shouldDrawSurvivalElements())
			{
				gui.setupOverlayRenderState(true, false, SurvivalReimagined.GUI_ICONS);
				int left = screenWidth / 2 + 91;
				int top = screenHeight - gui.rightHeight;
				renderTiredness(gui, mStack, left, top);
				gui.rightHeight += 10;
			}
		});
		event.registerAbove(VanillaGuiOverlay.SLEEP_FADE.id(), "tired_overlay", (gui, mStack, partialTicks, screenWidth, screenHeight) -> {
			assert Minecraft.getInstance().player != null : "Minecraft.getInstance().player is null";
			if (isEnabled(Tiredness.class) && gui.shouldDrawSurvivalElements())
			{
				LocalPlayer player = Minecraft.getInstance().player;
				if (!player.hasEffect(SRMobEffects.TIRED.get()))
					return;
				//noinspection DataFlowIssue
				int amplifier = player.getEffect(SRMobEffects.TIRED.get()).getAmplifier() + 1;
				Minecraft.getInstance().getProfiler().push("tired_overlay");
				RenderSystem.disableDepthTest();
				float opacity = (amplifier * 20f) / 100.0F;
				if (opacity > 1.0F)
					opacity = 1.0F - (amplifier * 20f - 100) / 10.0F;

				int color = (int) (220.0F * opacity) << 24 | 1052704;
				GuiComponent.fill(mStack, 0, 0, screenWidth, screenHeight, color);
				RenderSystem.enableDepthTest();
				Minecraft.getInstance().getProfiler().pop();
			}
		});
	}

	private static final Vec2 UV_NOT_TIRED = new Vec2(0, 0);
	private static final Vec2 UV_SLEEPY = new Vec2(9, 0);
	private static final Vec2 UV_TIRED = new Vec2(18, 0);

	@OnlyIn(Dist.CLIENT)
	private static void renderTiredness(Gui gui, PoseStack matrixStack, int left, int top) {
		Player player = (Player)Minecraft.getInstance().getCameraEntity();
		assert player != null : "Minecraft.getInstance().getCameraEntity() is null";
		float tiredness = player.getPersistentData().getFloat(TIREDNESS_TAG);
		int numberOfZ = 0;
		if (tiredness < tirednessToSleep) {
			numberOfZ += tiredness / (tirednessToSleep / 6);
		}
		else if (tiredness < tirednessToEffect) {
			float tirednessBetweenSleepEffect = (float) (tirednessToEffect - tirednessToSleep);
			numberOfZ += 6 + ((tiredness - tirednessToSleep) / (tirednessBetweenSleepEffect / 2));
		}
		else {
			float tirednessToBlind = (float) (tirednessPerLevel * 5);
			numberOfZ += 8 + ((tiredness - tirednessToEffect) / (tirednessToBlind / 2));
		}
		numberOfZ = Mth.clamp(numberOfZ, 0, 10);
		Minecraft.getInstance().getProfiler().push("tiredness");
		for(int i = 0; i < numberOfZ; ++i) {
			Vec2 uv = UV_NOT_TIRED;
			if (i >= 8)
				uv = UV_TIRED;
			else if (i >= 6)
				uv = UV_SLEEPY;

			int x = left - (i * 8) - 9;
			GuiComponent.blit(matrixStack, x, top, (int) uv.x, (int) uv.y, 9, 9);
		}
		Minecraft.getInstance().getProfiler().pop();
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void debugScreen(CustomizeGuiOverlayEvent.DebugText event) {
		if (!this.isEnabled())
			return;
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer playerEntity = mc.player;
		if (playerEntity == null)
			return;
		if (mc.options.renderDebug && !mc.showOnlyReducedInfo()) {
			event.getLeft().add(String.format("Tiredness: %s", new DecimalFormat("#.#").format(playerEntity.getPersistentData().getFloat(TIREDNESS_TAG))));
		}
	}
}