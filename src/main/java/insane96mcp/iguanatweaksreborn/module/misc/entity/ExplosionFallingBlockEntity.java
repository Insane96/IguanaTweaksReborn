package insane96mcp.iguanatweaksreborn.module.misc.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class ExplosionFallingBlockEntity extends FallingBlockEntity {
	public Entity source;

	public ExplosionFallingBlockEntity(Level level, double x, double y, double z, BlockState fallingBlockState) {
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

	@Override
	public void tick() {
		super.tick();
		if (this.level instanceof ServerLevel)
			((ServerLevel) this.level).sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 1, 0, 0, 0, 0);
	}
}
