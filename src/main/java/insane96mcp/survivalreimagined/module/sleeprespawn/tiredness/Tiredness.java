package insane96mcp.survivalreimagined.module.sleeprespawn.tiredness;

import com.mojang.blaze3d.systems.RenderSystem;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.data.IdTagMatcher;
import insane96mcp.insanelib.world.effect.ILMobEffect;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.data.generator.SRItemTagsProvider;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.setup.SRRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
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
import net.minecraftforge.registries.RegistryObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Tiredness", description = "Prevents sleeping if the player is not tired. Tiredness is gained by gaining exhaustion. Allows you to sleep during daytime if too tired. Energy Boost Items are controlled via json in this feature's folder")
@LoadFeature(module = Modules.Ids.SLEEP_RESPAWN)
public class Tiredness extends SRFeature {

	public static final RegistryObject<MobEffect> TIRED = SRRegistries.MOB_EFFECTS.register("tired", () -> new ILMobEffect(MobEffectCategory.HARMFUL, 0x818894, false)
			.addAttributeModifier(Attributes.MOVEMENT_SPEED, "697c48dd-6bbd-4082-8501-040bb9812c09", -0.05F, AttributeModifier.Operation.MULTIPLY_TOTAL)
			.addAttributeModifier(Attributes.ATTACK_SPEED, "40c789ef-d30d-4a27-8f46-13fe0edbb259", -0.05F, AttributeModifier.Operation.MULTIPLY_TOTAL));
	public static final RegistryObject<MobEffect> ENERGY_BOOST = SRRegistries.MOB_EFFECTS.register("energy_boost", () -> new ILMobEffect(MobEffectCategory.BENEFICIAL, 0x857965, true));

	public static final String NOT_TIRED = "survivalreimagined.not_tired";
	public static final String TIRED_ENOUGH = "survivalreimagined.tired_enough";
	public static final String TOO_TIRED = "survivalreimagined.too_tired";
	public static final TagKey<Item> ENERGY_BOOST_ITEM_TAG = SRItemTagsProvider.create("energy_boost");

	public static final List<EnergyBoostItem> ENERGY_BOOST_ITEMS_DEFAULT = new ArrayList<>(Arrays.asList(
			new EnergyBoostItem(IdTagMatcher.newTag("survivalreimagined:energy_boost"), 0, 0),
			new EnergyBoostItem(IdTagMatcher.newId("farmersdelight:hot_cocoa"), 80, 0)
	));
	public static final List<EnergyBoostItem> energyBoostItems = new ArrayList<>();

	@Config(min = 0d, max = 128d)
	@Label(name = "Tiredness gained multiplier", description = "Multiply the tiredness gained by this value. Normally you gain tiredness equal to the exhaustion gained. 'Effective Hunger' doesn't affect the exhaustion gained.")
	public static Double tirednessGainMultiplier = 1d;
	@Config
	@Label(name = "Prevent Spawn Point", description = "If true the player will not set the spawn point if can't sleep.")
	public static Boolean shouldPreventSpawnPoint = false;
	@Config(min = 0d)
	@Label(name = "Tiredness for effect", description = "Tiredness required to get the Tired effect and be able to sleep.")
	public static Double tirednessToEffect = 500d;
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
			SET_AT_EFFECT keeps the current tiredness but if higher than 'Tiredness for effect' it's set to that""")
	public static OnDeath onDeathBehaviour = OnDeath.SET_AT_EFFECT;

	public Tiredness(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		JSON_CONFIGS.add(new JsonConfig<>("energy_boost_items.json", energyBoostItems, ENERGY_BOOST_ITEMS_DEFAULT, EnergyBoostItem.LIST_TYPE));
	}

	enum OnDeath {
		RESET,
		KEEP,
		SET_AT_EFFECT
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (!this.isEnabled()
				|| event.player.level().isClientSide
				|| event.phase == TickEvent.Phase.START)
			return;

		ServerPlayer serverPlayer = (ServerPlayer) event.player;
		tickEnergyBoostEffect(serverPlayer);
	}

	private void tickEnergyBoostEffect(ServerPlayer player) {
		if (!player.hasEffect(ENERGY_BOOST.get()))
			return;

		//noinspection ConstantConditions
		int effectLevel = player.getEffect(ENERGY_BOOST.get()).getAmplifier() + 1;
		TirednessHandler.subtract(player, 0.01f * effectLevel);

		if (player.tickCount % 20 == 0) {
			TirednessHandler.syncToClient(player);
		}
	}

