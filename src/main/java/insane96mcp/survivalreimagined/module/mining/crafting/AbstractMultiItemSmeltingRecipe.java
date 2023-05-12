package insane96mcp.survivalreimagined.module.mining.crafting;

import insane96mcp.survivalreimagined.module.mining.feature.MultiBlockFurnaces;
import insane96mcp.survivalreimagined.module.mining.inventory.AbstractMultiBlockFurnaceMenu;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMultiItemSmeltingRecipe implements Recipe<Container> {
    protected final RecipeType<?> type;
    protected final ResourceLocation id;
    private final CookingBookCategory category;
    protected final String group;
    final NonNullList<Ingredient> ingredients;
    final float experience;
    final float doubleOutputChance;
    private final ItemStack result;
    protected final int cookingTime;

    private static final RandomSource RANDOM = RandomSource.create();

    public AbstractMultiItemSmeltingRecipe(RecipeType<?> type, ResourceLocation pId, String pGroup, CookingBookCategory pCategory, NonNullList<Ingredient> ingredients, ItemStack pResult, float doubleOutputChance, float pExperience, int pCookingTime) {
        this.type = type;
        this.category = pCategory;
        this.id = pId;
        this.group = pGroup;
        this.ingredients = NonNullList.withSize(4, Ingredient.EMPTY);
        for (int i = 0; i < ingredients.size(); i++) {
            this.ingredients.set(i, ingredients.get(i));
        }
        this.result = pResult;
        this.doubleOutputChance = doubleOutputChance;
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
                    break;
                }
            }
            if (!ingredientMatches)
                return false;
        }
        return true;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        ItemStack stack = this.result.copy();
        if (RANDOM.nextFloat() < doubleOutputChance)
            stack.setCount(stack.getCount() + 1);
        return stack;
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
        nonnulllist.addAll(this.ingredients);
        return nonnulllist;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MultiBlockFurnaces.RECIPE_SERIALIZER.get();
    }

    public float getDoubleOutputChance() {
        return this.doubleOutputChance;
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
