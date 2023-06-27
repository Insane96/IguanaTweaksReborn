package insane96mcp.survivalreimagined.data.criterion;

import com.google.gson.JsonObject;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SurvivalReimagined.MOD_ID)
public class OverweightCrateCarryTrigger extends SimpleCriterionTrigger<OverweightCrateCarryTrigger.TriggerInstance> {
	static final ResourceLocation ID = new ResourceLocation(SurvivalReimagined.MOD_ID, "overweight_crate_carry");

	public static OverweightCrateCarryTrigger TRIGGER = CriteriaTriggers.register(new OverweightCrateCarryTrigger());

	@Override
	protected TriggerInstance createInstance(JsonObject jsonObject, EntityPredicate.Composite entityPredicateComposite, DeserializationContext context) {
		return new TriggerInstance(entityPredicateComposite);
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	public void trigger(ServerPlayer player) {
		this.trigger(player, (TriggerInstance::matches));
	}

	public static class TriggerInstance extends AbstractCriterionTriggerInstance {
		public TriggerInstance(EntityPredicate.Composite composite) {
			super(ID, composite);
		}

		public JsonObject serializeToJson(SerializationContext context) {
			return super.serializeToJson(context);
		}

		public boolean matches() {
			return true;
		}
	}
}
