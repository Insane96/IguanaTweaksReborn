package insane96mcp.survivalreimagined.module.sleeprespawn.block;

import insane96mcp.survivalreimagined.module.sleeprespawn.feature.Death;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GraveBlockEntity extends BlockEntity {
    public static final String ITEMS_TAG = "items";
    public static final String XP_STORED_TAG = "xp_stored";
    private List<ItemStack> items = new ArrayList<>();
    private int xpStored = 0;
    public GraveBlockEntity(BlockPos pos, BlockState state) {
        super(Death.GRAVE_BLOCK_ENTITY_TYPE.get(), pos, state);
    }

    public List<ItemStack> getItems() {
        return this.items;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
        this.setChanged();
    }

    public int getXpStored() {
        return this.xpStored;
    }

    public void setXpStored(int xpStored) {
        this.xpStored = xpStored;
        this.setChanged();
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        ListTag itemsList = compoundTag.getList(ITEMS_TAG, 10);

        for(int i = 0; i < itemsList.size(); ++i) {
            CompoundTag itemStackTag = itemsList.getCompound(i);
            this.items.add(ItemStack.of(itemStackTag));
        }
        this.xpStored = compoundTag.getInt(XP_STORED_TAG);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        ListTag itemsList = new ListTag();
        for (ItemStack itemStack : this.items) {
            CompoundTag itemStackTag = new CompoundTag();
            itemStack.save(itemStackTag);
            itemsList.add(itemStackTag);
        }
        compoundTag.put(ITEMS_TAG, itemsList);
        compoundTag.putInt(XP_STORED_TAG, xpStored);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // Will get tag from #getUpdateTag
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return super.getUpdateTag();
    }
}
