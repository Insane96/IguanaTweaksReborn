package insane96mcp.iguanatweaksreborn.module.experience.enchantments.integration;

import dev.gigaherz.toolbelt.slot.BeltExtensionSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

import java.util.List;

public class ToolBelt {
    public static void putItems(List<ItemStack> items, LivingEntity livingEntity) {
        LazyOptional<BeltExtensionSlot> oBeltExtensionSlot = BeltExtensionSlot.get(livingEntity);
        oBeltExtensionSlot.ifPresent(beltExtensionSlot ->
                beltExtensionSlot.getSlots().forEach(extensionSlot ->
                        items.add(extensionSlot.getContents())
                )
        );
    }
}
