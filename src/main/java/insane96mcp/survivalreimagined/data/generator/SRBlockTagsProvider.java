package insane96mcp.survivalreimagined.data.generator;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.farming.feature.BoneMeal;
import insane96mcp.survivalreimagined.module.items.feature.Crate;
import insane96mcp.survivalreimagined.module.items.feature.FlintExpansion;
import insane96mcp.survivalreimagined.module.mining.feature.Mithril;
import insane96mcp.survivalreimagined.module.mining.feature.SoulSteel;
import insane96mcp.survivalreimagined.module.movement.feature.Minecarts;
import insane96mcp.survivalreimagined.module.sleeprespawn.feature.Death;
import insane96mcp.survivalreimagined.module.world.feature.CoalFire;
import insane96mcp.survivalreimagined.module.world.feature.OreGeneration;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class SRBlockTagsProvider extends BlockTagsProvider {
    public static final TagKey<Block> RESPAWN_OBELISK_BLOCKS_TO_ROT = create("structures/respawn_obelisk/blocks_to_rot");
    public static final TagKey<Block> OBSIDIANS = create("obsidians");
    public static final TagKey<Block> HARDER_CROPS = create("harder_crops");
    public static final TagKey<Block> HARDER_CROPS_BLACKLIST = create("harder_crops_blacklist");
    public static final TagKey<Block> HARDNESS_BLACKLIST = create("hardness_blacklist");
    public static final TagKey<Block> DEPTH_MULTIPLIER_BLACKLIST = create("depth_multiplier_blacklist");
    public static final TagKey<Block> TALL_GRASS = create("tall_grass");
    public static final TagKey<Block> NO_BLOCK_XP_MULTIPLIER = create("no_block_xp_multiplier");

    public SRBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        //Vanilla Tags
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(Crate.BLOCK.get());

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(CoalFire.CHARCOAL_LAYER.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(Mithril.BLOCK.get(), Mithril.ORE.get(), Mithril.DEEPSLATE_ORE.get())
                .add(SoulSteel.BLOCK.block().get())
                .add(OreGeneration.IRON_ORE_ROCK.block().get()).add(OreGeneration.COPPER_ORE_ROCK.block().get()).add(OreGeneration.GOLD_ORE_ROCK.block().get())
                .add(OreGeneration.POOR_RICH_IRON_ORE.poorOre().block().get(), OreGeneration.POOR_RICH_IRON_ORE.richOre().block().get(), OreGeneration.POOR_RICH_IRON_ORE.poorDeepslateOre().block().get(), OreGeneration.POOR_RICH_IRON_ORE.richDeepslateOre().block().get())
                .add(OreGeneration.POOR_RICH_GOLD_ORE.poorOre().block().get(), OreGeneration.POOR_RICH_GOLD_ORE.richOre().block().get(), OreGeneration.POOR_RICH_GOLD_ORE.poorDeepslateOre().block().get(), OreGeneration.POOR_RICH_GOLD_ORE.richDeepslateOre().block().get())
                .add(OreGeneration.POOR_RICH_COPPER_ORE.poorOre().block().get(), OreGeneration.POOR_RICH_COPPER_ORE.richOre().block().get(), OreGeneration.POOR_RICH_COPPER_ORE.poorDeepslateOre().block().get(), OreGeneration.POOR_RICH_COPPER_ORE.richDeepslateOre().block().get())
                .add(FlintExpansion.FLINT_BLOCK.get()).add(FlintExpansion.POLISHED_FLINT_BLOCK.get())
                .add(Death.GRAVE.block().get());

        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(Crate.BLOCK.get())
                .add(OreGeneration.POOR_RICH_IRON_ORE.poorOre().block().get(), OreGeneration.POOR_RICH_IRON_ORE.richOre().block().get(), OreGeneration.POOR_RICH_IRON_ORE.poorDeepslateOre().block().get(), OreGeneration.POOR_RICH_IRON_ORE.richDeepslateOre().block().get())
                .add(OreGeneration.POOR_RICH_COPPER_ORE.poorOre().block().get(), OreGeneration.POOR_RICH_COPPER_ORE.richOre().block().get(), OreGeneration.POOR_RICH_COPPER_ORE.poorDeepslateOre().block().get(), OreGeneration.POOR_RICH_COPPER_ORE.richDeepslateOre().block().get());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(OreGeneration.POOR_RICH_GOLD_ORE.poorOre().block().get(), OreGeneration.POOR_RICH_GOLD_ORE.richOre().block().get(), OreGeneration.POOR_RICH_GOLD_ORE.poorDeepslateOre().block().get(), OreGeneration.POOR_RICH_GOLD_ORE.richDeepslateOre().block().get())
                .add(Mithril.BLOCK.get(), Mithril.ORE.get(), Mithril.DEEPSLATE_ORE.get())
                .add(SoulSteel.BLOCK.block().get());

        tag(BlockTags.RAILS)
                .add(Minecarts.NETHER_INFUSED_POWERED_RAIL.get());

        tag(BlockTags.IRON_ORES)
                .add(OreGeneration.POOR_RICH_IRON_ORE.poorOre().block().get(), OreGeneration.POOR_RICH_IRON_ORE.richOre().block().get(), OreGeneration.POOR_RICH_IRON_ORE.poorDeepslateOre().block().get(), OreGeneration.POOR_RICH_IRON_ORE.richDeepslateOre().block().get());
        tag(BlockTags.GOLD_ORES)
                .add(OreGeneration.POOR_RICH_GOLD_ORE.poorOre().block().get(), OreGeneration.POOR_RICH_GOLD_ORE.richOre().block().get(), OreGeneration.POOR_RICH_GOLD_ORE.poorDeepslateOre().block().get(), OreGeneration.POOR_RICH_GOLD_ORE.richDeepslateOre().block().get());
        tag(BlockTags.COPPER_ORES)
                .add(OreGeneration.POOR_RICH_COPPER_ORE.poorOre().block().get(), OreGeneration.POOR_RICH_COPPER_ORE.richOre().block().get(), OreGeneration.POOR_RICH_COPPER_ORE.poorDeepslateOre().block().get(), OreGeneration.POOR_RICH_COPPER_ORE.richDeepslateOre().block().get());

        tag(BlockTags.BEACON_BASE_BLOCKS)
                .add(Mithril.BLOCK.get(), SoulSteel.BLOCK.block().get());

        //Mod's tags
        tag(RESPAWN_OBELISK_BLOCKS_TO_ROT)
                .add(Blocks.COBBLESTONE).add(Blocks.MOSSY_COBBLESTONE).add(Blocks.STONE_BRICKS).add(Blocks.STONE_BRICK_SLAB).add(Blocks.SANDSTONE).add(Blocks.SANDSTONE_SLAB).add(Blocks.ORANGE_TERRACOTTA).add(Blocks.COBBLESTONE_SLAB).add(Blocks.SNOW_BLOCK).add(Blocks.SNOW).add(Blocks.BLUE_ICE).add(Blocks.DEEPSLATE_BRICKS).add(Blocks.DEEPSLATE_BRICK_SLAB).add(Blocks.COBBLED_DEEPSLATE).add(Blocks.BIRCH_PLANKS).add(Blocks.BIRCH_SLAB).add(Blocks.BIRCH_LOG).add(Blocks.PRISMARINE).add(Blocks.PRISMARINE_BRICKS).add(Blocks.PRISMARINE_BRICK_SLAB);

        tag(OBSIDIANS)
                .add(Blocks.OBSIDIAN).add(Blocks.CRYING_OBSIDIAN);

        tag(HARDER_CROPS);
        tag(HARDER_CROPS_BLACKLIST);

        tag(HARDNESS_BLACKLIST)
                .addTag(OBSIDIANS);
        tag(DEPTH_MULTIPLIER_BLACKLIST)
                .addTag(OBSIDIANS);

        tag(TALL_GRASS)
                .add(Blocks.GRASS).add(Blocks.TALL_GRASS).add(Blocks.FERN).add(Blocks.DEAD_BUSH);

        tag(NO_BLOCK_XP_MULTIPLIER);
        tag(BoneMeal.BLOCK_BLACKLIST);

    }

    public static TagKey<Block> create(String tagName) {
        return TagKey.create(Registries.BLOCK, new ResourceLocation(SurvivalReimagined.MOD_ID, tagName));
    }
}
