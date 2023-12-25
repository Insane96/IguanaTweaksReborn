package insane96mcp.iguanatweaksreborn.module.mining.forging;

import insane96mcp.iguanatweaksreborn.setup.client.SRBookCategory;
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
    final Ingredient ingredient;
    final int ingredientAmount;
    final Ingredient gear;
    private final ItemStack result;
    protected final int smashesRequired;
    protected final float experience;

    public ForgeRecipe(ResourceLocation pId, SRBookCategory pCategory, Ingredient ingredient, int ingredientAmount, Ingredient gear, ItemStack pResult, int smashesRequired, float experience) {
        this.type = Forging.FORGE_RECIPE_TYPE.get();
        this.category = pCategory;
        this.id = pId;
        this.ingredient = ingredient;
        this.ingredientAmount = ingredientAmount;
        this.gear = gear;
        this.result = pResult;
        this.smashesRequired = smashesRequired;
        this.experience = experience;
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
        return Forging.FORGE_RECIPE_SERIALIZER.get();
    }

    /**
     * Gets the times required to smash the forge with the hammer to craft the item
     */
    public int getSmashesRequired() {
        return this.smashesRequired;
    }

    public float getExperience() {
        return this.experience;
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

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(Forging.FORGE.item().get());
    }
}
