package insane96mcp.iguanatweaksreborn.module.sleeprespawn.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.sleeprespawn.utils.EnergyBoostItem;
import insane96mcp.iguanatweaksreborn.network.MessageTirednessSync;
import insane96mcp.iguanatweaksreborn.network.SyncHandler;
import insane96mcp.iguanatweaksreborn.setup.ITMobEffects;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.MCUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.network.NetworkDirection;

import java.text.DecimalFormat;
import java.util.List;

@Label(name = "Tiredness", description = "Prevents sleeping if the player is not tired. Tiredness is gained by gaining exhaustion. Allows you to sleep during daytime if too tired")
@LoadFeature(module = Modules.Ids.SLEEP_RESPAWN)
public class Tiredness extends Feature {
	private static ForgeConfigSpec.ConfigValue<List<? extends String>> energyBoostItemsConfig;
	private static final List<String> energyBoostItemsDefault = List.of(
			"#iguanatweaksreborn:energy_boost",
			"farmersdelight:hot_cocoa,80,0"
	);
	public static List<EnergyBoostItem> energyBoostItems;

	@Config(min = 0d, max = 128d)
	@Label(name = "Tiredness gained multiplier", description = "Multiply the tiredness gained by this value. Normally you gain tiredness equal to the exhaustion gained. 'Effective Hunger' doesn't affect the exhaustion gained.")
	public static Double tirednessGainMultiplier = 1d;
	@Config
	@Label(name = "Prevent Spawn Point", description = "If true the player will not set the spawn point if he/she can't sleep.")
	public static Boolean shouldPreventSpawnPoint = false;
	@Config(min = 0d)
	@Label(name = "Tiredness to sleep", description = "Tiredness required to be able to sleep.")
	public static Double tirednessToSleep = 320d;
	@Config(min = 0d)
	@Label(name = "Tiredness for effect", description = "Tiredness required to get the Tired effect.")
	public static Double tirednessToEffect = 400d;
	@Config(min = 0d)
	@Label(name = "Tiredness per level", description = "Every this Tiredness above 'Tiredness for effect' will add a new level of Tired.")
	public static Double tirednessPerLevel = 20d;

