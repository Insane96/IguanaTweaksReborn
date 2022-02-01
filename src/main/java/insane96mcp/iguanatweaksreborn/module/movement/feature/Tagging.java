package insane96mcp.iguanatweaksreborn.module.movement.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Tagging", description = "Player's slowed down for a brief moment when hit.")
public class Tagging extends Feature {

	private final ForgeConfigSpec.ConfigValue<Integer> slownessLevelConfig;
	private final ForgeConfigSpec.ConfigValue<Integer> durationMultiplierConfig;

	public int slownessLevel = 1;
	public int durationMultiplier = 6;

	public Tagging(Module module) {
		super(Config.builder, module);
		slownessLevelConfig = Config.builder
				.comment("Which level of Slowness is applied to the player (level 0 is Slowness I).")
				.defineInRange("Slowness level", this.slownessLevel, 0, 10);
		durationMultiplierConfig = Config.builder
				.comment("Slowness is applied for damage_taken * this_value ticks.")
				.defineInRange("Duration multiplier", this.durationMultiplier, 0, 100);
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.slownessLevel = this.slownessLevelConfig.get();
		this.durationMultiplier = this.durationMultiplierConfig.get();
	}

	@SubscribeEvent
	public void onDamageTaken(LivingDamageEvent event) {
		if (!this.isEnabled())
			return;
		if (!(event.getEntity() instanceof Player playerEntity))
			return;
		if (event.getSource().getEntity() instanceof LivingEntity)
			playerEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int) (event.getAmount() * this.durationMultiplier), this.slownessLevel, false, false, true));
	}
}