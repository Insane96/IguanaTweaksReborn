package insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.protection;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;

public class BlastProtection extends ITRProtectionEnchantment {
    public BlastProtection() {
        super(Rarity.UNCOMMON);
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getBaseCost() {
        return 5;
    }

    @Override
    public int getCostPerLevel() {
        return 8;
    }

    @Override
    public float getDamageReductionPerLevel() {
        return 0.08f;
    }

    @Override
    public boolean isSourceReduced(DamageSource source) {
        return source.is(DamageTypeTags.IS_EXPLOSION);
    }
}