	private static void applyTired(float tiredness, ServerPlayer player) {
		if (tiredness < tirednessToEffect)
			return;
		int amplifier = Math.min((int) ((tiredness - tirednessToEffect) / tirednessPerLevel), 4);
		if (amplifier >= 0) {
			int oAmplifier = -1;
			if (player.hasEffect(TIRED.get())) {
				oAmplifier = player.getEffect(TIRED.get()).getAmplifier();
			}
			if (amplifier != oAmplifier)
				player.addEffect(new MobEffectInstance(TIRED.get(), -1, amplifier, true, false, true));
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
				|| player.level().isClientSide)
			return;

		ServerPlayer serverPlayer = (ServerPlayer) player;

		float tiredness = TirednessHandler.get(serverPlayer);
		float newTiredness = TirednessHandler.addAndGet(serverPlayer, amount * tirednessGainMultiplier.floatValue());
		if (tiredness < tirednessToEffect && newTiredness >= tirednessToEffect) {
			serverPlayer.displayClientMessage(Component.translatable(TIRED_ENOUGH), false);
		}
		applyTired(tiredness, serverPlayer);

		TirednessHandler.syncToClient(serverPlayer);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onTiredBreakSpeed(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled()
				|| !event.getEntity().hasEffect(TIRED.get()))
			return;

		//noinspection ConstantConditions
		int level = event.getEntity().getEffect(TIRED.get()).getAmplifier() + 1;
		event.setNewSpeed(event.getNewSpeed() * (1 - (level * 0.05f)));
	}

	@SubscribeEvent
	public void notTiredToSleep(PlayerSleepInBedEvent event) {
		if (!this.isEnabled()
				|| event.getResultStatus() != null
				|| event.getEntity().level().isClientSide)
			return;

		ServerPlayer player = (ServerPlayer) event.getEntity();

		if (!player.hasEffect(TIRED.get())) {
			event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
			player.displayClientMessage(Component.translatable(NOT_TIRED), true);
			if (!shouldPreventSpawnPoint)
				player.setRespawnPosition(player.level().dimension(), event.getPos(), player.getYRot(), false, true);
		}
		else {
			event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
			player.startSleeping(event.getPos());
			((ServerLevel)player.level()).updateSleepingPlayerList();
			if (!shouldPreventSpawnPoint)
				player.setRespawnPosition(player.level().dimension(), event.getPos(), player.getYRot(), false, true);
		}
	}

	//Run after Sleeping Effects
	@SubscribeEvent(priority = EventPriority.LOW)
	public void resetTirednessOnWakeUp(SleepFinishedTimeEvent event) {
		if (!this.isEnabled())
			return;
		event.getLevel().players().stream().filter(LivingEntity::isSleeping).toList().forEach(player -> {
			float tirednessOnWakeUp = TirednessHandler.getOnWakeUp(player);
			TirednessHandler.set(player, tirednessOnWakeUp);
			player.removeEffect(TIRED.get());
		});

		int toSub = 11458;
		if (event.getLevel().getLevelData().isRaining())
			toSub = 11990;
		event.setTimeAddition(event.getNewTime() - toSub);
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
				&& player.hasEffect(TIRED.get());
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
			case KEEP -> { }
			case SET_AT_EFFECT -> {
				if (tiredness > tirednessToEffect)
					tiredness = tirednessToEffect.floatValue();
			}
		}

		TirednessHandler.set(event.getEntity(), tiredness);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerGui(RegisterGuiOverlaysEvent event) {
		event.registerAbove(VanillaGuiOverlay.SLEEP_FADE.id(), "tired_overlay", (gui, guiGraphics, partialTicks, screenWidth, screenHeight) -> {
			assert Minecraft.getInstance().player != null : "Minecraft.getInstance().player is null";
			if (isEnabled(Tiredness.class) && gui.shouldDrawSurvivalElements())
			{
				LocalPlayer player = Minecraft.getInstance().player;
				if (!player.hasEffect(TIRED.get()))
					return;
				//noinspection DataFlowIssue
				int amplifier = player.getEffect(TIRED.get()).getAmplifier() + 1;
				//No overlay at Tired I
				if (amplifier < 2)
					return;
				amplifier--;
				Minecraft.getInstance().getProfiler().push("tired_overlay");
				RenderSystem.disableDepthTest();
				float opacity = (amplifier * 25f) / 100.0F;
				if (opacity > 1.0F)
					opacity = 1.0F - (amplifier * 25f - 100) / 10.0F;

				int color = (int) (220.0F * opacity) << 24 | 1052704;
				guiGraphics.fill(0, 0, screenWidth, screenHeight, color);
				RenderSystem.enableDepthTest();
				Minecraft.getInstance().getProfiler().pop();
			}
		});
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
			event.getLeft().add(String.format("Tiredness: %s", new DecimalFormat("#.#").format(TirednessHandler.get(playerEntity))));
		}
	}
}