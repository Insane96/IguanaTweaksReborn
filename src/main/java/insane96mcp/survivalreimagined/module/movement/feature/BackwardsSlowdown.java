package insane96mcp.survivalreimagined.module.movement.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

@Label(name = "Backwards slowdown", description = "Player's slowed down when walking backwards.")
@LoadFeature(module = Modules.Ids.MOVEMENT)
public class BackwardsSlowdown extends Feature {

	private final UUID BACKWARD_WALK_SLOWDOWN = UUID.fromString("3c085336-5bee-465f-b4a8-6677f245e7fc");

	@Config(min = 0d, max = 1d)
	@Label(name = "Slowdown", description = "How much slower will the player go when walking backwards.")
	public static Double slowdown = 0.2d;

	public BackwardsSlowdown(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (!this.isEnabled()
				|| event.phase != TickEvent.Phase.START
				|| slowdown == 0d
				|| event.player.getAbilities().flying)
			return;

		AttributeModifier modifier = event.player.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(BACKWARD_WALK_SLOWDOWN);
		if (event.player.zza < 0f) {
			if (modifier == null) {
				MCUtils.applyModifier(event.player, Attributes.MOVEMENT_SPEED, BACKWARD_WALK_SLOWDOWN, "backward slowdown", -slowdown, AttributeModifier.Operation.MULTIPLY_BASE, false);
			}
			else if (modifier.getAmount() != -slowdown) {
				event.player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(BACKWARD_WALK_SLOWDOWN);
				MCUtils.applyModifier(event.player, Attributes.MOVEMENT_SPEED, BACKWARD_WALK_SLOWDOWN, "backward slowdown", -slowdown, AttributeModifier.Operation.MULTIPLY_BASE, false);
			}
		}
		else {
			if (modifier != null) {
				event.player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(BACKWARD_WALK_SLOWDOWN);
			}
		}
	}
}