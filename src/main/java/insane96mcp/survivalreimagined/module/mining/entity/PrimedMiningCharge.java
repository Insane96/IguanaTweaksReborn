package insane96mcp.survivalreimagined.module.mining.entity;

import insane96mcp.survivalreimagined.module.mining.feature.MiningCharge;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class PrimedMiningCharge extends Entity implements TraceableEntity {
    private static final EntityDataAccessor<Integer> DATA_FUSE_ID = SynchedEntityData.defineId(PrimedMiningCharge.class, EntityDataSerializers.INT);
    Direction direction = Direction.DOWN;
    private static final int DEFAULT_FUSE_TIME = 50;
    @javax.annotation.Nullable
    private LivingEntity owner;

    public PrimedMiningCharge(EntityType<? extends PrimedMiningCharge> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.blocksBuilding = true;
    }

    public PrimedMiningCharge(Level pLevel, double pX, double pY, double pZ, @javax.annotation.Nullable LivingEntity pOwner, Direction direction) {
        this(MiningCharge.PRIMED_MINING_CHARGE.get(), pLevel);
        this.setPos(pX, pY, pZ);
        this.setFuse(DEFAULT_FUSE_TIME);
        this.xo = pX;
        this.yo = pY;
        this.zo = pZ;
        this.owner = pOwner;
        this.direction = direction;
    }

    protected void defineSynchedData() {
        this.entityData.define(DATA_FUSE_ID, DEFAULT_FUSE_TIME);
    }

    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    /**
     * Returns {@code true} if other Entities should be prevented from moving through this Entity.
     */
    public boolean isPickable() {
        return !this.isRemoved();
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick() {
        int fuse = this.getFuse() - 1;
        this.setFuse(fuse);
        if (fuse <= 0) {
            this.discard();
            if (!this.level.isClientSide) {
                this.explode();
            }
        } else {
            if (this.level.isClientSide) {
                this.level.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }

    }

    protected void explode() {
        BlockPos explosionCenter = this.blockPosition().relative(this.direction, 2);
        Iterable<BlockPos> positions = BlockPos.betweenClosed(explosionCenter.offset(-1, -1, -1), explosionCenter.offset(1, 1, 1));
        for (BlockPos pos : positions) {
            BlockState blockstate = this.level.getBlockState(pos);
            if (!blockstate.isAir()) {
                this.level.getProfiler().push("mining_charge_explosion");
                if (this.level instanceof ServerLevel) {
                    BlockEntity blockEntity = blockstate.hasBlockEntity() ? this.level.getBlockEntity(pos) : null;
                    LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel) this.level)).withRandom(this.level.getRandom()).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity).withOptionalParameter(LootContextParams.THIS_ENTITY, this);
                    blockstate.getDrops(lootcontext$builder).forEach((stack) -> this.level.addFreshEntity(new ItemEntity(this.level, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, stack)));
                }
                this.level.getProfiler().pop();
                this.level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
        }
        this.level.playSound(null, explosionCenter, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1.0f, 1.25f);
        List<Entity> entitiesInExplosion = this.level.getEntities(null, new AABB(explosionCenter.offset(-3, -3, -3), explosionCenter.offset(3, 3, 3)));
        for (Entity entity : entitiesInExplosion) {
            if (entity.tickCount == 0)
                continue;
            DamageSource damageSource = level.damageSources().explosion(this, this.owner);
            entity.hurt(damageSource, 15f);
        }
        if (this.level instanceof ServerLevel serverLevel)
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, explosionCenter.getCenter().x, explosionCenter.getCenter().y, explosionCenter.getCenter().z, 1, 0d, 0d, 0d, 0d);
    }

    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putShort("Fuse", (short)this.getFuse());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.setFuse(pCompound.getShort("Fuse"));
    }

    /**
     * Returns null or the LivingEntity it was ignited by
     */
    @Nullable
    public LivingEntity getOwner() {
        return this.owner;
    }

    protected float getEyeHeight(Pose pPose, EntityDimensions pSize) {
        return 0.15F;
    }

    public void setFuse(int pLife) {
        this.entityData.set(DATA_FUSE_ID, pLife);
    }

    /**
     * Gets the fuse from the data manager
     */
    public int getFuse() {
        return this.entityData.get(DATA_FUSE_ID);
    }
}
