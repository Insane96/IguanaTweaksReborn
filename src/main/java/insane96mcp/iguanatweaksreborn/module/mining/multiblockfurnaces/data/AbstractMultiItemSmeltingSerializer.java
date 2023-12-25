package insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.crafting.AbstractMultiItemSmeltingRecipe;
import insane96mcp.iguanatweaksreborn.setup.client.SRBookCategory;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class AbstractMultiItemSmeltingSerializer implements RecipeSerializer<AbstractMultiItemSmeltingRecipe> {
    private final CookieBaker<AbstractMultiItemSmeltingRecipe> factory;

    public AbstractMultiItemSmeltingSerializer(CookieBaker<AbstractMultiItemSmeltingRecipe> pFactory) {
        this.factory = pFactory;
    }

    public AbstractMultiItemSmeltingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
        String group = GsonHelper.getAsString(pJson, "group", "");
        SRBookCategory category = SRBookCategory.CODEC.byName(GsonHelper.getAsString(pJson, "category", null), this.getDefaultBookCategory());
        NonNullList<Ingredient> ingredients = itemsFromJson(GsonHelper.getAsJsonArray(pJson, "ingredients"));
        if (ingredients.isEmpty()) {
            throw new JsonParseException("No ingredients for shapeless recipe");
        }
        else if (ingredients.size() > getIngredientSlotsCount()) {
            throw new JsonParseException("Too many ingredients for multi item smelting recipe. The maximum is %d".formatted(getIngredientSlotsCount()));
        }
        //Forge: Check if primitive string to keep vanilla or an object which can contain a count field.
        if (!pJson.has("result")) throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
        ItemStack result;
        if (pJson.get("result").isJsonObject()) result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "result"));
        else {
            String s1 = GsonHelper.getAsString(pJson, "result");
            ResourceLocation resourcelocation = new ResourceLocation(s1);
            result = new ItemStack(ForgeRegistries.ITEMS.getValue(resourcelocation));
        }
        float outputIncrease = GsonHelper.getAsFloat(pJson, "output_increase", 0f);
        float experience = GsonHelper.getAsFloat(pJson, "experience", 0.0F);
        int cookingTime = GsonHelper.getAsInt(pJson, "cookingtime");
        AbstractMultiItemSmeltingRecipe.Recycle recycle = null;
        if (pJson.has("recycle")) {
            if (ingredients.size() > 1)
                throw new JsonParseException("Too many ingredients for multi item smelting recipe. The maximum is 1 when recycling is present");
            recycle = AbstractMultiItemSmeltingRecipe.Recycle.fromJson(pJson.getAsJsonObject("recycle"));
        }
        return this.factory.create(pRecipeId, group, category, ingredients, result, outputIncrease, experience, cookingTime, recycle);
    }

    protected abstract SRBookCategory getDefaultBookCategory();

    private static NonNullList<Ingredient> itemsFromJson(JsonArray pIngredientArray) {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();

        for(int i = 0; i < pIngredientArray.size(); ++i) {
            Ingredient ingredient = Ingredient.fromJson(pIngredientArray.get(i));
            nonnulllist.add(ingredient);
        }

        return nonnulllist;
    }

    public AbstractMultiItemSmeltingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        String group = pBuffer.readUtf();
        SRBookCategory category = pBuffer.readEnum(SRBookCategory.class);
        int ingredientsAmount = pBuffer.readVarInt();
        NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientsAmount, Ingredient.EMPTY);

        ingredients.replaceAll(ignored -> Ingredient.fromNetwork(pBuffer));
        ItemStack itemstack = pBuffer.readItem();
        float outputIncrease = pBuffer.readFloat();
        float experience = pBuffer.readFloat();
        int cookingTime = pBuffer.readVarInt();
        AbstractMultiItemSmeltingRecipe.Recycle recycle = AbstractMultiItemSmeltingRecipe.Recycle.fromNetwork(pBuffer);
        return this.factory.create(pRecipeId, group, category, ingredients, itemstack, outputIncrease, experience, cookingTime, recycle);
    }

    public void toNetwork(FriendlyByteBuf pBuffer, AbstractMultiItemSmeltingRecipe pRecipe) {
        pBuffer.writeUtf(pRecipe.getGroup());
        pBuffer.writeEnum(pRecipe.category());
        pBuffer.writeVarInt(pRecipe.getIngredients().size());

        for(Ingredient ingredient : pRecipe.getIngredients()) {
            ingredient.toNetwork(pBuffer);
        }

        pBuffer.writeItem(pRecipe.getResultItem(RegistryAccess.EMPTY));
        pBuffer.writeFloat(pRecipe.getOutputIncrease());
        pBuffer.writeFloat(pRecipe.getExperience());
        pBuffer.writeVarInt(pRecipe.getCookingTime());
        if (pRecipe.getRecycle() != null) {
            pBuffer.writeBoolean(true);
            pRecipe.getRecycle().toNetwork(pBuffer);
        }
        else {
            pBuffer.writeBoolean(false);
        }
    }

    public interface CookieBaker<T extends AbstractMultiItemSmeltingRecipe> {
        T create(ResourceLocation pId, String pGroup, SRBookCategory pCategory, NonNullList<Ingredient> ingredients, ItemStack pResult, float outputIncrease, float pExperience, int pCookingTime, AbstractMultiItemSmeltingRecipe.Recycle recycle);
    }

    abstract int getIngredientSlotsCount();
}