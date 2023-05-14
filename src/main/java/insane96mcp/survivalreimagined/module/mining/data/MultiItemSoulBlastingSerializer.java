package insane96mcp.survivalreimagined.module.mining.data;

import insane96mcp.survivalreimagined.module.mining.crafting.MultiItemSoulBlastingRecipe;

public class MultiItemSoulBlastingSerializer extends AbstractMultiItemSmeltingSerializer {

    public MultiItemSoulBlastingSerializer() {
        super(MultiItemSoulBlastingRecipe::new);
    }

    @Override
    int getIngredientSlotsCount() {
        return 6;
    }
}