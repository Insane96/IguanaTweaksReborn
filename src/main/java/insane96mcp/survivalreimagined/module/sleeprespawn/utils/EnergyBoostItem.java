package insane96mcp.survivalreimagined.module.sleeprespawn.utils;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;

@JsonAdapter(EnergyBoostItem.Serializer.class)
public class EnergyBoostItem extends IdTagMatcher {
    public int duration;
    public int amplifier;

    public EnergyBoostItem(Type type, String location) {
        super(type, location);
        this.duration = 0;
        this.amplifier = 0;
    }

    public EnergyBoostItem(Type type, String location, int duration, int amplifier) {
        super(type, location);
        this.duration = duration;
        this.amplifier = amplifier;
    }

    public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<EnergyBoostItem>>(){}.getType();
    public static class Serializer implements JsonDeserializer<EnergyBoostItem>, JsonSerializer<EnergyBoostItem> {
        @Override
        public EnergyBoostItem deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String id = GsonHelper.getAsString(json.getAsJsonObject(), "id", "");
            String tag = GsonHelper.getAsString(json.getAsJsonObject(), "tag", "");

            if (!id.equals("") && !ResourceLocation.isValidResourceLocation(id)) {
                throw new JsonParseException("Invalid id: %s".formatted(id));
            }
            if (!tag.equals("") && !ResourceLocation.isValidResourceLocation(id)) {
                throw new JsonParseException("Invalid tag: %s".formatted(tag));
            }

            EnergyBoostItem energyBoostItem;
            if (!id.equals("") && !tag.equals("")){
                throw new JsonParseException("Invalid object containing both tag (%s) and id (%s)".formatted(tag, id));
            }
            else if (!id.equals("")) {
                energyBoostItem = new EnergyBoostItem(Type.ID, id);
            }
            else if (!tag.equals("")){
                energyBoostItem = new EnergyBoostItem(Type.TAG, tag);
            }
            else {
                throw new JsonParseException("Invalid object missing either tag and id");
            }

            energyBoostItem.duration = GsonHelper.getAsInt(json.getAsJsonObject(), "duration", 0);
            energyBoostItem.amplifier = GsonHelper.getAsInt(json.getAsJsonObject(), "amplifier", 0);

            return energyBoostItem;
        }

        @Override
        public JsonElement serialize(EnergyBoostItem src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            if (src.type == Type.ID) {
                jsonObject.addProperty("id", src.location.toString());
            }
            else if (src.type == Type.TAG) {
                jsonObject.addProperty("tag", src.location.toString());
            }
            if (src.duration > 0)
                jsonObject.addProperty("duration", src.duration);
            if (src.amplifier > 0)
                jsonObject.addProperty("amplifier", src.amplifier);

            return jsonObject;
        }
    }

}