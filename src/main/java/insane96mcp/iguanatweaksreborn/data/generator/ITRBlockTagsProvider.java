package insane96mcp.iguanatweaksreborn.data.generator;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.farming.HardCrops;
import insane96mcp.iguanatweaksreborn.module.mining.blockhardness.BlockHardness;
import insane96mcp.iguanatweaksreborn.module.misc.Tweaks;
import insane96mcp.iguanatweaksreborn.module.misc.beaconconduit.BeaconConduit;
import insane96mcp.iguanatweaksreborn.module.sleeprespawn.death.Death;
import insane96mcp.iguanatweaksreborn.module.world.desirepaths.DesirePaths;
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

public class ITRBlockTagsProvider extends BlockTagsProvider {
    public static final TagKey<Block> RESPAWN_OBELISK_BLOCKS_TO_ROT = create("structures/respawn_obelisk/blocks_to_rot");
    public static final TagKey<Block> OBSIDIANS = create("obsidians");
    public static final TagKey<Block> GRASS_BLOCKS = create("grass_blocks");
    public static final TagKey<Block> COPPER_ORES = create("copper_ores");
    public static final TagKey<Block> GOLD_ORES = create("gold_ores");
    public static final TagKey<Block> IRON_ORES = create("iron_ores");

    public ITRBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        //Vanilla Tags
        tag(BlockTags.FALL_DAMAGE_RESETTING)
                .add(Blocks.COBWEB);
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(Death.GRAVE.block().get())
                .add(BeaconConduit.BEACON.block().get());

        //Mod's tags
        tag(RESPAWN_OBELISK_BLOCKS_TO_ROT)
                .add(Blocks.COBBLESTONE).add(Blocks.MOSSY_COBBLESTONE).add(Blocks.STONE_BRICKS).add(Blocks.STONE_BRICK_SLAB).add(Blocks.SANDSTONE).add(Blocks.SANDSTONE_SLAB).add(Blocks.ORANGE_TERRACOTTA).add(Blocks.COBBLESTONE_SLAB).add(Blocks.SNOW_BLOCK).add(Blocks.SNOW).add(Blocks.BLUE_ICE).add(Blocks.DEEPSLATE_BRICKS).add(Blocks.DEEPSLATE_BRICK_SLAB).add(Blocks.COBBLED_DEEPSLATE).add(Blocks.BIRCH_PLANKS).add(Blocks.BIRCH_SLAB).add(Blocks.BIRCH_LOG).add(Blocks.PRISMARINE).add(Blocks.PRISMARINE_BRICKS).add(Blocks.PRISMARINE_BRICK_SLAB);

        tag(OBSIDIANS)
                .add(Blocks.OBSIDIAN).add(Blocks.CRYING_OBSIDIAN);

        tag(HardCrops.HARDER_CROPS_TAG)
                .add(Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.BEETROOTS);

        tag(BlockHardness.HARDNESS_BLACKLIST)
                .addTag(OBSIDIANS);
        tag(BlockHardness.DEPTH_MULTIPLIER_BLACKLIST)
                .addTag(OBSIDIANS);

        tag(DesirePaths.TALL_GRASS)
                .add(Blocks.GRASS).add(Blocks.TALL_GRASS).add(Blocks.FERN).add(Blocks.DEAD_BUSH);

        tag(GRASS_BLOCKS)
                .add(Blocks.GRASS_BLOCK, Blocks.PODZOL, Blocks.MYCELIUM);

		//noinspection unchecked
		tag(Tweaks.BREAK_ON_FALL)
				.addTags(Tags.Blocks.GLASS, BlockTags.LEAVES);
		//noinspection unchecked
		tag(TimberTrees.TIMBER_TRUNKS)
				.addTags(BlockTags.OVERWORLD_NATURAL_LOGS);

    }

    public static TagKey<Block> create(String tagName) {
        return TagKey.create(Registries.BLOCK, new ResourceLocation(IguanaTweaksReborn.MOD_ID, tagName));
    }
}
