package insane96mcp.survivalreimagined.module.items.block;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.items.feature.Crate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class CrateBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
    public static final int COLUMNS = 9;
    public static final int ROWS = 1;
    public static final int CONTAINER_SIZE = COLUMNS * ROWS;
    public static final String ITEMS_TAG = "Items";
    private static final int[] SLOTS = IntStream.range(0, CONTAINER_SIZE).toArray();
    private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        protected void onOpen(Level p_155062_, BlockPos p_155063_, BlockState p_155064_) {
            CrateBlockEntity.this.playSound(p_155064_, SoundEvents.BARREL_OPEN);
            CrateBlockEntity.this.updateBlockState(p_155064_, true);
        }

        protected void onClose(Level p_155072_, BlockPos p_155073_, BlockState p_155074_) {
            CrateBlockEntity.this.playSound(p_155074_, SoundEvents.BARREL_CLOSE);
            CrateBlockEntity.this.updateBlockState(p_155074_, false);
        }

        protected void openerCountChanged(Level p_155066_, BlockPos p_155067_, BlockState p_155068_, int p_155069_, int p_155070_) {
        }

        protected boolean isOwnContainer(Player p_155060_) {
            if (p_155060_.containerMenu instanceof ChestMenu) {
                Container container = ((ChestMenu)p_155060_.containerMenu).getContainer();
                return container == CrateBlockEntity.this;
            } else {
                return false;
            }
        }
    };

    public CrateBlockEntity(BlockPos pos, BlockState state) {
        super(Crate.BLOCK_ENTITY_TYPE.get(), pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag p_187459_) {
        super.saveAdditional(p_187459_);
        if (!this.trySaveLootTable(p_187459_)) {
            ContainerHelper.saveAllItems(p_187459_, this.items);
        }
    }

    @Override
    public void load(CompoundTag p_155055_) {
        super.load(p_155055_);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(p_155055_)) {
            ContainerHelper.loadAllItems(p_155055_, this.items);
        }
    }

    @Override
    public int[] getSlotsForFace(Direction p_19238_) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int p_19235_, ItemStack stack, @Nullable Direction p_19237_) {
        return !(Block.byItem(stack.getItem()) instanceof ShulkerBoxBlock) && !(Block.byItem(stack.getItem()) instanceof CrateBlock) && stack.getItem().canFitInsideContainerItems(); // FORGE: Make shulker boxes respect Item#canFitInsideContainerItems
    }

    @Override
    public boolean canTakeItemThroughFace(int p_19239_, ItemStack p_19240_, Direction p_19241_) {
        return true;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(SurvivalReimagined.MOD_ID + ".container.crate");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new CrateMenu(id, inventory, this);
    }

    public void startOpen(Player p_58616_) {
        if (!this.remove && !p_58616_.isSpectator()) {
            this.openersCounter.incrementOpeners(p_58616_, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public void stopOpen(Player p_58614_) {
        if (!this.remove && !p_58614_.isSpectator()) {
            this.openersCounter.decrementOpeners(p_58614_, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // Will get tag from #getUpdateTag
        return ClientboundBlockEntityDataPacket.create(this);
    }

    void updateBlockState(BlockState p_58607_, boolean p_58608_) {
        this.level.setBlock(this.getBlockPos(), p_58607_.setValue(BarrelBlock.OPEN, p_58608_), 3);
    }

    void playSound(BlockState p_58601_, SoundEvent p_58602_) {
        Vec3i vec3i = p_58601_.getValue(BarrelBlock.FACING).getNormal();
        double d0 = (double)this.worldPosition.getX() + 0.5D + (double)vec3i.getX() / 2.0D;
        double d1 = (double)this.worldPosition.getY() + 0.5D + (double)vec3i.getY() / 2.0D;
        double d2 = (double)this.worldPosition.getZ() + 0.5D + (double)vec3i.getZ() / 2.0D;
        this.level.playSound(null, d0, d1, d2, p_58602_, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
    }
}
