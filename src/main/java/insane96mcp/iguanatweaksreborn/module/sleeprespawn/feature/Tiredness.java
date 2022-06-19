package insane96mcp.iguanatweaksreborn.module.sleeprespawn.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.network.MessageTirednessSync;
import insane96mcp.iguanatweaksreborn.network.SyncHandler;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.setup.ITMobEffects;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkDirection;

import java.text.DecimalFormat;

@Label(name = "Tiredness", description = "Prevents sleeping if the player is not tired. Tiredness is gained by gaining exhaustion. Allows you to sleep during daytime if too tired")
public class Tiredness extends Feature {

	private final ForgeConfigSpec.DoubleValue tirednessGainMultiplierConfig;
	private final ForgeConfigSpec.BooleanValue shouldPreventSpawnPointConfig;
	private final ForgeConfigSpec.DoubleValue tirednessToSleepConfig;
	private final ForgeConfigSpec.DoubleValue tirednessToEffectConfig;
	private final ForgeConfigSpec.DoubleValue tirednessPerLevelConfig;

	public double tirednessGainMultiplier = 1d;
	public boolean shouldPreventSpawnPoint = false;
	public double tirednessToSleep = 320d;
	public double tirednessToEffect = 400d;
	public double tirednessPerLevel = 20d;

	public Tiredness(Module module) {
		super(Config.builder, module);
		this.pushConfig(Config.builder);
		tirednessGainMultiplierConfig = Config.builder
				.comment("Multiply the tiredness gained by this value. Normally you gain tiredness equal to the exhaustion gained. 'Effective Hunger' doesn't affect the exhaustion gained.")
				.defineInRange("Tiredness gained multiplier", this.tirednessGainMultiplier, 0d, 128d);
		shouldPreventSpawnPointConfig = Config.builder
				.comment("If true the player will not set the spawn point if he/she can't sleep.")
				.define("Prevent Spawn Point", this.shouldPreventSpawnPoint);
		tirednessToSleepConfig = Config.builder
				.comment("Tiredness required to be able to sleep.")
				.defineInRange("Tiredness to sleep", this.tirednessToSleep, 0d, Double.MAX_VALUE);
		tirednessToEffectConfig = Config.builder
				.comment("Tiredness required to get the Tired effect.")
				.defineInRange("Tiredness for effect", this.tirednessToEffect, 0d, Double.MAX_VALUE);
		tirednessPerLevelConfig = Config.builder
				.comment("Every this Tiredness above 'Tiredness for effect' will add a new level of Tired.")
				.defineInRange("Tiredness per level", this.tirednessPerLevel, 0d, Double.MAX_VALUE);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.tirednessGainMultiplier = this.tirednessGainMultiplierConfig.get();
		this.shouldPreventSpawnPoint = this.shouldPreventSpawnPointConfig.get();
		this.tirednessToSleep = this.tirednessToSleepConfig.get();
		this.tirednessToEffect = this.tirednessToEffectConfig.get();
		this.tirednessPerLevel = this.tirednessPerLevelConfig.get();
	}

