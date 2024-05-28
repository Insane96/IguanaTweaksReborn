package insane96mcp.iguanatweaksreborn.module.experience.enchantments.integration;

import dev.gigaherz.toolbelt.belt.ToolBeltItem;
import dev.gigaherz.toolbelt.slot.BeltExtensionSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

import java.util.List;

public class ToolBelt {
    public static void putItems(List<ItemStack> items, LivingEntity livingEntity) {
        LazyOptional<BeltExtensionSlot> oBeltExtensionSlot = BeltExtensionSlot.get(livingEntity);
        oBeltExtensionSlot.ifPresent(beltExtensionSlot ->
                beltExtensionSlot.getSlots().forEach(extensionSlot -> {
                            ItemStack belt = extensionSlot.getContents();
                            if (belt.is(dev.gigaherz.toolbelt.ToolBelt.BELT.get())) {
                                belt.getCapability(ToolBeltItem.ITEM_HANDLER).ifPresent(cap -> {
                                    int slots = cap.getSlots();
                                    for (int i = 0; i < slots; i++)
                                        items.add(cap.getStackInSlot(i));
                                });
                            }
                        }
                )
        );
    }
}
