package insane96mcp.survivalreimagined.data.criterion;

import com.google.gson.JsonObject;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SurvivalReimagined.MOD_ID)
public class ActivateRespawnObeliskTrigger extends SimpleCriterionTrigger<ActivateRespawnObeliskTrigger.TriggerInstance> {
	static final ResourceLocation ID = new ResourceLocation(SurvivalReimagined.MOD_ID, "activate_respawn_obelisk");

	public static ActivateRespawnObeliskTrigger TRIGGER = CriteriaTriggers.register(new ActivateRespawnObeliskTrigger());

	@Override
	protected TriggerInstance createInstance(JsonObject pJson, ContextAwarePredicate pPredicate, DeserializationContext pDeserializationContext) {
		return new TriggerInstance(pPredicate);
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	public void trigger(ServerPlayer player) {
		this.trigger(player, TriggerInstance::matches);
	}

	public static class TriggerInstance extends AbstractCriterionTriggerInstance {
		public TriggerInstance(ContextAwarePredicate pPredicate) {
			super(ID, pPredicate);
		}

		public boolean matches() {
			return true;
		}
	}
}
