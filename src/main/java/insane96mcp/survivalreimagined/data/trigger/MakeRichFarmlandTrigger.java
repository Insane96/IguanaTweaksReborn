package insane96mcp.survivalreimagined.data.trigger;

import com.google.gson.JsonObject;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SurvivalReimagined.MOD_ID)
public class MakeRichFarmlandTrigger extends SimpleCriterionTrigger<MakeRichFarmlandTrigger.TriggerInstance> {
	static final ResourceLocation ID = new ResourceLocation(SurvivalReimagined.MOD_ID, "make_rich_farmland");

	public static MakeRichFarmlandTrigger TRIGGER = CriteriaTriggers.register(new MakeRichFarmlandTrigger());

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
