package insane96mcp.survivalreimagined.module.sleeprespawn.block;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class RespawnObeliskBlock extends Block {
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    private static final ImmutableList<Vec3i> RESPAWN_HORIZONTAL_OFFSETS = ImmutableList.of(new Vec3i(0, 0, -1), new Vec3i(-1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(1, 0, 0), new Vec3i(-1, 0, -1), new Vec3i(1, 0, -1), new Vec3i(-1, 0, 1), new Vec3i(1, 0, 1));
    private static final ImmutableList<Vec3i> RESPAWN_OFFSETS = (new ImmutableList.Builder<Vec3i>()).addAll(RESPAWN_HORIZONTAL_OFFSETS).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vec3i::below).iterator()).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vec3i::above).iterator()).add(new Vec3i(0, 1, 0)).build();

    public RespawnObeliskBlock(Properties properties) {
        super(properties);
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        if (!state.getValue(ENABLED) && level.getBlockState(pos.north(4)).is(Blocks.IRON_BLOCK)
                && level.getBlockState(pos.south(4)).is(Blocks.IRON_BLOCK)
                && level.getBlockState(pos.east(4)).is(Blocks.IRON_BLOCK)
                && level.getBlockState(pos.west(4)).is(Blocks.IRON_BLOCK)) {
            enable(player, level, pos, state);
            return InteractionResult.SUCCESS;
        }
        else if (!level.isClientSide) {
            ServerPlayer serverplayer = (ServerPlayer)player;
            if (serverplayer.getRespawnDimension() != level.dimension() || !pos.equals(serverplayer.getRespawnPosition())) {
                serverplayer.setRespawnPosition(level.dimension(), pos, 0.0F, false, true);
                level.playSound(null, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55886_) {
        p_55886_.add(ENABLED);
    }

    public static void enable(@Nullable Entity p_270997_, Level p_270172_, BlockPos p_270534_, BlockState p_270661_) {
        BlockState blockstate = p_270661_.setValue(ENABLED, true);
        p_270172_.setBlock(p_270534_, blockstate, 3);
        p_270172_.gameEvent(GameEvent.BLOCK_CHANGE, p_270534_, GameEvent.Context.of(p_270997_, blockstate));
        p_270172_.playSound(null, (double)p_270534_.getX() + 0.5D, (double)p_270534_.getY() + 0.5D, (double)p_270534_.getZ() + 0.5D, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public static int lightLevel(BlockState state) {
        return state.getValue(ENABLED) ? 15 : 7;
    }
}
