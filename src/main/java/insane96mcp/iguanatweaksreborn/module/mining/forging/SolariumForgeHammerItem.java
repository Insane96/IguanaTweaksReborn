package insane96mcp.iguanatweaksreborn.module.mining.forging;

import insane96mcp.iguanatweaksreborn.module.items.solarium.Solarium;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import org.jetbrains.annotations.Nullable;

public class SolariumForgeHammerItem extends ForgeHammerItem {
    public SolariumForgeHammerItem(Tier tier, int useCooldown, int useDamageTaken, Properties pProperties) {
        super(tier, useCooldown, useDamageTaken, pProperties);
    }

    @Override
    public int getUseCooldown(@Nullable LivingEntity entity, ItemStack stack) {
        int cooldown = super.getUseCooldown(entity, stack);
        if (entity == null)
            return cooldown;
        return cooldown - (int)  (Solarium.getCalculatedSkyLightRatio(entity) * 10);
    }
}
