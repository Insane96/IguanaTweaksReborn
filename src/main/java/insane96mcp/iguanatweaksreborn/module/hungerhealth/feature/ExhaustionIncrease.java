package insane96mcp.iguanatweaksreborn.module.hungerhealth.feature;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Exhaustion Increase", description = "Make the player consume more hunger with different actions")
@LoadFeature(module = Modules.Ids.HUNGER_HEALTH)
public class ExhaustionIncrease extends Feature {
	@Config(min = 0d, max = 128d)
	@Label(name = "Block Break Exhaustion Multiplier", description = "When you break a block you'll get exhaustion equal to the block hardness multiplied by this value. Setting this to 0 will default to the vanilla exhaustion (0.005). (It's not affected by the Global Hardness Features)")
	public static Double blockBreakExhaustionMultiplier = 0d;
	@Config(min = 0d, max = 128d)
	@Label(name = "Exhaustion per tick when breaking a block", description = "When breaking block you'll get exhaustion every tick during the breaking.")
	public static Double exhaustionOnBlockBreaking = 0.005d;
	@Config(min = 0d, max = 128d)
	@Label(name = "Passive Exhaustion", description = "Every second the player will get this exhaustion.")
	public static Double passiveExhaustion = 0.005d;
	@Config
	@Label(name = "Effective Hunger", description = "When affected by the hunger effect ANY action will give you 100% more exhaustion per level.")
	public static Boolean effectiveHunger = true;

	public ExhaustionIncrease(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void breakExhaustion(BlockEvent.BreakEvent event) {
		if (!this.isEnabled()
				|| blockBreakExhaustionMultiplier == 0d)
			return;
		ServerLevel world = (ServerLevel) event.getLevel();
		BlockState state = world.getBlockState(event.getPos());
		double hardness = state.getDestroySpeed(event.getLevel(), event.getPos());
		double exhaustion = (hardness * blockBreakExhaustionMultiplier) - 0.005f;
		exhaustion = Math.max(exhaustion, 0d);
		event.getPlayer().causeFoodExhaustion((float) exhaustion);
	}

	@SubscribeEvent
	public void onBreakingBlock(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled()
				|| exhaustionOnBlockBreaking == 0d)
			return;
		event.getEntity().causeFoodExhaustion(exhaustionOnBlockBreaking.floatValue());
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (!this.isEnabled()
				|| event.phase != TickEvent.Phase.END
				|| event.player.level.isClientSide
				|| passiveExhaustion == 0d
				|| event.player.tickCount % 20 != 0)
			return;

		event.player.causeFoodExhaustion(passiveExhaustion.floatValue());
	}

	public static float increaseHungerEffectiveness(Player player, float amount) {
		if (!isEnabled(ExhaustionIncrease.class)
				|| !effectiveHunger
				|| !player.hasEffect(MobEffects.HUNGER))
			return amount;

		//noinspection ConstantConditions
		int amp = player.getEffect(MobEffects.HUNGER).getAmplifier() + 1;
		return amount * (amp * 1f + 1);
	}
}
