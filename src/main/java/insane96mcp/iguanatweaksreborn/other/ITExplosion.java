package insane96mcp.iguanatweaksreborn.other;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.ExplosionContext;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ITExplosion extends Explosion {

    public ITExplosion(World worldIn, @Nullable Entity entityIn, double x, double y, double z, float size, List<BlockPos> affectedPositions) {
        super(worldIn, entityIn, x, y, z, size, affectedPositions);
    }

    public ITExplosion(World worldIn, @Nullable Entity exploderIn, double xIn, double yIn, double zIn, float sizeIn, boolean causesFireIn, Mode modeIn, List<BlockPos> affectedBlockPositionsIn) {
        super(worldIn, exploderIn, xIn, yIn, zIn, sizeIn, causesFireIn, modeIn, affectedBlockPositionsIn);
    }

    public ITExplosion(World worldIn, @Nullable Entity exploderIn, double xIn, double yIn, double zIn, float sizeIn, boolean causesFireIn, Mode modeIn) {
        super(worldIn, exploderIn, xIn, yIn, zIn, sizeIn, causesFireIn, modeIn);
    }

    public ITExplosion(World world, @Nullable Entity exploder, @Nullable DamageSource source, @Nullable ExplosionContext context, double x, double y, double z, float size, boolean causesFire, Mode mode) {
        super(world, exploder, source, context, x, y, z, size, causesFire, mode);
    }

    /*
        Creates an IguanaTweaks Explosion from an already existing Explosion
     */
    public ITExplosion(Explosion explosion) {
        //super(explosion.world, explosion.exploder, explosion.getDamageSource(), explosion.context, explosion.getPosition().x, explosion.getPosition().y + explosion.exploder.getSize(explosion.exploder.getPose()).height / 2, explosion.getPosition().z, explosion.size, explosion.causesFire, explosion.mode);
        super(explosion.world, explosion.exploder, explosion.getDamageSource(), explosion.context, explosion.getPosition().x, explosion.getPosition().y, explosion.getPosition().z, explosion.size, explosion.causesFire, explosion.mode);
    }

    public void doExplosionA() {
        Set<BlockPos> set = Sets.newHashSet();
        int i = 16;

        int rays = 0;
        for(int j = 0; j < 16; ++j) {
            for(int k = 0; k < 16; ++k) {
                for(int l = 0; l < 16; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        rays++;
                        double d0 = (float)j / 15.0F * 2.0F - 1.0F;
                        double d1 = (float)k / 15.0F * 2.0F - 1.0F;
                        double d2 = (float)l / 15.0F * 2.0F - 1.0F;
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 = d0 / d3;
                        d1 = d1 / d3;
                        d2 = d2 / d3;
                        //Remove the randomness in explosions, assuming the random float will always output 0.5
                        //float rayStrength = this.size * (0.7F + this.world.rand.nextFloat() * 0.6F);
                        float rayStrength = this.size;
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

        float affectedEntitiesRadius = this.size * 2.0F;
        int x1 = MathHelper.floor(this.getPosition().getX() - (double)affectedEntitiesRadius - 1.0D);
        int x2 = MathHelper.floor(this.getPosition().getX() + (double)affectedEntitiesRadius + 1.0D);
        int y1 = MathHelper.floor(this.getPosition().getY() - (double)affectedEntitiesRadius - 1.0D);
        int y2 = MathHelper.floor(this.getPosition().getY() + (double)affectedEntitiesRadius + 1.0D);
        int z1 = MathHelper.floor(this.getPosition().getZ() - (double)affectedEntitiesRadius - 1.0D);
        int z2 = MathHelper.floor(this.getPosition().getZ() + (double)affectedEntitiesRadius + 1.0D);
        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this.exploder, new AxisAlignedBB(x1, y1, z1, x2, y2, z2));
        net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.world, this, list, affectedEntitiesRadius);

        for(Entity entity : list) {
            if (entity.isImmuneToExplosions())
                continue;

            double distanceRatio = (MathHelper.sqrt(entity.getDistanceSq(this.getPosition())) / affectedEntitiesRadius);
            if (distanceRatio > 1.0D)
                continue;

            double xDistance = entity.getPosX() - this.getPosition().x;
            double yDistance = (entity instanceof TNTEntity ? entity.getPosY() : entity.getPosYEye()) - this.getPosition().y;
            double zDistance = entity.getPosZ() - this.getPosition().z;
            double d13 = (double)MathHelper.sqrt(xDistance * xDistance + yDistance * yDistance + zDistance * zDistance);
            if (d13 == 0.00)
                continue;

            //xDistance = xDistance / d13;
            yDistance = yDistance / d13;
            //zDistance = zDistance / d13;
            double blockDensity = Math.max(getBlockDensity(this.getPosition(), entity), 0.5);
            //double blockDensity = getBlockDensity(this.getPosition(), entity);
            //entity.sendMessage(new StringTextComponent("block den: " + blockDensity), entity.getUniqueID());
            double d10 = (1.0D - distanceRatio) * blockDensity;
            entity.attackEntityFrom(this.getDamageSource(), (float)((int)((d10 * d10 + d10) / 2.0D * 7.0D * (double)affectedEntitiesRadius + 1.0D)));
            double d11 = d10;
            if (entity instanceof LivingEntity) {
                d11 = getBlastDamageReduction((LivingEntity)entity, d10);
            }
            d11 *= this.size;
            d11 = Math.max(d11, this.size * 0.1);

            entity.setMotion(entity.getMotion().add(xDistance * d11, yDistance * d11, zDistance * d11));
            if (entity instanceof PlayerEntity) {
                PlayerEntity playerentity = (PlayerEntity)entity;
                if (!playerentity.isSpectator() && (!playerentity.isCreative() || !playerentity.abilities.isFlying)) {
                    this.getPlayerKnockbackMap().put(playerentity, new Vector3d(xDistance * d10, yDistance * d10, zDistance * d10));
                }
            }
        }
    }

    /*public void doExplosionB(boolean spawnParticles) {
        if (this.world.isRemote) {
            this.world.playSound(this.getPosition().x, this.getPosition().y, this.getPosition().z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F, false);
        }

        boolean flag = this.mode != Explosion.Mode.NONE;
        if (spawnParticles) {
            if (!(this.size < 2.0F) && flag) {
                this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getPosition().x, this.getPosition().y, this.getPosition().z, 1.0D, 0.0D, 0.0D);
            } else {
                this.world.addParticle(ParticleTypes.EXPLOSION, this.getPosition().x, this.getPosition().y, this.getPosition().z, 1.0D, 0.0D, 0.0D);
            }
        }

        if (flag) {
            ObjectArrayList<Pair<ItemStack, BlockPos>> objectarraylist = new ObjectArrayList<>();
            Collections.shuffle(this.getAffectedBlockPositions(), this.world.rand);

            for(BlockPos blockpos : this.getAffectedBlockPositions()) {
                BlockState blockstate = this.world.getBlockState(blockpos);
                Block block = blockstate.getBlock();
                if (!blockstate.isAir(this.world, blockpos)) {
                    BlockPos blockpos1 = blockpos.toImmutable();
                    this.world.getProfiler().startSection("explosion_blocks");
                    if (blockstate.canDropFromExplosion(this.world, blockpos, this) && this.world instanceof ServerWorld) {
                        TileEntity tileentity = blockstate.hasTileEntity() ? this.world.getTileEntity(blockpos) : null;
                        LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld)this.world)).withRandom(this.world.rand).withParameter(LootParameters.field_237457_g_, Vector3d.copyCentered(blockpos)).withParameter(LootParameters.TOOL, ItemStack.EMPTY).withNullableParameter(LootParameters.BLOCK_ENTITY, tileentity).withNullableParameter(LootParameters.THIS_ENTITY, this.exploder);
                        if (this.mode == Explosion.Mode.DESTROY) {
                            lootcontext$builder.withParameter(LootParameters.EXPLOSION_RADIUS, this.size);
                        }

                        blockstate.getDrops(lootcontext$builder).forEach((stack) -> {
                            handleExplosionDrops(objectarraylist, stack, blockpos1);
                        });
                    }

                    blockstate.onBlockExploded(this.world, blockpos, this);
                    this.world.getProfiler().endSection();
                }
            }

            for(Pair<ItemStack, BlockPos> pair : objectarraylist) {
                Block.spawnAsEntity(this.world, pair.getSecond(), pair.getFirst());
            }
        }

        if (this.causesFire) {
            for(BlockPos blockpos2 : this.getAffectedBlockPositions()) {
                if (this.random.nextInt(3) == 0 && this.world.getBlockState(blockpos2).isAir() && this.world.getBlockState(blockpos2.down()).isOpaqueCube(this.world, blockpos2.down())) {
                    this.world.setBlockState(blockpos2, AbstractFireBlock.getFireForPlacement(this.world, blockpos2));
                }
            }
        }
    }*/

    public static float getBlockDensity(Vector3d explosionVector, Entity entity) {
        AxisAlignedBB axisalignedbb = entity.getBoundingBox();
        double d0 = 1.0D / ((axisalignedbb.maxX - axisalignedbb.minX) * 2.0D + 1.0D);
        double d1 = 1.0D / ((axisalignedbb.maxY - axisalignedbb.minY) * 2.0D + 1.0D);
        double d2 = 1.0D / ((axisalignedbb.maxZ - axisalignedbb.minZ) * 2.0D + 1.0D);
        double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
        double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;
        if (!(d0 < 0.0D) && !(d1 < 0.0D) && !(d2 < 0.0D)) {
            int i = 0;
            int j = 0;

            for(float f = 0.0F; f <= 1.0F; f = (float)((double)f + d0)) {
                for(float f1 = 0.0F; f1 <= 1.0F; f1 = (float)((double)f1 + d1)) {
                    for(float f2 = 0.0F; f2 <= 1.0F; f2 = (float)((double)f2 + d2)) {
                        double d5 = MathHelper.lerp((double)f, axisalignedbb.minX, axisalignedbb.maxX);
                        double d6 = MathHelper.lerp((double)f1, axisalignedbb.minY, axisalignedbb.maxY);
                        double d7 = MathHelper.lerp((double)f2, axisalignedbb.minZ, axisalignedbb.maxZ);
                        Vector3d vector3d = new Vector3d(d5 + d3, d6, d7 + d4);
                        BlockRayTraceResult rayTraceResult = entity.world.rayTraceBlocks(new RayTraceContext(vector3d, explosionVector, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity));
                        if (rayTraceResult.getType() == RayTraceResult.Type.MISS) {
                            ++i;
                        }

                        ++j;
                    }
                }
            }

            return (float)i / (float)j;
        } else {
            return 0.0F;
        }
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
