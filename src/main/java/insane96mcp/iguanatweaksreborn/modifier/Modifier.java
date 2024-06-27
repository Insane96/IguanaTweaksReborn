package insane96mcp.iguanatweaksreborn.modifier;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.util.LogHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@JsonAdapter(Modifier.Serializer.class)
public class Modifier {
    protected final float multiplier;

    protected Modifier(float multiplier) {
        this.multiplier = multiplier;
    }

    public float getMultiplier(LivingEntity entity, Level level, BlockPos pos) {
        return this.multiplier;
    }

    public float getMultiplier(Level level, BlockPos pos) {
        return this.multiplier;
    }

    public static class Serializer implements JsonDeserializer<Modifier> {
        @Override
        public Modifier deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new Modifier(GsonHelper.getAsFloat(jObject, "multiplier"));
        }
    }

    public static List<Modifier> getListFromJson(JsonObject jObject, String memberName, JsonDeserializationContext context) {
        List<Modifier> modifiers = new ArrayList<>();
        if (!jObject.has(memberName))
            return modifiers;
        JsonArray aModifiers = GsonHelper.getAsJsonArray(jObject, memberName);
        for (JsonElement jsonElement : aModifiers) {
            JsonObject jObjectModifier = jsonElement.getAsJsonObject();
            ResourceLocation modifierId = ResourceLocation.tryParse(GsonHelper.getAsString(jObjectModifier, "id"));
            Type modifierType = Modifiers.MODIFIERS.get(modifierId);
            if (modifierType == null) {
                LogHelper.error("modifier %s does not exist. Skipping".formatted(modifierId));
                continue;
            }
            modifiers.add(context.deserialize(jObjectModifier, modifierType));
        }
        return modifiers;
    }
}
