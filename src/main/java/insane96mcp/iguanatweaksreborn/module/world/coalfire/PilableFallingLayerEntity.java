package insane96mcp.iguanatweaksreborn.module.world.coalfire;

import insane96mcp.iguanatweaksreborn.setup.SRRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PilableFallingLayerEntity extends FallingBlockEntity {
    public PilableFallingLayerEntity(EntityType<? extends FallingBlockEntity> entityType, Level level) {
        super(entityType, level);
    }

    public PilableFallingLayerEntity(Level level, double x, double y, double z, BlockState blockState) {
        this(SRRegistries.PILABLE_FALLING_LAYER.get(), level);
        this.blockState = blockState;
        this.blocksBuilding = true;
        this.setPos(x, y, z);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.setStartPos(this.blockPosition());
    }

    public static PilableFallingLayerEntity fall(Level level, BlockPos pos, BlockState state) {
        PilableFallingLayerEntity fallingblockentity = new PilableFallingLayerEntity(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, state.hasProperty(BlockStateProperties.WATERLOGGED) ? state.setValue(BlockStateProperties.WATERLOGGED, Boolean.FALSE) : state);
        level.setBlock(pos, state.getFluidState().createLegacyBlock(), 3);
        level.addFreshEntity(fallingblockentity);
        return fallingblockentity;
    }

    public void tick() {
        if (!(this.blockState.getBlock() instanceof PilableLayerBlock)) {
            this.discard();
        } else {
            Block block = this.blockState.getBlock();
            ++this.time;
            if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
            }

            this.move(MoverType.SELF, this.getDeltaMovement());
            if (!this.level().isClientSide) {
                BlockPos pos = this.blockPosition();
                if (this.level().getFluidState(pos).is(FluidTags.WATER)) {
                    this.discard();
                    return;
                }
                if (this.getDeltaMovement().lengthSqr() > 1.0D) {
                    BlockHitResult blockhitresult = this.level().clip(new ClipContext(new Vec3(this.xo, this.yo, this.zo), this.position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, this));
                    if (blockhitresult.getType() != HitResult.Type.MISS && this.level().getFluidState(blockhitresult.getBlockPos()).is(FluidTags.WATER)) {
                        this.discard();
                        return;
                    }
                }

                if (!this.onGround()) {
                    if (!this.level().isClientSide && (this.time > 100 && (pos.getY() <= this.level().getMinBuildHeight() || pos.getY() > this.level().getMaxBuildHeight()) || this.time > 600)) {
                        if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                            this.spawnAtLocation(block);
                        }

                        this.discard();
                    }
                }
                else {
                    BlockState inState = this.level().getBlockState(pos);
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
                    if (!inState.is(Blocks.MOVING_PISTON)) {
                        if (!this.cancelDrop) {
                            boolean canBeReplaced = inState.canBeReplaced(new DirectionalPlaceContext(this.level(), pos, Direction.DOWN, new ItemStack(blockState.getBlock().asItem()), Direction.UP));
                            boolean isFree = PilableLayerBlock.isFree(this.level().getBlockState(pos.below()));
                            boolean canSurvive = this.blockState.canSurvive(this.level(), pos) && !isFree;
                            if (canBeReplaced && canSurvive) {
                                int remaining = 0;

                                if (inState.is(blockState.getBlock())) {
                                    int layers = blockState.getValue(PilableLayerBlock.LAYERS);
                                    int toLayers = inState.getValue(PilableLayerBlock.LAYERS);
                                    int total = layers + toLayers;
                                    int target = Mth.clamp(total, 1, 8);
                                    remaining = total - target;
                                    blockState = blockState.setValue(PilableLayerBlock.LAYERS, target);
                                }

                                if (this.level().setBlock(pos, blockState, 3)) {
                                    ((ServerLevel) this.level()).getChunkSource().chunkMap.broadcast(this,
                                            new ClientboundBlockUpdatePacket(pos, this.level().getBlockState(pos)));

                                    if (block instanceof Fallable fallable) {
                                        fallable.onLand(this.level(), pos, blockState, inState, this);
                                    }
                                    this.discard();

                                    if (remaining != 0) {
                                        BlockPos above = pos.above();
                                        blockState = blockState.setValue(PilableLayerBlock.LAYERS, remaining);
                                        if (level().getBlockState(above).canBeReplaced()) {
                                            if (!this.level().setBlock(above, blockState, 3)) {
                                                ((ServerLevel) this.level()).getChunkSource().chunkMap.broadcast(this,
                                                        new ClientboundBlockUpdatePacket(above, this.level().getBlockState(above)));
                                                this.dropBlockContent(blockState, pos);
                                            }
                                        }
                                    }
                                    return;
                                }
                            }
                            else {
                                this.discard();
                                if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                    this.callOnBrokenAfterFall(block, pos);
                                    this.dropBlockContent(blockState, pos);
                                }
                            }
                        }
                        else {
                            this.discard();
                            this.callOnBrokenAfterFall(block, pos);
                        }
                    }
                }
            }

            this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
        }
    }

    private void dropBlockContent(BlockState state, BlockPos pos) {
        Block.dropResources(state, level(), pos, null, null, ItemStack.EMPTY);

        level().levelEvent(null, 2001, pos, Block.getId(state));
    }
}
