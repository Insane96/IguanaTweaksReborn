package insane96mcp.survivalreimagined.module.sleeprespawn.death;

import insane96mcp.enhancedai.setup.EAStrings;
import insane96mcp.insanelib.setup.ILStrings;
import insane96mcp.insanelib.util.MCUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GraveBlock extends BaseEntityBlock implements EntityBlock {
    protected static final VoxelShape SHAPE_X = Block.box(3.0D, 0.0D, 6.0D, 13.0D, 12.0D, 10.0d);
    protected static final VoxelShape SHAPE_Z = Block.box(6.0D, 0.0D, 3.0D, 10.0D, 12.0D, 13.0d);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final ResourceLocation CONTENTS = new ResourceLocation("contents");

    public GraveBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        Direction direction = blockState.getValue(FACING);
        return direction.getAxis() == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getClockWise()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor levelAccessor, BlockPos pos, BlockPos p_60546_) {
        if (state.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        return super.updateShape(state, direction, newState, levelAccessor, pos, p_60546_);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_48814_) {
        p_48814_.add(FACING, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GraveBlockEntity(pos, state);
    }

    public List<ItemStack> getDrops(BlockState state, LootParams.Builder context) {
        BlockEntity blockentity = context.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockentity instanceof GraveBlockEntity graveBlockEntity) {
            context = context.withDynamicDrop(CONTENTS, (consumer) -> {
                for(ItemStack itemStack : graveBlockEntity.getItems()) {
                    consumer.accept(itemStack);
                }
            });
        }

        return super.getDrops(state, context);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean p_60519_) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof GraveBlockEntity graveBlockEntity) {
                graveBlockEntity.getItems().forEach(itemStack -> dropGraveItems(level, itemStack, pos));
                int xpStored = graveBlockEntity.getXpStored();
                if (xpStored > 0) {
                    Zombie zombie = EntityType.ZOMBIE.create(level);
                    if (zombie == null)
                        return;
                    zombie.setPos(pos.getCenter());
                    zombie.setPersistenceRequired();
                    zombie.lootTable = new ResourceLocation("minecraft:empty");
                    zombie.getPersistentData().putDouble(ILStrings.Tags.EXPERIENCE_MULTIPLIER, 0d);
                    zombie.getPersistentData().putBoolean(Death.PLAYER_GHOST, true);
                    zombie.getPersistentData().putLong(Death.SPAWNED_GAME_TIME, level.getGameTime());
                    zombie.getPersistentData().putBoolean(EAStrings.Tags.Zombie.MINER, true);
                    zombie.getPersistentData().putBoolean("mobspropertiesrandomness:processed", true);
                    //zombie.setCustomName(Component.translatable(Death.PLAYER_GHOST_LANG, player.getName().getString()));
                    if (zombie.getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null)
                        zombie.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1d);
                    MCUtils.applyModifier(zombie, Attributes.MOVEMENT_SPEED, Death.MOVEMENT_SPEED_BONUS, "Ghost movement speed bonus", 0.5d, AttributeModifier.Operation.MULTIPLY_BASE, true);
                    MCUtils.applyModifier(zombie, Attributes.ATTACK_DAMAGE, Death.ATTACK_DAMAGE_BONUS, "Ghost attack damage bonus", 1d, AttributeModifier.Operation.ADDITION, true);
                    zombie.setSilent(true);
                    zombie.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, -1, 0, false, false, false));
                    zombie.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, -1, 0, false, false, false));
                    zombie.getPersistentData().putInt(Death.XP_TO_DROP, xpStored);
                    MCUtils.applyModifier(zombie, Attributes.ATTACK_DAMAGE, Death.ATTACK_DAMAGE_XP_BONUS, "Ghost attack damage bonus by XP", graveBlockEntity.getXpStored() * 0.001d, AttributeModifier.Operation.MULTIPLY_BASE, true);
                    MCUtils.applyModifier(zombie, Attributes.MAX_HEALTH, Death.HEALTH_XP_BONUS, "Ghost health bonus by XP", graveBlockEntity.getXpStored() * 0.001d, AttributeModifier.Operation.MULTIPLY_BASE, true);
                    level.addFreshEntity(zombie);
                    level.playSound(null, pos, SoundEvents.GHAST_AMBIENT, SoundSource.BLOCKS, 1.0f, 0.7f);
                }
            }

            super.onRemove(state, level, pos, newState, p_60519_);
        }
    }
    public void dropGraveItems(Level level, ItemStack stack, BlockPos pos) {
        if (stack.isEmpty())
            return;

        ItemEntity itementity = new ItemEntity(level, pos.getCenter().x, pos.getCenter().y, pos.getCenter().z, stack);
        //itementity.setPickUpDelay(40);
        //2 minutes
        itementity.lifespan = 2400;

        float f = level.random.nextFloat() * 0.25F;
        float f1 = level.random.nextFloat() * ((float)Math.PI * 2F);
        itementity.setDeltaMovement((-Mth.sin(f1) * f), 0.2F, (Mth.cos(f1) * f));
        level.addFreshEntity(itementity);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == Death.GRAVE_BLOCK_ENTITY_TYPE.get() && !level.isClientSide ? GraveBlockEntity::serverTick : null;
    }
}
