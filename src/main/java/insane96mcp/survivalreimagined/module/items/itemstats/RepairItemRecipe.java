package insane96mcp.survivalreimagined.module.items.itemstats;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class RepairItemRecipe extends CustomRecipe {
    protected final Ingredient itemToRepair;
    protected final Ingredient material;
    protected final int amount;
    protected final float maxRepair;
    public RepairItemRecipe(ResourceLocation pId, CraftingBookCategory pCategory, Ingredient itemToRepair, Ingredient material, int amount, float maxRepair) {
        super(pId, pCategory);
        this.itemToRepair = itemToRepair;
        this.material = material;
        this.amount = amount;
        this.maxRepair = maxRepair;
    }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level pLevel) {
        ItemStack stackToRepair = ItemStack.EMPTY;
        int amount = 0;
        for (int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            ItemStack itemInCell = craftingContainer.getItem(i);
            if (itemInCell.isEmpty())
                continue;

            if (this.itemToRepair.test(itemInCell)) {
                if (!stackToRepair.isEmpty())
                    return false;

                stackToRepair = itemInCell;
            }
            else if (this.material.test(itemInCell)) {
                amount++;
            }
        }
        return !stackToRepair.isEmpty() && amount > 0;
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer, RegistryAccess pRegistryAccess) {
        ItemStack stackToRepair = ItemStack.EMPTY;
        int amount = 0;
        ItemStack result = stackToRepair.copy();
        for (int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            ItemStack itemInCell = craftingContainer.getItem(i);
            if (itemInCell.isEmpty())
                continue;

            if (this.itemToRepair.test(itemInCell)) {
                if (!stackToRepair.isEmpty())
                    return result;

                stackToRepair = itemInCell;
            }
            else if (this.material.test(itemInCell)) {
                amount++;
            }
        }
        result = new ItemStack(stackToRepair.getItem());
        if (stackToRepair.hasCustomHoverName())
            result.setHoverName(stackToRepair.getHoverName());
        //int maxRepair = (int) (stackToRepair.getMaxDamage() * this.maxRepair);
        result.setDamageValue((int) (stackToRepair.getDamageValue() - ((float) stackToRepair.getMaxDamage() / this.amount * amount)));
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }
}
