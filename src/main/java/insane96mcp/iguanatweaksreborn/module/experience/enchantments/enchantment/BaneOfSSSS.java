package insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment;

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
    public void doPostAttack(LivingEntity attacker, Entity target, int lvl) {
        if (!(target instanceof LivingEntity livingentity))
            return;

        if (lvl > 0 && (livingentity.getMobType() == MobType.ARTHROPOD || livingentity instanceof Creeper)) {
            int i = 20 + attacker.getRandom().nextInt(10 * lvl);
            livingentity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, i, 3));
        }
    }
}
