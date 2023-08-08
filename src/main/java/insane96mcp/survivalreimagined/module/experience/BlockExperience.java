package insane96mcp.survivalreimagined.module.experience;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.data.generator.SRBlockTagsProvider;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Experience From Blocks", description = "Decrease / Increase experience dropped by blocks broken")
@LoadFeature(module = Modules.Ids.EXPERIENCE, enabledByDefault = false)
public class BlockExperience extends Feature {
	public static final TagKey<Block> NO_BLOCK_XP_MULTIPLIER = SRBlockTagsProvider.create("no_block_xp_multiplier");

	@Config(min = 0d, max = 128d)
	@Label(name = "Experience from Blocks Multiplier", description = "Experience dropped by blocks (Ores and Spawners) will be multiplied by this multiplier. Experience dropped by blocks are still affected by 'Global Experience Multiplier'\nCan be set to 0 to make blocks drop no experience")
	public static Double blockMultiplier = 1d;

	public BlockExperience(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void onBlockXPDrop(BlockEvent.BreakEvent event) {
		if (!this.isEnabled()
				|| blockMultiplier == 1.0d
				|| !event.getState().is(NO_BLOCK_XP_MULTIPLIER))
			return;

		int xpToDrop = event.getExpToDrop();
		xpToDrop *= blockMultiplier;
		event.setExpToDrop(xpToDrop);
	}
}