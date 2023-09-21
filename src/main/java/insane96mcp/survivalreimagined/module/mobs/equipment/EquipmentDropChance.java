package insane96mcp.survivalreimagined.module.mobs.equipment;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;

import javax.annotation.Nullable;
import java.util.ArrayList;

@JsonAdapter(EquipmentDropChance.Serializer.class)
public class EquipmentDropChance {
    public IdTagMatcher entity;
    public EquipmentSlot slot;
    @SerializedName("drop_chance")
    public Float dropChance;

    public EquipmentDropChance(IdTagMatcher entity, EquipmentSlot slot) {
        this(entity, slot, null);
    }
    public EquipmentDropChance(IdTagMatcher entity, EquipmentSlot slot, @Nullable Float dropChance) {
        this.entity = entity;
        this.slot = slot;
        this.dropChance = dropChance;
    }

    /**
     * Applies the drop chance to the slot, if drop chance is null the drop chance will not change from vanilla
     */
    public void apply(Mob mob) {
        if (dropChance == null)
            return;

        mob.setDropChance(this.slot, this.dropChance);
    }

    public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<EquipmentDropChance>>(){}.getType();

    public static class Serializer implements JsonDeserializer<EquipmentDropChance>, JsonSerializer<EquipmentDropChance> {
        @Override
        public EquipmentDropChance deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            IdTagMatcher entity = context.deserialize(json.getAsJsonObject().get("entity"), IdTagMatcher.class);
            Float dropChance = json.getAsJsonObject().has("drop_chance") ? GsonHelper.getAsFloat(json.getAsJsonObject(), "drop_chance", Float.MIN_VALUE) : null;
            return new EquipmentDropChance(entity, EquipmentSlot.byName(GsonHelper.getAsString(json.getAsJsonObject(), "slot")), dropChance);
        }

        @Override
        public JsonElement serialize(EquipmentDropChance src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("entity", context.serialize(src.entity));
            jsonObject.addProperty("slot", src.slot.getName());
            if (src.dropChance != null)
                jsonObject.addProperty("drop_chance", src.dropChance);

            return jsonObject;
        }
    }
}
