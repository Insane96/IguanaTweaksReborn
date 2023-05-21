package insane96mcp.survivalreimagined.module.world.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.scheduled.ScheduledTasks;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.world.scheduled.LeafDecayScheduledTick;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import snownee.kiwi.config.KiwiConfigManager;
import snownee.passablefoliage.PassableFoliageCommonConfig;

@Label(name = "Fast Leaf Decay", description = "Makes leaves decay faster")
@LoadFeature(module = Modules.Ids.WORLD)
public class FastLeafDecay extends Feature {

	@Config(min = 1)
	@Label(name = "Min ticks to decay")
	public static Integer minTicksToDecay = 50;

	@Config(min = 1)
	@Label(name = "Max ticks to decay")
	public static Integer maxTicksToDecay = 400;

	public FastLeafDecay(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@Override
	public void loadConfigOptions() {
		super.loadConfigOptions();
		PassableFoliageCommonConfig.fallDamageReduction = 0.2f;
		PassableFoliageCommonConfig.fallDamageThreshold = 8;
		KiwiConfigManager.getHandler(PassableFoliageCommonConfig.class).save();
	}

	@SubscribeEvent
	public void onLogBroken(BlockEvent.BreakEvent event) {
		if (!(event.getLevel() instanceof ServerLevel serverLevel)
				|| !(serverLevel.getBlockState(event.getPos()).is(BlockTags.LOGS)))
			return;
		decayAdjacentLeaves(serverLevel, event.getPos(), event.getLevel().getRandom());
	}

	public static void decayAdjacentLeaves(ServerLevel level, BlockPos pos, RandomSource random) {
		if (!Feature.isEnabled(FastLeafDecay.class))
			return;

		Direction.stream().forEach(direction -> {
			BlockState adjacentState = level.getBlockState(pos.relative(direction));
			if (!(adjacentState.getBlock() instanceof LeavesBlock))
				return;
			ScheduledTasks.schedule(new LeafDecayScheduledTick(Mth.nextInt(random, minTicksToDecay, maxTicksToDecay), level, pos.relative(direction), random));
		});
	}
}