	public Tiredness(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@Override
	public void loadConfigOptions() {
		super.loadConfigOptions();
		energyBoostItemsConfig = this.getBuilder()
				.comment("""
						A list of items that when consumed will give the Energy Boost effect.
						You can specify the item/tag only and the duration will be calculated from the hunger restored or you can include duration,amplifier for customs.
						The iguanatweaksreborn:energy_boost item tag can be used to add items without a custom duration
						Format is 'modid:item_id' / '#modid:item_tag' or 'modid:item_id,duration,amplifier' / '#modid:item_tag,duration,amplifier'.""")
				.defineList("Plants Growth Multiplier", energyBoostItemsDefault, o -> o instanceof String);
	}

	@Override
	public void readConfig(final ModConfigEvent event) {
		super.readConfig(event);
		energyBoostItems = EnergyBoostItem.parseStringList(energyBoostItemsConfig.get());
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (!this.isEnabled()
				|| event.player.level.isClientSide
				|| event.phase == TickEvent.Phase.START)
			return;

		ServerPlayer serverPlayer = (ServerPlayer) event.player;
		if (!serverPlayer.hasEffect(ITMobEffects.ENERGY_BOOST.get()))
			return;

		CompoundTag persistentData = serverPlayer.getPersistentData();
		float tiredness = persistentData.getFloat(Strings.Tags.TIREDNESS);
		int effectLevel = serverPlayer.getEffect(ITMobEffects.ENERGY_BOOST.get()).getAmplifier() + 1;
		float newTiredness = Math.max(tiredness - (0.05f * effectLevel), 0);
		persistentData.putFloat(Strings.Tags.TIREDNESS, newTiredness);

		if (serverPlayer.tickCount % 20 == 0) {
			Object msg = new MessageTirednessSync(newTiredness);
			SyncHandler.CHANNEL.sendTo(msg, serverPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
		}
	}

	@SubscribeEvent
	public void onItemFinishUse(LivingEntityUseItemEvent.Finish event) {
		if (!this.isEnabled()
				|| !event.getItem().isEdible())
			return;
		EnergyBoostItem energyBoostItem = null;
		for (EnergyBoostItem energyBoostItem1 : energyBoostItems) {
			if (energyBoostItem1.matchesItem(event.getItem().getItem()))
				energyBoostItem = energyBoostItem1;
		}
		if (energyBoostItem == null)
			return;

		Player playerEntity = (Player) event.getEntity();
		int duration, amplifier;
		if (energyBoostItem.duration == 0) {
			FoodProperties food = event.getItem().getItem().getFoodProperties(event.getItem(), playerEntity);
			duration = (int) ((food.getNutrition() + food.getNutrition() * food.getSaturationModifier() * 2) * 20);
			amplifier = 0;
		}
		else {
			duration = energyBoostItem.duration;
			amplifier = energyBoostItem.amplifier;
		}

		playerEntity.addEffect(MCUtils.createEffectInstance(ITMobEffects.ENERGY_BOOST.get(), duration, amplifier, true, false, true, false));
	}

	public static void onFoodExhaustion(Player player, float amount) {
		if (!isEnabled(Tiredness.class))
			return;

		if (player.level.isClientSide)
			return;

		ServerPlayer serverPlayer = (ServerPlayer) player;

		CompoundTag persistentData = serverPlayer.getPersistentData();
		float tiredness = persistentData.getFloat(Strings.Tags.TIREDNESS);
		float newTiredness = tiredness + amount;
		persistentData.putFloat(Strings.Tags.TIREDNESS, newTiredness);
		if (tiredness < tirednessToSleep && newTiredness >= tirednessToSleep) {
			serverPlayer.displayClientMessage(Component.translatable(Strings.Translatable.TIRED_ENOUGH), false);
		}
		else if (tiredness >= tirednessToEffect && player.tickCount % 20 == 0) {
			serverPlayer.addEffect(new MobEffectInstance(ITMobEffects.TIRED.get(), 25, Math.min((int) ((tiredness - tirednessToEffect) / tirednessPerLevel), 4), true, false, true));
		}

		Object msg = new MessageTirednessSync(newTiredness);
		SyncHandler.CHANNEL.sendTo(msg, serverPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onTiredBreakSpeed(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled()
				|| !event.getEntity().hasEffect(ITMobEffects.TIRED.get()))
			return;

		//noinspection ConstantConditions
		int level = event.getEntity().getEffect(ITMobEffects.TIRED.get()).getAmplifier() + 1;
		event.setNewSpeed(event.getNewSpeed() * (1 - (level * 0.05f)));
	}

	@SubscribeEvent
	public void notTiredToSleep(PlayerSleepInBedEvent event) {
		if (!this.isEnabled()
				|| event.getResultStatus() != null
				|| event.getEntity().level.isClientSide)
			return;

		ServerPlayer player = (ServerPlayer) event.getEntity();

		if (player.getPersistentData().getFloat(Strings.Tags.TIREDNESS) < tirednessToSleep) {
			event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
			player.displayClientMessage(Component.translatable(Strings.Translatable.NOT_TIRED), true);
			if (!shouldPreventSpawnPoint)
				player.setRespawnPosition(player.level.dimension(), event.getPos(), player.getYRot(), false, true);
		}
		else if (player.getPersistentData().getFloat(Strings.Tags.TIREDNESS) > tirednessToEffect) {
			event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
			player.startSleeping(event.getPos());
			((ServerLevel)player.level).updateSleepingPlayerList();
		}
	}

	@SubscribeEvent
	public void resetTirednessOnWakeUp(SleepFinishedTimeEvent event) {
		if (!this.isEnabled())
			return;
		event.getLevel().players().stream().filter(LivingEntity::isSleeping).toList().forEach((player) -> {
			float tirednessOnWakeUp = Mth.clamp(player.getPersistentData().getFloat(Strings.Tags.TIREDNESS) - tirednessToEffect.floatValue(), 0, Float.MAX_VALUE);
			player.getPersistentData().putFloat(Strings.Tags.TIREDNESS, tirednessOnWakeUp);
		});
	}

	@SubscribeEvent
	public void allowSleepAtDay(SleepingTimeCheckEvent event) {
		if (!this.canSleepDuringDay(event.getEntity()))
			return;
		event.setResult(Event.Result.ALLOW);
	}

	public static boolean canSleepDuringDay(Player player) {
		return isEnabled(Tiredness.class)
				&& player.getPersistentData().getFloat(Strings.Tags.TIREDNESS) > tirednessToEffect;
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onFog(ViewportEvent.RenderFog event) {
		if (!this.isEnabled()
				|| event.getCamera().getEntity().isSpectator()
				|| !(event.getCamera().getEntity() instanceof LivingEntity livingEntity)
				|| !livingEntity.hasEffect(ITMobEffects.TIRED.get()))
			return;

		int amplifier = livingEntity.getEffect(ITMobEffects.TIRED.get()).getAmplifier();
		if (amplifier < 1)
			return;
		float renderDistance = Minecraft.getInstance().gameRenderer.getRenderDistance();
		float near = -8;
		float far = Math.min(48f, renderDistance) - ((amplifier - 1) * 10);
		event.setNearPlaneDistance(near);
		event.setFarPlaneDistance(far);
		event.setCanceled(true);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onFog(ViewportEvent.ComputeFogColor event) {
		if (!this.isEnabled()
				|| event.getCamera().getEntity().isSpectator()
				|| !(event.getCamera().getEntity() instanceof LivingEntity livingEntity)
				|| !livingEntity.hasEffect(ITMobEffects.TIRED.get()))
			return;

		int amplifier = livingEntity.getEffect(ITMobEffects.TIRED.get()).getAmplifier();
		if (amplifier < 1)
			return;
		float color = 0f;
		event.setRed(color);
		event.setGreen(color);
		event.setBlue(color);
	}

	public static final ResourceLocation GUI_ICONS = new ResourceLocation(IguanaTweaksReborn.MOD_ID, "textures/gui/icons.png");

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerGui(RegisterGuiOverlaysEvent event) {
		event.registerAbove(VanillaGuiOverlay.FOOD_LEVEL.id(), "tiredness", (gui, mStack, partialTicks, screenWidth, screenHeight) -> {
			boolean isMounted = Minecraft.getInstance().player.getVehicle() instanceof LivingEntity;
			if (isEnabled(Tiredness.class) && !isMounted && !Minecraft.getInstance().options.hideGui && gui.shouldDrawSurvivalElements())
			{
				gui.setupOverlayRenderState(true, false, GUI_ICONS);
				int left = screenWidth / 2 + 91;
				int top = screenHeight - gui.rightHeight;
				renderTiredness(gui, mStack, left, top);
				gui.rightHeight += 10;
			}
		});
	}

	private static final Vec2 UV_NOT_TIRED = new Vec2(0, 0);
	private static final Vec2 UV_SLEEPY = new Vec2(9, 0);
	private static final Vec2 UV_TIRED = new Vec2(18, 0);

	@OnlyIn(Dist.CLIENT)
	private static void renderTiredness(Gui gui, PoseStack matrixStack, int left, int top) {
		Player player = (Player)Minecraft.getInstance().getCameraEntity();
		float tiredness = player.getPersistentData().getFloat(Strings.Tags.TIREDNESS);
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
			gui.blit(matrixStack, x, top, (int) uv.x, (int) uv.y, 9, 9);
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
		if (mc.options.renderDebug & !mc.showOnlyReducedInfo()) {
			event.getLeft().add(String.format("Tiredness: %s", new DecimalFormat("#.#").format(playerEntity.getPersistentData().getFloat(Strings.Tags.TIREDNESS))));
		}
	}
}