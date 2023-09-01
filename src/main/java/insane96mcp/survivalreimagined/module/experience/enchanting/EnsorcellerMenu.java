package insane96mcp.survivalreimagined.module.experience.enchanting;

import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

import java.util.List;

public class EnsorcellerMenu extends AbstractContainerMenu {
    public static final int ITEM_SLOT = 0;
    public static final int SLOT_COUNT = 1;
    private static final int INV_SLOT_START = ITEM_SLOT + 1;
    private static final int INV_SLOT_END = INV_SLOT_START + 27;
    private static final int USE_ROW_SLOT_START = INV_SLOT_END;
    private static final int USE_ROW_SLOT_END = USE_ROW_SLOT_START + 9;
    private final Container container;
    private final ContainerData data;
    private final ContainerLevelAccess access;
    protected final Level level;
    public static final int MAX_STEPS = 15;
    public static final int LVL_ON_JACKPOT = 20;
    public DataSlot rollCost = DataSlot.standalone();
    public DataSlot enchantmentSeed = DataSlot.standalone();
    private final RandomSource random = RandomSource.create();

    public EnsorcellerMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(EnsorcellerBlockEntity.DATA_COUNT), ContainerLevelAccess.NULL);
    }

    protected EnsorcellerMenu(int pContainerId, Inventory pPlayerInventory, Container pContainer, ContainerData pData, ContainerLevelAccess access) {
        super(EnchantingFeature.ENSORCELLER_MENU_TYPE.get(), pContainerId);
        checkContainerSize(pContainer, SLOT_COUNT);
        checkContainerDataCount(pData, EnsorcellerBlockEntity.DATA_COUNT);
        this.container = pContainer;
        this.data = pData;
        this.access = access;
        this.level = pPlayerInventory.player.level();
        this.addSlot(new Slot(pContainer, ITEM_SLOT, 36, 16) {
            public boolean mayPlace(ItemStack stack) {
                return true;
            }
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public void setChanged() {
                super.setChanged();
                EnsorcellerMenu.this.slotsChanged(this.container);
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
        this.addDataSlots(pData);
        this.addDataSlot(this.enchantmentSeed).set(pPlayerInventory.player.getEnchantmentSeed());
        this.addDataSlot(this.rollCost);
        this.updateRollCost();
    }

    @Override
    public void slotsChanged(Container pContainer) {
        this.updateRollCost();
        this.updateCanEnchant();
        super.slotsChanged(pContainer);
    }

    @Override
    public boolean clickMenuButton(Player player, int pId) {
        //Roll and enchant buttons
        if (pId < 0 || pId > 1) {
            Util.logAndPauseIfInIde(player.getName() + " pressed invalid button id: " + pId);
            return false;
        }

        //Roll
        if (pId == 0) {
            if (player.experienceLevel < this.rollCost.get() && !player.getAbilities().instabuild
                    || this.getSteps() == MAX_STEPS
                    || this.rollCost.get() <= 0)
                return false;

            //This is executed only server side
            this.access.execute((level, blockPos) -> {
                this.incrementSteps((int) (this.level.random.triangle(3, 2) + 1) * this.rollCost.get());
                this.incrementLevelsUsed();
                if (!player.getAbilities().instabuild)
                    ((ServerPlayer)player).setExperienceLevels(player.experienceLevel - this.rollCost.get());
                if (this.getSteps() > MAX_STEPS) {
                    level.playSound(null, blockPos, SoundEvents.RESPAWN_ANCHOR_DEPLETE.get(), SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.1F + 0.8F);
                    this.setSteps(0);
                    this.resetLevelsUsed();
                }
                else if (this.getSteps() == MAX_STEPS) {
                    level.playSound(null, blockPos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0F, 0.6F);
                }
                else {
                    level.playSound(null, blockPos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.1F + 0.8F + this.getSteps() / 20f);
                }
            });
        }
        //Enchant
        else if (this.canEnchant()) {
            //This is executed only server side
            this.access.execute((level, blockPos) -> {
                ItemStack enchantableItem = this.container.getItem(ITEM_SLOT);
                List<EnchantmentInstance> enchantments = getEnchantmentList(this.random, this.getEnchantingSeed(), enchantableItem, this.getSteps() == MAX_STEPS ? LVL_ON_JACKPOT : this.getSteps());
                if (!enchantments.isEmpty()) {
                    player.onEnchantmentPerformed(enchantableItem, 0);

                    for (EnchantmentInstance enchantmentInstance : enchantments) {
                        enchantableItem.enchant(enchantmentInstance.enchantment, enchantmentInstance.level);
                    }
                    if (EnchantingFeature.ensorcellerNoMerge)
                        enchantableItem.getOrCreateTag().putBoolean(EnsorcellerBlock.CANNOT_MERGE_TAG, true);

                    player.awardStat(Stats.ENCHANT_ITEM);
                    if (player instanceof ServerPlayer)
                        CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer) player, enchantableItem, this.getLevelsUsed());

                    this.container.setChanged();
                    this.setSteps(0);
                    this.resetLevelsUsed();
                    this.slotsChanged(this.container);
                    level.playSound(null, blockPos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.25F);
                    this.rerollEnchantingSeed();
                }
            });
        }
        this.updateRollCost();
        this.updateCanEnchant();
        this.broadcastChanges();
        return true;
    }

    public static List<EnchantmentInstance> getEnchantmentList(RandomSource source, long seed, ItemStack pStack, int xpLvl) {
        source.setSeed(seed);
        return EnchantmentHelper.selectEnchantment(source, pStack, xpLvl,false);
    }

    private void updateRollCost() {
        this.access.execute((level, blockPos) -> {
            this.rollCost.set(calculateRollCost(level, blockPos));
        });
    }

    public static int calculateRollCost(Level level, BlockPos pos) {
        int dayTime = (int) (level.getDayTime() % 24000L);
        if (level.getBrightness(LightLayer.SKY, pos) <= 10 || (dayTime >= 12786 && dayTime <= 23216) || level.isThundering())
            return 0;

        int cost = 3;
        if (dayTime >= 4200 && dayTime < 7800)
            cost = 1;
        else if (dayTime >= 1200 && dayTime < 10800)
            cost = 2;

        if (level.isRaining())
            cost++;
        return cost > 3 ? 0 : cost;
    }

    private void updateCanEnchant() {
        ItemStack stack = this.container.getItem(ITEM_SLOT);
        List<EnchantmentInstance> enchantments = getEnchantmentList(this.random, this.getEnchantingSeed(), stack, this.getSteps() == MAX_STEPS ? LVL_ON_JACKPOT : this.getSteps());
        this.setCanEnchant(!stack.isEmpty() && stack.isEnchantable() && !stack.is(Items.BOOK) && this.getSteps() > 0 && !enchantments.isEmpty());
    }

    public int getSteps() {
        return this.data.get(EnsorcellerBlockEntity.DATA_STEPS);
    }

    public void setSteps(int steps) {
        this.data.set(EnsorcellerBlockEntity.DATA_STEPS, steps);
    }

    public void incrementSteps(int steps) {
        setSteps(getSteps() + steps);
    }

    public int getLevelsUsed() {
        return this.data.get(EnsorcellerBlockEntity.DATA_LEVELS_USED);
    }

    public void incrementLevelsUsed() {
        this.data.set(EnsorcellerBlockEntity.DATA_LEVELS_USED, this.data.get(EnsorcellerBlockEntity.DATA_LEVELS_USED) + this.rollCost.get());
    }

    public void resetLevelsUsed() {
        this.data.set(EnsorcellerBlockEntity.DATA_LEVELS_USED, 0);
    }

    public boolean canEnchant() {
        return this.data.get(EnsorcellerBlockEntity.DATA_CAN_ENCHANT) == 1;
    }

    public void setCanEnchant(boolean canEnchant) {
        this.data.set(EnsorcellerBlockEntity.DATA_CAN_ENCHANT, canEnchant ? 1 : 0);
    }

    public void rerollEnchantingSeed() { this.data.set(EnsorcellerBlockEntity.DATA_ENCHANTING_SEED, this.level.random.nextInt()); }
    public int getEnchantingSeed() {
        return this.data.get(EnsorcellerBlockEntity.DATA_ENCHANTING_SEED);
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
