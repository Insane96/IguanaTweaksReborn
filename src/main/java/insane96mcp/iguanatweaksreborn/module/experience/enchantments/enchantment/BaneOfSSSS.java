package insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BaneOfSSSS extends BonusDamageEnchantment {
    public static final TagKey<EntityType<?>> AFFECTED_BY_BANE_OF_SSSSS = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(IguanaTweaksReborn.MOD_ID, "enchantments/bane_of_sssss"));
    public BaneOfSSSS() {
        super(Rarity.UNCOMMON, EnchantmentsFeature.ITR_WEAPONS_CATEGORY, new EquipmentSlot[]{ EquipmentSlot.MAINHAND });
    }

    @Override
    public void doPostAttack(@NotNull LivingEntity attacker, @NotNull Entity target, int lvl) {
        if (!(target instanceof LivingEntity livingentity))
            return;

        if (lvl > 0 && livingentity.getType().is(AFFECTED_BY_BANE_OF_SSSSS)) {
            int i = 20 + attacker.getRandom().nextInt(10 * lvl);
            livingentity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, i, 3));
        }
    }

    @Override
    public float getDamageBonus(LivingEntity attacker, LivingEntity target, ItemStack stack, int lvl) {
        if (!target.getType().is(AFFECTED_BY_BANE_OF_SSSSS))
            return 0f;
        return this.getDamageBonus(stack, lvl);
    }

    @Override
    public boolean isAffectedByEnchantment(LivingEntity target) {
        return target.getType().is(AFFECTED_BY_BANE_OF_SSSSS);
    }
}
