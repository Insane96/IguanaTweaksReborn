package insane96mcp.survivalreimagined.module.experience.enchanting;

import insane96mcp.survivalreimagined.module.experience.GlobalExperience;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class EnsorcellerBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    public static final int DATA_COUNT = 3;
    public static final int DATA_STEPS = 0;
    public static final int DATA_LEVELS_USED = 1;
    public static final int DATA_CAN_ENCHANT = 2;
    protected NonNullList<ItemStack> items = NonNullList.withSize(EnsorcellerMenu.SLOT_COUNT, ItemStack.EMPTY);
    public int steps;
    public int levelsUsed;
    public boolean canEnchant;

    protected final ContainerData dataAccess = new ContainerData() {
        public int get(int dataId) {
            return switch (dataId) {
                case DATA_STEPS -> EnsorcellerBlockEntity.this.steps;
                case DATA_LEVELS_USED -> EnsorcellerBlockEntity.this.levelsUsed;
                case DATA_CAN_ENCHANT -> EnsorcellerBlockEntity.this.canEnchant ? 1 : 0;
                default -> 0;
            };
        }

        public void set(int dataId, int data) {
            switch (dataId) {
                case DATA_STEPS -> EnsorcellerBlockEntity.this.steps = data;
                case DATA_LEVELS_USED -> EnsorcellerBlockEntity.this.levelsUsed = data;
                case DATA_CAN_ENCHANT -> EnsorcellerBlockEntity.this.canEnchant = data == 1;
            }

        }

        public int getCount() {
            return DATA_COUNT;
        }
    };

    protected EnsorcellerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(EnchantingFeature.ENSORCELLER_BLOCK_ENTITY_TYPE.get(), pPos, pBlockState);
    }

    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(pTag, this.items);
        this.steps = pTag.getInt("Steps");
        this.levelsUsed = pTag.getInt("RollsPerformed");
        this.canEnchant = pTag.getBoolean("CanEnchant");
    }

    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt("Steps", this.steps);
        pTag.putInt("RollsPerformed", this.levelsUsed);
        pTag.putBoolean("CanEnchant", this.canEnchant);
        ContainerHelper.saveAllItems(pTag, this.items);
    }

    @Override
    public int[] getSlotsForFace(Direction pSide) {
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return false;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.enchant");
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new EnsorcellerMenu(pContainerId, pInventory, this, this.dataAccess, ContainerLevelAccess.create(this.level, this.worldPosition));
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return this.items.get(pSlot);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return ContainerHelper.removeItem(this.items, pSlot, pAmount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return ContainerHelper.takeItem(this.items, pSlot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        ItemStack itemInSlot = this.items.get(slot);
        this.canEnchant = !itemInSlot.isEmpty() && itemInSlot.isEnchantable() && this.steps > 0;
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return Container.stillValidBlockEntity(this, pPlayer);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    public void dropExperience(Level level) {
        if (this.levelsUsed > 0) {
            ExperienceOrb experienceOrb = EntityType.EXPERIENCE_ORB.create(level);
            experienceOrb.setPos(this.getBlockPos().getX() + 0.5d, this.getBlockPos().getY() + 0.5d, this.getBlockPos().getZ() + 0.5d);
            experienceOrb.getPersistentData().putBoolean(GlobalExperience.XP_PROCESSED, true);
            experienceOrb.value = this.levelsUsed * 5;
            level.addFreshEntity(experienceOrb);
        }
    }

    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
