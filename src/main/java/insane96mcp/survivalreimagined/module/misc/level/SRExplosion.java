package insane96mcp.survivalreimagined.module.misc.level;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import insane96mcp.survivalreimagined.event.SREventFactory;
import insane96mcp.survivalreimagined.module.misc.entity.SRFallingBlockEntity;
import insane96mcp.survivalreimagined.module.misc.feature.ExplosionOverhaul;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SRExplosion extends Explosion {
	ObjectArrayList<Pair<ItemStack, BlockPos>> droppedItems = new ObjectArrayList<>();
	boolean dealsKnockback;
	boolean creeperCollateral;
	public final boolean poofParticles;

	public SRExplosion(Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Explosion.BlockInteraction blockInteraction, boolean creeperCollateral) {
		this(level, source, damageSource, damageCalculator, x, y, z, radius, fire, blockInteraction, true, creeperCollateral, true);
	}

	public SRExplosion(Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Explosion.BlockInteraction blockInteraction, boolean dealsKnockback, boolean creeperCollateral, boolean poofParticles) {
		super(level, source, damageSource, damageCalculator, x, y, z, radius, fire, blockInteraction);
		this.dealsKnockback = dealsKnockback;
		this.creeperCollateral = creeperCollateral;
		this.poofParticles = poofParticles;
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
							BlockPos blockpos = BlockPos.containing(d4, d6, d8);
							BlockState blockstate = this.level.getBlockState(blockpos);
							FluidState fluidstate = this.level.getFluidState(blockpos);
							Optional<Float> optional = this.damageCalculator.getBlockExplosionResistance(this, this.level, blockpos, blockstate, fluidstate);
							if (optional.isPresent()) {
								float resistance = optional.get();
								float multiplier = 0.3f;
								/*if (blockstate.getMaterial().equals(Material.STONE) /*&& this.stoneResistanceDivider >= 0)
									multiplier /= 5f;*/
								rayStrength -= (resistance + 0.3F) * multiplier;
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
		if (this.blockInteraction == BlockInteraction.KEEP)
			return;
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
			block.wasExploded(this.level, BlockPos.containing(this.getPosition()), this);
			BlockPos blockpos1 = blockpos.immutable();
			this.level.setBlockAndUpdate(blockpos1, Blocks.AIR.defaultBlockState());
			SRFallingBlockEntity fallingBlockEntity = new SRFallingBlockEntity(this.level, blockpos1.getX() + 0.5f, blockpos1.getY() + 2.0625f, blockpos1.getZ() + 0.5f, blockstate);
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
			if (entity.tickCount == 0 && !(entity instanceof PartEntity<?>)  && !(entity instanceof SRFallingBlockEntity)  && !ExplosionOverhaul.affectJustSpawnedEntities)
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
			if (!(entity instanceof SRFallingBlockEntity))
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
						damageAmount *= blockingDamageReduction;
						player.hurtCurrentlyUsedShield(damageAmount);
						player.level.broadcastEntityEvent(player, (byte) 29);
					}
				}
				entity.hurt(source, damageAmount);

				if (this.dealsKnockback) {
					double d11 = d10;
					if (entity instanceof LivingEntity) {
						d11 = getKnockbackReduction((LivingEntity) entity, d11);
					}
					if (knockbackScaleWithSize)
						d11 *= this.radius;
					d11 = Mth.clamp(d11, this.radius * 0.05d, 10d);
					if (entity instanceof SRFallingBlockEntity || ExplosionOverhaul.shouldTakeReducedKnockback(entity))
						d11 *= 0.2d;
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
		this.level.gameEvent(this.source, GameEvent.EXPLODE, new Vec3(this.getPosition().x, this.getPosition().y, this.getPosition().z));
		if (this.blockInteraction == BlockInteraction.KEEP)
			return;
		Util.shuffle(this.toBlow, this.level.getRandom());
		for(BlockPos blockpos : this.getToBlow()) {
			BlockState blockstate = this.level.getBlockState(blockpos);
			if (!blockstate.isAir()) {
				BlockPos immutableBlockPos = blockpos.immutable();
				this.level.getProfiler().push("explosion_blocks");
				if (!doesCreeperCollateralApply() && blockstate.canDropFromExplosion(this.level, blockpos, this) && this.level instanceof ServerLevel) {
					BlockEntity blockEntity = blockstate.hasBlockEntity() ? this.level.getBlockEntity(blockpos) : null;
					LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel) this.level)).withRandom(this.level.getRandom()).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockpos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity).withOptionalParameter(LootContextParams.THIS_ENTITY, this.source);
					if (this.blockInteraction == Explosion.BlockInteraction.DESTROY_WITH_DECAY) {
						lootcontext$builder.withParameter(LootContextParams.EXPLOSION_RADIUS, this.radius);
					}
					blockstate.getDrops(lootcontext$builder).forEach((stack) -> addBlockDrops(droppedItems, stack, immutableBlockPos));
				}
				blockstate.onBlockExploded(this.level, blockpos, this);
				this.level.getProfiler().pop();
			}
		}
	}

	public boolean doesCreeperCollateralApply() {
		return this.creeperCollateral && this.getExploder() instanceof Creeper;
	}

	public void dropItems() {
		for(Pair<ItemStack, BlockPos> pair : droppedItems) {
			Block.popResource(this.level, pair.getSecond(), pair.getFirst());
		}
	}

	public void processFire() {
		if (this.fire) {
			for(BlockPos blockPos : this.getToBlow()) {
				if (this.random.nextInt(3) == 0 && this.level.getBlockState(blockPos).isAir() && this.level.getBlockState(blockPos.below()).isSolidRender(this.level, blockPos.below())) {
					this.level.setBlockAndUpdate(blockPos, BaseFireBlock.getState(this.level, blockPos));
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
	   Since Mojang decided to round down the damage and used the same method for both Knockback and Damage, the latter doesn't work as it's always rounded down to 0
	 */
	public static double getKnockbackReduction(LivingEntity livingEntity, double knockback) {
		double knockbackReduction = 0d;
		int blastProtLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, livingEntity);
		if (blastProtLevel > 0) {
			knockbackReduction += blastProtLevel * 0.15d;
		}

		if (livingEntity.getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null) {
			//noinspection ConstantConditions
			knockbackReduction += livingEntity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getValue();
		}

		knockback -= knockback * knockbackReduction;
		return knockback;
	}

	@Nullable
	public static SRExplosion explode(Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction, boolean poofParticles) {
		if (!(level instanceof ServerLevel serverLevel))
			return null;
		Explosion.BlockInteraction blockInteraction = switch (explosionInteraction) {
			case NONE -> BlockInteraction.KEEP;
			case BLOCK ->
					level.getGameRules().getBoolean(GameRules.RULE_BLOCK_EXPLOSION_DROP_DECAY) ? BlockInteraction.DESTROY_WITH_DECAY : BlockInteraction.DESTROY;
			case MOB ->
					net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(level, source) ? level.getGameRules().getBoolean(GameRules.RULE_MOB_EXPLOSION_DROP_DECAY) ? BlockInteraction.DESTROY_WITH_DECAY : BlockInteraction.DESTROY : BlockInteraction.KEEP;
			case TNT ->
					level.getGameRules().getBoolean(GameRules.RULE_TNT_EXPLOSION_DROP_DECAY) ? BlockInteraction.DESTROY_WITH_DECAY : BlockInteraction.DESTROY;
		};
		return explode(serverLevel, source, damageSource, damageCalculator, x, y, z, radius, fire, blockInteraction, poofParticles);
	}

	public static SRExplosion explode(ServerLevel level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Explosion.BlockInteraction blockInteraction, boolean poofParticles) {
		SRExplosion explosion = new SRExplosion(level, source, damageSource, damageCalculator, x, y, z, radius, fire, blockInteraction, true, ExplosionOverhaul.creeperCollateral, poofParticles);
		if (SREventFactory.onSRExplosionCreated(explosion)) return explosion;
		explosion.gatherAffectedBlocks(!ExplosionOverhaul.disableExplosionRandomness);
		if (ExplosionOverhaul.enableFlyingBlocks)
			explosion.fallingBlocks();
		explosion.destroyBlocks();
		explosion.processEntities(ExplosionOverhaul.blockingDamageScaling, ExplosionOverhaul.knockbackScalesWithSize);
		explosion.dropItems();
		explosion.processFire();
		if (explosion.blockInteraction == Explosion.BlockInteraction.KEEP) {
			explosion.clearToBlow();
		}
		for (ServerPlayer serverPlayer : level.players()) {
			if (serverPlayer.distanceToSqr(explosion.getPosition().x, explosion.getPosition().y, explosion.getPosition().z) < 4096.0D) {
				serverPlayer.connection.send(new ClientboundExplodePacket(explosion.getPosition().x, explosion.getPosition().y, explosion.getPosition().z, explosion.radius, explosion.getToBlow(), explosion.getHitPlayers().get(serverPlayer)));
			}
		}
		return explosion;
	}
}
