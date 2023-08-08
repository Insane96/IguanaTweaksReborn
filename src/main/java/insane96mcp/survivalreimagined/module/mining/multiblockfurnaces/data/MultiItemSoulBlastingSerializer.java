package insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.data;

import insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.crafting.MultiItemSoulBlastingRecipe;
import insane96mcp.survivalreimagined.setup.client.SRBookCategory;

public class MultiItemSoulBlastingSerializer extends AbstractMultiItemSmeltingSerializer {

    public MultiItemSoulBlastingSerializer() {
        super(MultiItemSoulBlastingRecipe::new);
    }

    @Override
    protected SRBookCategory getDefaultBookCategory() {
        return SRBookCategory.SOUL_BLAST_FURNACE_MISC;
    }

    @Override
    int getIngredientSlotsCount() {
        return 6;
    }
}