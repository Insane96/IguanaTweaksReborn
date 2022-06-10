package insane96mcp.iguanatweaksreborn.module.misc.level;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.misc.entity.ExplosionFallingBlockEntity;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ITExplosion extends Explosion {
	ObjectArrayList<Pair<ItemStack, BlockPos>> droppedItems = new ObjectArrayList<>();
	boolean dealsKnockback;

	public ITExplosion(Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Explosion.BlockInteraction blockInteraction) {
		this(level, source, damageSource, damageCalculator, x, y, z, radius, fire, blockInteraction, true);
	}

	public ITExplosion(Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Explosion.BlockInteraction blockInteraction, boolean dealsKnockback) {
		super(level, source, damageSource, damageCalculator, x, y, z, radius, fire, blockInteraction);
		this.dealsKnockback = dealsKnockback;
	}

	public void gatherAffectedBlocks(boolean randomize) {
		Set<BlockPos> set = Sets.newHashSet();
		for(int j = 0; j < 16; ++j) {
			for(int k = 0; k < 16; ++k) {
				for(int l = 0; l < 16; ++l) {
					if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
						double d0 = (float)j / 15.0F * 2.0F - 1.0F;
						double d1 = (float)k / 15.0F * 2.0F - 1.0F;
						double d2 = (float)l / 15.0F * 2.0F - 1.0F;
						double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
						d0 = d0 / d3;
						d1 = d1 / d3;
						d2 = d2 / d3;
						float rayStrength;
						if (!randomize)
							rayStrength = this.radius;
						else
							rayStrength = this.radius * (0.7F + this.level.getRandom().nextFloat() * 0.6F);
						double d4 = this.getPosition().x();
						double d6 = this.getPosition().y();
						double d8 = this.getPosition().z();
						for(float f1 = 0.3F; rayStrength > 0.0F; rayStrength -= 0.22500001F) {
							BlockPos blockpos = new BlockPos(d4, d6, d8);
							BlockState blockstate = this.level.getBlockState(blockpos);
							FluidState fluidstate = this.level.getFluidState(blockpos);
							Optional<Float> optional = this.damageCalculator.getBlockExplosionResistance(this, this.level, blockpos, blockstate, fluidstate);
							if (optional.isPresent()) {
								rayStrength -= (optional.get() + 0.3F) * 0.3F;
							}
							if (rayStrength > 0.0F && this.damageCalculator.shouldBlockExplode(this, this.level, blockpos, blockstate, rayStrength)) {
								set.add(blockpos);
							}
							d4 += d0 * (double)0.3F;
							d6 += d1 * (double)0.3F;
							d8 += d2 * (double)0.3F;
						}
					}
				}
			}
		}
		this.getToBlow().addAll(set);
	}

	public void fallingBlocks() {
		for(BlockPos blockpos : this.getToBlow()) {
			BlockState blockstate = this.level.getBlockState(blockpos);
			Block block = blockstate.getBlock();
			if (blockstate.isAir())
				continue;
			if (block instanceof TntBlock) {
				block.wasExploded(this.level, blockpos, this);
				this.level.setBlockAndUpdate(blockpos, Blocks.AIR.defaultBlockState());
				continue;
			}
			if (block instanceof MovingPistonBlock) {
				PistonMovingBlockEntity tileEntity = (PistonMovingBlockEntity) this.level.getBlockEntity(blockpos);
				blockstate = tileEntity.getMovedState();
				block = blockstate.getBlock();
			}
			block.wasExploded(this.level, new BlockPos(this.getPosition()), this);
			BlockPos blockpos1 = blockpos.immutable();
			this.level.setBlockAndUpdate(blockpos1, Blocks.AIR.defaultBlockState());
			ExplosionFallingBlockEntity fallingBlockEntity = new ExplosionFallingBlockEntity(this.level, blockpos1.getX() + 0.5f, blockpos1.getY() + 2.0625f, blockpos1.getZ() + 0.5f, blockstate);
			fallingBlockEntity.time = 1;
			fallingBlockEntity.source = this.source;
			this.level.addFreshEntity(fallingBlockEntity);
		}
		this.clearToBlow();
	}

	public void processEntities(double blockingDamageReduction, boolean knockbackScaleWithSize) {
		float affectedEntitiesRadius = this.radius * 2.0F;
		List<Entity> list = gatherAffectedEntities(affectedEntitiesRadius);
		net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.level, this, list, affectedEntitiesRadius);
		for(Entity entity : list) {
			if (entity.tickCount == 0 && !(entity instanceof PartEntity<?>)  && !Modules.misc.explosionOverhaul.affectJustSpawnedEntities)
				continue;
			if (entity.ignoreExplosion())
				continue;
			double distanceRatio = (Mth.sqrt((float) entity.distanceToSqr(this.getPosition())) / affectedEntitiesRadius);
			if (distanceRatio > 1.0D)
				continue;
			double xDistance = entity.getX() - this.getPosition().x;
			double yDistance = (entity.getEyeY() - this.getPosition().y);// * 0.6667d;
			double zDistance = entity.getZ() - this.getPosition().z;
			double d13 = Mth.sqrt((float) (xDistance * xDistance + yDistance * yDistance + zDistance * zDistance));
			if (d13 == 0.00)
				continue;
			//xDistance = xDistance / d13;
			if (!(entity instanceof ExplosionFallingBlockEntity))
				yDistance = yDistance / d13;
			//zDistance = zDistance / d13;
			double blockDensity = getSeenPercent(this.getPosition(), entity);
			double d10 = (1.0D - distanceRatio) * blockDensity;
			//Damage Entities in the explosion radius
			float damageAmount = (float) ((int) ((d10 * d10 + d10) / 2.0D * 7.0D * (double) affectedEntitiesRadius + 1.0D));
			if (blockDensity > 0d) {
				DamageSource source = this.getDamageSource();
				if (entity instanceof ServerPlayer player && blockingDamageReduction > 0d) {
					if (damageAmount > 0.0F && player.isDamageSourceBlocked(source)) {
						source.bypassArmor = true;
						damageAmount *= blockingDamageReduction;
						player.hurtCurrentlyUsedShield(damageAmount);
						player.level.broadcastEntityEvent(player, (byte) 29);
					}
				}
				entity.hurt(source, damageAmount);

				if (this.dealsKnockback) {
					double d11 = d10;
					if (entity instanceof LivingEntity) {
						d11 = getBlastKnockbackReduction((LivingEntity) entity, d11);
					}
					if (knockbackScaleWithSize)
						d11 *= this.radius;
					d11 = Math.max(d11, this.radius * 0.05d);
					if (entity instanceof ExplosionFallingBlockEntity)
						d11 = Math.min(d11, 0.5d);
					else if (Modules.misc.explosionOverhaul.shouldTakeReducedKnockback(entity))
						d11 *= 0.25d;
					entity.setDeltaMovement(entity.getDeltaMovement().add(xDistance * d11, yDistance * d11, zDistance * d11));
					if (entity instanceof Player player) {
						if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
							this.getHitPlayers().put(player, new Vec3(xDistance * d11, yDistance * d11, zDistance * d11));
						}
					}
				}
			}
		}
	}

	public void destroyBlocks() {
		if (this.blockInteraction == Explosion.BlockInteraction.NONE)
			return;
		Collections.shuffle(this.getToBlow(), this.level.getRandom());
		for(BlockPos blockpos : this.getToBlow()) {
			BlockState blockstate = this.level.getBlockState(blockpos);
			if (!blockstate.isAir()) {
				BlockPos immutableBlockPos = blockpos.immutable();
				this.level.getProfiler().push("explosion_blocks");
				if (blockstate.canDropFromExplosion(this.level, blockpos, this) && this.level instanceof ServerLevel) {
					BlockEntity blockEntity = blockstate.hasBlockEntity() ? this.level.getBlockEntity(blockpos) : null;
					LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel) this.level)).withRandom(this.level.getRandom()).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockpos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity).withOptionalParameter(LootContextParams.THIS_ENTITY, this.source);
					if (this.blockInteraction == Explosion.BlockInteraction.DESTROY) {
						lootcontext$builder.withParameter(LootContextParams.EXPLOSION_RADIUS, this.radius);
					}
					blockstate.getDrops(lootcontext$builder).forEach((stack) -> addBlockDrops(droppedItems, stack, immutableBlockPos));
				}
				blockstate.onBlockExploded(this.level, blockpos, this);
				this.level.getProfiler().pop();
			}
		}
	}

	public void dropItems() {
		for(Pair<ItemStack, BlockPos> pair : droppedItems) {
			Block.popResource(this.level, pair.getSecond(), pair.getFirst());
		}
	}

	//Should be called in remote world only
	public void playSound() {
		this.level.playLocalSound(this.getPosition().x, this.getPosition().y, this.getPosition().z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (this.level.getRandom().nextFloat() - this.level.getRandom().nextFloat()) * 0.2F) * 0.7F, false);
	}

	public void spawnParticles() {
		if (!(this.radius < 2.0F) && this.blockInteraction != Explosion.BlockInteraction.NONE) {
			this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getPosition().x, this.getPosition().y, this.getPosition().z, 1.0D, 0.0D, 0.0D);
		}
		else {
			this.level.addParticle(ParticleTypes.EXPLOSION, this.getPosition().x, this.getPosition().y, this.getPosition().z, 1.0D, 0.0D, 0.0D);
		}
	}

	public void processFire() {
		if (this.fire) {
			for(BlockPos blockpos2 : this.getToBlow()) {
				if (this.random.nextInt(3) == 0 && this.level.getBlockState(blockpos2).isAir() && this.level.getBlockState(blockpos2.below()).isSolidRender(this.level, blockpos2.below())) {
					this.level.setBlockAndUpdate(blockpos2, BaseFireBlock.getState(this.level, blockpos2));
				}
			}
		}
	}

	private List<Entity> gatherAffectedEntities(float affectedRadius) {
		int z2 = Mth.floor(this.getPosition().z() + (double)affectedRadius + 1.0D);
		int x1 = Mth.floor(this.getPosition().x() - (double)affectedRadius - 1.0D);
		int x2 = Mth.floor(this.getPosition().x() + (double)affectedRadius + 1.0D);
		int z1 = Mth.floor(this.getPosition().z() - (double)affectedRadius - 1.0D);
		int y1 = Mth.floor(this.getPosition().y() - (double)affectedRadius - 1.0D);
		int y2 = Mth.floor(this.getPosition().y() + (double)affectedRadius + 1.0D);
		return this.level.getEntities(this.source, new AABB(x1, y1, z1, x2, y2, z2));
	}

	/*
	   Since Mojang decided to round down the damage and use the same method for both Knockback and Damage, the latter doesn't work as it's always rounded down to 0
	 */
	public static double getBlastKnockbackReduction(LivingEntity entityLivingBaseIn, double knockback) {
		int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, entityLivingBaseIn);
		if (i > 0) {
			knockback -= knockback * (double)((float)i * 0.15F);
		}
		return knockback;
	}
}
