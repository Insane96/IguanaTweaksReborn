package insane96mcp.survivalreimagined.module.experience.enchantment;

import insane96mcp.survivalreimagined.module.experience.feature.EnchantmentsFeature;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DamageEnchantment;

public class BaneOfSSSS extends DamageEnchantment {
    public BaneOfSSSS() {
        super(Rarity.UNCOMMON, DamageEnchantment.ARTHROPODS, EquipmentSlot.MAINHAND);
    }

    @Override
    public float getDamageBonus(int level, MobType mobType, ItemStack enchantedItem) {
        return mobType == MobType.ARTHROPOD ? (float)level * 2.5F : 0.0F;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return EnchantmentsFeature.isBaneOfSSSSSEnabled() && super.canEnchant(stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return EnchantmentsFeature.isBaneOfSSSSSEnabled() && super.canApplyAtEnchantingTable(stack);
    }

    @Override
    public void doPostAttack(LivingEntity livingEntity, Entity entity, int lvl) {
        if (!(entity instanceof LivingEntity livingentity))
            return;

        if (lvl > 0 && (livingentity.getMobType() == MobType.ARTHROPOD || livingentity instanceof Creeper)) {
            int i = 20 + livingEntity.getRandom().nextInt(10 * lvl);
            livingentity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, i, 3));
        }
    }
}
