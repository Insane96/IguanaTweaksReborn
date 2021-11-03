package insane96mcp.iguanatweaksreborn.modules.misc.world;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import insane96mcp.iguanatweaksreborn.modules.Modules;
import insane96mcp.iguanatweaksreborn.modules.misc.entity.ExplosionFallingBlockEntity;
import insane96mcp.iguanatweaksreborn.utils.MCUtils;
import insane96mcp.iguanatweaksreborn.utils.Reflection;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.PistonTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.ExplosionContext;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("deprecation")
public class ITExplosion extends Explosion {

	ObjectArrayList<Pair<ItemStack, BlockPos>> droppedItems = new ObjectArrayList<>();

	public ITExplosion(World world, @Nullable Entity exploder, @Nullable DamageSource source, @Nullable ExplosionContext context, double x, double y, double z, float size, boolean causesFire, Mode mode) {
		super(world, exploder, source, context, x, y, z, size, causesFire, mode);
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
							rayStrength = this.size;
						else
							rayStrength = this.size * (0.7F + this.world.rand.nextFloat() * 0.6F);
						double d4 = this.getPosition().getX();
						double d6 = this.getPosition().getY();
						double d8 = this.getPosition().getZ();
						for(float f1 = 0.3F; rayStrength > 0.0F; rayStrength -= 0.22500001F) {
							BlockPos blockpos = new BlockPos(d4, d6, d8);
							BlockState blockstate = this.world.getBlockState(blockpos);
							FluidState fluidstate = this.world.getFluidState(blockpos);
							Optional<Float> optional = this.context.getExplosionResistance(this, this.world, blockpos, blockstate, fluidstate);
							if (optional.isPresent()) {
								rayStrength -= (optional.get() + 0.3F) * 0.3F;
							}
							if (rayStrength > 0.0F && this.context.canExplosionDestroyBlock(this, this.world, blockpos, blockstate, rayStrength)) {
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
		this.getAffectedBlockPositions().addAll(set);
	}

	public void fallingBlocks() {
		for(BlockPos blockpos : this.getAffectedBlockPositions()) {
			BlockState blockstate = this.world.getBlockState(blockpos);
			Block block = blockstate.getBlock();
			if (blockstate.isAir(this.world, blockpos))
				continue;
			if (block instanceof TNTBlock) {
				block.onExplosionDestroy(this.world, blockpos, this);
				this.world.setBlockState(blockpos, Blocks.AIR.getDefaultState());
				continue;
			}
			if (block instanceof MovingPistonBlock) {
				PistonTileEntity tileEntity = (PistonTileEntity) this.world.getTileEntity(blockpos);
				blockstate = tileEntity.getPistonState();
				block = blockstate.getBlock();
			}
			block.onExplosionDestroy(this.world, new BlockPos(this.getPosition()), this);
			BlockPos blockpos1 = blockpos.toImmutable();
			this.world.setBlockState(blockpos1, Blocks.AIR.getDefaultState());
			ExplosionFallingBlockEntity fallingBlockEntity = new ExplosionFallingBlockEntity(this.world, blockpos1.getX() + 0.5f, blockpos1.getY() + 2.0625f, blockpos1.getZ() + 0.5f, blockstate);
			fallingBlockEntity.fallTime = 1;
			fallingBlockEntity.exploder = this.exploder;
			this.world.addEntity(fallingBlockEntity);
		}
		this.clearAffectedBlockPositions();
	}

	public void processEntities(double blockingDamageReduction, boolean knockbackScaleWithSize) {
		float affectedEntitiesRadius = this.size * 2.0F;
		List<Entity> list = gatherAffectedEntities(affectedEntitiesRadius);
		for(Entity entity : list) {
			if (entity.ticksExisted == 0 && !Modules.misc.explosionOverhaul.affectJustSpawnedEntities)
				continue;
			if (entity.isImmuneToExplosions())
				continue;
			double distanceRatio = (MathHelper.sqrt(entity.getDistanceSq(this.getPosition())) / affectedEntitiesRadius);
			if (distanceRatio > 1.0D)
				continue;
			double xDistance = entity.getPosX() - this.getPosition().x;
			double yDistance = (entity.getPosYEye() - this.getPosition().y) * 0.6667d;
			double zDistance = entity.getPosZ() - this.getPosition().z;
			double d13 = MathHelper.sqrt(xDistance * xDistance + yDistance * yDistance + zDistance * zDistance);
			if (d13 == 0.00)
				continue;
			//xDistance = xDistance / d13;
			if (!(entity instanceof ExplosionFallingBlockEntity))
				yDistance = yDistance / d13;
			//zDistance = zDistance / d13;
			double blockDensity = getBlockDensity(this.getPosition(), entity);
			double d10 = (1.0D - distanceRatio) * blockDensity;
			//Damage Entities in the explosion radius
			float damageAmount = (float) ((int) ((d10 * d10 + d10) / 2.0D * 7.0D * (double) affectedEntitiesRadius + 1.0D));
			if (blockDensity > 0d) {
				DamageSource source = this.getDamageSource();
				if (entity instanceof ServerPlayerEntity && blockingDamageReduction > 0d) {
					ServerPlayerEntity player = (ServerPlayerEntity) entity;
					if (damageAmount > 0.0F && MCUtils.canBlockDamageSource(source, player)) {
						source.isUnblockable = true;
						damageAmount *= blockingDamageReduction;
						///d10 *= blockingDamageReduction;
						Reflection.PlayerEntity_damageShield(player, damageAmount);
						player.world.setEntityState(player, (byte) 29);
						//player.blockUsingShield((LivingEntity)entity);
					}
				}
				entity.attackEntityFrom(source, damageAmount);

				double d11 = d10;
				if (entity instanceof LivingEntity) {
					d11 = getBlastDamageReduction((LivingEntity) entity, d11);
				}
				if (knockbackScaleWithSize)
					d11 *= this.size;
				d11 = Math.max(d11, this.size * 0.05d);
				if (entity instanceof ExplosionFallingBlockEntity)
					d11 = Math.min(d11, 0.5d);
				entity.setMotion(entity.getMotion().add(xDistance * d11, yDistance * d11, zDistance * d11));
				if (entity instanceof PlayerEntity) {
					PlayerEntity playerentity = (PlayerEntity)entity;
					if (!playerentity.isSpectator() && (!playerentity.isCreative() || !playerentity.abilities.isFlying)) {
						this.getPlayerKnockbackMap().put(playerentity, new Vector3d(xDistance * d11, yDistance * d11, zDistance * d11));
					}
				}
			}
		}
	}

	public void destroyBlocks() {
		net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.world, this, gatherAffectedEntities(this.size * 2.0F), this.size * 2.0F);
		if (this.mode == Explosion.Mode.NONE)
			return;
		Collections.shuffle(this.getAffectedBlockPositions(), this.world.rand);
		for(BlockPos blockpos : this.getAffectedBlockPositions()) {
			BlockState blockstate = this.world.getBlockState(blockpos);
			if (!blockstate.isAir(this.world, blockpos)) {
				BlockPos immutableBlockPos = blockpos.toImmutable();
				this.world.getProfiler().startSection("explosion_blocks");
				if (blockstate.canDropFromExplosion(this.world, blockpos, this) && this.world instanceof ServerWorld) {
					TileEntity tileentity = blockstate.hasTileEntity() ? this.world.getTileEntity(blockpos) : null;
					LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld)this.world)).withRandom(this.world.rand).withParameter(LootParameters.field_237457_g_, Vector3d.copyCentered(blockpos)).withParameter(LootParameters.TOOL, ItemStack.EMPTY).withNullableParameter(LootParameters.BLOCK_ENTITY, tileentity).withNullableParameter(LootParameters.THIS_ENTITY, this.exploder);
					if (this.mode == Explosion.Mode.DESTROY) {
						lootcontext$builder.withParameter(LootParameters.EXPLOSION_RADIUS, this.size);
					}
					blockstate.getDrops(lootcontext$builder).forEach((stack) -> handleExplosionDrops(droppedItems, stack, immutableBlockPos));
				}
				blockstate.onBlockExploded(this.world, blockpos, this);
				this.world.getProfiler().endSection();
			}
		}
	}

	public void dropItems() {
		for(Pair<ItemStack, BlockPos> pair : droppedItems) {
			Block.spawnAsEntity(this.world, pair.getSecond(), pair.getFirst());
		}
	}

	//Should be called in remote world only
	public void playSound() {
		this.world.playSound(this.getPosition().x, this.getPosition().y, this.getPosition().z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F, false);
	}

	public void spawnParticles() {
		if (!(this.size < 2.0F) && this.mode != Explosion.Mode.NONE) {
			this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getPosition().x, this.getPosition().y, this.getPosition().z, 1.0D, 0.0D, 0.0D);
		}
		else {
			this.world.addParticle(ParticleTypes.EXPLOSION, this.getPosition().x, this.getPosition().y, this.getPosition().z, 1.0D, 0.0D, 0.0D);
		}
	}

	public void processFire() {
		if (this.causesFire) {
			for(BlockPos blockpos2 : this.getAffectedBlockPositions()) {
				if (this.random.nextInt(3) == 0 && this.world.getBlockState(blockpos2).isAir() && this.world.getBlockState(blockpos2.down()).isOpaqueCube(this.world, blockpos2.down())) {
					this.world.setBlockState(blockpos2, AbstractFireBlock.getFireForPlacement(this.world, blockpos2));
				}
			}
		}
	}

	private List<Entity> gatherAffectedEntities(float affectedRadius) {
		int x1 = MathHelper.floor(this.getPosition().getX() - (double)affectedRadius - 1.0D);
		int x2 = MathHelper.floor(this.getPosition().getX() + (double)affectedRadius + 1.0D);
		int y1 = MathHelper.floor(this.getPosition().getY() - (double)affectedRadius - 1.0D);
		int y2 = MathHelper.floor(this.getPosition().getY() + (double)affectedRadius + 1.0D);
		int z1 = MathHelper.floor(this.getPosition().getZ() - (double)affectedRadius - 1.0D);
		int z2 = MathHelper.floor(this.getPosition().getZ() + (double)affectedRadius + 1.0D);
		return this.world.getEntitiesWithinAABBExcludingEntity(this.exploder, new AxisAlignedBB(x1, y1, z1, x2, y2, z2));
	}

	/*
	   Since Mojang decided to round down the damage and use the same method for both Knockback and Damage, the latter doesn't work as it's always rounded down to 0
	 */
	public static double getBlastDamageReduction(LivingEntity entityLivingBaseIn, double damage) {
		int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.BLAST_PROTECTION, entityLivingBaseIn);
		if (i > 0) {
			damage -= damage * (double)((float)i * 0.15F);
		}
		return damage;
	}
}
