package insane96mcp.iguanatweaksreborn.module.world.timber;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;

@JsonAdapter(TreeInfo.Serializer.class)
public class TreeInfo {
    public IdTagMatcher log;
    public IdTagMatcher leaves;
    /**
     * result of straight logs / sideways logs. Tree will fall only if the ratio is above this value
     * E.g. 0.5 means that there should be a number of sideways logs equal or less half the number of straight logs
     */
    public float logsSidewaysRatio;
    public int minLogs;
    /**
     * Max leaves (manhattan) distance from logs
     */
    public int maxDistanceFromLogs;
    public float decayPercentage;

    public TreeInfo(IdTagMatcher log, IdTagMatcher leaves, float logsSidewaysRatio, int minLogs, int maxDistanceFromLogs, float decayPercentage) {
        this.log = log;
        this.leaves = leaves;
        this.logsSidewaysRatio = logsSidewaysRatio;
        this.minLogs = minLogs;
        this.maxDistanceFromLogs = maxDistanceFromLogs;
        this.decayPercentage = decayPercentage;
    }

    public TreeInfo() {
        this.logsSidewaysRatio = 0.6f;
        this.minLogs = 3;
        this.maxDistanceFromLogs = 8;
        this.decayPercentage = 0.1f;
    }

    public static class Builder {
        private final TreeInfo treeInfo = new TreeInfo();

        public Builder log(IdTagMatcher log) {
            treeInfo.log = log;
            return this;
        }

        public Builder leaves(IdTagMatcher leaves) {
            treeInfo.leaves = leaves;
            return this;
        }

        public Builder logsSidewaysRatio(float logsSidewaysRatio) {
            treeInfo.logsSidewaysRatio = logsSidewaysRatio;
            return this;
        }

        public Builder minLogs(int minLogs) {
            treeInfo.minLogs = minLogs;
            return this;
        }

        public Builder maxDistanceFromLogs(int maxDistanceFromLogs) {
            treeInfo.maxDistanceFromLogs = maxDistanceFromLogs;
            return this;
        }

        public Builder decayPercentage(float decayPercentage) {
            treeInfo.decayPercentage = decayPercentage;
            return this;
        }

        public TreeInfo build() {
            return treeInfo;
        }
    }

    public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<TreeInfo>>(){}.getType();
    public static class Serializer implements JsonDeserializer<TreeInfo>, JsonSerializer<TreeInfo> {
        @Override
        public TreeInfo deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            IdTagMatcher log = context.deserialize(jObject.get("log"), IdTagMatcher.class);
            IdTagMatcher leaves = context.deserialize(jObject.get("leaves"), IdTagMatcher.class);
            float logsSidewaysRatio = GsonHelper.getAsFloat(jObject, "logs_sideways_ratio", 0.6f);
            int minLogs = GsonHelper.getAsInt(jObject, "min_logs", 3);
            int maxDistanceFromLogs = GsonHelper.getAsInt(jObject, "max_distance_from_logs", 8);
            float decayPercentage = GsonHelper.getAsFloat(jObject, "decay_percentage", 0.1f);

            return new TreeInfo(log, leaves, logsSidewaysRatio, minLogs, maxDistanceFromLogs, decayPercentage);
        }

        @Override
        public JsonElement serialize(TreeInfo src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("log", context.serialize(src.log));
            jsonObject.add("leaves", context.serialize(src.leaves));
            if (src.logsSidewaysRatio != 0.6f)
                jsonObject.addProperty("logs_sideways_ratio", src.logsSidewaysRatio);
            if (src.minLogs != 3)
                jsonObject.addProperty("min_logs", src.minLogs);
            if (src.maxDistanceFromLogs != 8)
                jsonObject.addProperty("max_distance_from_logs", src.maxDistanceFromLogs);
            if (src.decayPercentage != 0.1f)
                jsonObject.addProperty("decay_percentage", src.decayPercentage);

            return jsonObject;
        }
    }
}
