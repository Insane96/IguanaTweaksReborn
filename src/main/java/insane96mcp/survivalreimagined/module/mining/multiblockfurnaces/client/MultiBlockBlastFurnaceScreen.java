package insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.client;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.inventory.MultiBlockBlastFurnaceMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MultiBlockBlastFurnaceScreen extends AbstractMultiBlockFurnaceScreen<MultiBlockBlastFurnaceMenu>{
    private static final ResourceLocation TEXTURE = new ResourceLocation(SurvivalReimagined.MOD_ID, "textures/gui/container/blast_furnace.png");
    public MultiBlockBlastFurnaceScreen(MultiBlockBlastFurnaceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, new MultiBlockBlastFurnaceRecipeBookComponent(), pPlayerInventory, pTitle, TEXTURE);
    }
}
