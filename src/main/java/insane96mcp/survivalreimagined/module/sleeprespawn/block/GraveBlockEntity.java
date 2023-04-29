package insane96mcp.survivalreimagined.module.sleeprespawn.block;

import insane96mcp.survivalreimagined.module.sleeprespawn.feature.Death;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class GraveBlockEntity extends BlockEntity {
    public static final String XP_STORED_TAG = "xp_stored";
    private NonNullList<ItemStack> items = NonNullList.create();
    private int xpStored = 0;
    public GraveBlockEntity(BlockPos pos, BlockState state) {
        super(Death.GRAVE_BLOCK_ENTITY_TYPE.get(), pos, state);
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    public void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    public int getXpStored() {
        return this.xpStored;
    }

    public void setXpStored(int xpStored) {
        this.xpStored = xpStored;
    }

    @Override
    public void load(CompoundTag p_155055_) {
        super.load(p_155055_);
        this.items = NonNullList.create();
        ContainerHelper.loadAllItems(p_155055_, this.items);
        this.xpStored = p_155055_.getInt(XP_STORED_TAG);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        ContainerHelper.saveAllItems(compoundTag, this.items);
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
