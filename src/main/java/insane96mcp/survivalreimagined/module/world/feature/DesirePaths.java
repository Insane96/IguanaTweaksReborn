package insane96mcp.survivalreimagined.module.world.feature;

import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.world.data.BlockTransformation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Desire Paths", description = "Wear down grass when passing on it.")
@LoadFeature(module = Modules.Ids.WORLD)
public class DesirePaths extends SRFeature {

	//Add Dirt to Path mod dependency

	private static final List<String> DEFAULT_TRANSFORMATION_LIST = List.of("minecraft:grass_block,minecraft:dirt", "minecraft:dirt,minecraft:coarse_dirt");
	private static ForgeConfigSpec.ConfigValue<List<? extends String>> transformationListConfig;

	public static ArrayList<BlockTransformation> transformationList;

	public DesirePaths(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@Override
	public void loadConfigOptions() {
		super.loadConfigOptions();
		transformationListConfig = this.getBuilder()
				.comment("Transform list of blocks")
				.defineList("Transformation List", DEFAULT_TRANSFORMATION_LIST, o -> o instanceof String);
	}

	@Override
	public void readConfig(final ModConfigEvent event) {
		super.readConfig(event);
		transformationList = BlockTransformation.parseStringList(transformationListConfig.get());
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (!this.isEnabled()
				|| event.player.level.isClientSide
				|| event.player.walkDist == event.player.walkDistO
				|| event.player.tickCount % 2 == 1)
			return;

		AABB bb = event.player.getBoundingBox();
		int mX = Mth.floor(bb.minX);
		int mY = Mth.floor(bb.minY);
		int mZ = Mth.floor(bb.minZ);
		for (int x2 = mX; x2 < bb.maxX; x2++) {
			for (int z2 = mZ; z2 < bb.maxZ; z2++) {
				BlockPos pos = new BlockPos(x2, event.player.position().y - 0.002d, z2);
				BlockState state = event.player.level.getBlockState(pos);
				for (BlockTransformation blockTransformation : transformationList) {
					if (!blockTransformation.matchesBlock(state.getBlock()))
						continue;

					if (event.player.getRandom().nextFloat() < 0.02f)
						event.player.level.setBlockAndUpdate(pos, ForgeRegistries.BLOCKS.getValue(blockTransformation.transformTo).defaultBlockState());
				}
			}
		}
	}
}