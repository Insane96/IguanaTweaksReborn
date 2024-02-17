package insane96mcp.iguanatweaksreborn.module.farming.plantsgrowth;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@JsonAdapter(PlantGrowthMultiplier.Serializer.class)
public class PlantGrowthMultiplier {
	public final IdTagMatcher block;
	private final float growthMultiplier;
	protected final List<PlantGrowthModifier> modifiers = new ArrayList<>();
	/*private float noSunlightMultiplier = 1f;
	private int minSunlightRequired = 0;
	private float nightTimeMultiplier = 1f;
	private List<IdTagMatcher> correctBiomes = new ArrayList<>();
	private boolean invertCorrectBiomes = false;
	private float wrongBiomeMultiplier = 1f;
	public final List<SeasonMultiplier> seasonsMultipliers = new ArrayList<>();*/

	private PlantGrowthMultiplier(IdTagMatcher block, float growthMultiplier, List<PlantGrowthModifier> modifiers) {
		this.block = block;
		this.growthMultiplier = growthMultiplier;
		this.modifiers.addAll(modifiers);
	}

	/*@JsonAdapter(SeasonMultiplier.Serializer.class)
	public record SeasonMultiplier(Object season, float multiplier) {
		public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<SeasonMultiplier>>(){}.getType();

		public static class Serializer implements JsonDeserializer<SeasonMultiplier>, JsonSerializer<SeasonMultiplier> {
			@Override
			public SeasonMultiplier deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
				JsonObject jObject = json.getAsJsonObject();
				Object season = null;
				if (jObject.has("season")) {
					if (!ModList.get().isLoaded("sereneseasons"))
						throw new JsonParseException("Tried deserializing Plant Growth Modifier `season_multipliers` but Serene Season is not installed");
					season = SereneSeasons.deserializeSeason(jObject, "season");
				}
				float multiplier = GsonHelper.getAsFloat(jObject, "multiplier");

				return new SeasonMultiplier(season, multiplier);
			}

			@Override
			public JsonElement serialize(SeasonMultiplier src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
				JsonObject jObject = new JsonObject();
				if (src.season != null) {
					if (!ModList.get().isLoaded("sereneseasons"))
						throw new JsonParseException("Tried serializing Plant Growth Modifier `season_multipliers` but Serene Season is not installed");
					jObject.addProperty("season", SereneSeasons.serializeSeason(src.season));
				}
				jObject.addProperty("multiplier", src.multiplier);
				return jObject;
			}
		}
	}
*/
	/**
	 * Returns >=1 for the chance 1 in this to grow
	 * Values between 0 and 1 have no effect
	 * Returns 0 when the plant will not grow
	 */
	public float getMultiplier(BlockState state, Level level, BlockPos pos) {
		if (!this.block.matchesBlock(state))
			return 1f;
		float multiplier = this.growthMultiplier;
		for (PlantGrowthModifier modifier : this.modifiers) {
			multiplier *= modifier.getMultiplier(state, level, pos);
		}
		return multiplier;
		/*int dayTime = (int) (level.dayTime() % 24000);
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
			//If is not in correct biome or if is but the correct biomes becomes wrong biomes
			if (!this.invertCorrectBiomes && !isInCorrectBiome
					|| this.invertCorrectBiomes && isInCorrectBiome) {
				multiplier *= this.wrongBiomeMultiplier;
			}
		}
		if (!this.seasonsMultipliers.isEmpty()) {
			for (SeasonMultiplier seasonMultiplier : this.seasonsMultipliers) {
				if (SereneSeasons.doesSeasonMatch(seasonMultiplier.season, level)) {
					multiplier *= seasonMultiplier.multiplier;
					break;
				}
			}
		}

		return multiplier;*/
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<PlantGrowthMultiplier>>(){}.getType();

	public static class Serializer implements JsonDeserializer<PlantGrowthMultiplier>/*, JsonSerializer<PlantGrowthMultiplier>*/ {
		@Override
		public PlantGrowthMultiplier deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jObject = json.getAsJsonObject();
			IdTagMatcher block = context.deserialize(jObject.get("block"), IdTagMatcher.class);
			float multiplier = GsonHelper.getAsFloat(jObject, "growth_multiplier", 1f);

			List<PlantGrowthModifier> modifiers = new ArrayList<>();
			JsonArray aModifiers = GsonHelper.getAsJsonArray(jObject, "modifiers", null);
			if (aModifiers != null) {
				for (JsonElement jsonElement : aModifiers) {
					JsonObject jObjectModifier = jsonElement.getAsJsonObject();
					ResourceLocation modifierId = ResourceLocation.tryParse(GsonHelper.getAsString(jObjectModifier, "id"));
					Type modifierType = PlantGrowthModifiers.MODIFIERS.get(modifierId);
					if (modifierType == null)
						throw new JsonParseException("modifier %s does not exist".formatted(modifierId));
					modifiers.add(context.deserialize(jObjectModifier, modifierType));
				}
			}

			return new PlantGrowthMultiplier(block, multiplier, modifiers);
		}

		/*@Override
		public JsonElement serialize(PlantGrowthMultiplier src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
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
				if (src.invertCorrectBiomes)
					jsonObject.addProperty("inverse_correct_biomes", true);
			}
			if (!src.seasonsMultipliers.isEmpty()) {
				JsonArray aSeasonMultipliers = new JsonArray();
				for (SeasonMultiplier seasonMultiplier : src.seasonsMultipliers) {
					aSeasonMultipliers.add(context.serialize(seasonMultiplier, SeasonMultiplier.class));
				}
				jsonObject.add("season_multipliers", aSeasonMultipliers);
			}
			return jsonObject;
		}*/
	}
}