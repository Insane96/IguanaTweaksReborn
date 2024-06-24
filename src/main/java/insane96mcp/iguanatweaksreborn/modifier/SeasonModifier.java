package insane96mcp.iguanatweaksreborn.modifier;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

import java.util.ArrayList;
import java.util.List;

@JsonAdapter(SeasonModifier.Serializer.class)
public class SeasonModifier extends Modifier {
    final List<Season> seasons = new ArrayList<>();
    protected SeasonModifier(float multiplier, List<Season> seasons) {
        super(multiplier);
        this.seasons.addAll(seasons);
    }

    @Override
    public float getMultiplier(Level level, BlockPos pos) {
        for (Season season : this.seasons) {
            if (SeasonHelper.getSeasonState(level).getSeason().equals(season)) {
                return this.multiplier;
            }
        }
        return 1f;
    }

    public static class Serializer implements JsonDeserializer<SeasonModifier> {
        @Override
        public SeasonModifier deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            List<Season> seasons = new ArrayList<>();
            JsonArray aCorrectBiomes = GsonHelper.getAsJsonArray(json.getAsJsonObject(), "seasons");
            for (JsonElement jsonElement : aCorrectBiomes) {
                Season season = context.deserialize(jsonElement, Season.class);
                seasons.add(season);
            }
            return new SeasonModifier(GsonHelper.getAsFloat(jObject, "multiplier"), seasons);
        }
    }
}
