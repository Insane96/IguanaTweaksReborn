package insane96mcp.survivalreimagined.data;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.items.feature.Crate;
import insane96mcp.survivalreimagined.module.items.feature.FlintExpansion;
import insane96mcp.survivalreimagined.module.items.feature.Mithril;
import insane96mcp.survivalreimagined.module.movement.feature.Minecarts;
import insane96mcp.survivalreimagined.module.world.feature.BeegVeins;
import insane96mcp.survivalreimagined.module.world.feature.Fire;
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
    public static final TagKey<Block> NERFED_BONEMEAL_BLACKLIST = create("nerfed_bone_meal_blacklist");

    public SRBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        //Vanilla Tags
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(Crate.BLOCK.get());

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(Fire.CHARCOAL_LAYER.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(Mithril.BLOCK.get()).add(Mithril.ORE.get()).add(Mithril.DEEPSLATE_ORE.get())
                .add(BeegVeins.IRON_ORE_ROCK.get()).add(BeegVeins.COPPER_ORE_ROCK.get()).add(BeegVeins.GOLD_ORE_ROCK.get())
                .add(FlintExpansion.FLINT_BLOCK.get()).add(FlintExpansion.POLISHED_FLINT_BLOCK.get());

        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(Crate.BLOCK.get());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(Mithril.BLOCK.get()).add(Mithril.ORE.get()).add(Mithril.DEEPSLATE_ORE.get());

        tag(BlockTags.RAILS)
                .add(Minecarts.NETHER_INFUSED_POWERED_RAIL.get());

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
        tag(NERFED_BONEMEAL_BLACKLIST);

    }

    private static TagKey<Block> create(String tagName) {
        return TagKey.create(Registries.BLOCK, new ResourceLocation(SurvivalReimagined.MOD_ID, tagName));
    }
}
