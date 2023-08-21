package insane96mcp.survivalreimagined.module.experience.anvils;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@JsonAdapter(AnvilRecipe.Serializer.class)
public class AnvilRecipe {
    final IdTagMatcher itemToRepair;
    final List<RepairData> repairData;

    public AnvilRecipe(IdTagMatcher itemToRepair, List<RepairData> repairData) {
        this.itemToRepair = itemToRepair;
        this.repairData = repairData;
    }

    public AnvilRecipe(IdTagMatcher itemToRepair, RepairData... repairData) {
        this.itemToRepair = itemToRepair;
        this.repairData = List.of(repairData);
    }

    public boolean isItemToRepair(ItemStack stack) {
        return this.itemToRepair.matchesItem(stack.getItem());
    }

    public Optional<RepairData> getRepairDataFromMaterial(ItemStack stack) {
        for (RepairData repairData : this.repairData) {
            if (repairData.repairMaterial.matchesItem(stack.getItem()))
                return Optional.of(repairData);
        }
        return Optional.empty();
    }

    public record RepairData(@SerializedName("repair_material") IdTagMatcher repairMaterial, @SerializedName("amount") int amountRequired, @SerializedName("max_repair") float maxRepair, @SerializedName("cost_multiplier") float costMultiplier) { }

    public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<AnvilRecipe>>(){}.getType();
    public static class Serializer implements JsonDeserializer<AnvilRecipe>, JsonSerializer<AnvilRecipe> {
        @Override
        public AnvilRecipe deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            IdTagMatcher itemToRepair = context.deserialize(json.getAsJsonObject().get("item_to_repair"), IdTagMatcher.class);
            List<RepairData> repairData = new ArrayList<>();
            JsonArray array = GsonHelper.getAsJsonArray(json.getAsJsonObject(), "repair");
            for (JsonElement element : array) {
                IdTagMatcher repairMaterial = context.deserialize(element.getAsJsonObject().get("repair_material"), IdTagMatcher.class);
                int amount = GsonHelper.getAsInt(element.getAsJsonObject(), "amount");
                if (amount < 1)
                    throw new JsonParseException("amount must be greater than 0");
                float maxRepair = GsonHelper.getAsFloat(element.getAsJsonObject(), "max_repair", 1f);
                if (maxRepair > 1f || maxRepair < 0f)
                    throw new JsonParseException("max_repair must be between 0 and 1");
                float costMultiplier = GsonHelper.getAsFloat(element.getAsJsonObject(), "cost_multiplier", 1f);
                if (costMultiplier < 0f)
                    throw new JsonParseException("cost_multiplier must be greater than or equal to 0");
                repairData.add(new RepairData(repairMaterial, amount, maxRepair, costMultiplier));
            }

            return new AnvilRecipe(itemToRepair, repairData);
        }

        @Override
        public JsonElement serialize(AnvilRecipe src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            JsonElement itemToRepair = context.serialize(src.itemToRepair, IdTagMatcher.class);
            jsonObject.add("item_to_repair", itemToRepair);
            JsonArray repair = new JsonArray();
            for (RepairData repairData : src.repairData) {
                JsonObject r = new JsonObject();
                r.add("repair_material", context.serialize(repairData.repairMaterial, IdTagMatcher.class));
                r.addProperty("amount", repairData.amountRequired);
                if (repairData.maxRepair < 1f)
                    r.addProperty("max_repair", repairData.maxRepair);
                if (repairData.costMultiplier != 1f)
                    r.addProperty("cost_multiplier", repairData.costMultiplier);
                r.add("repair_material", context.serialize(repairData.repairMaterial, IdTagMatcher.class));
                repair.add(r);
            }
            jsonObject.add("repair", repair);

            return jsonObject;
        }
    }
}
