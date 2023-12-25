package insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.inventory;

import insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.block.AbstractMultiBlockFurnaceBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MultiBlockFurnaceResultSlot extends Slot {

    private final Player player;
    private int removeCount;

    public MultiBlockFurnaceResultSlot(Player pPlayer, Container pContainer, int pSlot, int pXPosition, int pYPosition) {
        super(pContainer, pSlot, pXPosition, pYPosition);
        this.player = pPlayer;
    }

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     */
    public boolean mayPlace(ItemStack pStack) {
        return false;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new stack.
     */
    public ItemStack remove(int pAmount) {
        if (this.hasItem()) {
            this.removeCount += Math.min(pAmount, this.getItem().getCount());
        }

        return super.remove(pAmount);
    }

    public void onTake(Player pPlayer, ItemStack pStack) {
        this.checkTakeAchievements(pStack);
        super.onTake(pPlayer, pStack);
    }

    /**
     * Typically increases an internal count, then calls {@code onCrafting(item)}.
     * @param pStack the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    protected void onQuickCraft(ItemStack pStack, int pAmount) {
        this.removeCount += pAmount;
        this.checkTakeAchievements(pStack);
    }

    //Ignore max item stack size
    @Override
    public int getMaxStackSize(ItemStack pStack) {
        return super.getMaxStackSize();
    }

    /**
     *
     * @param pStack the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    protected void checkTakeAchievements(ItemStack pStack) {
        pStack.onCraftedBy(this.player.level(), this.player, this.removeCount);
        if (this.player instanceof ServerPlayer && this.container instanceof AbstractMultiBlockFurnaceBlockEntity abstractMultiBlockFurnaceBlockEntity) {
            abstractMultiBlockFurnaceBlockEntity.awardUsedRecipesAndPopExperience((ServerPlayer)this.player);
        }

        this.removeCount = 0;
        net.minecraftforge.event.ForgeEventFactory.firePlayerSmeltedEvent(this.player, pStack);
    }
}
