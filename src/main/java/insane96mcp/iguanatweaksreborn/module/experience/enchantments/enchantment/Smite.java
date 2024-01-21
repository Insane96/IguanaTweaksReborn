package insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class Smite extends BonusDamageEnchantment {
    public static final TagKey<EntityType<?>> AFFECTED_BY_SMITE = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(IguanaTweaksReborn.MOD_ID, "enchantments/smite"));
    public Smite() {
        super(Rarity.UNCOMMON, EnchantmentsFeature.ITR_WEAPONS, new EquipmentSlot[]{ EquipmentSlot.MAINHAND });
    }

    @Override
    public float getDamageBonus(LivingEntity attacker, LivingEntity target, ItemStack stack, int lvl) {
        if (!target.getType().is(AFFECTED_BY_SMITE))
            return 0f;
        return this.getDamageBonus(stack, lvl);
    }

    @Override
    public float getDamageBonus(ItemStack stack, int lvl) {
        return 1.5f * lvl * this.getDamageBonusRatio(stack);
    }
}
