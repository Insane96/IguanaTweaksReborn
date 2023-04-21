package insane96mcp.survivalreimagined.module.farming.utils;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;

@JsonAdapter(HoeStat.Serializer.class)
public class HoeStat extends IdTagMatcher {
	public int cooldown;
	public int damageOnTill;

	public int scytheRadius;

	public HoeStat(Type type, String location) {
		super(type, location);
	}

	public HoeStat(Type type, String location, int cooldown) {
		this(type, location, cooldown, 1);
	}

	public HoeStat(Type type, String location, int cooldown, int damageOnTill) {
		this(type, location, cooldown, damageOnTill, 1);
	}

	public HoeStat(Type type, String location, int cooldown, int damageOnTill, int scytheRadius) {
		super(type, location);
		this.cooldown = cooldown;
		this.damageOnTill = damageOnTill;
		this.scytheRadius = scytheRadius;
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<HoeStat>>(){}.getType();

	public static class Serializer implements JsonDeserializer<HoeStat>, JsonSerializer<HoeStat> {
		@Override
		public HoeStat deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String id = GsonHelper.getAsString(json.getAsJsonObject(), "id", "");
			String tag = GsonHelper.getAsString(json.getAsJsonObject(), "tag", "");

			if (!id.equals("") && !ResourceLocation.isValidResourceLocation(id)) {
				throw new JsonParseException("Invalid id: %s".formatted(id));
			}
			if (!tag.equals("") && !ResourceLocation.isValidResourceLocation(id)) {
				throw new JsonParseException("Invalid tag: %s".formatted(tag));
			}

			HoeStat hoeStat;
			if (!id.equals("") && !tag.equals("")){
				throw new JsonParseException("Invalid object containing both tag (%s) and id (%s)".formatted(tag, id));
			}
			else if (!id.equals("")) {
				hoeStat = new HoeStat(Type.ID, id);
			}
			else if (!tag.equals("")){
				hoeStat = new HoeStat(Type.TAG, tag);
			}
			else {
				throw new JsonParseException("Invalid object missing either tag and id");
			}

			String dimension = GsonHelper.getAsString(json.getAsJsonObject(), "dimension", "");
			if (!dimension.equals("")) {
				if (!ResourceLocation.isValidResourceLocation(dimension)) {
					throw new JsonParseException("Invalid dimension: %s".formatted(dimension));
				}
				else {
					hoeStat.dimension = ResourceLocation.tryParse(dimension);
				}
			}

			hoeStat.cooldown = GsonHelper.getAsInt(json.getAsJsonObject(), "cooldown");
			hoeStat.damageOnTill = GsonHelper.getAsInt(json.getAsJsonObject(), "damage_on_till", 1);
			hoeStat.scytheRadius = GsonHelper.getAsInt(json.getAsJsonObject(), "scythe_radius", 1);

			return hoeStat;
		}

		@Override
		public JsonElement serialize(HoeStat src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			if (src.type == Type.ID) {
				jsonObject.addProperty("id", src.location.toString());
			}
			else if (src.type == Type.TAG) {
				jsonObject.addProperty("tag", src.location.toString());
			}
			if (src.dimension != null) {
				jsonObject.addProperty("dimension", src.dimension.toString());
			}
			jsonObject.addProperty("cooldown", src.cooldown);
			if (src.damageOnTill > 1)
				jsonObject.addProperty("damage_on_till", src.damageOnTill);
			jsonObject.addProperty("scythe_radius", src.scytheRadius);
			return jsonObject;
		}
	}
}