package insane96mcp.iguanatweaksreborn.module.experience.feature;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Global Experience", description = "Decrease / Increase every experience point dropped in the world")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class GlobalExperience extends Feature {

	@Config(min = 0d, max = 128d)
	@Label(name = "Global Experience Multiplier", description = "Experience dropped will be multiplied by this multiplier.\nCan be set to 0 to disable experience drop from any source.")
	public static Double globalMultiplier = 1.25d;

	public GlobalExperience(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void onXPOrbDrop(EntityJoinLevelEvent event) {
		if (!this.isEnabled()
				|| globalMultiplier == 1.0d
				|| !(event.getEntity() instanceof ExperienceOrb xpOrb)
				|| xpOrb.getPersistentData().getBoolean(Strings.Tags.XP_PROCESSED))
			return;

		if (globalMultiplier == 0d)
			xpOrb.remove(Entity.RemovalReason.KILLED);
		else
			xpOrb.value *= globalMultiplier;

		xpOrb.getPersistentData().putBoolean(Strings.Tags.XP_PROCESSED, true);
		if (xpOrb.value <= 0d)
			xpOrb.remove(Entity.RemovalReason.KILLED);
	}
}