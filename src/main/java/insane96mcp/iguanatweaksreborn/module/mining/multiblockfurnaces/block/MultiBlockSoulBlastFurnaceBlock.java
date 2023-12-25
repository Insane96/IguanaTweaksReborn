package insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.block;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.data.generator.SRBlockTagsProvider;
import insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.MultiBlockFurnaces;
import insane96mcp.iguanatweaksreborn.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.Map;

public class MultiBlockSoulBlastFurnaceBlock extends AbstractMultiBlockFurnace {

    public static TagKey<Block> BOTTOM_BLOCKS_TAG = SRBlockTagsProvider.create("soul_blast_furnace/bottom_blocks");
    public static TagKey<Block> MIDDLE_BLOCKS_TAG = SRBlockTagsProvider.create("soul_blast_furnace/middle_blocks");
    public static TagKey<Block> TOP_BLOCKS_TAG = SRBlockTagsProvider.create("soul_blast_furnace/top_blocks");
    public static Map<Vec3i, TagKey<Block>> RELATIVE_POS_BLOCK_TAGS = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(new Vec3i(-1, -1, -1), BOTTOM_BLOCKS_TAG),
            new AbstractMap.SimpleEntry<>(new Vec3i(-1, -1, 0), BOTTOM_BLOCKS_TAG),
            new AbstractMap.SimpleEntry<>(new Vec3i(-1, -1, 1), BOTTOM_BLOCKS_TAG),
            new AbstractMap.SimpleEntry<>(new Vec3i(0, -1, -1), BOTTOM_BLOCKS_TAG),
            new AbstractMap.SimpleEntry<>(new Vec3i(0, -1, 1), BOTTOM_BLOCKS_TAG),
            new AbstractMap.SimpleEntry<>(new Vec3i(0, -1, 0), BOTTOM_BLOCKS_TAG),
            new AbstractMap.SimpleEntry<>(new Vec3i(1, -1, -1), BOTTOM_BLOCKS_TAG),
            new AbstractMap.SimpleEntry<>(new Vec3i(1, -1, 0), BOTTOM_BLOCKS_TAG),
            new AbstractMap.SimpleEntry<>(new Vec3i(1, -1, 1), BOTTOM_BLOCKS_TAG),

            new AbstractMap.SimpleEntry<>(new Vec3i(-1, 0, -1), MIDDLE_BLOCKS_TAG),
            new AbstractMap.SimpleEntry<>(new Vec3i(-1, 0, 0), MIDDLE_BLOCKS_TAG),
            new AbstractMap.SimpleEntry<>(new Vec3i(-1, 0, 1), MIDDLE_BLOCKS_TAG),
            new AbstractMap.SimpleEntry<>(new Vec3i(0, 0, -1), MIDDLE_BLOCKS_TAG),
            new AbstractMap.SimpleEntry<>(new Vec3i(0, 0, 1), MIDDLE_BLOCKS_TAG),
            new AbstractMap.SimpleEntry<>(new Vec3i(1, 0, -1), MIDDLE_BLOCKS_TAG),
            new AbstractMap.SimpleEntry<>(new Vec3i(1, 0, 0), MIDDLE_BLOCKS_TAG),
            new AbstractMap.SimpleEntry<>(new Vec3i(1, 0, 1), MIDDLE_BLOCKS_TAG),

            new AbstractMap.SimpleEntry<>(new Vec3i(-1, 1, -1), TOP_BLOCKS_TAG),
            new AbstractMap.SimpleEntry<>(new Vec3i(-1, 1, 0), TOP_BLOCKS_TAG),
            new AbstractMap.SimpleEntry<>(new Vec3i(-1, 1, 1), TOP_BLOCKS_TAG),
            new AbstractMap.SimpleEntry<>(new Vec3i(0, 1, -1), TOP_BLOCKS_TAG),
            new AbstractMap.SimpleEntry<>(new Vec3i(0, 1, 1), TOP_BLOCKS_TAG),
            new AbstractMap.SimpleEntry<>(new Vec3i(1, 1, -1), TOP_BLOCKS_TAG),
            new AbstractMap.SimpleEntry<>(new Vec3i(1, 1, 0), TOP_BLOCKS_TAG),
            new AbstractMap.SimpleEntry<>(new Vec3i(1, 1, 1), TOP_BLOCKS_TAG)
    );

    public MultiBlockSoulBlastFurnaceBlock(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new MultiBlockSoulBlastFurnaceBlockEntity(pPos, pState);
    }

    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTicker(pLevel, pBlockEntityType, MultiBlockFurnaces.SOUL_BLAST_FURNACE_BLOCK_ENTITY_TYPE.get());
    }

    @Override
    protected void openContainer(Level pLevel, BlockPos pPos, Player pPlayer) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof MultiBlockSoulBlastFurnaceBlockEntity) {
            if (this.isValidMultiBlock(pLevel, pPos)) {
                pPlayer.openMenu((MenuProvider)blockentity);
                pPlayer.awardStat(Stats.INTERACT_WITH_BLAST_FURNACE);
            }
            else {
                pPlayer.sendSystemMessage(Component.translatable(this.getInvalidStructureLang()));
            }
        }
    }

    @Override
    public boolean isValidMultiBlock(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Direction direction = state.getValue(FACING);
        BlockPos midBlock = pos.relative(direction.getOpposite()).above();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        boolean hasFoundThisBlock = false;
        for (Map.Entry<Vec3i, TagKey<Block>> entry : RELATIVE_POS_BLOCK_TAGS.entrySet()) {
            mutableBlockPos.set(midBlock.offset(entry.getKey()));
            Block block = level.getBlockState(mutableBlockPos).getBlock();
            if (block.equals(this) && !hasFoundThisBlock) {
                hasFoundThisBlock = true;
                continue;
            }
            if (!Utils.isBlockInTag(block, entry.getValue()))
                return false;
        }
        return true;
    }

    @Override
    protected String getInvalidStructureLang() {
        return IguanaTweaksReborn.MOD_ID + ".soul_blast_furnace_invalid_structure";
    }

    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles).
     */
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pState.getValue(LIT)) {
            double d0 = pPos.getX() + 0.5D;
            double d1 = pPos.getY();
            double d2 = pPos.getZ() + 0.5D;
            if (pRandom.nextDouble() < 0.1D) {
                pLevel.playLocalSound(d0, d1, d2, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction = pState.getValue(FACING);
            Direction.Axis direction$axis = direction.getAxis();
            double d3 = 0.52D;
            double d4 = pRandom.nextDouble() * 0.6D - 0.3D;
            double d5 = direction$axis == Direction.Axis.X ? direction.getStepX() * 0.52D : d4;
            double d6 = pRandom.nextDouble() * 9.0D / 16.0D;
            double d7 = direction$axis == Direction.Axis.Z ? direction.getStepZ() * 0.52D : d4;
            pLevel.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
        }
    }
}
