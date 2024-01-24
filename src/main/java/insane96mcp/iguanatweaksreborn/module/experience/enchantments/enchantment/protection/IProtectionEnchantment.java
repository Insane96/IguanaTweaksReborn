package insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.protection;

import net.minecraft.world.damagesource.DamageSource;

public interface IProtectionEnchantment {
    float getDamageReduction(int lvl);
    boolean isSourceReduced(DamageSource source);
}
