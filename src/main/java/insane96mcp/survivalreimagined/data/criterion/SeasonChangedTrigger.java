package insane96mcp.survivalreimagined.data.criterion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonChangedEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = SurvivalReimagined.MOD_ID)
public class SeasonChangedTrigger extends SimpleCriterionTrigger<SeasonChangedTrigger.TriggerInstance> {
	static final ResourceLocation ID = new ResourceLocation(SurvivalReimagined.MOD_ID, "season_changed");

	public static SeasonChangedTrigger TRIGGER = CriteriaTriggers.register(new SeasonChangedTrigger());

	@Override
	protected TriggerInstance createInstance(JsonObject jsonObject, EntityPredicate.Composite entityPredicateComposite, DeserializationContext context) {
		List<Season.SubSeason> seasons = new ArrayList<>();
		if (jsonObject.has("seasons")) {
			JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "seasons");
			for(int i = 0; i < jsonArray.size(); ++i) {
				JsonElement jsonElement = jsonArray.get(i);
				if (jsonElement.isJsonPrimitive()) {
					seasons.add(Season.SubSeason.valueOf(jsonElement.getAsString()));
				}
			}
		}
		return new TriggerInstance(entityPredicateComposite, seasons);
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	public void trigger(ServerPlayer player, Season.SubSeason season) {
		this.trigger(player, triggerInstance -> triggerInstance.matches(season));
	}

	@SubscribeEvent
	public static void onAnvilRepair(SeasonChangedEvent.Standard event) {
		if (!(event.getLevel() instanceof ServerLevel serverLevel))
			return;

		serverLevel.players().forEach(serverPlayer -> TRIGGER.trigger(serverPlayer, event.getNewSeason()));
	}

	public static class TriggerInstance extends AbstractCriterionTriggerInstance {
		public List<Season.SubSeason> seasons;
		public TriggerInstance(EntityPredicate.Composite composite, List<Season.SubSeason> seasons) {
			super(ID, composite);
			this.seasons = seasons;
		}

		public JsonObject serializeToJson(SerializationContext context) {
			JsonObject jsonObject = super.serializeToJson(context);
			JsonArray jsonArray = new JsonArray();
			for (Season.SubSeason season : this.seasons) {
				jsonArray.add(season.toString());
			}
			jsonObject.add("seasons", jsonArray);

			return jsonObject;
		}

		public boolean matches(Season.SubSeason season) {
			return this.seasons.contains(season);
		}
	}
}
