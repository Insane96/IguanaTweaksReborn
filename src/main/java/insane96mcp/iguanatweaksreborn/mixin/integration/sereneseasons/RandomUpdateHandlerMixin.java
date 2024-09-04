package insane96mcp.iguanatweaksreborn.mixin.integration.sereneseasons;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import sereneseasons.handler.season.RandomUpdateHandler;

@Mixin(RandomUpdateHandler.class)
public class RandomUpdateHandlerMixin {
    /**
     * @return
     * @author Insane96MCP
     * @reason Backport of the fix for the melting of ice
     */
    /*@Overwrite(remap = false)
    private static void meltInChunk(ChunkMap chunkManager, LevelChunk chunkIn, float meltChance) {
        ServerLevel world = chunkManager.level;
        ChunkPos chunkpos = chunkIn.getPos();
        int i = chunkpos.getMinBlockX();
        int j = chunkpos.getMinBlockZ();

        if (meltChance > 0.0F && world.random.nextFloat() < meltChance) {
            BlockPos topAirPos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, world.getBlockRandomPos(i, 0, j, 15));
            BlockPos topGroundPos = topAirPos.below();
            BlockState aboveGroundState = world.getBlockState(topAirPos);
            BlockState groundState = world.getBlockState(topGroundPos);
            Holder<Biome> biome = world.getBiome(topAirPos);
            Holder<Biome> groundBiome = world.getBiome(topGroundPos);

            if (!biome.is(ModTags.Biomes.BLACKLISTED_BIOMES)
                    && SeasonHooks.getBiomeTemperature(world, biome, topGroundPos) >= 0.15F
                    && aboveGroundState.getBlock() == Blocks.SNOW)
                world.setBlockAndUpdate(topAirPos, Blocks.AIR.defaultBlockState());

            if (!groundBiome.is(ModTags.Biomes.BLACKLISTED_BIOMES)
                    && SeasonHooks.getBiomeTemperature(world, groundBiome, topGroundPos) >= 0.15F
                    && groundState.getBlock() == Blocks.ICE)
                ((IceBlockInvoker) Blocks.ICE).invokeMelt(groundState, world, topGroundPos);
        }

    }*/

    @ModifyArg(method = "meltInChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"))
    private static BlockPos onGetBiome(BlockPos blockPos, @Local(ordinal = 1) BlockPos topGroundPos) {
        return topGroundPos;
    }
}
