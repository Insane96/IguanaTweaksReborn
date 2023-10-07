package insane96mcp.survivalreimagined.module.experience.enchanting;

import net.minecraft.Util;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SREnchantingTableMenu extends AbstractContainerMenu {
    public static final int ITEM_SLOT = 0;
    public static final int CATALYST_SLOT = 1;
    public static final int SLOT_COUNT = 2;
    private static final int INV_SLOT_START = CATALYST_SLOT + 1;
    private static final int INV_SLOT_END = INV_SLOT_START + 27;
    private static final int USE_ROW_SLOT_START = INV_SLOT_END;
    private static final int USE_ROW_SLOT_END = USE_ROW_SLOT_START + 9;
    private final Container container;
    private final ContainerLevelAccess access;
    protected final Level level;
    //Given by tool's enchanting power and bookshelves
    public DataSlot maxCost = DataSlot.standalone();
    public DataSlot enchantCost = DataSlot.standalone();

    public SREnchantingTableMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, new SimpleContainer(SLOT_COUNT), ContainerLevelAccess.NULL);
    }

    protected SREnchantingTableMenu(int pContainerId, Inventory pPlayerInventory, Container pContainer, ContainerLevelAccess access) {
        super(EnchantingFeature.ENCHANTING_TABLE_MENU_TYPE.get(), pContainerId);
        checkContainerSize(pContainer, SLOT_COUNT);
        this.container = pContainer;
        this.access = access;
        this.level = pPlayerInventory.player.level();
        this.addSlot(new Slot(pContainer, ITEM_SLOT, 10, 18) {
            public int getMaxStackSize() {
                return 1;
            }
        });
        this.addSlot(new Slot(pContainer, CATALYST_SLOT, 28, 18) {
            public boolean mayPlace(ItemStack stack) {
                return stack.is(net.minecraftforge.common.Tags.Items.ENCHANTING_FUELS);
            }
        });

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(pPlayerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(pPlayerInventory, k, 8 + k * 18, 142));
        }
        this.addDataSlot(this.maxCost);
        this.addDataSlot(this.enchantCost);
    }

    @Override
    public boolean clickMenuButton(Player player, int pId) {
        if (pId != 0) {
            Util.logAndPauseIfInIde(player.getName() + " pressed invalid button id: " + pId);
            return false;
        }
        this.access.execute((level, blockPos) -> {

        });
        this.broadcastChanges();
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slotClicked = this.slots.get(pIndex);
        if (!slotClicked.hasItem())
            return itemstack;

        ItemStack itemInSlot = slotClicked.getItem();
        itemstack = itemInSlot.copy();
        if (pIndex == ITEM_SLOT) {
            if (!this.moveItemStackTo(itemInSlot, INV_SLOT_START, USE_ROW_SLOT_END, true))
                return ItemStack.EMPTY;
        }
        else {
            if (this.slots.get(ITEM_SLOT).hasItem() || !this.slots.get(ITEM_SLOT).mayPlace(itemInSlot))
                return ItemStack.EMPTY;

            ItemStack itemstack2 = itemInSlot.copyWithCount(1);
            itemInSlot.shrink(1);
            this.slots.get(ITEM_SLOT).setByPlayer(itemstack2);
        }

        if (itemInSlot.isEmpty())
            slotClicked.setByPlayer(ItemStack.EMPTY);
        else
            slotClicked.setChanged();

        if (itemInSlot.getCount() == itemstack.getCount())
            return ItemStack.EMPTY;

        slotClicked.onTake(pPlayer, itemInSlot);

        return itemstack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return this.container.stillValid(pPlayer);
    }
}
