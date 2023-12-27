package insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment;

import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.enchantment.DiggingEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.state.BlockState;

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
        return 2 + 10 * (level - 1);
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
        int level = heldStack.getEnchantmentLevel(EnchantmentsFeature.BLASTING.get());
        if (level == 0)
            return 0f;
        if (!(heldStack.getItem() instanceof DiggerItem diggerItem))
            return 0f;

        float miningSpeedBoost = (float) (level * Math.min(10f, Math.pow(3d, (6f - state.getBlock().getExplosionResistance()) / 1.5f))) / 6f * diggerItem.speed;
        return EnchantmentsFeature.applyMiningSpeedModifiers(miningSpeedBoost, false, entity);
    }
}
