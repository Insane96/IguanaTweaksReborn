package insane96mcp.survivalreimagined.module.mining.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Simple slot extension that can't have items placed in it
 */
public class DisabledSlot extends Slot {

    public DisabledSlot(Container pContainer, int pSlot, int pXPosition, int pYPosition) {
        super(pContainer, pSlot, pXPosition, pYPosition);
    }

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     */
    public boolean mayPlace(ItemStack pStack) {
        return false;
    }

    public boolean isActive() {
        return false;
    }
}
