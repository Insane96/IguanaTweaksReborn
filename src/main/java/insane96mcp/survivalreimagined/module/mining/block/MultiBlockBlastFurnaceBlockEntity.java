package insane96mcp.survivalreimagined.module.mining.block;

import insane96mcp.survivalreimagined.module.mining.feature.SoulSteel;
import insane96mcp.survivalreimagined.module.mining.inventory.MultiBlockBlastFurnaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class MultiBlockBlastFurnaceBlockEntity extends AbstractMultiBlockFurnaceBlockEntity {
    public MultiBlockBlastFurnaceBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(SoulSteel.BLAST_FURNACE_BLOCK_ENTITY_TYPE.get(), pPos, pBlockState, SoulSteel.RECIPE_TYPE.get());
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.blast_furnace");
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new MultiBlockBlastFurnaceMenu(pContainerId, pInventory, this, this.dataAccess);
    }

    protected int getBurnDuration(ItemStack pFuel) {
        return super.getBurnDuration(pFuel) / 2;
    }
}
