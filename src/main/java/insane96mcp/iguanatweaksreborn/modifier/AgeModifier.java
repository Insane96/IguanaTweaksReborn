package insane96mcp.iguanatweaksreborn.modifier;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.iguanatweaksreborn.module.farming.livestock.Livestock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@JsonAdapter(AgeModifier.Serializer.class)
public class AgeModifier extends Modifier {
    Livestock.Age age;
    protected AgeModifier(float multiplier, Livestock.Age age) {
        super(multiplier);
        this.age = age;
    }

    @Override
    public float getMultiplier(LivingEntity entity, Level level, BlockPos pos) {
        if (!(entity instanceof AgeableMob ageableMob)
                || Livestock.getAge(ageableMob) != this.age)
            return 1f;
        return this.multiplier;
    }

    public static class Serializer implements JsonDeserializer<AgeModifier> {
        @Override
        public AgeModifier deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new AgeModifier(GsonHelper.getAsFloat(jObject, "multiplier"), context.deserialize(jObject.get("age"), Livestock.Age.class));
        }
    }
}
