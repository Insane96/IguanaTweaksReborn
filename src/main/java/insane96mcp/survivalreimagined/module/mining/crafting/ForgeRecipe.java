package insane96mcp.survivalreimagined.module.mining.crafting;

import insane96mcp.survivalreimagined.module.mining.feature.Forging;
import insane96mcp.survivalreimagined.module.mining.inventory.ForgeMenu;
import insane96mcp.survivalreimagined.setup.client.SRBookCategory;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class ForgeRecipe implements Recipe<Container> {
    protected final RecipeType<?> type;
    protected final ResourceLocation id;
    private final SRBookCategory category;
    final Ingredient gear;
    final Ingredient ingredient;
    final int ingredientAmount;
    private final ItemStack result;
    protected final int smashesRequired;

    public ForgeRecipe(ResourceLocation pId, SRBookCategory pCategory, Ingredient ingredient, int ingredientAmount, Ingredient gear, ItemStack pResult, int smashesRequired) {
        this.type = Forging.FORGE_RECIPE_TYPE.get();
        this.category = pCategory;
        this.id = pId;
        this.gear = gear;
        this.ingredient = ingredient;
        this.ingredientAmount = ingredientAmount;
        this.result = pResult;
        this.smashesRequired = smashesRequired;
    }

    @Override
    public boolean matches(Container container, Level pLevel) {
        return this.ingredient.test(container.getItem(ForgeMenu.INGREDIENT_SLOT))
                && container.getItem(ForgeMenu.INGREDIENT_SLOT).getCount() >= this.ingredientAmount
                && this.gear.test(container.getItem(ForgeMenu.GEAR_SLOT));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.add(this.ingredient);
        nonnulllist.add(this.gear);
        return nonnulllist;
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public int getIngredientAmount() {
        return this.ingredientAmount;
    }

    public Ingredient getGear() {
        return this.gear;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    /**
     * Gets the times required to smash the anvil with the hammer to craft the item
     */
    public int getSmashesRequired() {
        return this.smashesRequired;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public RecipeType<?> getType() {
        return this.type;
    }

    public SRBookCategory category() {
        return this.category;
    }
}
