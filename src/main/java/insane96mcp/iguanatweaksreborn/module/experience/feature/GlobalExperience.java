package insane96mcp.iguanatweaksreborn.module.experience.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Global Experience", description = "Decrease / Increase every experience point dropped in the world")
public class GlobalExperience extends Feature {

	private final ForgeConfigSpec.ConfigValue<Double> globalMultiplierConfig;

	public double globalMultiplier = 1.25d;

	public GlobalExperience(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		globalMultiplierConfig = Config.builder
				.comment("Experience dropped will be multiplied by this multiplier.\nCan be set to 0 to disable experience drop from any source.")
				.defineInRange("Global Experience Multiplier", this.globalMultiplier, 0.0d, 1000d);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.globalMultiplier = this.globalMultiplierConfig.get();
	}

	@SubscribeEvent
	public void onXPOrbDrop(EntityJoinWorldEvent event) {
		if (!this.isEnabled())
			return;
		if (this.globalMultiplier == 1.0d)
			return;
		if (!(event.getEntity() instanceof ExperienceOrb xpOrb))
			return;
		if (xpOrb.getPersistentData().getBoolean(Strings.Tags.XP_PROCESSED))
			return;

		if (this.globalMultiplier == 0d)
			xpOrb.remove(Entity.RemovalReason.KILLED);
		else
			xpOrb.value *= this.globalMultiplier;

		xpOrb.getPersistentData().putBoolean(Strings.Tags.XP_PROCESSED, true);
		if (xpOrb.value <= 0d)
			xpOrb.remove(Entity.RemovalReason.KILLED);
	}
}