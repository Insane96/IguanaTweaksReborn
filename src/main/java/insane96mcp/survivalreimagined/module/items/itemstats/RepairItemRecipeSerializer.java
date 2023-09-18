package insane96mcp.survivalreimagined.module.items.itemstats;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public class RepairItemRecipeSerializer implements RecipeSerializer<RepairItemRecipe> {
    private final RepairItemRecipeSerializer.CookieBaker<RepairItemRecipe> factory;

    public RepairItemRecipeSerializer() {
        this.factory = RepairItemRecipe::new;
    }

    @Override
    public RepairItemRecipe fromJson(ResourceLocation pRecipeId, JsonObject serializedRecipe) {
        CraftingBookCategory craftingBookCategory = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(serializedRecipe, "category", (String)null), CraftingBookCategory.MISC);
        JsonElement jsonelement = GsonHelper.getAsJsonObject(serializedRecipe, "item_to_repair");
        Ingredient itemToRepair = Ingredient.fromJson(jsonelement, false);
        jsonelement = (GsonHelper.isArrayNode(serializedRecipe, "material") ? GsonHelper.getAsJsonArray(serializedRecipe, "material") : GsonHelper.getAsJsonObject(serializedRecipe, "material"));
        Ingredient material = Ingredient.fromJson(jsonelement, false);
        int i = GsonHelper.getAsInt(serializedRecipe, "amount");
        return this.factory.create(pRecipeId, craftingBookCategory, itemToRepair, material, i);
    }

    @Override
    public @Nullable RepairItemRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        CraftingBookCategory craftingBookCategory = pBuffer.readEnum(CraftingBookCategory.class);
        Ingredient itemToRepair = Ingredient.fromNetwork(pBuffer);
        Ingredient material = Ingredient.fromNetwork(pBuffer);
        int i = pBuffer.readVarInt();
        return this.factory.create(pRecipeId, craftingBookCategory, itemToRepair, material, i);
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, RepairItemRecipe pRecipe) {
        pBuffer.writeEnum(pRecipe.category());
        pRecipe.itemToRepair.toNetwork(pBuffer);
        pRecipe.material.toNetwork(pBuffer);
        pBuffer.writeVarInt(pRecipe.amount);
    }

    public interface CookieBaker<T extends RepairItemRecipe> {
        T create(ResourceLocation pId, CraftingBookCategory pCategory, Ingredient itemToRepair, Ingredient material, int amount);
    }
}
