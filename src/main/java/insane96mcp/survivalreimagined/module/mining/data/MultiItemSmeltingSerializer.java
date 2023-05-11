package insane96mcp.survivalreimagined.module.mining.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import insane96mcp.survivalreimagined.module.mining.crafting.MultiItemSmeltingRecipe;
import insane96mcp.survivalreimagined.module.mining.inventory.AbstractMultiBlockFurnaceMenu;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistries;

public class MultiItemSmeltingSerializer implements RecipeSerializer<MultiItemSmeltingRecipe> {
    private final int defaultCookingTime;
    private final CookieBaker<MultiItemSmeltingRecipe> factory;

    public MultiItemSmeltingSerializer(CookieBaker<MultiItemSmeltingRecipe> pFactory, int pDefaultCookingTime) {
        this.defaultCookingTime = pDefaultCookingTime;
        this.factory = pFactory;
    }

    public MultiItemSmeltingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
        String group = GsonHelper.getAsString(pJson, "group", "");
        CookingBookCategory category = CookingBookCategory.CODEC.byName(GsonHelper.getAsString(pJson, "category", null), CookingBookCategory.MISC);
        NonNullList<Ingredient> ingredients = itemsFromJson(GsonHelper.getAsJsonArray(pJson, "ingredients"));
        if (ingredients.isEmpty()) {
            throw new JsonParseException("No ingredients for shapeless recipe");
        }
        else if (ingredients.size() > AbstractMultiBlockFurnaceMenu.INGREDIENT_SLOTS.length) {
            throw new JsonParseException("Too many ingredients for multi item smelting recipe. The maximum is %d".formatted(AbstractMultiBlockFurnaceMenu.INGREDIENT_SLOTS.length));
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
        float experience = GsonHelper.getAsFloat(pJson, "experience", 0.0F);
        int cookingTime = GsonHelper.getAsInt(pJson, "cookingtime", this.defaultCookingTime);
        return this.factory.create(pRecipeId, group, category, ingredients, result, experience, cookingTime);
    }

    private static NonNullList<Ingredient> itemsFromJson(JsonArray pIngredientArray) {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();

        for(int i = 0; i < pIngredientArray.size(); ++i) {
            Ingredient ingredient = Ingredient.fromJson(pIngredientArray.get(i));
            nonnulllist.add(ingredient);
        }

        return nonnulllist;
    }

    public MultiItemSmeltingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        String group = pBuffer.readUtf();
        CookingBookCategory category = pBuffer.readEnum(CookingBookCategory.class);
        int ingredientsAmount = pBuffer.readVarInt();
        NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientsAmount, Ingredient.EMPTY);

        ingredients.replaceAll(ignored -> Ingredient.fromNetwork(pBuffer));
        ItemStack itemstack = pBuffer.readItem();
        float f = pBuffer.readFloat();
        int i = pBuffer.readVarInt();
        return this.factory.create(pRecipeId, group, category, ingredients, itemstack, f, i);
    }

    public void toNetwork(FriendlyByteBuf pBuffer, MultiItemSmeltingRecipe pRecipe) {
        pBuffer.writeUtf(pRecipe.getGroup());
        pBuffer.writeEnum(pRecipe.category());
        pBuffer.writeVarInt(pRecipe.getIngredients().size());

        for(Ingredient ingredient : pRecipe.getIngredients()) {
            ingredient.toNetwork(pBuffer);
        }

        pBuffer.writeItem(pRecipe.result);
        pBuffer.writeFloat(pRecipe.getExperience());
        pBuffer.writeVarInt(pRecipe.getCookingTime());
    }

    public interface CookieBaker<T extends MultiItemSmeltingRecipe> {
        T create(ResourceLocation pId, String pGroup, CookingBookCategory pCategory, NonNullList<Ingredient> ingredients, ItemStack pResult, float pExperience, int pCookingTime);
    }
}