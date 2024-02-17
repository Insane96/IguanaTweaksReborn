package insane96mcp.iguanatweaksreborn.module.farming.plantsgrowth.modifier;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

@JsonAdapter(CorrectBiomeModifier.Serializer.class)
public class CorrectBiomeModifier extends AbstractBiomeModifier {
    protected CorrectBiomeModifier(float multiplier, List<IdTagMatcher> biomes) {
        super(multiplier, biomes);
    }

    @Override
    public float getMultiplier(BlockState state, Level level, BlockPos pos) {
        Holder<Biome> currentBiome = level.getBiome(pos);
        for (IdTagMatcher biome : this.biomes) {
            if (biome.matchesBiome(currentBiome))
                return 1f;
        }
        return this.multiplier;
    }

    public static class Serializer implements JsonDeserializer<CorrectBiomeModifier> {
        @Override
        public CorrectBiomeModifier deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            List<IdTagMatcher> biomes = new ArrayList<>();
            JsonArray aCorrectBiomes = GsonHelper.getAsJsonArray(json.getAsJsonObject(), "biomes");
            for (JsonElement jsonElement : aCorrectBiomes) {
                IdTagMatcher biome = context.deserialize(jsonElement, IdTagMatcher.class);
                biomes.add(biome);
            }
            return new CorrectBiomeModifier(GsonHelper.getAsFloat(jObject, "multiplier"), biomes);
        }
    }
}