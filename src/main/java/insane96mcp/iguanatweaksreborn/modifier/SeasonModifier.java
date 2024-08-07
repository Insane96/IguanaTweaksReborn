package insane96mcp.iguanatweaksreborn.modifier;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

import java.util.ArrayList;
import java.util.List;

@JsonAdapter(SeasonModifier.Serializer.class)
public class SeasonModifier extends Modifier {
    final List<Season> seasons = new ArrayList<>();
    protected SeasonModifier(float modifier, Operation operation, List<Season> seasons) {
        super(modifier, operation);
        this.seasons.addAll(seasons);
    }

    @Override
    public boolean shouldApply(Level level, BlockPos pos, @Nullable LivingEntity entity) {
        for (Season season : this.seasons) {
            if (SeasonHelper.getSeasonState(level).getSeason().equals(season))
                return true;
        }
        return false;
    }

    public static class Serializer implements JsonDeserializer<SeasonModifier> {
        @Override
        public SeasonModifier deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            List<Season> seasons = new ArrayList<>();
            JsonArray aSeasons = GsonHelper.getAsJsonArray(json.getAsJsonObject(), "seasons");
            if (aSeasons.isEmpty())
                throw new JsonParseException("seasons list must contain at least one entry");
            for (JsonElement jsonElement : aSeasons) {
                Season season = context.deserialize(jsonElement, Season.class);
                seasons.add(season);
            }
            return new SeasonModifier(
                    GsonHelper.getAsFloat(jObject, "modifier"),
                    context.deserialize(jObject.get("operation"), Operation.class),
                    seasons
            );
        }
    }
}
