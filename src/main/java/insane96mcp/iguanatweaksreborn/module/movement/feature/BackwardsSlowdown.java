package insane96mcp.iguanatweaksreborn.module.movement.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.util.MCUtils;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

@Label(name = "Backwards slowdown", description = "Player's slowed down when walking backwards.")
public class BackwardsSlowdown extends Feature {

	private final UUID BACKWARD_WALK_SLOWDOWN = UUID.fromString("3c085336-5bee-465f-b4a8-6677f245e7fc");

	private final ForgeConfigSpec.ConfigValue<Double> slowdownConfig;

	public double slowdown = 0.25d;

	public BackwardsSlowdown(Module module) {
		super(Config.builder, module);
		this.pushConfig(Config.builder);
		slowdownConfig = Config.builder
				.comment("How much slower will the player go when walking backwards.")
				.defineInRange("Slowdown", this.slowdown, 0d, 1d);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.slowdown = this.slowdownConfig.get();
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (!this.isEnabled())
			return;

		if (event.phase != TickEvent.Phase.START)
			return;

		if (this.slowdown == 0d)
			return;

		AttributeModifier modifier = event.player.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(BACKWARD_WALK_SLOWDOWN);
		if (event.player.zza < 0f) {
			if (modifier == null) {
				MCUtils.applyModifier(event.player, Attributes.MOVEMENT_SPEED, BACKWARD_WALK_SLOWDOWN, "backward slowdown", -this.slowdown, AttributeModifier.Operation.MULTIPLY_BASE, false);
			}
			else if (modifier.getAmount() != -slowdown) {
				event.player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(BACKWARD_WALK_SLOWDOWN);
				MCUtils.applyModifier(event.player, Attributes.MOVEMENT_SPEED, BACKWARD_WALK_SLOWDOWN, "backward slowdown", -this.slowdown, AttributeModifier.Operation.MULTIPLY_BASE, false);
			}
		}
		else {
			if (modifier != null) {
				event.player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(BACKWARD_WALK_SLOWDOWN);
			}
		}
	}
}