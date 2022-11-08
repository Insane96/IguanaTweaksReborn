package insane96mcp.iguanatweaksreborn.module.farming.utils;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;

import java.util.List;

@JsonAdapter(PlantGrowthModifier.Serializer.class)
public class PlantGrowthModifier extends IdTagMatcher {
	private double growthMultiplier;
	private double noSunlightGrowthMultiplier;
	private int minSunlightRequired;
	private double nightTimeGrowthMultiplier;
	//TODO biomesGrowth
	private List<IdTagMatcher> biomesGrowth;
	private double wrongBiomeMultiplier = 1d;

	public PlantGrowthModifier(IdTagMatcher.Type type, String id) {
		super(type, id);
	}

	public PlantGrowthModifier(IdTagMatcher.Type type, String id, double growthMultiplier, double noSunlightGrowthMultiplier, int minSunlightRequired, double nightTimeGrowthMultiplier) {
		super(type, id);
		this.growthMultiplier = growthMultiplier;
		this.noSunlightGrowthMultiplier = noSunlightGrowthMultiplier;
		this.minSunlightRequired = minSunlightRequired;
		this.nightTimeGrowthMultiplier = nightTimeGrowthMultiplier;
	}

	/**
	 * Returns -1 when the block doesn't match the PlantGrowthModifier
	 */
	public double getMultiplier(Block block, Level level, BlockPos pos) {
		if (!this.matchesBlock(block))
			return -1d;
		double multiplier = this.growthMultiplier;
		int skyLight = level.getBrightness(LightLayer.SKY, pos);
		if (skyLight < this.minSunlightRequired)
			multiplier *= this.noSunlightGrowthMultiplier;
		int dayTime = (int) (level.dayTime() % 24000);
		if (dayTime >= 12786 && dayTime < 23216)
			multiplier *= this.nightTimeGrowthMultiplier;

		return multiplier;
	}

	public static class Serializer implements JsonDeserializer<PlantGrowthModifier>, JsonSerializer<PlantGrowthModifier> {
		@Override
		public PlantGrowthModifier deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String id = GsonHelper.getAsString(json.getAsJsonObject(), "id", "");
			String tag = GsonHelper.getAsString(json.getAsJsonObject(), "tag", "");

			if (!id.equals("") && !ResourceLocation.isValidResourceLocation(id)) {
				throw new JsonParseException("Invalid id for PlantGrowthModifier: %s".formatted(id));
			}
			if (!tag.equals("") && !ResourceLocation.isValidResourceLocation(id)) {
				throw new JsonParseException("Invalid tag for PlantGrowthModifier: %s".formatted(tag));
			}

			PlantGrowthModifier plantGrowthModifier;
			if (!id.equals("") && !tag.equals("")){
				throw new JsonParseException("Invalid HoeCooldown containing both tag (%s) and id (%s)".formatted(tag, id));
			}
			else if (!id.equals("")) {
				plantGrowthModifier = new PlantGrowthModifier(Type.ID, id);
			}
			else if (!tag.equals("")){
				plantGrowthModifier = new PlantGrowthModifier(Type.TAG, id);
			}
			else {
				throw new JsonParseException("Invalid HoeCooldown missing either tag and id");
			}

			String dimension = GsonHelper.getAsString(json.getAsJsonObject(), "dimension", "");
			if (!dimension.equals("")) {
				if (!ResourceLocation.isValidResourceLocation(dimension)) {
					throw new JsonParseException("Invalid dimension for PlantGrowthModifier: %s".formatted(dimension));
				}
				else {
					plantGrowthModifier.dimension = ResourceLocation.tryParse(dimension);
				}
			}

			plantGrowthModifier.growthMultiplier = GsonHelper.getAsDouble(json.getAsJsonObject(), "growth_multiplier", 1d);
			plantGrowthModifier.noSunlightGrowthMultiplier = GsonHelper.getAsDouble(json.getAsJsonObject(), "no_sunlight_growth_multiplier", 1d);
			plantGrowthModifier.minSunlightRequired = GsonHelper.getAsInt(json.getAsJsonObject(), "min_sunlight_required", 15);
			if (plantGrowthModifier.minSunlightRequired < 0 || plantGrowthModifier.minSunlightRequired > 15)
				throw new JsonParseException("Invalid min_sunlight_required, must be between 0 and 15");
			plantGrowthModifier.nightTimeGrowthMultiplier = GsonHelper.getAsDouble(json.getAsJsonObject(), "night_time_growth_multiplier", 1d);
			//TODO Correct biome
			plantGrowthModifier.wrongBiomeMultiplier = GsonHelper.getAsDouble(json.getAsJsonObject(), "wrong_biome_multiplier", 1d);

			return plantGrowthModifier;
		}

		@Override
		public JsonElement serialize(PlantGrowthModifier src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
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
			if (src.growthMultiplier != 1d)
				jsonObject.addProperty("growth_multiplier", src.growthMultiplier);
			if (src.noSunlightGrowthMultiplier != 1d)
				jsonObject.addProperty("no_sunlight_growth_multiplier", src.noSunlightGrowthMultiplier);
			if (src.minSunlightRequired != 15)
				jsonObject.addProperty("min_sunlight_required", src.minSunlightRequired);
			if (src.nightTimeGrowthMultiplier != 1d)
				jsonObject.addProperty("night_time_growth_multiplier", src.nightTimeGrowthMultiplier);
			//TODO Correct biome
			if (src.wrongBiomeMultiplier != 1d)
				jsonObject.addProperty("wrong_biome_multiplier", src.wrongBiomeMultiplier);
			return jsonObject;
		}
	}
}