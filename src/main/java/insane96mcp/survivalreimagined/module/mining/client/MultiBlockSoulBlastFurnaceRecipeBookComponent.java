package insane96mcp.survivalreimagined.module.mining.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Set;

public class MultiBlockSoulBlastFurnaceRecipeBookComponent extends AbstractMultiBlockFurnaceRecipeBookComponent {
    private static final Component FILTER_NAME = Component.translatable("gui.recipebook.toggleRecipes.multiItemSmelting");

    protected Component getRecipeFilterName() {
        return FILTER_NAME;
    }

    protected Set<Item> getFuelItems() {
        return Set.of(Items.LAVA_BUCKET);
    }
}
