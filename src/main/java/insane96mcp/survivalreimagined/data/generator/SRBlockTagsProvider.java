package insane96mcp.survivalreimagined.data.generator;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.farming.feature.BoneMeal;
import insane96mcp.survivalreimagined.module.farming.feature.HarderCrops;
import insane96mcp.survivalreimagined.module.items.feature.Crate;
import insane96mcp.survivalreimagined.module.items.feature.ExplosiveBarrel;
import insane96mcp.survivalreimagined.module.items.feature.FlintExpansion;
import insane96mcp.survivalreimagined.module.mining.block.MultiBlockBlastFurnaceBlock;
import insane96mcp.survivalreimagined.module.mining.block.MultiBlockSoulBlastFurnaceBlock;
import insane96mcp.survivalreimagined.module.mining.feature.Durium;
import insane96mcp.survivalreimagined.module.mining.feature.Forging;
import insane96mcp.survivalreimagined.module.mining.feature.MultiBlockFurnaces;
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
                .add(Crate.BLOCK.block().get(), ExplosiveBarrel.BLOCK.block().get());

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(CoalFire.CHARCOAL_LAYER.block().get())
                .add(CoalFire.SOUL_SAND_HELLISH_COAL_ORE.block().get(), CoalFire.SOUL_SOIL_HELLISH_COAL_ORE.block().get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(Durium.BLOCK.block().get(), Durium.ORE.block().get(), Durium.DEEPSLATE_ORE.block().get(), Durium.SCRAP_BLOCK.block().get())
                .add(SoulSteel.BLOCK.block().get())
                .add(OreGeneration.IRON_ORE_ROCK.block().get()).add(OreGeneration.COPPER_ORE_ROCK.block().get()).add(OreGeneration.GOLD_ORE_ROCK.block().get())
                .add(OreGeneration.POOR_RICH_IRON_ORE.poorOre().block().get(), OreGeneration.POOR_RICH_IRON_ORE.richOre().block().get(), OreGeneration.POOR_RICH_IRON_ORE.poorDeepslateOre().block().get(), OreGeneration.POOR_RICH_IRON_ORE.richDeepslateOre().block().get())
                .add(OreGeneration.POOR_RICH_GOLD_ORE.poorOre().block().get(), OreGeneration.POOR_RICH_GOLD_ORE.richOre().block().get(), OreGeneration.POOR_RICH_GOLD_ORE.poorDeepslateOre().block().get(), OreGeneration.POOR_RICH_GOLD_ORE.richDeepslateOre().block().get())
                .add(OreGeneration.POOR_RICH_COPPER_ORE.poorOre().block().get(), OreGeneration.POOR_RICH_COPPER_ORE.richOre().block().get(), OreGeneration.POOR_RICH_COPPER_ORE.poorDeepslateOre().block().get(), OreGeneration.POOR_RICH_COPPER_ORE.richDeepslateOre().block().get())
                .add(FlintExpansion.FLINT_BLOCK.block().get(), FlintExpansion.POLISHED_FLINT_BLOCK.block().get(), FlintExpansion.FLINT_ROCK.block().get())
                .add(Death.GRAVE.block().get())
                .add(MultiBlockFurnaces.BLAST_FURNACE.block().get(), MultiBlockFurnaces.SOUL_BLAST_FURNACE.block().get())
                .add(Forging.FORGE.block().get());

        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(Crate.BLOCK.block().get())
                .add(OreGeneration.POOR_RICH_IRON_ORE.poorOre().block().get(), OreGeneration.POOR_RICH_IRON_ORE.richOre().block().get(), OreGeneration.POOR_RICH_IRON_ORE.poorDeepslateOre().block().get(), OreGeneration.POOR_RICH_IRON_ORE.richDeepslateOre().block().get())
                .add(OreGeneration.POOR_RICH_COPPER_ORE.poorOre().block().get(), OreGeneration.POOR_RICH_COPPER_ORE.richOre().block().get(), OreGeneration.POOR_RICH_COPPER_ORE.poorDeepslateOre().block().get(), OreGeneration.POOR_RICH_COPPER_ORE.richDeepslateOre().block().get());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(OreGeneration.POOR_RICH_GOLD_ORE.poorOre().block().get(), OreGeneration.POOR_RICH_GOLD_ORE.richOre().block().get(), OreGeneration.POOR_RICH_GOLD_ORE.poorDeepslateOre().block().get(), OreGeneration.POOR_RICH_GOLD_ORE.richDeepslateOre().block().get())
                .add(Durium.BLOCK.block().get(), Durium.ORE.block().get(), Durium.DEEPSLATE_ORE.block().get(), Durium.SCRAP_BLOCK.block().get())
                .add(SoulSteel.BLOCK.block().get())
                .add(CoalFire.SOUL_SAND_HELLISH_COAL_ORE.block().get(), CoalFire.SOUL_SOIL_HELLISH_COAL_ORE.block().get());

        tag(BlockTags.RAILS)
                .add(Minecarts.COPPER_POWERED_RAIL.block().get(), Minecarts.GOLDEN_POWERED_RAIL.block().get(), Minecarts.NETHER_INFUSED_POWERED_RAIL.block().get());

        tag(BlockTags.IRON_ORES)
                .add(OreGeneration.POOR_RICH_IRON_ORE.poorOre().block().get(), OreGeneration.POOR_RICH_IRON_ORE.richOre().block().get(), OreGeneration.POOR_RICH_IRON_ORE.poorDeepslateOre().block().get(), OreGeneration.POOR_RICH_IRON_ORE.richDeepslateOre().block().get());
        tag(BlockTags.GOLD_ORES)
                .add(OreGeneration.POOR_RICH_GOLD_ORE.poorOre().block().get(), OreGeneration.POOR_RICH_GOLD_ORE.richOre().block().get(), OreGeneration.POOR_RICH_GOLD_ORE.poorDeepslateOre().block().get(), OreGeneration.POOR_RICH_GOLD_ORE.richDeepslateOre().block().get());
        tag(BlockTags.COPPER_ORES)
                .add(OreGeneration.POOR_RICH_COPPER_ORE.poorOre().block().get(), OreGeneration.POOR_RICH_COPPER_ORE.richOre().block().get(), OreGeneration.POOR_RICH_COPPER_ORE.poorDeepslateOre().block().get(), OreGeneration.POOR_RICH_COPPER_ORE.richDeepslateOre().block().get());

        tag(BlockTags.BEACON_BASE_BLOCKS)
                .add(Durium.BLOCK.block().get(), SoulSteel.BLOCK.block().get());

        //Mod's tags
        tag(RESPAWN_OBELISK_BLOCKS_TO_ROT)
                .add(Blocks.COBBLESTONE).add(Blocks.MOSSY_COBBLESTONE).add(Blocks.STONE_BRICKS).add(Blocks.STONE_BRICK_SLAB).add(Blocks.SANDSTONE).add(Blocks.SANDSTONE_SLAB).add(Blocks.ORANGE_TERRACOTTA).add(Blocks.COBBLESTONE_SLAB).add(Blocks.SNOW_BLOCK).add(Blocks.SNOW).add(Blocks.BLUE_ICE).add(Blocks.DEEPSLATE_BRICKS).add(Blocks.DEEPSLATE_BRICK_SLAB).add(Blocks.COBBLED_DEEPSLATE).add(Blocks.BIRCH_PLANKS).add(Blocks.BIRCH_SLAB).add(Blocks.BIRCH_LOG).add(Blocks.PRISMARINE).add(Blocks.PRISMARINE_BRICKS).add(Blocks.PRISMARINE_BRICK_SLAB);

        tag(OBSIDIANS)
                .add(Blocks.OBSIDIAN).add(Blocks.CRYING_OBSIDIAN);

        tag(HarderCrops.HARDER_CROPS_TAG)
                .add(Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.BEETROOTS);

        tag(HARDNESS_BLACKLIST)
                .addTag(OBSIDIANS);
        tag(DEPTH_MULTIPLIER_BLACKLIST)
                .addTag(OBSIDIANS);

        tag(TALL_GRASS)
                .add(Blocks.GRASS).add(Blocks.TALL_GRASS).add(Blocks.FERN).add(Blocks.DEAD_BUSH);

        tag(NO_BLOCK_XP_MULTIPLIER);
        tag(BoneMeal.BLOCK_BLACKLIST);

        tag(MultiBlockBlastFurnaceBlock.BOTTOM_BLOCKS_TAG)
                .add(Blocks.SMOOTH_STONE, Blocks.SMOOTH_STONE_SLAB);
        tag(MultiBlockBlastFurnaceBlock.MIDDLE_BLOCKS_TAG)
                .add(Blocks.BRICKS);
        tag(MultiBlockBlastFurnaceBlock.TOP_BLOCKS_TAG)
                .add(Blocks.BRICKS, Blocks.BRICK_STAIRS);

        tag(MultiBlockSoulBlastFurnaceBlock.BOTTOM_BLOCKS_TAG)
                .add(Blocks.GILDED_BLACKSTONE, Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICKS);
        tag(MultiBlockSoulBlastFurnaceBlock.MIDDLE_BLOCKS_TAG)
                .add(Blocks.RED_NETHER_BRICKS);
        tag(MultiBlockSoulBlastFurnaceBlock.TOP_BLOCKS_TAG)
                .add(Blocks.RED_NETHER_BRICKS, Blocks.RED_NETHER_BRICK_STAIRS);
    }

    public static TagKey<Block> create(String tagName) {
        return TagKey.create(Registries.BLOCK, new ResourceLocation(SurvivalReimagined.MOD_ID, tagName));
    }
}
