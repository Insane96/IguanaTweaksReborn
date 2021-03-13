package insane96mcp.iguanatweaksreborn.modules.experience.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Global Experience", description = "Decrease (or Increase) every experience point dropped in the world")
public class GlobalExperienceFeature extends Feature {

	private final ForgeConfigSpec.ConfigValue<Double> globalMultiplierConfig;

	public double globalMultiplier = 1.0d;

	public GlobalExperienceFeature(Module module) {
		super(Config.builder, module, false);
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

        if (!(event.getEntity() instanceof ExperienceOrbEntity))
            return;

        ExperienceOrbEntity xpOrb = (ExperienceOrbEntity) event.getEntity();

        if (this.globalMultiplier == 0d)
            xpOrb.remove();
        else
            xpOrb.xpValue *= this.globalMultiplier;

        if (xpOrb.xpValue <= 0d)
            xpOrb.remove();
    }
}
