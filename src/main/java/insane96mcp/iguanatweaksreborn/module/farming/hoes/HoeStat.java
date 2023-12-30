package insane96mcp.iguanatweaksreborn.module.farming.hoes;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;

@JsonAdapter(HoeStat.Serializer.class)
public class HoeStat {
	public IdTagMatcher hoe;
	public int cooldown;
	public int damageOnTill;

	public int scytheRadius;

	public HoeStat(IdTagMatcher hoe, int cooldown) {
		this(hoe, cooldown, 1);
	}

	public HoeStat(IdTagMatcher hoe, int cooldown, int damageOnTill) {
		this(hoe, cooldown, damageOnTill, 1);
	}

	public HoeStat(IdTagMatcher hoe, int cooldown, int damageOnTill, int scytheRadius) {
		this.hoe = hoe;
		this.cooldown = cooldown;
		this.damageOnTill = damageOnTill;
		this.scytheRadius = scytheRadius;
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<HoeStat>>(){}.getType();

	public static class Serializer implements JsonDeserializer<HoeStat>, JsonSerializer<HoeStat> {
		@Override
		public HoeStat deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			IdTagMatcher idTagMatcher = context.deserialize(json.getAsJsonObject().get("hoe"), IdTagMatcher.class);
			return new HoeStat(idTagMatcher, GsonHelper.getAsInt(json.getAsJsonObject(), "cooldown"), GsonHelper.getAsInt(json.getAsJsonObject(), "damage_on_till", 1), GsonHelper.getAsInt(json.getAsJsonObject(), "scythe_radius", 1));
		}

		@Override
		public JsonElement serialize(HoeStat src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("hoe", context.serialize(src.hoe));
			jsonObject.addProperty("cooldown", src.cooldown);
			if (src.damageOnTill > 1)
				jsonObject.addProperty("damage_on_till", src.damageOnTill);
			jsonObject.addProperty("scythe_radius", src.scytheRadius);
			return jsonObject;
		}
	}
}