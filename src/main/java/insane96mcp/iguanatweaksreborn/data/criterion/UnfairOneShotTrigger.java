package insane96mcp.iguanatweaksreborn.data.criterion;

import com.google.gson.JsonObject;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID)
public class UnfairOneShotTrigger extends SimpleCriterionTrigger<UnfairOneShotTrigger.TriggerInstance> {
	static final ResourceLocation ID = new ResourceLocation(IguanaTweaksReborn.MOD_ID, "unfair_oneshot");

	public static UnfairOneShotTrigger TRIGGER = CriteriaTriggers.register(new UnfairOneShotTrigger());

	@Override
	protected TriggerInstance createInstance(JsonObject jsonObject, ContextAwarePredicate pPredicate, DeserializationContext context) {
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
