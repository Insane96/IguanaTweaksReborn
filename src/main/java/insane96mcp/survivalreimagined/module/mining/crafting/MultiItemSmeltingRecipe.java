package insane96mcp.survivalreimagined.module.mining.crafting;

import insane96mcp.survivalreimagined.module.mining.inventory.AbstractMultiBlockFurnaceMenu;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class MultiItemSmeltingRecipe implements Recipe<Container> {
    protected final RecipeType<?> type;
    protected final ResourceLocation id;
    private final CookingBookCategory category;
    protected final String group;
    protected final Ingredient[] ingredients;
    protected final ItemStack result;
    protected final float experience;
    protected final int cookingTime;

    public MultiItemSmeltingRecipe(RecipeType<?> pType, ResourceLocation pId, String pGroup, CookingBookCategory pCategory, Ingredient[] ingredients, ItemStack pResult, float pExperience, int pCookingTime) {
        this.type = pType;
        this.category = pCategory;
        this.id = pId;
        this.group = pGroup;
        this.ingredients = ingredients;
        this.result = pResult;
        this.experience = pExperience;
        this.cookingTime = pCookingTime;
    }

    @Override
    public boolean matches(Container container, Level pLevel) {
        List<Integer> checkedSlots = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            boolean ingredientMatches = false;
            for (int slot : AbstractMultiBlockFurnaceMenu.INGREDIENT_SLOTS) {
                if (checkedSlots.contains(slot))
                    continue;
                if (ingredient.test(container.getItem(slot))) {
                    checkedSlots.add(slot);
                    ingredientMatches = true;
                }
            }
            if (!ingredientMatches)
                return false;
        }
        return true;
    }

    @Override
    public ItemStack assemble(Container p_44001_, RegistryAccess p_267165_) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.addAll(List.of(this.ingredients));
        return nonnulllist;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }
    /**
     * Gets the experience of this recipe
     */
    public float getExperience() {
        return this.experience;
    }

    /**
     * Recipes with equal group are combined into one button in the recipe book
     */
    public String getGroup() {
        return this.group;
    }

    /**
     * Gets the cook time in ticks
     */
    public int getCookingTime() {
        return this.cookingTime;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public RecipeType<?> getType() {
        return this.type;
    }

    public CookingBookCategory category() {
        return this.category;
    }
}
