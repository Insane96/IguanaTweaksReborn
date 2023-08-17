package insane96mcp.survivalreimagined.module.experience.enchantments.enchantment;

import insane96mcp.survivalreimagined.setup.SREnchantments;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.enchantment.DiggingEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeMod;

public class Blasting extends Enchantment {
    static final EnchantmentCategory PICKAXES = EnchantmentCategory.create("pickaxes", item -> item instanceof PickaxeItem);

    public Blasting() {
        super(Rarity.COMMON, PICKAXES, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getMinCost(int level) {
        return 1 + 10 * (level - 1);
    }

    @Override
    public int getMaxCost(int level) {
        return this.getMinCost(level) + 50;
    }

    public boolean checkCompatibility(Enchantment other) {
        return !(other instanceof DiggingEnchantment) && super.checkCompatibility(other);
    }

    public static float getMiningSpeedBoost(LivingEntity entity, BlockState state) {
        ItemStack heldStack = entity.getMainHandItem();
        if (!heldStack.isCorrectToolForDrops(state))
            return 0f;
        int level = heldStack.getEnchantmentLevel(SREnchantments.BLASTING.get());
        if (level == 0)
            return 0f;

        float miningSpeedBoost = (float) (level * Math.pow(2.5d, 6.5f - state.getBlock().getExplosionResistance()));

        if (MobEffectUtil.hasDigSpeed(entity)) {
            miningSpeedBoost *= 1.0F + (float)(MobEffectUtil.getDigSpeedAmplification(entity) + 1) * 0.2F;
        }

        if (entity.hasEffect(MobEffects.DIG_SLOWDOWN)) {
            float miningFatigueMultiplier = switch (entity.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) {
				case 0 -> 0.3F;
				case 1 -> 0.09F;
				case 2 -> 0.0027F;
				default -> 8.1E-4F;
			};
			miningSpeedBoost *= miningFatigueMultiplier;
        }

        if (entity.isEyeInFluidType(ForgeMod.WATER_TYPE.get()) && !EnchantmentHelper.hasAquaAffinity(entity)) {
            miningSpeedBoost /= 5.0F;
        }

        if (!entity.onGround()) {
            miningSpeedBoost /= 5.0F;
        }

        return miningSpeedBoost;
    }
}