	public void onFoodExhaustion(Player player, float amount) {
		if (!this.isEnabled())
			return;

		if (player.level.isClientSide)
			return;

		ServerPlayer serverPlayer = (ServerPlayer) player;

		CompoundTag persistentData = serverPlayer.getPersistentData();
		float tiredness = persistentData.getFloat(Strings.Tags.TIREDNESS);
		float newTiredness = tiredness + amount;
		persistentData.putFloat(Strings.Tags.TIREDNESS, newTiredness);
		if (tiredness < this.tirednessToSleep && newTiredness >= this.tirednessToSleep) {
			serverPlayer.displayClientMessage(new TranslatableComponent(Strings.Translatable.TIRED_ENOUGH), false);
		}
		else if (tiredness >= this.tirednessToEffect && player.tickCount % 20 == 0) {
			serverPlayer.addEffect(new MobEffectInstance(ITMobEffects.TIRED.get(), 25, Math.min((int) ((tiredness - this.tirednessToEffect) / this.tirednessPerLevel), 4), true, false, true));
		}

		Object msg = new MessageTirednessSync(newTiredness);
		SyncHandler.CHANNEL.sendTo(msg, serverPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onTiredBreakSpeed(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled()
				|| !event.getPlayer().hasEffect(ITMobEffects.TIRED.get()))
			return;

		//noinspection ConstantConditions
		int level = event.getPlayer().getEffect(ITMobEffects.TIRED.get()).getAmplifier() + 1;
		event.setNewSpeed(event.getNewSpeed() * (1 - (level * 0.05f)));
	}

	@SubscribeEvent
	public void notTiredToSleep(PlayerSleepInBedEvent event) {
		if (!this.isEnabled()
				|| event.getResultStatus() != null
				|| event.getPlayer().level.isClientSide)
			return;

		ServerPlayer player = (ServerPlayer) event.getPlayer();

		if (player.getPersistentData().getFloat(Strings.Tags.TIREDNESS) < this.tirednessToSleep) {
			event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
			player.displayClientMessage(new TranslatableComponent(Strings.Translatable.NOT_TIRED), true);
			if (!this.shouldPreventSpawnPoint)
				player.setRespawnPosition(player.level.dimension(), event.getPos(), player.getYRot(), false, true);
		}
		else if (player.getPersistentData().getFloat(Strings.Tags.TIREDNESS) > this.tirednessToEffect) {
			event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
			player.startSleeping(event.getPos());
			((ServerLevel)player.level).updateSleepingPlayerList();
		}
	}

	@SubscribeEvent
	public void resetTirednessOnWakeUp(SleepFinishedTimeEvent event) {
		if (!this.isEnabled())
			return;
		event.getWorld().players().stream().filter(LivingEntity::isSleeping).toList().forEach((player) -> player.getPersistentData().putFloat(Strings.Tags.TIREDNESS, Mth.nextFloat(player.getRandom(), 0f, (float) (this.tirednessToSleep * 0.05f))));
	}

	@SubscribeEvent
	public void allowSleepAtDay(SleepingTimeCheckEvent event) {
		if (!this.isEnabled()
				|| event.getPlayer().getPersistentData().getFloat(Strings.Tags.TIREDNESS) < this.tirednessToEffect)
			return;
		event.setResult(Event.Result.ALLOW);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onFog(EntityViewRenderEvent.RenderFogEvent event) {
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
	public void onFog(EntityViewRenderEvent.FogColors event) {
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
	public void registerGui() {
		OverlayRegistry.registerOverlayAbove(ForgeIngameGui.FOOD_LEVEL_ELEMENT, "Tiredness", (gui, mStack, partialTicks, screenWidth, screenHeight) -> {
			boolean isMounted = Minecraft.getInstance().player.getVehicle() instanceof LivingEntity;
			if (this.isEnabled() && !isMounted && !Minecraft.getInstance().options.hideGui && gui.shouldDrawSurvivalElements())
			{
				gui.setupOverlayRenderState(true, false, GUI_ICONS);
				int left = screenWidth / 2 + 91;
				int top = screenHeight - gui.right_height;
				renderTiredness(gui, mStack, left, top);
				gui.right_height += 10;
			}
		});
	}

	private static final Vec2 UV_NOT_TIRED = new Vec2(0, 0);
	private static final Vec2 UV_SLEEPY = new Vec2(9, 0);
	private static final Vec2 UV_TIRED = new Vec2(18, 0);

	@OnlyIn(Dist.CLIENT)
	private void renderTiredness(Gui gui, PoseStack matrixStack, int left, int top) {
		Player player = (Player)Minecraft.getInstance().getCameraEntity();
		float tiredness = player.getPersistentData().getFloat(Strings.Tags.TIREDNESS);
		int numberOfZ = 0;
		if (tiredness < this.tirednessToSleep) {
			numberOfZ += tiredness / (this.tirednessToSleep / 6);
		}
		else if (tiredness < this.tirednessToEffect) {
			float tirednessBetweenSleepEffect = (float) (this.tirednessToEffect - this.tirednessToSleep);
			numberOfZ += 6 + ((tiredness - this.tirednessToSleep) / (tirednessBetweenSleepEffect / 2));
		}
		else {
			float tirednessToBlind = (float) (this.tirednessPerLevel * 5);
			numberOfZ += 8 + ((tiredness - this.tirednessToEffect) / (tirednessToBlind / 2));
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
	public void debugScreen(RenderGameOverlayEvent.Text event) {
		if (!this.isEnabled())
			return;
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer playerEntity = mc.player;
		if (playerEntity == null)
			return;
		if (mc.options.renderDebug) {
			event.getLeft().add(String.format("Tiredness: %s", new DecimalFormat("#.#").format(playerEntity.getPersistentData().getFloat(Strings.Tags.TIREDNESS))));
		}
	}
}