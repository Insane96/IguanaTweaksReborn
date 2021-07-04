package insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.block.BlockState;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Exhaustion Increase", description = "Make the player consume more hunger with different actions")
public class ExhaustionIncreaseFeature extends Feature {

	private final ForgeConfigSpec.ConfigValue<Double> blockBreakExhaustionMultiplierConfig;
	private final ForgeConfigSpec.ConfigValue<Double> exhaustionOnBlockBreakingConfig;

	public double blockBreakExhaustionMultiplier = 0d;
	public double exhaustionOnBlockBreaking = 0.005d;

	public ExhaustionIncreaseFeature(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		blockBreakExhaustionMultiplierConfig = Config.builder
				.comment("When you break a block you'll get exhaustion equal to the block hardness multiplied by this value. Setting this to 0 will default to the vanilla exaustion (0.005). (It's not affected by the Mining Hardness Features)")
				.defineInRange("Block Break Exhaustion Multiplier", blockBreakExhaustionMultiplier, 0.0d, 1024d);
		exhaustionOnBlockBreakingConfig = Config.builder
				.comment("When breaking block you'll get exhaustion every tick during the breaking.")
				.defineInRange("Exhaution per tick when breaking a block", exhaustionOnBlockBreaking, 0.0d, 1024d);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.blockBreakExhaustionMultiplier = this.blockBreakExhaustionMultiplierConfig.get();
		this.exhaustionOnBlockBreaking = this.exhaustionOnBlockBreakingConfig.get();
	}

    @SubscribeEvent
    public void breakExaustion(BlockEvent.BreakEvent event) {
		if (!this.isEnabled())
			return;
		if (blockBreakExhaustionMultiplier == 0d)
			return;
		ServerWorld world = (ServerWorld) event.getWorld();
		BlockState state = world.getBlockState(event.getPos());
		double hardness = state.getBlockHardness(event.getWorld(), event.getPos());
		double exhaustion = (hardness * blockBreakExhaustionMultiplier) - 0.005f;
		exhaustion = Math.max(exhaustion, 0d);
		event.getPlayer().addExhaustion((float) exhaustion);
	}

	@SubscribeEvent
	public void onBreakingBlock(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled())
			return;
		if (this.exhaustionOnBlockBreaking == 0d)
			return;
		event.getPlayer().addExhaustion((float) this.exhaustionOnBlockBreaking);
	}
}
