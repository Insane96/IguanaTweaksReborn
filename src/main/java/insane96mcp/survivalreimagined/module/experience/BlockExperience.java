package insane96mcp.survivalreimagined.module.experience;

import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.data.IdTagRange;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.data.generator.SRBlockTagsProvider;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Experience from Blocks", description = "Decrease / Increase experience dropped by blocks broken. Custom xp for blocks can be added in Experience/Experience from Blocks/blocks_experience.json")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class BlockExperience extends SRFeature {
	public static final TagKey<Block> NO_BLOCK_XP_MULTIPLIER = SRBlockTagsProvider.create("no_block_xp_multiplier");

	@Config(min = 0d, max = 128d)
	@Label(name = "Experience from Blocks Multiplier", description = "Experience dropped by blocks (Ores and Spawners) will be multiplied by this multiplier. Experience dropped by blocks are still affected by 'Global Experience Multiplier'\nCan be set to 0 to make blocks drop no experience")
	public static Double blockMultiplier = 1d;

	public static final ArrayList<IdTagRange> CUSTOM_BLOCKS_EXPERIENCE_DEFAULT = new ArrayList<>(List.of(
			IdTagRange.newTag("survivalreimagined:copper_ores", 0, 2),
			IdTagRange.newTag("survivalreimagined:iron_ores", 1, 2),
			IdTagRange.newTag("survivalreimagined:gold_ores", 2, 3),

			IdTagRange.newId("minecraft:sculk_catalyst", 40, 40)
	));

	public static final ArrayList<IdTagRange> customBlocksExperience = new ArrayList<>();

	public BlockExperience(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		JSON_CONFIGS.add(new JsonConfig<>("blocks_experience.json", customBlocksExperience, CUSTOM_BLOCKS_EXPERIENCE_DEFAULT, IdTagRange.LIST_TYPE));
	}

	//Run before smartness
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onBlockXPDrop(BlockEvent.BreakEvent event) {
		if (!this.isEnabled()
				|| event.getState().is(NO_BLOCK_XP_MULTIPLIER))
			return;

		handleBlockDrop(event);
		handleMultiplier(event);
	}

	private static void handleBlockDrop(BlockEvent.BreakEvent event) {
		int silkTouchLevel = event.getPlayer().getMainHandItem().getEnchantmentLevel(Enchantments.SILK_TOUCH);
		if (silkTouchLevel > 0)
			return;
		for (IdTagRange idTagRange : customBlocksExperience) {
			if (idTagRange.id.matchesBlock(event.getState().getBlock()))
				event.setExpToDrop(idTagRange.getRandomIntBetween(event.getLevel().getRandom()));
		}
	}

	private static void handleMultiplier(BlockEvent.BreakEvent event) {
		if (blockMultiplier == 1d)
			return;
		int xpToDrop = event.getExpToDrop();
		xpToDrop *= blockMultiplier;
		event.setExpToDrop(xpToDrop);
	}
}