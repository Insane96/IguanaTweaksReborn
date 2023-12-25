package insane96mcp.iguanatweaksreborn.data.generator;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.combat.fletching.Fletching;
import insane96mcp.iguanatweaksreborn.module.experience.enchanting.EnchantingFeature;
import insane96mcp.iguanatweaksreborn.module.farming.HardCrops;
import insane96mcp.iguanatweaksreborn.module.farming.bonemeal.BoneMeal;
import insane96mcp.iguanatweaksreborn.module.items.crate.Crate;
import insane96mcp.iguanatweaksreborn.module.items.explosivebarrel.ExplosiveBarrel;
import insane96mcp.iguanatweaksreborn.module.items.flintexpansion.FlintExpansion;
import insane96mcp.iguanatweaksreborn.module.mining.Durium;
import insane96mcp.iguanatweaksreborn.module.mining.SoulSteel;
import insane96mcp.iguanatweaksreborn.module.mining.forging.Forging;
import insane96mcp.iguanatweaksreborn.module.mining.keego.Keego;
import insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.MultiBlockFurnaces;
import insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.block.MultiBlockBlastFurnaceBlock;
import insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.block.MultiBlockSoulBlastFurnaceBlock;
import insane96mcp.iguanatweaksreborn.module.misc.Tweaks;
import insane96mcp.iguanatweaksreborn.module.misc.beaconconduit.BeaconConduit;
import insane96mcp.iguanatweaksreborn.module.movement.minecarts.Minecarts;
import insane96mcp.iguanatweaksreborn.module.sleeprespawn.death.Death;
import insane96mcp.iguanatweaksreborn.module.world.coalfire.CoalFire;
import insane96mcp.iguanatweaksreborn.module.world.oregeneration.OreGeneration;
import insane96mcp.iguanatweaksreborn.module.world.timber.TimberTrees;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
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
    public static final TagKey<Block> GRASS_BLOCKS = create("grass_blocks");
    public static final TagKey<Block> COPPER_ORES = create("copper_ores");
    public static final TagKey<Block> GOLD_ORES = create("gold_ores");
    public static final TagKey<Block> IRON_ORES = create("iron_ores");

    public SRBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        //Vanilla Tags
        /*tag(BlockTags.MINEABLE_WITH_HOE)
                .add(Solarium.SOLIUM_MOSS.block().get());*/

        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(Crate.BLOCK.get(), ExplosiveBarrel.BLOCK.block().get(), Fletching.FLETCHING_TABLE.block().get());

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
                .add(Death.GRAVE.block().get())
                .add(MultiBlockFurnaces.BLAST_FURNACE.block().get(), MultiBlockFurnaces.SOUL_BLAST_FURNACE.block().get())
                .add(Forging.FORGE.block().get())
                .add(FlintExpansion.FLINT_ROCK.block().get())
                .add(EnchantingFeature.ENCHANTING_TABLE.block().get())
                .add(BeaconConduit.BEACON.block().get());

        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(Crate.BLOCK.get())
                .add(OreGeneration.POOR_RICH_IRON_ORE.poorOre().block().get(), OreGeneration.POOR_RICH_IRON_ORE.richOre().block().get(), OreGeneration.POOR_RICH_IRON_ORE.poorDeepslateOre().block().get(), OreGeneration.POOR_RICH_IRON_ORE.richDeepslateOre().block().get())
                .add(OreGeneration.POOR_RICH_COPPER_ORE.poorOre().block().get(), OreGeneration.POOR_RICH_COPPER_ORE.richOre().block().get(), OreGeneration.POOR_RICH_COPPER_ORE.poorDeepslateOre().block().get(), OreGeneration.POOR_RICH_COPPER_ORE.richDeepslateOre().block().get())
                .add(EnchantingFeature.ENCHANTING_TABLE.block().get());

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
                .add(Durium.BLOCK.block().get(), SoulSteel.BLOCK.block().get(), Keego.BLOCK.block().get());

        tag(BlockTags.FALL_DAMAGE_RESETTING)
                .add(Blocks.COBWEB);

        tag(BlockTags.REPLACEABLE_BY_TREES)
                .add(OreGeneration.COPPER_ORE_ROCK.block().get(), OreGeneration.IRON_ORE_ROCK.block().get(), OreGeneration.GOLD_ORE_ROCK.block().get())
                .add(FlintExpansion.FLINT_ROCK.block().get());

        //Mod's tags
        tag(RESPAWN_OBELISK_BLOCKS_TO_ROT)
                .add(Blocks.COBBLESTONE).add(Blocks.MOSSY_COBBLESTONE).add(Blocks.STONE_BRICKS).add(Blocks.STONE_BRICK_SLAB).add(Blocks.SANDSTONE).add(Blocks.SANDSTONE_SLAB).add(Blocks.ORANGE_TERRACOTTA).add(Blocks.COBBLESTONE_SLAB).add(Blocks.SNOW_BLOCK).add(Blocks.SNOW).add(Blocks.BLUE_ICE).add(Blocks.DEEPSLATE_BRICKS).add(Blocks.DEEPSLATE_BRICK_SLAB).add(Blocks.COBBLED_DEEPSLATE).add(Blocks.BIRCH_PLANKS).add(Blocks.BIRCH_SLAB).add(Blocks.BIRCH_LOG).add(Blocks.PRISMARINE).add(Blocks.PRISMARINE_BRICKS).add(Blocks.PRISMARINE_BRICK_SLAB);

        tag(OBSIDIANS)
                .add(Blocks.OBSIDIAN).add(Blocks.CRYING_OBSIDIAN);

        tag(HardCrops.HARDER_CROPS_TAG)
                .add(Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.BEETROOTS);

        tag(HARDNESS_BLACKLIST)
                .addTag(OBSIDIANS);
        tag(DEPTH_MULTIPLIER_BLACKLIST)
                .addTag(OBSIDIANS);

        tag(TALL_GRASS)
                .add(Blocks.GRASS).add(Blocks.TALL_GRASS).add(Blocks.FERN).add(Blocks.DEAD_BUSH);

        tag(GRASS_BLOCKS)
                .add(Blocks.GRASS_BLOCK, Blocks.PODZOL, Blocks.MYCELIUM);

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

        tag(COPPER_ORES).add(Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE, OreGeneration.POOR_RICH_COPPER_ORE.poorOre().block().get(), OreGeneration.POOR_RICH_COPPER_ORE.richOre().block().get(), OreGeneration.POOR_RICH_COPPER_ORE.poorDeepslateOre().block().get(), OreGeneration.POOR_RICH_COPPER_ORE.richDeepslateOre().block().get());
        tag(IRON_ORES).add(Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE, OreGeneration.POOR_RICH_IRON_ORE.poorOre().block().get(), OreGeneration.POOR_RICH_IRON_ORE.richOre().block().get(), OreGeneration.POOR_RICH_IRON_ORE.poorDeepslateOre().block().get(), OreGeneration.POOR_RICH_IRON_ORE.richDeepslateOre().block().get());
        tag(GOLD_ORES).add(Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE, OreGeneration.POOR_RICH_GOLD_ORE.poorOre().block().get(), OreGeneration.POOR_RICH_GOLD_ORE.richOre().block().get(), OreGeneration.POOR_RICH_GOLD_ORE.poorDeepslateOre().block().get(), OreGeneration.POOR_RICH_GOLD_ORE.richDeepslateOre().block().get());

        //noinspection unchecked
        tag(Tweaks.FALL_ON_BREAK)
                .addTags(Tags.Blocks.GLASS, BlockTags.LEAVES);

        tag(TimberTrees.TIMBER_TRUNKS)
                .addTag(BlockTags.OVERWORLD_NATURAL_LOGS);
    }

    public static TagKey<Block> create(String tagName) {
        return TagKey.create(Registries.BLOCK, new ResourceLocation(IguanaTweaksReborn.MOD_ID, tagName));
    }
}
