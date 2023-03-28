package insane96mcp.survivalreimagined.module.world.feature;

import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.world.data.BlockTransformation;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
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
	public static final ResourceLocation TALL_GRASS = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "tall_grass");

	private static final List<String> DEFAULT_TRANSFORMATION_LIST = List.of("minecraft:grass_block,minecraft:dirt", "minecraft:dirt,minecraft:coarse_dirt");
	private static ForgeConfigSpec.ConfigValue<List<? extends String>> transformationListConfig;

	public static ArrayList<BlockTransformation> transformationList;

	@Config
	@Label(name = "Break Tall Grass", description = "Tall grass is broken when grass is transformed")
	public static Boolean breakTallGrass = true;

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
				|| event.player.walkDistO - event.player.walkDist < 0.04f
				|| event.player.tickCount % 2 == 1)
			return;

		AABB bb = event.player.getBoundingBox();
		int mX = Mth.floor(bb.minX);
		int mZ = Mth.floor(bb.minZ);
		for (int x2 = mX; x2 < bb.maxX; x2++) {
			for (int z2 = mZ; z2 < bb.maxZ; z2++) {
				BlockPos pos = new BlockPos(x2, event.player.position().y - 0.002d, z2);
				BlockState state = event.player.level.getBlockState(pos);
				for (BlockTransformation blockTransformation : transformationList) {
					if (!blockTransformation.matchesBlock(state.getBlock()))
						continue;

					if (event.player.getRandom().nextFloat() < 0.02f) {
						Block block = ForgeRegistries.BLOCKS.getValue(blockTransformation.transformTo);
						if (block == null) continue;
						event.player.level.setBlockAndUpdate(pos, block.defaultBlockState());

						if (!breakTallGrass) continue;
						pos = new BlockPos(x2, event.player.position().y + 0.002d, z2);
						state = event.player.level.getBlockState(pos);
						if (Utils.isBlockInTag(state.getBlock(), TALL_GRASS)) {
							event.player.level.destroyBlock(pos, false, event.player);
						}
					}
				}
			}
		}
	}
}