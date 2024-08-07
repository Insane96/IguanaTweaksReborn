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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@JsonAdapter(BiomeModifier.Serializer.class)
public class BiomeModifier extends Modifier {
    protected final List<IdTagMatcher> biomes = new ArrayList<>();
    protected final boolean inverse;

    protected BiomeModifier(float modifier, Operation operation, List<IdTagMatcher> biomes, boolean inverse) {
        super(modifier, operation);
        this.biomes.addAll(biomes);
        this.inverse = inverse;
    }

    @Override
    public boolean shouldApply(Level level, BlockPos pos, @Nullable LivingEntity entity) {
        Holder<Biome> currentBiome = level.getBiome(pos);
        for (IdTagMatcher biome : this.biomes) {
            if (biome.matchesBiome(currentBiome))
                return !inverse;
        }
        return inverse;
    }

    public static class Serializer implements JsonDeserializer<BiomeModifier> {
        @Override
        public BiomeModifier deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            List<IdTagMatcher> biomes = new ArrayList<>();
            JsonArray aCorrectBiomes = GsonHelper.getAsJsonArray(json.getAsJsonObject(), "biomes");
            if (aCorrectBiomes.isEmpty())
                throw new JsonParseException("biomes list must contain at least one entry");
            for (JsonElement jsonElement : aCorrectBiomes) {
                IdTagMatcher biome = context.deserialize(jsonElement, IdTagMatcher.class);
                biomes.add(biome);
            }
            return new BiomeModifier(
                    GsonHelper.getAsFloat(jObject, "modifier"),
                    context.deserialize(jObject.get("operation"), Operation.class),
                    biomes,
                    GsonHelper.getAsBoolean(jObject, "inverse")
            );
        }
    }
}
