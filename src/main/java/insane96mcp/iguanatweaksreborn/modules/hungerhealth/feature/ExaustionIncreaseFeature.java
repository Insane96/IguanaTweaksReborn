package insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.block.BlockState;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Exaustion Increase", description = "Make the player consume more hunger with different actions")
public class ExaustionIncreaseFeature extends Feature {

	private final ForgeConfigSpec.ConfigValue<Double> blockBreakExaustionMultiplierConfig;

	public double blockBreakExaustionMultiplier = 0.01d;

	public ExaustionIncreaseFeature(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		blockBreakExaustionMultiplierConfig = Config.builder
				.comment("When breaking block you'll get exaustion equal to the block hardness (block hardness multipliers are taken into account too) multiplied by this value. Setting this to 0 will default to the vanilla exaustion (0.005). (It's not affected by the Mining Hardness Features)")
				.defineInRange("Block Break Exaustion Multiplier", blockBreakExaustionMultiplier, 0.0d, 1024d);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
        blockBreakExaustionMultiplier = blockBreakExaustionMultiplierConfig.get();
    }

    @SubscribeEvent
    public void breakExaustion(BlockEvent.BreakEvent event) {
        if (!this.isEnabled())
            return;
        if (blockBreakExaustionMultiplier == 0d)
            return;

        ServerWorld world = (ServerWorld) event.getWorld();
        BlockState state = world.getBlockState(event.getPos());
        double hardness = state.getBlockHardness(event.getWorld(), event.getPos());
        /*Block block = state.getBlock();
        ResourceLocation dimensionId = world.getDimensionKey().getLocation();
        double globalHardnessMultiplier = Modules.miningModule.globalHardnessFeature.getBlockGlobalHardness(block, dimensionId);
        if (globalHardnessMultiplier != -1d)
            hardness *= globalHardnessMultiplier;
        double singleHardness = Modules.miningModule.customHardnessFeature.getBlockSingleHardness(block, dimensionId);
        if (singleHardness != -1d)
            hardness = singleHardness;*/

        double exhaustion = (hardness * blockBreakExaustionMultiplier) - 0.005f;
        exhaustion = Math.max(exhaustion, 0d);
        event.getPlayer().addExhaustion((float) exhaustion);
    }
}
