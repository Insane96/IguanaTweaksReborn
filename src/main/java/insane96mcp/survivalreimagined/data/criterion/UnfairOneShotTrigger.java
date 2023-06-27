package insane96mcp.survivalreimagined.data.criterion;

import com.google.gson.JsonObject;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SurvivalReimagined.MOD_ID)
public class UnfairOneShotTrigger extends SimpleCriterionTrigger<UnfairOneShotTrigger.TriggerInstance> {
	static final ResourceLocation ID = new ResourceLocation(SurvivalReimagined.MOD_ID, "unfair_oneshot");

	public static UnfairOneShotTrigger TRIGGER = CriteriaTriggers.register(new UnfairOneShotTrigger());

	@Override
	protected TriggerInstance createInstance(JsonObject jsonObject, EntityPredicate.Composite entityPredicateComposite, DeserializationContext context) {
		return new TriggerInstance(entityPredicateComposite);
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	public void trigger(ServerPlayer player) {
		this.trigger(player, TriggerInstance::matches);
	}

	public static class TriggerInstance extends AbstractCriterionTriggerInstance {
		public TriggerInstance(EntityPredicate.Composite composite) {
			super(ID, composite);
		}

		public boolean matches() {
			return true;
		}
	}
}
