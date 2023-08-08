package insane96mcp.survivalreimagined.module.mining.forging;

import com.google.gson.JsonObject;
import insane96mcp.survivalreimagined.setup.client.SRBookCategory;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class ForgeRecipeSerializer implements RecipeSerializer<ForgeRecipe> {
    private final CookieBaker<ForgeRecipe> factory;

    public ForgeRecipeSerializer() {
        this.factory = ForgeRecipe::new;
    }

    public ForgeRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
        SRBookCategory category = SRBookCategory.CODEC.byName(GsonHelper.getAsString(pJson, "category", null), SRBookCategory.FORGE_MISC);
        Ingredient ingredient = Ingredient.fromJson(pJson.getAsJsonObject("ingredient"));
        //Forge: Check if primitive string to keep vanilla or an object which can contain a count field.
        if (!pJson.has("result"))
            throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
        int ingredientAmount = GsonHelper.getAsInt(pJson, "amount", 1);
        Ingredient gear = Ingredient.fromJson(pJson.getAsJsonObject("gear"));
        ItemStack result;
        if (pJson.get("result").isJsonObject())
            result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(pJson, "result"), true, true);
        else {
            String s1 = GsonHelper.getAsString(pJson, "result");
            ResourceLocation resourcelocation = new ResourceLocation(s1);
            result = new ItemStack(ForgeRegistries.ITEMS.getValue(resourcelocation));
        }
        int smashesRequired = GsonHelper.getAsInt(pJson, "smashes_required");
        float experience = GsonHelper.getAsFloat(pJson, "experience", 0);
        return this.factory.create(pRecipeId, category, ingredient, ingredientAmount, gear, result, smashesRequired, experience);
    }

    public ForgeRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        SRBookCategory category = pBuffer.readEnum(SRBookCategory.class);
        Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
        int ingredientAmount = pBuffer.readVarInt();
        Ingredient gear = Ingredient.fromNetwork(pBuffer);
        ItemStack result = pBuffer.readItem();
        int smashesRequired = pBuffer.readVarInt();
        float experience = pBuffer.readFloat();
        return this.factory.create(pRecipeId, category, ingredient, ingredientAmount, gear, result, smashesRequired, experience);
    }

    public void toNetwork(FriendlyByteBuf pBuffer, ForgeRecipe pRecipe) {
        pBuffer.writeEnum(pRecipe.category());
        pRecipe.getIngredient().toNetwork(pBuffer);
        pBuffer.writeVarInt(pRecipe.getIngredientAmount());
        pRecipe.getGear().toNetwork(pBuffer);
        pBuffer.writeItem(pRecipe.getResultItem(RegistryAccess.EMPTY));
        pBuffer.writeVarInt(pRecipe.getSmashesRequired());
        pBuffer.writeFloat(pRecipe.getExperience());
    }

    public interface CookieBaker<T extends ForgeRecipe> {
        T create(ResourceLocation pId, SRBookCategory pCategory, Ingredient ingredient, int ingredientAmount, Ingredient gear, ItemStack pResult, int smashesRequired, float experience);
    }
}