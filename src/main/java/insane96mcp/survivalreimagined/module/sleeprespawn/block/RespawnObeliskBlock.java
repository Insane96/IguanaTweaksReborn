package insane96mcp.survivalreimagined.module.sleeprespawn.block;

import com.google.common.collect.ImmutableList;
import insane96mcp.survivalreimagined.module.misc.utils.IdTagValue;
import insane96mcp.survivalreimagined.module.sleeprespawn.feature.Respawn;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

public class RespawnObeliskBlock extends Block {
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    private static final ImmutableList<Vec3i> CATALYST_RELATIVE_POSITIONS = ImmutableList.of(
            new Vec3i(-4, 0, 0),
            new Vec3i(4, 0, 0),
            new Vec3i(0, 0, -4),
            new Vec3i(0, 0, 4)
    );

    private static final ImmutableList<Vec3i> RESPAWN_HORIZONTAL_OFFSETS = ImmutableList.of(new Vec3i(0, 0, -1), new Vec3i(-1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(1, 0, 0), new Vec3i(-1, 0, -1), new Vec3i(1, 0, -1), new Vec3i(-1, 0, 1), new Vec3i(1, 0, 1));
    private static final ImmutableList<Vec3i> RESPAWN_OFFSETS = (new ImmutableList.Builder<Vec3i>()).addAll(RESPAWN_HORIZONTAL_OFFSETS).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vec3i::below).iterator()).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vec3i::above).iterator()).add(new Vec3i(0, 1, 0)).build();

    public RespawnObeliskBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.getStateDefinition().any().setValue(ENABLED, false));
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        if (!state.getValue(ENABLED)) {
            boolean hasCatalyst = true;
            BlockPos.MutableBlockPos relativePos = new BlockPos.MutableBlockPos();
            for (Vec3i rel : CATALYST_RELATIVE_POSITIONS) {
                relativePos.set(pos).move(rel);
                if (!isBlockCatalyst(level.getBlockState(relativePos).getBlock())) {
                    hasCatalyst = false;
                    break;
                }
            }
            if (hasCatalyst) {
                enable(player, level, pos, state);
                return InteractionResult.SUCCESS;
            }
            else {
                if (!level.isClientSide && hand == InteractionHand.MAIN_HAND)
                    player.sendSystemMessage(Component.literal("Can't activate. Missing catalyst blocks."));
                return InteractionResult.PASS;
            }
        }
        else if (state.getValue(ENABLED) && !level.isClientSide) {
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

    @Override
    public Optional<Vec3> getRespawnPosition(BlockState state, EntityType<?> type, LevelReader levelReader, BlockPos pos, float orientation, @org.jetbrains.annotations.Nullable LivingEntity entity) {
        if (!levelReader.getBlockState(pos).getValue(RespawnObeliskBlock.ENABLED))
            return Optional.empty();
        boolean hasCatalyst = true;
        BlockPos.MutableBlockPos relativePos = new BlockPos.MutableBlockPos();
        for (Vec3i rel : CATALYST_RELATIVE_POSITIONS) {
            relativePos.set(pos).move(rel);
            if (!isBlockCatalyst(levelReader.getBlockState(relativePos).getBlock())) {
                hasCatalyst = false;
                break;
            }
        }
        if (hasCatalyst && state.getBlock() instanceof RespawnObeliskBlock)
        {
            return RespawnAnchorBlock.findStandUpPosition(type, levelReader, pos);
        }
        return Optional.empty();
    }

    public static void onObeliskRespawn(Player player, Level level, BlockPos respawnPos) {
        BlockPos.MutableBlockPos relativePos = new BlockPos.MutableBlockPos();
        boolean hasDestroyedBlock = false;
        for (Vec3i rel : CATALYST_RELATIVE_POSITIONS) {
            relativePos.set(respawnPos).move(rel);
            double chance = getCatalystBlockChanceToBreak(level.getBlockState(relativePos).getBlock());
            if (chance > 0d && level.getRandom().nextDouble() < chance) {
                level.destroyBlock(relativePos, false);
                hasDestroyedBlock = true;
            }
        }
        if (hasDestroyedBlock) {
            level.setBlock(respawnPos, level.getBlockState(respawnPos).setValue(RespawnObeliskBlock.ENABLED, false), 3);
            level.playSound(null, respawnPos.getX(), respawnPos.getY(), respawnPos.getZ(), SoundEvents.RESPAWN_ANCHOR_DEPLETE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            player.sendSystemMessage(Component.literal("Respawn Obelisk disabled because one or more catalysts have been destroyed."));
        }
    }

    public static int lightLevel(BlockState state) {
        return state.getValue(ENABLED) ? 15 : 7;
    }

    public static boolean isBlockCatalyst(Block block) {
        return Respawn.respawnObeliskCatalysts.stream().anyMatch(idTagValue -> idTagValue.matchesBlock(block));
    }

    public static double getCatalystBlockChanceToBreak(Block block) {
        Optional<IdTagValue> catalyst = Respawn.respawnObeliskCatalysts.stream().filter(idTagValue -> idTagValue.matchesBlock(block)).findFirst();
        return catalyst.map(idTagValue -> idTagValue.value).orElse(0d);
    }
}
