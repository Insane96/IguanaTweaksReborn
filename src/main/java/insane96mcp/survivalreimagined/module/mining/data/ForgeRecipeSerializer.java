package insane96mcp.survivalreimagined.module.mining.data;

import com.google.gson.JsonObject;
import insane96mcp.survivalreimagined.module.mining.crafting.ForgeRecipe;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class ForgeRecipeSerializer implements RecipeSerializer<ForgeRecipe> {
    private final CookieBaker<ForgeRecipe> factory;

    public ForgeRecipeSerializer(CookieBaker<ForgeRecipe> pFactory) {
        this.factory = pFactory;
    }

    public ForgeRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
        CookingBookCategory category = CookingBookCategory.CODEC.byName(GsonHelper.getAsString(pJson, "category", null), CookingBookCategory.MISC);
        Ingredient ingredient = Ingredient.fromJson(pJson.getAsJsonObject("ingredient"));
        //Forge: Check if primitive string to keep vanilla or an object which can contain a count field.
        if (!pJson.has("result"))
            throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
        int ingredientAmount = GsonHelper.getAsInt(pJson, "amount", 1);
        ItemStack result;
        if (pJson.get("result").isJsonObject())
            result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(pJson, "result"), true, true);
        else {
            String s1 = GsonHelper.getAsString(pJson, "result");
            ResourceLocation resourcelocation = new ResourceLocation(s1);
            result = new ItemStack(ForgeRegistries.ITEMS.getValue(resourcelocation));
        }
        int smashesRequired = GsonHelper.getAsInt(pJson, "smashes_required");
        return this.factory.create(pRecipeId, category, ingredient, ingredientAmount, result, smashesRequired);
    }

    public ForgeRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        CookingBookCategory category = pBuffer.readEnum(CookingBookCategory.class);
        Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
        int ingredientAmount = pBuffer.readInt();
        ItemStack result = pBuffer.readItem();
        int smashesRequired = pBuffer.readVarInt();
        return this.factory.create(pRecipeId, category, ingredient, ingredientAmount, result, smashesRequired);
    }

    public void toNetwork(FriendlyByteBuf pBuffer, ForgeRecipe pRecipe) {
        pBuffer.writeUtf(pRecipe.getGroup());
        pBuffer.writeEnum(pRecipe.category());
        pRecipe.getIngredient().toNetwork(pBuffer);
        pBuffer.writeInt(pRecipe.getIngredientAmount());

        pBuffer.writeItem(pRecipe.getResultItem(RegistryAccess.EMPTY));
        pBuffer.writeFloat(pRecipe.getSmashesRequired());
    }

    public interface CookieBaker<T extends ForgeRecipe> {
        T create(ResourceLocation pId, CookingBookCategory pCategory, Ingredient ingredient, int ingredientAmount, ItemStack pResult, int smashesRequired);
    }
}