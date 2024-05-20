package insane96mcp.iguanatweaksreborn.modifier;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;

@JsonAdapter(WrongBiomeModifier.Serializer.class)
public class WrongBiomeModifier extends AbstractBiomeModifier {
    protected WrongBiomeModifier(float multiplier, List<IdTagMatcher> biomes) {
        super(multiplier, biomes);
    }

    @Override
    public float getMultiplier(Level level, BlockPos pos) {
        Holder<Biome> currentBiome = level.getBiome(pos);
        for (IdTagMatcher biome : this.biomes) {
            if (biome.matchesBiome(currentBiome))
                return this.multiplier;
        }
        return 1f;
    }

    @Override
    public float getMultiplier(LivingEntity entity, Level level, BlockPos pos) {
        return this.getMultiplier(level, pos);
    }

    public static class Serializer implements JsonDeserializer<WrongBiomeModifier> {
        @Override
        public WrongBiomeModifier deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            List<IdTagMatcher> biomes = new ArrayList<>();
            JsonArray aCorrectBiomes = GsonHelper.getAsJsonArray(json.getAsJsonObject(), "biomes");
            for (JsonElement jsonElement : aCorrectBiomes) {
                IdTagMatcher biome = context.deserialize(jsonElement, IdTagMatcher.class);
                biomes.add(biome);
            }
            return new WrongBiomeModifier(GsonHelper.getAsFloat(jObject, "multiplier"), biomes);
        }
    }
}