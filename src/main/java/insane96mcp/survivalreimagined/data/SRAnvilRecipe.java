package insane96mcp.survivalreimagined.data;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.lang.reflect.Type;

@JsonAdapter(SRAnvilRecipe.Serializer.class)
public class SRAnvilRecipe {

    Ingredient left;
    Ingredient right;
    int amount;
    ItemStack result;
    boolean keepDamage;

    public SRAnvilRecipe(Ingredient left, Ingredient right, int amount, ItemStack result, boolean keepDamage) {
        this.right = right;
        this.amount = amount;
        this.left = left;
        this.result = result;
        this.keepDamage = keepDamage;
    }

    public boolean matches(ItemStack left, ItemStack right) {
        return this.left.test(left) && this.right.test(right) && right.getCount() >= amount;
    }

    public ItemStack assemble(ItemStack left, ItemStack right) {
        ItemStack result = this.result.copy();
        if (this.keepDamage)
            result.setDamageValue(left.getDamageValue());
        return result;
    }

    public static class Serializer implements JsonSerializer<SRAnvilRecipe>, JsonDeserializer<SRAnvilRecipe> {

        @Override
        public JsonElement serialize(SRAnvilRecipe src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("left", src.left.toJson());
            jsonObject.add("right", src.right.toJson());
            jsonObject.addProperty("amount", src.amount);
            jsonObject.add("result", context.serialize(src.result));
            jsonObject.addProperty("keep_damage", src.keepDamage);
            return jsonObject;
        }

        @Override
        public SRAnvilRecipe deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Ingredient left = Ingredient.fromJson(GsonHelper.getAsJsonObject(json.getAsJsonObject(), "left"));
            Ingredient right = Ingredient.fromJson(GsonHelper.getAsJsonObject(json.getAsJsonObject(), "right"));
            int amount = GsonHelper.getAsInt(json.getAsJsonObject(), "amount");
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json.getAsJsonObject(), "result"));
            boolean keepDurability = GsonHelper.getAsBoolean(json.getAsJsonObject(), "keep_durability", false);
            return new SRAnvilRecipe(left, right, amount, result, keepDurability);
        }
    }
}