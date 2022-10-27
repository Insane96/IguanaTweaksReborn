package insane96mcp.iguanatweaksreborn.module.hungerhealth.feature;

import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Exhaustion Increase", description = "Make the player consume more hunger with different actions")
public class ExhaustionIncrease extends Feature {

	private final ForgeConfigSpec.ConfigValue<Double> blockBreakExhaustionMultiplierConfig;
	private final ForgeConfigSpec.ConfigValue<Double> exhaustionOnBlockBreakingConfig;
	private final ForgeConfigSpec.ConfigValue<Double> passiveExhaustionConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> effectiveHungerConfig;

	public double blockBreakExhaustionMultiplier = 0d;
	public double exhaustionOnBlockBreaking = 0.005d;
	public double passiveExhaustion = 0.005d;
	public boolean effectiveHunger = true;

	public ExhaustionIncrease(Module module) {
		super(ITCommonConfig.builder, module);
		ITCommonConfig.builder.comment(this.getDescription()).push(this.getName());
		blockBreakExhaustionMultiplierConfig = ITCommonConfig.builder
				.comment("When you break a block you'll get exhaustion equal to the block hardness multiplied by this value. Setting this to 0 will default to the vanilla exhaustion (0.005). (It's not affected by the Global Hardness Features)")
				.defineInRange("Block Break Exhaustion Multiplier", blockBreakExhaustionMultiplier, 0.0d, 1024d);
		exhaustionOnBlockBreakingConfig = ITCommonConfig.builder
				.comment("When breaking block you'll get exhaustion every tick during the breaking.")
				.defineInRange("Exhaustion per tick when breaking a block", exhaustionOnBlockBreaking, 0.0d, 1024d);
		passiveExhaustionConfig = ITCommonConfig.builder
				.comment("Every second the player will get this exhaustion.")
				.defineInRange("Passive Exhaustion", this.passiveExhaustion, 0.0d, 1024d);
		effectiveHungerConfig = ITCommonConfig.builder
				.comment("When affected by the hunger effect ANY action will give you 100% more exhaustion per level.")
				.define("Effective Hunger", this.effectiveHunger);
		ITCommonConfig.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.blockBreakExhaustionMultiplier = this.blockBreakExhaustionMultiplierConfig.get();
		this.exhaustionOnBlockBreaking = this.exhaustionOnBlockBreakingConfig.get();
		this.passiveExhaustion = this.passiveExhaustionConfig.get();
		this.effectiveHunger = this.effectiveHungerConfig.get();
	}

	@SubscribeEvent
	public void breakExhaustion(BlockEvent.BreakEvent event) {
		if (!this.isEnabled())
			return;
		if (blockBreakExhaustionMultiplier == 0d)
			return;
		ServerLevel world = (ServerLevel) event.getWorld();
		BlockState state = world.getBlockState(event.getPos());
		double hardness = state.getDestroySpeed(event.getWorld(), event.getPos());
		double exhaustion = (hardness * blockBreakExhaustionMultiplier) - 0.005f;
		exhaustion = Math.max(exhaustion, 0d);
		event.getPlayer().causeFoodExhaustion((float) exhaustion);
	}

	@SubscribeEvent
	public void onBreakingBlock(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled())
			return;
		if (this.exhaustionOnBlockBreaking == 0d)
			return;
		event.getPlayer().causeFoodExhaustion((float) this.exhaustionOnBlockBreaking);
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (!this.isEnabled())
			return;
		if (event.phase != TickEvent.Phase.END)
			return;
		if (event.player.level.isClientSide)
			return;
		if (this.passiveExhaustion == 0d)
			return;
		if (event.player.tickCount % 20 != 0)
			return;

		event.player.causeFoodExhaustion((float) this.passiveExhaustion);
	}

	public float increaseHungerEffectiviness(Player player, float amount) {
		if (!this.isEnabled())
			return amount;

		if (!this.effectiveHunger)
			return amount;

		if (!player.hasEffect(MobEffects.HUNGER))
			return amount;

		int amp = player.getEffect(MobEffects.HUNGER).getAmplifier() + 1;
		return amount * (amp * 1f + 1);
	}
}
