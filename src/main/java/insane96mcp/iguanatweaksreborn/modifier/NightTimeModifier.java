package insane96mcp.iguanatweaksreborn.modifier;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@JsonAdapter(NightTimeModifier.Serializer.class)
public class NightTimeModifier extends Modifier {
    protected NightTimeModifier(float multiplier) {
        super(multiplier);
    }

    @Override
    public float getMultiplier(LivingEntity entity, Level level, BlockPos pos) {
        int dayTime = (int) (level.dayTime() % 24000);
        if (dayTime >= 12786 && dayTime < 23216)
            return this.multiplier;
        return 1f;
    }

    public static class Serializer implements JsonDeserializer<NightTimeModifier> {
        @Override
        public NightTimeModifier deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new NightTimeModifier(GsonHelper.getAsFloat(jObject, "multiplier"));
        }
    }
}
