package insane96mcp.survivalreimagined.module.items.explosivebarrel;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.misc.explosionoverhaul.SRExplosion;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

public class ExplosiveBarrelBlock extends Block {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public static final String UNSTABLE = SurvivalReimagined.MOD_ID + ".unstable";

    public ExplosiveBarrelBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public void onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable net.minecraft.core.Direction face, @Nullable LivingEntity igniter) {
        float f = 4.5F;
        SRExplosion.explode(level, null, null, null, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, f, false, Level.ExplosionInteraction.BLOCK, false);
    }

    public void onProjectileHit(Level p_57429_, BlockState p_57430_, BlockHitResult p_57431_, Projectile p_57432_) {
        if (!p_57429_.isClientSide) {
            BlockPos blockpos = p_57431_.getBlockPos();
            Entity entity = p_57432_.getOwner();
            if (p_57432_.isOnFire() && p_57432_.mayInteract(p_57429_, blockpos)) {
                onCaughtFire(p_57430_, p_57429_, blockpos, null, entity instanceof LivingEntity ? (LivingEntity)entity : null);
                p_57429_.removeBlock(blockpos, false);
            }
        }
    }

    public void onPlace(BlockState p_57466_, Level p_57467_, BlockPos p_57468_, BlockState p_57469_, boolean p_57470_) {
        if (!p_57469_.is(p_57466_.getBlock())) {
            if (p_57467_.hasNeighborSignal(p_57468_)) {
                onCaughtFire(p_57466_, p_57467_, p_57468_, null, null);
                p_57467_.removeBlock(p_57468_, false);
            }

        }
    }

    public void neighborChanged(BlockState p_57457_, Level p_57458_, BlockPos p_57459_, Block p_57460_, BlockPos p_57461_, boolean p_57462_) {
        if (p_57458_.hasNeighborSignal(p_57459_)) {
            onCaughtFire(p_57457_, p_57458_, p_57459_, null, null);
            p_57458_.removeBlock(p_57459_, false);
        }

    }

    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        onCaughtFire(level.getBlockState(pos), level, pos, null, explosion.getExploder() instanceof LivingEntity livingEntity ? livingEntity : null);
    }

    @Override
    public void appendHoverText(ItemStack p_49816_, @Nullable BlockGetter p_49817_, List<Component> components, TooltipFlag p_49819_) {
        super.appendHoverText(p_49816_, p_49817_, components, p_49819_);
        components.add(Component.translatable(UNSTABLE).withStyle(ChatFormatting.RED));
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 75;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 15;
    }

    public boolean dropFromExplosion(Explosion p_57427_) {
        return false;
    }

    public BlockState rotate(BlockState p_49085_, Rotation p_49086_) {
        return p_49085_.setValue(FACING, p_49086_.rotate(p_49085_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_49082_, Mirror p_49083_) {
        return p_49082_.rotate(p_49083_.getRotation(p_49082_.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49088_) {
        p_49088_.add(FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_49048_) {
        return this.defaultBlockState().setValue(FACING, p_49048_.getNearestLookingDirection().getOpposite());
    }
}
