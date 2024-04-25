package insane96mcp.iguanatweaksreborn.module.experience.enchantments.integration;

import com.teamabnormals.allurement.core.AllurementConfig;
import com.teamabnormals.allurement.core.other.AllurementUtil;
import com.teamabnormals.allurement.core.registry.AllurementEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;

public class Allurement {
    public static void onExperiencePickup(Player player, ExperienceOrb xpOrb) {
        int count = AllurementUtil.getTotalEnchantmentLevel(AllurementEnchantments.ALLEVIATING.get(), player, EquipmentSlot.Type.ARMOR);
        if (count > 0) {
            float factor = AllurementConfig.COMMON.alleviatingHealingFactor.get().floatValue() * (float)count;
            float i = Math.min((float)xpOrb.value * factor, player.getMaxHealth() - player.getHealth());
            xpOrb.value -= Math.round(i / factor);
            player.heal(i);
        }
    }
}
