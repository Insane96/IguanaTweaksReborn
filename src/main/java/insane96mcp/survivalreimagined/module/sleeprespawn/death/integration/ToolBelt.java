package insane96mcp.survivalreimagined.module.sleeprespawn.death.integration;

import dev.gigaherz.toolbelt.slot.BeltExtensionSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

import java.util.List;

public class ToolBelt {
    public static void onDeath(List<ItemStack> items, Player player) {
        LazyOptional<BeltExtensionSlot> oBeltExtensionSlot = BeltExtensionSlot.get(player);
        oBeltExtensionSlot.ifPresent(beltExtensionSlot -> {
            beltExtensionSlot.getSlots().forEach(extensionSlot -> {
                items.add(extensionSlot.getContents());
                extensionSlot.setContents(ItemStack.EMPTY);
            });
        });
    }
}
