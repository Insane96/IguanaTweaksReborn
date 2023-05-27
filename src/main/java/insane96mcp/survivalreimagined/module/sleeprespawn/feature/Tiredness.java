package insane96mcp.survivalreimagined.module.sleeprespawn.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.data.generator.SRItemTagsProvider;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.sleeprespawn.data.EnergyBoostItem;
import insane96mcp.survivalreimagined.module.sleeprespawn.utils.TirednessHandler;
import insane96mcp.survivalreimagined.network.NetworkHandler;
import insane96mcp.survivalreimagined.network.message.MessageTirednessSync;
import insane96mcp.survivalreimagined.setup.SRMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
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

	public static final String NOT_TIRED = "survivalreimagined.not_tired";
	public static final String TIRED_ENOUGH = "survivalreimagined.tired_enough";
	public static final String TOO_TIRED = "survivalreimagined.too_tired";
	public static final TagKey<Item> ENERGY_BOOST = SRItemTagsProvider.create("energy_boost");

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
	public static Double tirednessToSleep = 500d;
	@Config(min = 0d)
	@Label(name = "Tiredness for effect", description = "Tiredness required to get the Tired effect.")
	public static Double tirednessToEffect = 575d;
	@Config(min = 0d)
	@Label(name = "Tiredness per level", description = "Every this Tiredness above 'Tiredness for effect' will add a new level of Tired.")
	public static Double tirednessPerLevel = 75d;
	@Config(min = 0d)
	@Label(name = "Default Energy Boost Duration Multiplier", description = "By default if omitted in the json, food items will give 1 second of Energy Boost per effective nourishment (hunger + saturation) of the food. This multiplies the duration of the effect")
	public static Double defaultEnergyBoostDurationMultiplier = 5d;
	@Config
	@Label(name = "On death behaviour", description = """
			What to do with tiredness when the player dies.
			RESET resets the tiredness to 0
			KEEP keeps the current tiredness
			SET_AT_EFFECT keeps the current tiredness but if higher than 'Tiredness for effect' it's set to that
			SET_AT_TIRED keeps current tiredness but if higher than 'Tiredness to sleep' it's set to that""")
	public static OnDeath onDeathBehaviour = OnDeath.SET_AT_EFFECT;
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

	enum OnDeath {
		RESET,
		KEEP,
		SET_AT_EFFECT,
		SET_AT_TIRED
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (!this.isEnabled()
				|| event.player.level.isClientSide
				|| event.phase == TickEvent.Phase.START)
			return;

		ServerPlayer serverPlayer = (ServerPlayer) event.player;
		float tiredness = TirednessHandler.get(serverPlayer);
		applyTired(tiredness, serverPlayer);
		tickEnergyBoostEffect(serverPlayer);
	}

	private void tickEnergyBoostEffect(ServerPlayer player) {
		if (!player.hasEffect(SRMobEffects.ENERGY_BOOST.get()))
			return;

		//noinspection ConstantConditions
		int effectLevel = player.getEffect(SRMobEffects.ENERGY_BOOST.get()).getAmplifier() + 1;
		float newTiredness = TirednessHandler.subtractAndGet(player, 0.01f * effectLevel);

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

		float tiredness = TirednessHandler.get(serverPlayer);
		float newTiredness = TirednessHandler.addAndGet(serverPlayer, amount * tirednessGainMultiplier.floatValue());
		if (tiredness < tirednessToSleep && newTiredness >= tirednessToSleep) {
			//TODO Add effect
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

		if (TirednessHandler.get(player) < tirednessToSleep) {
			event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
			player.displayClientMessage(Component.translatable(NOT_TIRED), true);
			if (!shouldPreventSpawnPoint)
				player.setRespawnPosition(player.level.dimension(), event.getPos(), player.getYRot(), false, true);
		}
		else if (TirednessHandler.get(player) > tirednessToEffect) {
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
		event.getLevel().players().stream().filter(LivingEntity::isSleeping).toList().forEach(player -> {
			float tirednessOnWakeUp = Mth.clamp(TirednessHandler.get(player) - tirednessToEffect.floatValue(), 0, Float.MAX_VALUE);
			int duration = (int) (vigourDuration - (tirednessOnWakeUp * vigourPenalty));
			if (duration > 0)
				player.addEffect(new MobEffectInstance(SRMobEffects.VIGOUR.get(), duration * 20, vigourAmplifier, false, false, true));
			TirednessHandler.set(player, tirednessOnWakeUp);
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
				&& TirednessHandler.get(player) > tirednessToEffect;
	}

	@SubscribeEvent
	public void onServerStarted(ServerStartedEvent event) {
		if (!this.isEnabled())
			return;

		event.getServer().getGameRules().getRule(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE).set(1, event.getServer());
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerEvent.Clone event) {
		if (!this.isEnabled()
			|| !event.isWasDeath())
			return;

		float tiredness = TirednessHandler.get(event.getOriginal());
		switch (onDeathBehaviour) {
			case RESET -> tiredness = 0;
			case KEEP -> {

			}
			case SET_AT_EFFECT -> {
				if (tiredness > tirednessToEffect)
					tiredness = tirednessToEffect.floatValue();
			}
			case SET_AT_TIRED -> {
				if (tiredness > tirednessToSleep)
					tiredness = tirednessToSleep.floatValue();
			}
		}

		TirednessHandler.set(event.getEntity(), tiredness);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerGui(RegisterGuiOverlaysEvent event) {
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

	final List<String> testingPlayers = List.of("Dev", "Insane96MCP", "Carboniglio2");

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
			event.getLeft().add(String.format("Tiredness: %s", new DecimalFormat("#.#").format(TirednessHandler.get(playerEntity))));
		}
		if (!mc.options.renderDebug || mc.options.reducedDebugInfo().get()) {
			String toDraw = String.format("Tiredness: %s", new DecimalFormat("#.#").format(TirednessHandler.get(playerEntity)));
			int scaledHeight = mc.getWindow().getGuiScaledHeight();
			int top = scaledHeight - mc.font.lineHeight - 1;
			int left = 2;
			drawOnScreenWithBackground(event.getPoseStack(), left, top, toDraw, -1873784752, 14737632);
		}
	}

	private static void drawOnScreenWithBackground(PoseStack mStack, int x, int y, String text, int backgroundColor, int textColor) {
		ForgeGui.fill(mStack, x - 1, y - 1, x + Minecraft.getInstance().font.width(text) + 1, y + Minecraft.getInstance().font.lineHeight - 1, backgroundColor);
		Minecraft.getInstance().font.draw(mStack, text, x, y, textColor);
	}
}