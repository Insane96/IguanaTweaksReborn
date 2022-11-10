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

	public static class Serializer implements JsonDeserializer<BlockHardness>, JsonSerializer<BlockHardness> {
		@Override
		public BlockHardness deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String id = GsonHelper.getAsString(json.getAsJsonObject(), "id", "");
			String tag = GsonHelper.getAsString(json.getAsJsonObject(), "tag", "");

			if (!id.equals("") && !ResourceLocation.isValidResourceLocation(id)) {
				throw new JsonParseException("Invalid id: %s".formatted(id));
			}
			if (!tag.equals("") && !ResourceLocation.isValidResourceLocation(id)) {
				throw new JsonParseException("Invalid tag: %s".formatted(tag));
			}

			BlockHardness blockHardness;
			if (!id.equals("") && !tag.equals("")){
				throw new JsonParseException("Invalid object containing both tag (%s) and id (%s)".formatted(tag, id));
			}
			else if (!id.equals("")) {
				blockHardness = new BlockHardness(Type.ID, id);
			}
			else if (!tag.equals("")){
				blockHardness = new BlockHardness(Type.TAG, tag);
			}
			else {
				throw new JsonParseException("Invalid object missing either tag and id");
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
			jsonObject.addProperty("hardness", src.hardness);

			return jsonObject;
		}
	}
}