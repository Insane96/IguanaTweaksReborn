package insane96mcp.survivalreimagined.data.trigger;

import com.google.gson.JsonObject;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SurvivalReimagined.MOD_ID)
public class AnvilRepairTrigger extends SimpleCriterionTrigger<AnvilRepairTrigger.TriggerInstance> {
	static final ResourceLocation ID = new ResourceLocation(SurvivalReimagined.MOD_ID, "anvil_repair");

	public static AnvilRepairTrigger TRIGGER = CriteriaTriggers.register(new AnvilRepairTrigger());

	@Override
	protected TriggerInstance createInstance(JsonObject jsonObject, EntityPredicate.Composite entityPredicateComposite, DeserializationContext context) {
		ItemPredicate item1Predicates = ItemPredicate.fromJson(jsonObject.get("input1"));
		ItemPredicate item2Predicates = ItemPredicate.fromJson(jsonObject.get("input2"));
		ItemPredicate outputPredicates = ItemPredicate.fromJson(jsonObject.get("output"));
		return new TriggerInstance(entityPredicateComposite, item1Predicates, item2Predicates, outputPredicates);
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	protected void trigger(ServerPlayer player, ItemStack stack1, ItemStack stack2, ItemStack output) {
		this.trigger(player, (triggerInstance -> triggerInstance.matches(stack1, stack2, output)));
	}

	@SubscribeEvent
	public static void onAnvilRepair(AnvilRepairEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player))
			return;
		TRIGGER.trigger(player, event.getLeft(), event.getRight(), event.getOutput());
	}

	public static class TriggerInstance extends AbstractCriterionTriggerInstance {
		private final ItemPredicate inputPredicate;
		private final ItemPredicate input2Predicate;
		private final ItemPredicate outputPredicate;


		public TriggerInstance(EntityPredicate.Composite composite, ItemPredicate inputPredicate, ItemPredicate input2Predicate, ItemPredicate outputPredicate) {
			super(ID, composite);
			this.inputPredicate = inputPredicate;
			this.input2Predicate = input2Predicate;
			this.outputPredicate = outputPredicate;
		}

		public JsonObject serializeToJson(SerializationContext context) {
			JsonObject jsonobject = super.serializeToJson(context);
			if (this.inputPredicate != ItemPredicate.ANY) {
				jsonobject.add("input1", this.inputPredicate.serializeToJson());
			}
			if (this.input2Predicate != ItemPredicate.ANY) {
				jsonobject.add("input2", this.input2Predicate.serializeToJson());
			}
			if (this.outputPredicate != ItemPredicate.ANY) {
				jsonobject.add("output", this.outputPredicate.serializeToJson());
			}

			return jsonobject;
		}

		public boolean matches(ItemStack item1, ItemStack item2, ItemStack output) {
			if (inputPredicate != ItemPredicate.ANY && !inputPredicate.matches(item1))
				return false;
			if (input2Predicate != ItemPredicate.ANY && !input2Predicate.matches(item2))
				return false;
			if (outputPredicate != ItemPredicate.ANY && !outputPredicate.matches(output))
				return false;
			return true;
		}
	}
}
