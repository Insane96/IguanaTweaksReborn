package insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraftforge.common.ForgeConfigSpec;

@Label(name = "Faster Consuming", description = "Makes some items faster to use, like Potions or Soups")
public class FasterConsumingFeature extends Feature {

	private final ForgeConfigSpec.ConfigValue<Boolean> fasterPotionConsumingConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> fasterSoupConsumingConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> fasterMilkConsumingConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> fasterHoneyConsumingConfig;

	public boolean fasterPotionConsuming = true;
	public boolean fasterSoupConsuming = true;
	public boolean fasterMilkConsuming = true;
	public boolean fasterHoneyConsuming = true;

	public FasterConsumingFeature(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		fasterPotionConsumingConfig = Config.builder
				.comment("Makes potion faster to drink, 1 second instead of 1.6.")
				.define("Faster Potion Consuming", this.fasterPotionConsuming);
		fasterSoupConsumingConfig = Config.builder
				.comment("Makes soups faster to eat, 1 second instead of 1.6.")
				.define("Faster Soups Consuming", this.fasterSoupConsuming);
		fasterMilkConsumingConfig = Config.builder
				.comment("Makes milk faster to drink, 1 second instead of 1.6.")
				.define("Faster Milk Consuming", this.fasterMilkConsuming);
		fasterHoneyConsumingConfig = Config.builder
				.comment("Makes Honey faster to drink, 1 second instead of 2.")
				.define("Faster Honey Consuming", this.fasterHoneyConsuming);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.fasterPotionConsuming = this.fasterPotionConsumingConfig.get();
		this.fasterSoupConsuming = this.fasterSoupConsumingConfig.get();
		this.fasterMilkConsuming = this.fasterMilkConsumingConfig.get();
		this.fasterHoneyConsuming = this.fasterHoneyConsumingConfig.get();
	}
}
