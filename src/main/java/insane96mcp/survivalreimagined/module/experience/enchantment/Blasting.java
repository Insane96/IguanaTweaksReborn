package insane96mcp.survivalreimagined.module.experience.enchantment;

import insane96mcp.survivalreimagined.setup.SREnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.DiggingEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.state.BlockState;

public class Blasting extends Enchantment {
    public Blasting() {
        super(Rarity.UNCOMMON, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
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
        int level = entity.getItemBySlot(EquipmentSlot.MAINHAND).getEnchantmentLevel(SREnchantments.BLASTING.get());
        if (level == 0)
            return 0f;

        return (float) (level * Math.min(10f, Math.pow(3f, (6f - state.getBlock().getExplosionResistance()) / 1.5f)));
    }
}
