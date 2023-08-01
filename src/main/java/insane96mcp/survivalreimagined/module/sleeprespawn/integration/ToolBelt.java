package insane96mcp.survivalreimagined.module.sleeprespawn.integration;

import dev.gigaherz.toolbelt.slot.BeltExtensionSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;

import java.util.List;

public class ToolBelt {
    public static void onDeath(List<ItemStack> items, Player player) {
        if (!ModList.get().isLoaded("toolbelt"))
            return;
        LazyOptional<BeltExtensionSlot> oBeltExtensionSlot = BeltExtensionSlot.get(player);
        oBeltExtensionSlot.ifPresent(beltExtensionSlot -> {
            beltExtensionSlot.getSlots().forEach(extensionSlot -> {
                items.add(extensionSlot.getContents());
                extensionSlot.setContents(ItemStack.EMPTY);
            });
        });
    }
}
