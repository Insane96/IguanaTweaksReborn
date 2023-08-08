package insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.block;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.MultiBlockFurnaces;
import insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.inventory.MultiBlockSoulBlastFurnaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class MultiBlockSoulBlastFurnaceBlockEntity extends AbstractMultiBlockFurnaceBlockEntity {
    public MultiBlockSoulBlastFurnaceBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(MultiBlockFurnaces.SOUL_BLAST_FURNACE_BLOCK_ENTITY_TYPE.get(), pPos, pBlockState, MultiBlockFurnaces.SOUL_BLASTING_RECIPE_TYPE.get());
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(SurvivalReimagined.MOD_ID + ".container.soul_blast_furnace");
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new MultiBlockSoulBlastFurnaceMenu(pContainerId, pInventory, this, this.dataAccess);
    }

    @Override
    public int[] getIngredientSlots() {
        return  MultiBlockSoulBlastFurnaceMenu.getIngredientSlots();
    }

    protected int getBurnDuration(ItemStack pFuel) {
        return super.getBurnDuration(pFuel) / 4;
    }
}
