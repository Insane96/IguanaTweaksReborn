package insane96mcp.survivalreimagined.module.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;


public class FirestarterItem extends FlintAndSteelItem implements Vanishable {
    public FirestarterItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack p_41454_) {
        return 100;
    }

    @Override
    public InteractionResult useOn(UseOnContext p_41297_) {
        return InteractionResult.PASS;
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int tickCount) {
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player))
            return stack;
        BlockHitResult blockHitResult = getPlayerPOVHitResult(player.level, player, ClipContext.Fluid.NONE);
        super.useOn(new UseOnContext(player, player.getUsedItemHand(), blockHitResult));
        player.getCooldowns().addCooldown(stack.getItem(), 20);
        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        BlockHitResult hitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        ItemStack itemstack = player.getItemInHand(interactionHand);
        if (hitresult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = hitresult.getBlockPos();
            if (!level.mayInteract(player, blockpos)) {
                return InteractionResultHolder.pass(itemstack);
            }
            player.startUsingItem(interactionHand);
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        }
        return InteractionResultHolder.pass(itemstack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int tickCount) {
        if (!(livingEntity instanceof Player player))
            return;
        BlockHitResult hitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (hitresult.getType() != HitResult.Type.BLOCK) {
            player.stopUsingItem();
        }
        else {
            if (tickCount % 3 == 1) {
                if (tickCount <= 70)
                    level.addParticle(ParticleTypes.SMOKE, hitresult.getLocation().x, hitresult.getLocation().y, hitresult.getLocation().z, 0.0d, 0.1d, 0.0d);
                if (tickCount <= 30)
                    level.addParticle(ParticleTypes.FLAME, hitresult.getLocation().x, hitresult.getLocation().y, hitresult.getLocation().z, 0.0d, 0.1d, 0.0d);
            }

            if (tickCount == 80 || tickCount == 40) {
                level.playSound(player, hitresult.getBlockPos(), SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS);
            }
        }
    }

    public UseAnim getUseAnimation(ItemStack p_40678_) {
        return UseAnim.BOW;
    }
}
