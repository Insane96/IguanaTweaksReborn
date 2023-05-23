package insane96mcp.survivalreimagined.module.experience.data;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;

import java.util.ArrayList;

@JsonAdapter(TwinIdTagMatcher.Serializer.class)
public class TwinIdTagMatcher {

	public IdTagMatcher idTagMatcherA;
	public IdTagMatcher idTagMatcherB;

	public TwinIdTagMatcher(IdTagMatcher.Type typeA, String locationA, IdTagMatcher.Type typeB, String locationB) {
		this.idTagMatcherA = new IdTagMatcher(typeA, locationA);
		this.idTagMatcherB = new IdTagMatcher(typeB, locationB);
	}

	public boolean matchesItems(Item a, Item b) {
		return this.idTagMatcherA.matchesItem(a) && this.idTagMatcherB.matchesItem(b);
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<TwinIdTagMatcher>>(){}.getType();

	public static class Serializer implements JsonDeserializer<TwinIdTagMatcher>, JsonSerializer<TwinIdTagMatcher> {
		@Override
		public TwinIdTagMatcher deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String item1 = GsonHelper.getAsString(json.getAsJsonObject(), "item_1", "");
			IdTagMatcher.Type type1 = IdTagMatcher.Type.ID;
			if (item1.startsWith("#")) {
				type1 = IdTagMatcher.Type.TAG;
				item1 = item1.substring(1);
			}
			if (!item1.equals("") && !ResourceLocation.isValidResourceLocation(item1)) {
				throw new JsonParseException("Invalid from: %s".formatted(item1));
			}
			String item2 = GsonHelper.getAsString(json.getAsJsonObject(), "item_2", "");
			IdTagMatcher.Type type2 = IdTagMatcher.Type.ID;
			if (item2.startsWith("#")) {
				type2 = IdTagMatcher.Type.TAG;
				item2 = item2.substring(1);
			}
			if (!item2.equals("") && !ResourceLocation.isValidResourceLocation(item2)) {
				throw new JsonParseException("Invalid from: %s".formatted(item2));
			}

			return new TwinIdTagMatcher(type1, item1, type2, item2);
		}

		@Override
		public JsonElement serialize(TwinIdTagMatcher src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			if (src.idTagMatcherA.type == IdTagMatcher.Type.ID) {
				jsonObject.addProperty("item_1", src.idTagMatcherA.location.toString());
			}
			else {
				jsonObject.addProperty("item_1", "#" + src.idTagMatcherA.location.toString());
			}
			if (src.idTagMatcherB.type == IdTagMatcher.Type.ID) {
				jsonObject.addProperty("item_2", src.idTagMatcherB.location.toString());
			}
			else {
				jsonObject.addProperty("item_2", "#" + src.idTagMatcherB.location.toString());
			}
			return jsonObject;
		}
	}
}