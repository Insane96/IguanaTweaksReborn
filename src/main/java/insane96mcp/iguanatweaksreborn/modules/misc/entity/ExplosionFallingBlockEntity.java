package insane96mcp.iguanatweaksreborn.modules.misc.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

public class ExplosionFallingBlockEntity extends FallingBlockEntity {

    public Entity exploder;

    public ExplosionFallingBlockEntity(World worldIn, double x, double y, double z, BlockState fallingBlockState) {
        super(worldIn, x, y, z, fallingBlockState);
    }

    @Nullable
    @Override
    public ItemEntity entityDropItem(IItemProvider itemIn) {
        if (!(itemIn instanceof Block))
            return super.entityDropItem(itemIn);

        if (this.world.isRemote)
            return null;

        LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld)this.world)).withRandom(this.world.rand).withParameter(LootParameters.field_237457_g_, Vector3d.copyCentered(this.getPosition())).withParameter(LootParameters.TOOL, ItemStack.EMPTY).withNullableParameter(LootParameters.THIS_ENTITY, this.exploder);

        List<ItemStack> drops = this.getBlockState().getDrops(lootcontext$builder);

        if (drops.isEmpty())
            return null;
        for (ItemStack stack : drops) {
            ItemEntity itemEntity = new ItemEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ(), stack);
            itemEntity.setDefaultPickupDelay();
            if (captureDrops() != null)
                captureDrops().add(itemEntity);
            else
                this.world.addEntity(itemEntity);
        }
        return null;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world instanceof ServerWorld)
            ((ServerWorld) this.world).spawnParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, this.getPosX(), this.getPosY() + 0.5, this.getPosZ(), 1, 0, 0, 0, 0);
    }
}
