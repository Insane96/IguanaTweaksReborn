package insane96mcp.iguanatweaksreborn.module.farming.livestock;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.iguanatweaksreborn.modifier.Modifier;
import insane96mcp.iguanatweaksreborn.utils.ITRGsonHelper;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@JsonAdapter(LivestockData.Serializer.class)
public class LivestockData {
	final IdTagMatcher entity;
	@Nullable
	final Float livingDays;
	final float livingDaysFluctuation;
	final List<Modifier> growthSpeed;
	final List<Modifier> breedingCooldown;
	final List<Modifier> eggLayCooldown;
	@Nullable
	final Float breedingFailChance;
	final List<Modifier> breedingFailChanceModifiers;
	@Nullable
	final Integer cowFluidCooldown;
	final List<Modifier> cowFluidCooldownModifiers;
	@Nullable
	final Float sheepWoolGrowthChance;
	final List<Modifier> sheepWoolGrowthChanceModifiers;

    public LivestockData(IdTagMatcher entity, @Nullable Float livingDays, float livingDaysFluctuation, List<Modifier> growthSpeed, List<Modifier> breedingCooldown, List<Modifier> eggLayCooldown, @Nullable Float breedingFailChance, List<Modifier> breedingFailChanceModifiers, @Nullable Integer cowFluidCooldown, List<Modifier> cowFluidCooldownModifiers, @Nullable Float sheepWoolGrowthChance, List<Modifier> sheepWoolGrowthChanceModifiers) {
        this.entity = entity;
		this.livingDays = livingDays;
		this.livingDaysFluctuation = livingDaysFluctuation;
        this.growthSpeed = growthSpeed;
        this.breedingCooldown = breedingCooldown;
        this.eggLayCooldown = eggLayCooldown;
        this.breedingFailChance = breedingFailChance;
        this.breedingFailChanceModifiers = breedingFailChanceModifiers;
        this.cowFluidCooldown = cowFluidCooldown;
        this.cowFluidCooldownModifiers = cowFluidCooldownModifiers;
        this.sheepWoolGrowthChance = sheepWoolGrowthChance;
        this.sheepWoolGrowthChanceModifiers = sheepWoolGrowthChanceModifiers;
    }

	public boolean matches(Entity entity) {
		return this.entity.matchesEntity(entity);
	}

	public float getLivingDays(LivingEntity entity) {
		if (this.livingDays == null)
			return 0;
		return this.livingDays + ((entity.getRandom().nextFloat() * this.livingDaysFluctuation * 2f) - this.livingDaysFluctuation);
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<LivestockData>>(){}.getType();
    public static class Serializer implements JsonDeserializer<LivestockData> {
		@Override
		public LivestockData deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jObject = json.getAsJsonObject();
			IdTagMatcher entity = context.deserialize(jObject.get("entity"), IdTagMatcher.class);
			Float livingDays = ITRGsonHelper.getAsNullableFloat(jObject, "living_days");
			float livingDaysFluctuation = GsonHelper.getAsFloat(jObject, "living_days_fluctuation", 0);
			List<Modifier> growthSpeed = Modifier.getListFromJson(jObject, "growth_speed", context);
			List<Modifier> breedingCooldown = Modifier.getListFromJson(jObject, "breeding_cooldown", context);
			List<Modifier> eggLayCooldown = Modifier.getListFromJson(jObject, "egg_lay_cooldown", context);
			Float breedingFailChance = ITRGsonHelper.getAsNullableFloat(jObject, "breeding_fail_chance");
			List<Modifier> breedingFailChanceModifiers = Modifier.getListFromJson(jObject, "breeding_fail_chance_modifiers", context);
			Integer cowFluidCooldown = ITRGsonHelper.getAsNullableInt(jObject, "cow_fluid_cooldown");
			List<Modifier> cowFluidCooldownModifiers = Modifier.getListFromJson(jObject, "cow_fluid_cooldown_modifiers", context);
			Float sheepWoolGrowthChance = ITRGsonHelper.getAsNullableFloat(jObject, "sheep_wool_growth_chance");
			List<Modifier> sheepWoolGrowthChanceModifiers = Modifier.getListFromJson(jObject, "sheep_wool_growth_chance_modifiers", context);
			return new LivestockData(entity, livingDays, livingDaysFluctuation, growthSpeed, breedingCooldown, eggLayCooldown, breedingFailChance, breedingFailChanceModifiers, cowFluidCooldown, cowFluidCooldownModifiers, sheepWoolGrowthChance, sheepWoolGrowthChanceModifiers);
		}
	}
}