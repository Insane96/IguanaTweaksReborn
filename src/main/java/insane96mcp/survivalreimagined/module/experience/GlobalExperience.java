package insane96mcp.survivalreimagined.module.experience;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Global Experience", description = "Decrease / Increase every experience point dropped in the world")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class GlobalExperience extends Feature {

	public static final String XP_PROCESSED = SurvivalReimagined.RESOURCE_PREFIX + "xp_processed";

	@Config(min = 0d, max = 128d)
	@Label(name = "Global Experience Multiplier", description = "Experience dropped will be multiplied by this value.\nCan be set to 0 to disable experience drop from any source.")
	public static Double globalMultiplier = 1.25d;

	public GlobalExperience(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onXPOrbDrop(EntityJoinLevelEvent event) {
		if (!this.isEnabled()
				|| globalMultiplier == 1.0d
				|| !(event.getEntity() instanceof ExperienceOrb xpOrb)
				|| xpOrb.getPersistentData().getBoolean(XP_PROCESSED)
				|| event.getLevel().isClientSide)
			return;

		if (globalMultiplier == 0d)
			xpOrb.remove(Entity.RemovalReason.KILLED);
		else
			xpOrb.value *= globalMultiplier;

		xpOrb.getPersistentData().putBoolean(XP_PROCESSED, true);
		if (xpOrb.value <= 0d)
			xpOrb.remove(Entity.RemovalReason.KILLED);
	}
}