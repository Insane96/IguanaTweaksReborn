package insane96mcp.survivalreimagined.module.world.timber;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.util.IdTagMatcher;

import java.util.ArrayList;

@JsonAdapter(LogsLeavesPair.Serializer.class)
public class LogsLeavesPair {

    public final IdTagMatcher log;
    public final IdTagMatcher leaves;

    public LogsLeavesPair(IdTagMatcher log, IdTagMatcher leaves) {
        this.log = log;
        this.leaves = leaves;
    }

    public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<LogsLeavesPair>>(){}.getType();

    public static class Serializer implements JsonDeserializer<LogsLeavesPair>, JsonSerializer<LogsLeavesPair> {
        @Override
        public LogsLeavesPair deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            IdTagMatcher log = context.deserialize(json.getAsJsonObject().getAsJsonObject("log"), IdTagMatcher.class);
            IdTagMatcher leaves = context.deserialize(json.getAsJsonObject().getAsJsonObject("leaves"), IdTagMatcher.class);
            return new LogsLeavesPair(log, leaves);
        }

        @Override
        public JsonElement serialize(LogsLeavesPair src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("log", context.serialize(src.log));
            jsonObject.add("leaves", context.serialize(src.leaves));
            return jsonObject;
        }
    }
}
