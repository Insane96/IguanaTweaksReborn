package insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.client;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.inventory.MultiBlockSoulBlastFurnaceMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MultiBlockSoulBlastFurnaceScreen extends AbstractMultiBlockFurnaceScreen<MultiBlockSoulBlastFurnaceMenu>{
    private static final ResourceLocation TEXTURE = new ResourceLocation(SurvivalReimagined.MOD_ID, "textures/gui/container/soul_blast_furnace.png");
    public MultiBlockSoulBlastFurnaceScreen(MultiBlockSoulBlastFurnaceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, new MultiBlockSoulBlastFurnaceRecipeBookComponent(), pPlayerInventory, pTitle, TEXTURE);
    }
}
