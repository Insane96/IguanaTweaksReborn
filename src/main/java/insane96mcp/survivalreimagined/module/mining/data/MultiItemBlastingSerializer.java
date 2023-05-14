package insane96mcp.survivalreimagined.module.mining.data;

import insane96mcp.survivalreimagined.module.mining.crafting.MultiItemBlastingRecipe;

public class MultiItemBlastingSerializer extends AbstractMultiItemSmeltingSerializer {

    public MultiItemBlastingSerializer() {
        super(MultiItemBlastingRecipe::new);
    }

    @Override
    int getIngredientSlotsCount() {
        return 4;
    }
}