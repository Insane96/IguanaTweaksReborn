package insane96mcp.survivalreimagined.data.trigger;

import com.google.gson.JsonObject;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SurvivalReimagined.MOD_ID)
public class AnvilTransformBlockTrigger extends SimpleCriterionTrigger<AnvilTransformBlockTrigger.TriggerInstance> {
	static final ResourceLocation ID = new ResourceLocation(SurvivalReimagined.MOD_ID, "anvil_transform_block");

	public static AnvilTransformBlockTrigger TRIGGER = CriteriaTriggers.register(new AnvilTransformBlockTrigger());

	@Override
	protected TriggerInstance createInstance(JsonObject jsonObject, EntityPredicate.Composite entityPredicateComposite, DeserializationContext context) {
		return new TriggerInstance(entityPredicateComposite);
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	//TODO Add block transformed and block transformed to criteria
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
