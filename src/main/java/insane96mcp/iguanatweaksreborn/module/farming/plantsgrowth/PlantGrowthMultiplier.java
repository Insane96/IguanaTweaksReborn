package insane96mcp.iguanatweaksreborn.module.farming.plantsgrowth;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.iguanatweaksreborn.modifier.Modifier;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

@JsonAdapter(PlantGrowthMultiplier.Serializer.class)
public class PlantGrowthMultiplier {
	public final IdTagMatcher block;
	private final float growthMultiplier;
	protected final List<Modifier> modifiers = new ArrayList<>();

	private PlantGrowthMultiplier(IdTagMatcher block, float growthMultiplier, List<Modifier> modifiers) {
		this.block = block;
		this.growthMultiplier = growthMultiplier;
		this.modifiers.addAll(modifiers);
	}

	/**
	 * Returns >=1 for the chance 1 in this to grow
	 * Values between 0 and 1 have no effect
	 * Returns 0 when the plant will not grow
	 */
	public float getMultiplier(BlockState state, Level level, BlockPos pos) {
		if (!this.block.matchesBlock(state))
			return 1f;
        return Modifier.applyModifiers(this.growthMultiplier, this.modifiers, level, pos, null);
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<PlantGrowthMultiplier>>(){}.getType();

	public static class Serializer implements JsonDeserializer<PlantGrowthMultiplier>/*, JsonSerializer<PlantGrowthMultiplier>*/ {
		@Override
		public PlantGrowthMultiplier deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jObject = json.getAsJsonObject();
			IdTagMatcher block = context.deserialize(jObject.get("block"), IdTagMatcher.class);
			float multiplier = GsonHelper.getAsFloat(jObject, "growth_multiplier", 1f);

			List<Modifier> modifiers = Modifier.deserializeList(jObject, "modifiers", context);

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