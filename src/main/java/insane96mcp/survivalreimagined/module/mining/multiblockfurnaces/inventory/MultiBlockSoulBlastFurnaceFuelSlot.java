package insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MultiBlockSoulBlastFurnaceFuelSlot extends MultiBlockFurnaceFuelSlot {
    public MultiBlockSoulBlastFurnaceFuelSlot(AbstractMultiBlockFurnaceMenu pFurnaceMenu, Container pContainer, int pSlot, int pX, int pY) {
        super(pFurnaceMenu, pContainer, pSlot, pX, pY);
    }

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     */
    public boolean mayPlace(ItemStack pStack) {
        return pStack.is(Items.LAVA_BUCKET) || isBucket(pStack);
    }

    public static boolean isBucket(ItemStack pStack) {
        return pStack.is(Items.BUCKET);
    }
}
