package insane96mcp.iguanatweaksreborn.module.farming.livestock;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.iguanatweaksreborn.modifier.Modifier;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

@JsonAdapter(LivestockData.Serializer.class)
public class LivestockData {
	final IdTagMatcher entity;
	final List<Modifier> growthSpeed;
	final List<Modifier> breedingCooldown;
	final List<Modifier> eggLayCooldown;
	final float beedingFailChance;
	final List<Modifier> beedingFailChanceModifiers;
	final int cowFluidCooldown;
	final List<Modifier> cowFluidCooldownModifiers;
	final float sheepWoolGrowthChance;
	final List<Modifier> sheepWoolGrowthChanceModifiers;

    public LivestockData(IdTagMatcher entity, List<Modifier> growthSpeed, List<Modifier> breedingCooldown, List<Modifier> eggLayCooldown, float beedingFailChance, List<Modifier> beedingFailChanceModifiers, int cowFluidCooldown, List<Modifier> cowFluidCooldownModifiers, float sheepWoolGrowthChance, List<Modifier> sheepWoolGrowthChanceModifiers) {
        this.entity = entity;
        this.growthSpeed = growthSpeed;
        this.breedingCooldown = breedingCooldown;
        this.eggLayCooldown = eggLayCooldown;
        this.beedingFailChance = beedingFailChance;
        this.beedingFailChanceModifiers = beedingFailChanceModifiers;
        this.cowFluidCooldown = cowFluidCooldown;
        this.cowFluidCooldownModifiers = cowFluidCooldownModifiers;
        this.sheepWoolGrowthChance = sheepWoolGrowthChance;
        this.sheepWoolGrowthChanceModifiers = sheepWoolGrowthChanceModifiers;
    }

	public boolean matches(Entity entity) {
		return this.entity.matchesEntity(entity);
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<LivestockData>>(){}.getType();
    public static class Serializer implements JsonDeserializer<LivestockData> {
		@Override
		public LivestockData deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jObject = json.getAsJsonObject();
			IdTagMatcher entity = context.deserialize(jObject.get("entity"), IdTagMatcher.class);
			List<Modifier> growthSpeed = Modifier.getListFromJson(jObject, "growth_speed", context);
			List<Modifier> breedingCooldown = Modifier.getListFromJson(jObject, "breeding_cooldown", context);
			List<Modifier> eggLayCooldown = Modifier.getListFromJson(jObject, "egg_lay_cooldown", context);
			float breedingFailChance = GsonHelper.getAsFloat(jObject, "breeding_fail_chance", 0f);
			List<Modifier> breedingFailChanceModifiers = Modifier.getListFromJson(jObject, "breeding_fail_chance_modifiers", context);
			int cowFluidCooldown = GsonHelper.getAsInt(jObject, "cow_fluid_cooldown", 0);
			List<Modifier> cowFluidCooldownModifiers = Modifier.getListFromJson(jObject, "cow_fluid_cooldown_modifiers", context);
			float sheepWoolGrowthChance = GsonHelper.getAsFloat(jObject, "sheep_wool_growth_chance", 1f);
			List<Modifier> sheepWoolGrowthChanceModifiers = Modifier.getListFromJson(jObject, "sheep_wool_growth_chance_modifiers", context);
			return new LivestockData(entity, growthSpeed, breedingCooldown, eggLayCooldown, breedingFailChance, breedingFailChanceModifiers, cowFluidCooldown, cowFluidCooldownModifiers, sheepWoolGrowthChance, sheepWoolGrowthChanceModifiers);
		}
	}
}