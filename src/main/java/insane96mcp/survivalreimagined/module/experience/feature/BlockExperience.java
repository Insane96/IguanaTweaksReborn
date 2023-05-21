package insane96mcp.survivalreimagined.module.experience.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Experience From Blocks", description = "Decrease / Increase experience dropped by blocks broken")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class BlockExperience extends Feature {
	public static final ResourceLocation NO_BLOCK_XP_MULTIPLIER = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "no_block_xp_multiplier");

	@Config(min = 0d, max = 128d)
	@Label(name = "Experience from Blocks Multiplier", description = "Experience dropped by blocks (Ores and Spawners) will be multiplied by this multiplier. Experience dropped by blocks are still affected by 'Global Experience Multiplier'\nCan be set to 0 to make blocks drop no experience")
	public static Double blockMultiplier = 2d;

	public BlockExperience(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void onBlockXPDrop(BlockEvent.BreakEvent event) {
		if (!this.isEnabled()
				|| blockMultiplier == 1.0d
				|| !Utils.isBlockInTag(event.getState().getBlock(), NO_BLOCK_XP_MULTIPLIER))
			return;

		int xpToDrop = event.getExpToDrop();
		xpToDrop *= blockMultiplier;
		event.setExpToDrop(xpToDrop);
	}
}