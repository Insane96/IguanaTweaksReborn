package insane96mcp.iguanatweaksreborn.module.misc.utils;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;

@JsonAdapter(DeBuff.Serializer.class)
public class DeBuff {
	public Stat stat;
	public double min, max;
	public MobEffect effect;
	public int amplifier;

	public DeBuff() { }

	public DeBuff(Stat stat, double min, double max, MobEffect effect, int amplifier) {
		this.stat = stat;
		this.min = min;
		this.max = max;
		this.effect = effect;
		this.amplifier = amplifier;
	}

	public enum Stat {
		@SerializedName("hunger")
		HUNGER,
		@SerializedName("health")
		HEALTH,
		@SerializedName("experience_level")
		EXPERIENCE_LEVEL
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<DeBuff>>(){}.getType();

	public static class Serializer implements JsonDeserializer<DeBuff>, JsonSerializer<DeBuff> {
		@Override
		public DeBuff deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			DeBuff deBuff = new DeBuff();
			deBuff.stat = context.deserialize(json.getAsJsonObject().get("stat"), Stat.class);
			deBuff.min = GsonHelper.getAsDouble(json.getAsJsonObject(), "min", Double.MIN_VALUE);
			deBuff.max = GsonHelper.getAsDouble(json.getAsJsonObject(), "max", Double.MAX_VALUE);
			String sMobEffect = GsonHelper.getAsString(json.getAsJsonObject(), "effect");
			MobEffect mobEffect = ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.tryParse(sMobEffect));
			if (mobEffect == null) {
				throw new JsonParseException("%s effect doesn't exist".formatted(sMobEffect));
			}
			deBuff.effect = mobEffect;
			deBuff.amplifier = GsonHelper.getAsInt(json.getAsJsonObject(), "amplifier", 0);

			return deBuff;
		}

		@Override
		public JsonElement serialize(DeBuff src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("stat", context.serialize(src.stat));
			if (src.min != Double.MIN_VALUE)
				jsonObject.addProperty("min", src.min);
			if (src.max != Double.MAX_VALUE)
				jsonObject.addProperty("max", src.max);
			String sMobEffect = ForgeRegistries.MOB_EFFECTS.getKey(src.effect).toString();
			jsonObject.addProperty("effect", sMobEffect);
			if (src.amplifier > 0)
				jsonObject.addProperty("amplifier", src.amplifier);

			return jsonObject;
		}
	}
}