package insane96mcp.iguanatweaksreborn.module.mining.utils;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

@JsonAdapter(BlockHardness.Serializer.class)
public class BlockHardness extends IdTagMatcher {
	public double hardness;

	BlockHardness(Type type, String location) {
		super(type, location);
	}

	public BlockHardness(Type type, String location, Double hardness) {
		super(type, location);
		this.hardness = hardness;
	}

	public BlockHardness(Type type, String location, String dimension, Double hardness) {
		super(type, location, dimension);
		this.hardness = hardness;
	}

	public static class Serializer implements JsonDeserializer<BlockHardness>, JsonSerializer<BlockHardness> {
		@Override
		public BlockHardness deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String id = GsonHelper.getAsString(json.getAsJsonObject(), "id", "");
			String tag = GsonHelper.getAsString(json.getAsJsonObject(), "tag", "");

			if (!id.equals("") && !ResourceLocation.isValidResourceLocation(id)) {
				throw new JsonParseException("Invalid id for BlockHardness: %s".formatted(id));
			}
			if (!tag.equals("") && !ResourceLocation.isValidResourceLocation(id)) {
				throw new JsonParseException("Invalid tag for BlockHardness: %s".formatted(tag));
			}

			BlockHardness blockHardness;
			if (!id.equals("") && !tag.equals("")){
				throw new JsonParseException("Invalid CustomFoodProperties containing both tag (%s) and id (%s)".formatted(tag, id));
			}
			else if (!id.equals("")) {
				blockHardness = new BlockHardness(Type.ID, id);
			}
			else if (!tag.equals("")){
				blockHardness = new BlockHardness(Type.TAG, id);
			}
			else {
				throw new JsonParseException("Invalid CustomFoodProperties missing either tag and id");
			}

			String dimension = GsonHelper.getAsString(json.getAsJsonObject(), "dimension", "");
			if (!dimension.equals("")) {
				if (!ResourceLocation.isValidResourceLocation(dimension)) {
					throw new JsonParseException("Invalid dimension for HoeCooldown: %s".formatted(dimension));
				}
				else {
					blockHardness.dimension = ResourceLocation.tryParse(dimension);
				}
			}

			blockHardness.hardness = GsonHelper.getAsDouble(json.getAsJsonObject(), "hardness");

			return blockHardness;
		}

		@Override
		public JsonElement serialize(BlockHardness src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
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
			jsonObject.addProperty("hardness", src.hardness);

			return jsonObject;
		}
	}
}