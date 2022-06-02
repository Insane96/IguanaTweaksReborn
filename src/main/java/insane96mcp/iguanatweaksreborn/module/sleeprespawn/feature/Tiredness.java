package insane96mcp.iguanatweaksreborn.module.sleeprespawn.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeConfigSpec;

@Label(name = "Tiredness", description = "Prevents sleeping if the player is not tired. Tiredness is gained by gaining exhaustion")
public class Tiredness extends Feature {

	private final ForgeConfigSpec.ConfigValue<Double> tirednessGainMultiplierConfig;

	public double tirednessGainMultiplier = 1d;

	public Tiredness(Module module) {
		super(Config.builder, module, false);
		Config.builder.comment(this.getDescription()).push(this.getName());
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

	public void onExhaustion(Player player, float amount) {
		if (!this.isEnabled())
			return;

		if (player.level.isClientSide)
			return;

		ServerPlayer serverPlayer = (ServerPlayer) player;

		
	}
}