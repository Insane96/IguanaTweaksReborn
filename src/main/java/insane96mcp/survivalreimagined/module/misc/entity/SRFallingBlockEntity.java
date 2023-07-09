package insane96mcp.survivalreimagined.module.misc.entity;

import insane96mcp.survivalreimagined.utils.LogHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class SRFallingBlockEntity extends FallingBlockEntity {
	public Entity source;

	public SRFallingBlockEntity(Level level, BlockPos pos, BlockState fallingBlockState) {
		this(level, pos.getCenter().x, pos.getCenter().y, pos.getCenter().z, fallingBlockState);
	}

	public SRFallingBlockEntity(Level level, double x, double y, double z, BlockState fallingBlockState) {
		super(EntityType.FALLING_BLOCK, level);
		this.blockState = fallingBlockState;
		this.blocksBuilding = true;
		this.setPos(x, y, z);
		this.setDeltaMovement(Vec3.ZERO);
		this.xo = x;
		this.yo = y;
		this.zo = z;
		this.setStartPos(this.blockPosition());
	}

	public void tick() {
		if (this.blockState.isAir()) {
			this.discard();
		} else {
			Block block = this.blockState.getBlock();
			++this.time;
			if (!this.isNoGravity()) {
				this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
			}

			this.move(MoverType.SELF, this.getDeltaMovement());
			if (!this.level.isClientSide) {
				BlockPos blockpos = this.blockPosition();
				boolean isConcretePowder = this.blockState.getBlock() instanceof ConcretePowderBlock;
				boolean canBeHydrated = isConcretePowder && this.blockState.canBeHydrated(this.level, blockpos, this.level.getFluidState(blockpos), blockpos);
				double d0 = this.getDeltaMovement().lengthSqr();
				if (isConcretePowder && d0 > 1.0D) {
					BlockHitResult blockhitresult = this.level.clip(new ClipContext(new Vec3(this.xo, this.yo, this.zo), this.position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, this));
					if (blockhitresult.getType() != HitResult.Type.MISS && this.blockState.canBeHydrated(this.level, blockpos, this.level.getFluidState(blockhitresult.getBlockPos()), blockhitresult.getBlockPos())) {
						blockpos = blockhitresult.getBlockPos();
						canBeHydrated = true;
					}
				}

				if (!this.onGround && !canBeHydrated) {
					if (!this.level.isClientSide && (this.time > 100 && (blockpos.getY() <= this.level.getMinBuildHeight() || blockpos.getY() > this.level.getMaxBuildHeight()) || this.time > 600)) {
						if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
							this.spawnAtLocation(block);
						}

						this.discard();
					}
				}
				else {
					BlockState blockstate = this.level.getBlockState(blockpos);
					this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
					if (!blockstate.is(Blocks.MOVING_PISTON)) {
						if (!this.cancelDrop) {
							boolean canBeReplaced = blockstate.canBeReplaced(new DirectionalPlaceContext(this.level, blockpos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
							//boolean isHarderThanInside = blockstate.getDestroySpeed(this.level, blockpos) < this.blockState.getDestroySpeed(this.level, blockpos);
							boolean isFree = FallingBlock.isFree(this.level.getBlockState(blockpos.below())) && (!isConcretePowder || !canBeHydrated);
							boolean canSurviveAndIsNotFree = this.blockState.canSurvive(this.level, blockpos) && !isFree;
							if (canBeReplaced && canSurviveAndIsNotFree)
								this.place(blockstate, block, blockpos, true);
							else
								this.tryStackAboveOrMove(blockpos);
						}
						else {
							this.discard();
							this.callOnBrokenAfterFall(block, blockpos);
						}
					}
				}
			}

			this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
		}
	}

	public void tryStackAboveOrMove(BlockPos pos) {
		BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
		boolean maxStackReached = false;
		while (true) {
			blockPos.set(blockPos.above());
			if (blockPos.getY() - pos.getY() > 8) {
				maxStackReached = true;
				break;
			}
			if (this.tryPlace(blockPos))
				break;
		}
		/*if (maxStackReached) {
			List<Direction> directions = Util.shuffledCopy(Arrays.stream(Direction.values()).filter((direction) -> direction.getAxis().isHorizontal()).toArray(Direction[]::new), this.random);
			for (Direction direction : directions) {
				blockPos.set(blockPos.relative(direction));
				if (this.level.getBlockState(blockPos).canBeReplaced()) {
					this.setPos(this.position().relative(direction, 1d));
					return;
				}
			}
		}*/
	}

	public boolean tryPlace(BlockPos blockPos) {
		BlockState stateAt = this.level.getBlockState(blockPos);
		BlockState stateOn = this.level.getBlockState(blockPos.below());
		boolean canBeReplaced = stateAt.canBeReplaced(new DirectionalPlaceContext(this.level, blockPos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
		boolean isHarderThanInside = stateAt.getDestroySpeed(this.level, blockPos) < this.blockState.getDestroySpeed(this.level, blockPos);
		boolean canSurvive = this.blockState.canSurvive(this.level, blockPos);
		if (canBeReplaced && isHarderThanInside && canSurvive) {
			this.place(stateOn, this.blockState.getBlock(), blockPos, true);
			return true;
		}
		return false;
	}

	public void place(BlockState state, Block block, BlockPos pos, boolean breakBlock) {
			if (this.blockState.hasProperty(BlockStateProperties.WATERLOGGED) && this.level.getFluidState(pos).getType() == Fluids.WATER) {
				this.blockState = this.blockState.setValue(BlockStateProperties.WATERLOGGED, Boolean.TRUE);
			}

			if (breakBlock)
				this.level.destroyBlock(pos, false);
			if (this.level.setBlock(pos, this.blockState, 3)) {
				((ServerLevel)this.level).getChunkSource().chunkMap.broadcast(this, new ClientboundBlockUpdatePacket(pos, this.level.getBlockState(pos)));
				this.discard();
				if (block instanceof Fallable) {
					((Fallable)block).onLand(this.level, pos, this.blockState, state, this);
				}

				if (this.blockData != null && this.blockState.hasBlockEntity()) {
					BlockEntity blockentity = this.level.getBlockEntity(pos);
					if (blockentity != null) {
						CompoundTag compoundtag = blockentity.saveWithoutMetadata();

						for(String s : this.blockData.getAllKeys()) {
							compoundtag.put(s, this.blockData.get(s).copy());
						}

						try {
							blockentity.load(compoundtag);
						}
						catch (Exception exception) {
							LogHelper.error("Failed to load block entity from falling block", exception);
						}

						blockentity.setChanged();
					}
				}
			}
			else if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
				this.discard();
				this.callOnBrokenAfterFall(block, pos);
				this.spawnAtLocation(block);
			}
	}

	@Nullable
	@Override
	public ItemEntity spawnAtLocation(ItemLike itemIn) {
		if (!(itemIn instanceof Block))
			return super.spawnAtLocation(itemIn);

		if (this.level.isClientSide)
			return null;

		LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel) this.level)).withRandom(this.level.getRandom()).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.blockPosition())).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.THIS_ENTITY, this.source);

		List<ItemStack> drops = this.getBlockState().getDrops(lootcontext$builder);

		if (drops.isEmpty())
			return null;
		for (ItemStack stack : drops) {
			ItemEntity itemEntity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), stack);
			itemEntity.setDefaultPickUpDelay();
			if (captureDrops() != null)
				captureDrops().add(itemEntity);
			else
				this.level.addFreshEntity(itemEntity);
		}
		return null;
	}
}
