package insane96mcp.survivalreimagined.module.experience.enchantment;

import insane96mcp.survivalreimagined.setup.SREnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class Magnetic extends Enchantment {
    public Magnetic() {
        super(Rarity.RARE, EnchantmentCategory.ARMOR_LEGS, new EquipmentSlot[]{EquipmentSlot.LEGS});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinCost(int level) {
        return 15 + (level - 1) * 20;
    }

    @Override
    public int getMaxCost(int level) {
        return this.getMinCost(level) + 30;
    }

    public static void tryPullItems(LivingEntity entity) {
        int level = entity.getItemBySlot(EquipmentSlot.LEGS).getEnchantmentLevel(SREnchantments.MAGNETIC.get());
        if (level == 0)
            return;

        //List<ItemEntity> itemsInRange = entity.level.getEntitiesOfClass(ItemEntity.class, entity.getBoundingBox().inflate(level + 2), itemEntity -> !itemEntity.hasPickUpDelay());
        List<ItemEntity> itemsInRange = entity.level.getEntitiesOfClass(ItemEntity.class, entity.getBoundingBox().inflate(level + 2));
        for (ItemEntity itemEntity : itemsInRange) {
            Vec3 vecToEntity = new Vec3(entity.getX() - itemEntity.getX(), entity.getY() + (double)entity.getEyeHeight() / 2.0D - itemEntity.getY(), entity.getZ() - itemEntity.getZ());
            itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().add(vecToEntity.normalize().scale(0.02d + level * 0.01d)));
        }
    }
}
