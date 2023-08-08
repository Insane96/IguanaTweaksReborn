package insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.MultiBlockFurnaces;
import insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.crafting.AbstractMultiItemSmeltingRecipe;
import insane96mcp.survivalreimagined.setup.client.SRBookCategory;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class MultiItemSmeltingRecipeBuilder implements RecipeBuilder {
    private final RecipeCategory category;
    private final SRBookCategory bookCategory;
    @Nullable
    protected String group;
    final NonNullList<Ingredient> ingredients;
    float experience = 0f;
    float doubleOutputChance = 0f;
    private final Item result;
    protected final int cookingTime;
    @Nullable
    AbstractMultiItemSmeltingRecipe.Recycle recycle;
    private final RecipeSerializer<? extends AbstractMultiItemSmeltingRecipe> serializer;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();

    public MultiItemSmeltingRecipeBuilder(RecipeCategory pCategory, SRBookCategory pBookCategory, ItemLike pResult, NonNullList<Ingredient> pIngredients, int pCookingTime, RecipeSerializer<? extends AbstractMultiItemSmeltingRecipe> pSerializer) {
        this.category = pCategory;
        this.bookCategory = pBookCategory;
        this.result = pResult.asItem();
        this.ingredients = pIngredients;
        this.cookingTime = pCookingTime;
        this.serializer = pSerializer;
    }

    public static MultiItemSmeltingRecipeBuilder blasting(NonNullList<Ingredient> pIngredient, RecipeCategory pCategory, ItemLike pResult, int pCookingTime) {
        return new MultiItemSmeltingRecipeBuilder(pCategory, SRBookCategory.BLAST_FURNACE_MISC, pResult, pIngredient, pCookingTime, MultiBlockFurnaces.BLASTING_RECIPE_SERIALIZER.get());
    }

    public static MultiItemSmeltingRecipeBuilder soulBlasting(NonNullList<Ingredient> pIngredient, RecipeCategory pCategory, ItemLike pResult, int pCookingTime) {
        return new MultiItemSmeltingRecipeBuilder(pCategory, SRBookCategory.SOUL_BLAST_FURNACE_MISC, pResult, pIngredient, pCookingTime, MultiBlockFurnaces.SOUL_BLASTING_RECIPE_SERIALIZER.get());
    }

    @Override
    public MultiItemSmeltingRecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    @Override
    public MultiItemSmeltingRecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    public MultiItemSmeltingRecipeBuilder experience(float experience) {
        this.experience = experience;
        return this;
    }

    public MultiItemSmeltingRecipeBuilder doubleOutputChance(float doubleOutputChance) {
        this.doubleOutputChance = doubleOutputChance;
        return this;
    }

    public MultiItemSmeltingRecipeBuilder recycle(int amountAtMaxDurability) {
        return this.recycle(amountAtMaxDurability, 1f);
    }

    public MultiItemSmeltingRecipeBuilder recycle(int amountAtMaxDurability, float ratio) {
        this.recycle = new AbstractMultiItemSmeltingRecipe.Recycle(amountAtMaxDurability, ratio);
        return this;
    }

    @Override
    public Item getResult() {
        return this.result;
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        this.ensureValid(pRecipeId);
        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId)).rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(RequirementsStrategy.OR);
        pFinishedRecipeConsumer.accept(new MultiItemSmeltingRecipeBuilder.Result(pRecipeId, this.group == null ? "" : this.group, this.bookCategory, this.ingredients, this.result, this.experience, this.doubleOutputChance, this.cookingTime, this.recycle, this.advancement, pRecipeId.withPrefix("recipes/" + this.category.getFolderName() + "/"), this.serializer));
    }

    /**
     * Makes sure that this obtainable.
     */
    private void ensureValid(ResourceLocation pId) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + pId);
        }
    }

    static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final String group;
        private final SRBookCategory category;
        private final NonNullList<Ingredient> ingredients;
        private final Item result;
        private final float experience;
        private final float doubleOutputChance;
        private final int cookingTime;
        @Nullable
        private final AbstractMultiItemSmeltingRecipe.Recycle recycle;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final RecipeSerializer<? extends AbstractMultiItemSmeltingRecipe> serializer;

        public Result(ResourceLocation pId, String pGroup, SRBookCategory pCategory, NonNullList<Ingredient> pIngredients, Item pResult, float pExperience, float doubleOutputChance, int pCookingTime, @Nullable AbstractMultiItemSmeltingRecipe.Recycle recycle, Advancement.Builder pAdvancement, ResourceLocation pAdvancementId, RecipeSerializer<? extends AbstractMultiItemSmeltingRecipe> pSerializer) {
            this.id = pId;
            this.group = pGroup;
            this.category = pCategory;
            this.ingredients = pIngredients;
            this.result = pResult;
            this.experience = pExperience;
            this.doubleOutputChance = doubleOutputChance;
            this.cookingTime = pCookingTime;
            this.recycle = recycle;
            this.advancement = pAdvancement;
            this.advancementId = pAdvancementId;
            this.serializer = pSerializer;
        }

        public void serializeRecipeData(JsonObject pJson) {
            if (!this.group.isEmpty()) {
                pJson.addProperty("group", this.group);
            }

            pJson.addProperty("category", this.category.getSerializedName());
            JsonArray ingredientsArray = new JsonArray();
            for (Ingredient ingredient : this.ingredients) {
                if (!ingredient.isEmpty())
                    ingredientsArray.add(ingredient.toJson());
            }
            pJson.add("ingredients", ingredientsArray);
            pJson.addProperty("result", ForgeRegistries.ITEMS.getKey(this.result).toString());
            if (experience > 0f)
                pJson.addProperty("experience", this.experience);
            if (doubleOutputChance > 0f)
                pJson.addProperty("double_output_chance", this.doubleOutputChance);
            if (this.recycle != null)
                pJson.add("recycle", this.recycle.toJson());
            pJson.addProperty("cookingtime", this.cookingTime);
        }

        public RecipeSerializer<?> getType() {
            return this.serializer;
        }

        /**
         * Gets the ID for the recipe.
         */
        public ResourceLocation getId() {
            return this.id;
        }

        /**
         * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
         */
        @javax.annotation.Nullable
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @javax.annotation.Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}
