package insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.protection;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;

public class OverallProtection extends ITRProtectionEnchantment {
    public OverallProtection() {
        super(Rarity.VERY_RARE);
    }

    @Override
    public int getBaseCost() {
        return 20;
    }
    @Override
    public int getCostPerLevel() {
        return 40;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public float getDamageReductionPerLevel() {
        return 0.06f;
    }

    @Override
    public boolean isSourceReduced(DamageSource source) {
        return !source.is(DamageTypeTags.BYPASSES_ARMOR);
    }
}
