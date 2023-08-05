package insane96mcp.survivalreimagined.module.experience.feature.enchantingfeature;

import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import java.util.List;

public class EnsorcellerMenu extends AbstractContainerMenu {
    private final Container enchantSlots = new SimpleContainer(1) {
        /**
         * For block entities, ensures the chunk containing the block entity is saved to disk later - the game won't think
         * it hasn't changed and skip it.
         */
        public void setChanged() {
            super.setChanged();
            EnsorcellerMenu.this.slotsChanged(this);
        }
    };
    private final ContainerLevelAccess access;
    private final RandomSource random = RandomSource.create();
    public int rollPerformed;
    public int steps;
    public static final int MAX_STEPS = 12;
    public static final int LVL_ON_JACKPOT = 15;
    public boolean canEnchant;

    public EnsorcellerMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, ContainerLevelAccess.NULL);
    }

    protected EnsorcellerMenu(int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
        super(EnchantingFeature.ENSORCELLER_MENU_TYPE.get(), pContainerId);
        this.access = pAccess;
        this.addSlot(new Slot(this.enchantSlots, 0, 26, 15) {
            public boolean mayPlace(ItemStack p_39508_) {
                return true;
            }
            public int getMaxStackSize() {
                return 1;
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
        this.addDataSlot(DataSlot.standalone());
    }

    @Override
    public void slotsChanged(Container pContainer) {
        if (pContainer != this.enchantSlots)
            return;

        ItemStack itemstack = pContainer.getItem(0);
        if (itemstack.isEmpty() || !itemstack.isEnchantable()) {
            this.canEnchant = false;
            return;
        }

        this.access.execute((level, blockPos) -> {
            this.canEnchant = true;
            this.broadcastChanges();
        });
    }

    @Override
    public boolean clickMenuButton(Player player, int pId) {
        //Roll and enchant buttons
        if (pId < 0 || pId > 1) {
            Util.logAndPauseIfInIde(player.getName() + " pressed invalid button id: " + pId);
            return false;
        }

        this.access.execute((level, blockPos) -> {
            //Roll
            if (pId == 0) {
                if (player.experienceLevel <= 0 && !player.getAbilities().instabuild)
                    return;

                this.steps += this.random.nextInt(6) + 1;
                player.experienceLevel -= 1;
                if (this.steps > MAX_STEPS) {
                    level.playSound(null, blockPos, SoundEvents.RESPAWN_ANCHOR_DEPLETE.get(), SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.1F + 0.8F);
                    this.steps = 0;
                }
            }
            //Enchant
            else if (this.steps > 0) {
                ItemStack enchantableItem = this.enchantSlots.getItem(0);
                if (enchantableItem.isEmpty())
                    return;

                ItemStack result = enchantableItem;
                List<EnchantmentInstance> enchantments = this.getEnchantmentList(enchantableItem, this.steps == MAX_STEPS ? LVL_ON_JACKPOT : this.steps);
                if (!enchantments.isEmpty()) {
                    player.onEnchantmentPerformed(enchantableItem, 0);
                    boolean isBook = enchantableItem.is(Items.BOOK);
                    if (isBook) {
                        result = new ItemStack(Items.ENCHANTED_BOOK);
                        CompoundTag itemTag = enchantableItem.getTag();
                        if (itemTag != null)
                            result.setTag(itemTag);

                        this.enchantSlots.setItem(0, result);
                    }

                    for (EnchantmentInstance enchantmentInstance : enchantments) {
                        if (isBook)
                            EnchantedBookItem.addEnchantment(result, enchantmentInstance);
                        else
                            result.enchant(enchantmentInstance.enchantment, enchantmentInstance.level);
                    }

                    player.awardStat(Stats.ENCHANT_ITEM);
                    if (player instanceof ServerPlayer)
                        CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer)player, result, this.rollPerformed);

                    this.enchantSlots.setChanged();
                    this.steps = 0;
                    this.slotsChanged(this.enchantSlots);
                    level.playSound(null, blockPos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.1F + 1.25F);
                }
            }
            this.broadcastChanges();
        });
        return true;
    }

    private List<EnchantmentInstance> getEnchantmentList(ItemStack pStack, int pLevel) {
        List<EnchantmentInstance> list = EnchantmentHelper.selectEnchantment(this.random, pStack, pLevel, false);
        if (pStack.is(Items.BOOK) && list.size() > 1) {
            list.remove(this.random.nextInt(list.size()));
        }

        return list;
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (!slot.hasItem())
            return itemstack;

        ItemStack itemInSlot = slot.getItem();
        itemstack = itemInSlot.copy();
        if (pIndex == 0) {
            if (!this.moveItemStackTo(itemInSlot, 1, 37, true))
                return ItemStack.EMPTY;
        }
        else {
            if (this.slots.get(0).hasItem() || !this.slots.get(0).mayPlace(itemInSlot))
                return ItemStack.EMPTY;

            ItemStack itemstack2 = itemInSlot.copyWithCount(1);
            itemInSlot.shrink(1);
            this.slots.get(0).setByPlayer(itemstack2);
        }

        if (itemInSlot.isEmpty())
            slot.setByPlayer(ItemStack.EMPTY);
        else
            slot.setChanged();

        if (itemInSlot.getCount() == itemstack.getCount())
            return ItemStack.EMPTY;

        slot.onTake(pPlayer, itemInSlot);

        return itemstack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(this.access, pPlayer, EnchantingFeature.ENSORCELLER.block().get());
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.access.execute((level, blockPos) -> this.clearContainer(pPlayer, this.enchantSlots));
    }
}
