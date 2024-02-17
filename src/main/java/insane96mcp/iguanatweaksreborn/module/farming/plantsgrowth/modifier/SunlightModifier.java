package insane96mcp.iguanatweaksreborn.module.farming.plantsgrowth.modifier;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.iguanatweaksreborn.module.farming.plantsgrowth.PlantGrowthModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;

@JsonAdapter(SunlightModifier.Serializer.class)
public class SunlightModifier extends PlantGrowthModifier {
    int minSunlight;
    protected SunlightModifier(float multiplier, int minSunlight) {
        super(multiplier);
        this.minSunlight = minSunlight;
    }

    @Override
    public float getMultiplier(BlockState state, Level level, BlockPos pos) {
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        if (skyLight < this.minSunlight)
            return this.multiplier;
        return 1f;
    }

    public static class Serializer implements JsonDeserializer<SunlightModifier> {
        @Override
        public SunlightModifier deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new SunlightModifier(GsonHelper.getAsFloat(jObject, "multiplier"), GsonHelper.getAsInt(jObject, "min_sunlight"));
        }
    }
}
