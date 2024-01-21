package insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.damage;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class Smite extends BonusDamageEnchantment {
    public static final TagKey<EntityType<?>> AFFECTED_BY_SMITE = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(IguanaTweaksReborn.MOD_ID, "enchantments/smite"));
    public Smite() {
        super(Rarity.UNCOMMON, EnchantmentsFeature.WEAPONS_CATEGORY, new EquipmentSlot[]{ EquipmentSlot.MAINHAND });
    }

    @Override
    public boolean isAffectedByEnchantment(LivingEntity target) {
        return target.getType().is(AFFECTED_BY_SMITE);
    }

}
