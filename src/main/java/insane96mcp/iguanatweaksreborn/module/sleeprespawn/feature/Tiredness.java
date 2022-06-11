package insane96mcp.iguanatweaksreborn.module.sleeprespawn.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Tiredness", description = "Prevents sleeping if the player is not tired. Tiredness is gained by gaining exhaustion")
public class Tiredness extends Feature {

	private final ForgeConfigSpec.ConfigValue<Double> tirednessGainMultiplierConfig;

	public double tirednessGainMultiplier = 1d;

	public Tiredness(Module module) {
		super(Config.builder, module);
		this.pushConfig(Config.builder);
		tirednessGainMultiplierConfig = Config.builder
				.comment("Multiply the tiredness gained by this value. Normally you gain tiredness equal to the exhaustion gained. 'Effective Hunger' doesn't affect the exhaustion gained.")
				.defineInRange("Tiredness gained multiplier", this.tirednessGainMultiplier, 0d, 128d);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.tirednessGainMultiplier = this.tirednessGainMultiplierConfig.get();
	}

	public void onFoodExhaustion(Player player, float amount) {
		if (!this.isEnabled())
			return;

		if (player.level.isClientSide)
			return;

		ServerPlayer serverPlayer = (ServerPlayer) player;

		CompoundTag persistentData = serverPlayer.getPersistentData();
		float tiredness = persistentData.getFloat(Strings.Tags.TIREDNESS);
		persistentData.putFloat(Strings.Tags.TIREDNESS, tiredness + amount);
		if (tiredness < 320 && tiredness + amount >= 320) {
			serverPlayer.displayClientMessage(new TranslatableComponent(Strings.Translatable.TIRED_ENOUGH), false);
		}
		else if (tiredness >= 400) {
			if (player.getRandom().nextDouble() < Math.min((tiredness - 400) * 0.001d, 0.02d) && !player.hasEffect(MobEffects.BLINDNESS)) {
				serverPlayer.displayClientMessage(new TranslatableComponent(Strings.Translatable.TOO_TIRED), true);
				serverPlayer.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 25, 0, false, false, false));
			}
		}
	}

	@SubscribeEvent
	public void notTiredToSleep(PlayerSleepInBedEvent event) {
		if (!this.isEnabled() || event.getResultStatus() != null)
			return;
		if (event.getPlayer().getPersistentData().getFloat(Strings.Tags.TIREDNESS) < 320f) {
			event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
			event.getPlayer().displayClientMessage(new TranslatableComponent(Strings.Translatable.NOT_TIRED), true);
		}
	}

	@SubscribeEvent
	public void resetTirednessOnWakeUp(SleepFinishedTimeEvent event) {
		if (!this.isEnabled())
			return;
		event.getWorld().players().stream().filter(LivingEntity::isSleeping).toList().forEach((player) -> player.getPersistentData().putFloat(Strings.Tags.TIREDNESS, 0f));
	}
}