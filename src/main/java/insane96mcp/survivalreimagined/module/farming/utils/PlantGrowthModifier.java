package insane96mcp.survivalreimagined.module.farming.utils;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

@JsonAdapter(PlantGrowthModifier.Serializer.class)
public class PlantGrowthModifier extends IdTagMatcher {
	private float growthMultiplier = 1f;
	private float noSunlightMultiplier = 1f;
	private int minSunlightRequired = 0;
	private float nightTimeMultiplier = 1f;
	private List<IdTagMatcher> correctBiomes = new ArrayList<>();
	private float wrongBiomeMultiplier = 1f;

	public PlantGrowthModifier(IdTagMatcher.Type type, String id) {
		super(type, id);
	}

	private PlantGrowthModifier(IdTagMatcher.Type type, String id, float growthMultiplier, float noSunlightMultiplier, int minSunlightRequired, float nightTimeMultiplier) {
		super(type, id);
		this.growthMultiplier = growthMultiplier;
		this.noSunlightMultiplier = noSunlightMultiplier;
		this.minSunlightRequired = minSunlightRequired;
		this.nightTimeMultiplier = nightTimeMultiplier;
	}

	private PlantGrowthModifier(IdTagMatcher.Type type, String id, float growthMultiplier, float noSunlightMultiplier, int minSunlightRequired, float nightTimeMultiplier, List<IdTagMatcher> correctBiomes, float wrongBiomeMultiplier) {
		super(type, id);
		this.growthMultiplier = growthMultiplier;
		this.noSunlightMultiplier = noSunlightMultiplier;
		this.minSunlightRequired = minSunlightRequired;
		this.nightTimeMultiplier = nightTimeMultiplier;
		this.correctBiomes = new ArrayList<>(correctBiomes);
		this.wrongBiomeMultiplier = wrongBiomeMultiplier;
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
			multiplier *= this.noSunlightMultiplier;
		int dayTime = (int) (level.dayTime() % 24000);
		if (dayTime >= 12786 && dayTime < 23216)
			multiplier *= this.nightTimeMultiplier;
		Holder<Biome> biome = level.getBiome(pos);
		if (!this.correctBiomes.isEmpty()) {
			boolean isInCorrectBiome = false;
			for (IdTagMatcher correctBiome : this.correctBiomes) {
				if (correctBiome.matchesBiome(biome)) {
					isInCorrectBiome = true;
					break;
				}
			}
			if (!isInCorrectBiome)
				multiplier *= this.wrongBiomeMultiplier;
		}

		return multiplier;
	}

	public static class Builder {
		PlantGrowthModifier plantGrowthModifier;

		public Builder(IdTagMatcher.Type type, String id) {
			this.plantGrowthModifier = new PlantGrowthModifier(type, id);
		}

		public Builder setGrowthMultiplier(float growthMultiplier) {
			this.plantGrowthModifier.growthMultiplier = growthMultiplier;
			return this;
		}

		public Builder setNoSunglightMultipler(float noSunlightMultiplier, int minSunlight) {
			this.plantGrowthModifier.noSunlightMultiplier = noSunlightMultiplier;
			return this;
		}

		public Builder setNightTimeMultiplier(float nightTimeMultiplier) {
			this.plantGrowthModifier.nightTimeMultiplier = nightTimeMultiplier;
			return this;
		}

		public Builder setGrowthBiomes(List<IdTagMatcher> biomes, float wrongBiomeMultiplier) {
			this.plantGrowthModifier.correctBiomes = new ArrayList<>(biomes);
			this.plantGrowthModifier.wrongBiomeMultiplier = wrongBiomeMultiplier;
			return this;
		}

		public PlantGrowthModifier build() {
			return this.plantGrowthModifier;
		}
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<PlantGrowthModifier>>(){}.getType();

	public static class Serializer implements JsonDeserializer<PlantGrowthModifier>, JsonSerializer<PlantGrowthModifier> {
		@Override
		public PlantGrowthModifier deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String id = GsonHelper.getAsString(json.getAsJsonObject(), "id", "");
			String tag = GsonHelper.getAsString(json.getAsJsonObject(), "tag", "");

			if (!id.equals("") && !ResourceLocation.isValidResourceLocation(id)) {
				throw new JsonParseException("Invalid id: %s".formatted(id));
			}
			if (!tag.equals("") && !ResourceLocation.isValidResourceLocation(id)) {
				throw new JsonParseException("Invalid tag: %s".formatted(tag));
			}

			PlantGrowthModifier plantGrowthModifier;
			if (!id.equals("") && !tag.equals("")){
				throw new JsonParseException("Invalid object containing both tag (%s) and id (%s)".formatted(tag, id));
			}
			else if (!id.equals("")) {
				plantGrowthModifier = new PlantGrowthModifier(Type.ID, id);
			}
			else if (!tag.equals("")){
				plantGrowthModifier = new PlantGrowthModifier(Type.TAG, tag);
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
					plantGrowthModifier.dimension = ResourceLocation.tryParse(dimension);
				}
			}

			plantGrowthModifier.growthMultiplier = GsonHelper.getAsFloat(json.getAsJsonObject(), "growth_multiplier", 1f);
			plantGrowthModifier.noSunlightMultiplier = GsonHelper.getAsFloat(json.getAsJsonObject(), "no_sunlight_growth_multiplier", 1f);
			if (plantGrowthModifier.noSunlightMultiplier != 1d) {
				plantGrowthModifier.minSunlightRequired = GsonHelper.getAsInt(json.getAsJsonObject(), "min_sunlight_required");
				if (plantGrowthModifier.minSunlightRequired < 0 || plantGrowthModifier.minSunlightRequired > 15)
					throw new JsonParseException("Invalid min_sunlight_required, must be between 0 and 15");
			}
			plantGrowthModifier.nightTimeMultiplier = GsonHelper.getAsFloat(json.getAsJsonObject(), "night_time_growth_multiplier", 1f);
			JsonArray aCorrectBiomes = GsonHelper.getAsJsonArray(json.getAsJsonObject(), "correct_biomes", null);
			if (aCorrectBiomes != null) {
				for (JsonElement jsonElement : aCorrectBiomes) {
					IdTagMatcher biome = context.deserialize(jsonElement, IdTagMatcher.class);
					plantGrowthModifier.correctBiomes.add(biome);
				}
				plantGrowthModifier.wrongBiomeMultiplier = GsonHelper.getAsFloat(json.getAsJsonObject(), "wrong_biome_multiplier");
			}

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
			if (src.noSunlightMultiplier != 1d)
				jsonObject.addProperty("no_sunlight_growth_multiplier", src.noSunlightMultiplier);
			if (src.minSunlightRequired != 0)
				jsonObject.addProperty("min_sunlight_required", src.minSunlightRequired);
			if (src.nightTimeMultiplier != 1d)
				jsonObject.addProperty("night_time_growth_multiplier", src.nightTimeMultiplier);
			if (!src.correctBiomes.isEmpty()) {
				JsonArray aCorrectBiomes = new JsonArray();
				for (IdTagMatcher biome : src.correctBiomes) {
					aCorrectBiomes.add(context.serialize(biome, IdTagMatcher.class));
				}
				jsonObject.add("correct_biomes", aCorrectBiomes);
				if (src.wrongBiomeMultiplier != 1d)
					jsonObject.addProperty("wrong_biome_multiplier", src.wrongBiomeMultiplier);
			}
			return jsonObject;
		}
	}
}