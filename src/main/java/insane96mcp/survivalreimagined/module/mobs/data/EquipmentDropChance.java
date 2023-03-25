package insane96mcp.survivalreimagined.module.mobs.data;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;

import java.util.ArrayList;

@JsonAdapter(EquipmentDropChance.Serializer.class)
public class EquipmentDropChance extends IdTagMatcher {
    public EquipmentSlot slot;
    @SerializedName("drop_chance")
    public Float dropChance;

    public EquipmentDropChance(IdTagMatcher.Type type, String id) {
        super(type, id);
    }

    public EquipmentDropChance(IdTagMatcher.Type type, String id, EquipmentSlot slot) {
        super(type, id);
        this.slot = slot;
        this.dropChance = null;
    }
    public EquipmentDropChance(IdTagMatcher.Type type, String id, EquipmentSlot slot, float dropChance) {
        super(type, id);
        this.slot = slot;
        this.dropChance = dropChance;
    }

    //Applies the drop chance to the slot
    public boolean apply(Mob mob) {
        if (dropChance == null)
            return false;

        mob.setDropChance(this.slot, this.dropChance);
        return true;
    }

    public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<EquipmentDropChance>>(){}.getType();

    public static class Serializer implements JsonDeserializer<EquipmentDropChance>, JsonSerializer<EquipmentDropChance> {
        @Override
        public EquipmentDropChance deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String entity = GsonHelper.getAsString(json.getAsJsonObject(), "entity", "");
            if ("".equals(entity))
                throw new JsonParseException("Invalid entity: %s".formatted(entity));
            EquipmentDropChance equipmentDropChance;
            if (entity.startsWith("#")) {
                equipmentDropChance = new EquipmentDropChance(Type.TAG, entity.substring(1));
            }
            else {
                equipmentDropChance = new EquipmentDropChance(Type.ID, entity);
            }

            String sEquipmentSlot = GsonHelper.getAsString(json.getAsJsonObject(), "slot");
            equipmentDropChance.slot = EquipmentSlot.byName(sEquipmentSlot);

            if (json.getAsJsonObject().has("drop_chance"))
                equipmentDropChance.dropChance = GsonHelper.getAsFloat(json.getAsJsonObject(), "drop_chance", Float.MIN_VALUE);

            return equipmentDropChance;
        }

        @Override
        public JsonElement serialize(EquipmentDropChance src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            if (src.type == Type.ID) {
                jsonObject.addProperty("entity", src.location.toString());
            }
            else if (src.type == Type.TAG) {
                jsonObject.addProperty("entity", "#" + src.location.toString());
            }
            jsonObject.addProperty("slot", src.slot.getName());
            if (src.dropChance != null)
                jsonObject.addProperty("drop_chance", src.dropChance);

            return jsonObject;
        }
    }
}
