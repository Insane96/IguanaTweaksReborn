package insane96mcp.iguanatweaksreborn.modules.farming.feature;

import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.base.ITModule;
import insane96mcp.iguanatweaksreborn.base.Label;
import insane96mcp.iguanatweaksreborn.setup.Config;
import net.minecraft.block.*;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Plants Growth", description = "Slower Plants growing")
public class PlantsGrowthFeature extends ITFeature {

	private final ForgeConfigSpec.ConfigValue<Double> sugarCanesGrowthMultiplierConfig;
	private final ForgeConfigSpec.ConfigValue<Double> cactusGrowthMultiplierConfig;
	private final ForgeConfigSpec.ConfigValue<Double> cocoaBeansGrowthMultiplierConfig;
	private final ForgeConfigSpec.ConfigValue<Double> netherwartGrowthMultiplierConfig;
	private final ForgeConfigSpec.ConfigValue<Double> chorusPlantGrowthMultiplierConfig;
	private final ForgeConfigSpec.ConfigValue<Double> saplingGrowthMultiplierConfig;
	private final ForgeConfigSpec.ConfigValue<Double> stemGrowthMultiplierConfig;
	private final ForgeConfigSpec.ConfigValue<Double> berryBushGrowthMultiplierConfig;
	private final ForgeConfigSpec.ConfigValue<Double> kelpGrowthMultiplierConfig;
	private final ForgeConfigSpec.ConfigValue<Double> bambooGrowthMultiplierConfig;

	public double sugarCanesGrowthMultiplier = 2.5d;
	public double cactusGrowthMultiplier = 2.5d;
	public double cocoaBeansGrowthMultiplier = 3.0d;
	public double netherwartGrowthMultiplier = 3.0d;
	public double chorusPlantGrowthMultiplier = 3.0d;
	public double saplingGrowthMultiplier = 2.0d;
	public double stemGrowthMultiplier = 2.0d;
	public double berryBushGrowthMultiplier = 2.5d;
	public double kelpGrowthMultiplier = 2.5d;
	public double bambooGrowthMultiplier = 2.5d;

	public PlantsGrowthFeature(ITModule module) {
		super(module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		sugarCanesGrowthMultiplierConfig = Config.builder
				.comment("Increases the time required for Sugar Canes to grow (e.g. at 2.0 Sugar Canes will take twice to grow).\nSetting this to 0 will prevent Sugar Canes from growing naturally.\n1.0 will make Sugar Canes grow like normal.")
				.defineInRange("Sugar Canes Growth Speed Multiplier", sugarCanesGrowthMultiplier, 0.0d, 128d);
		cactusGrowthMultiplierConfig = Config.builder
				.comment("Increases the time required for Cactuses to grow (e.g. at 2.0 Cactuses will take twice to grow).\nSetting this to 0 will prevent Cactuses from growing naturally.\n1.0 will make Cactuses grow like normal.")
				.defineInRange("Cactus Growth Speed Multiplier", cactusGrowthMultiplier, 0.0d, 128d);
		cocoaBeansGrowthMultiplierConfig = Config.builder
				.comment("Increases the time required for Cocoa Beans to grow (e.g. at 2.0 Cocoa Beans will take twice to grow).\nSetting this to 0 will prevent Cocoa Beans from growing naturally.\n1.0 will make Cocoa Beans grow like normal.")
				.defineInRange("Cocoa Beans Growth Speed Multiplier", cocoaBeansGrowthMultiplier, 0.0d, 128d);
		netherwartGrowthMultiplierConfig = Config.builder
				.comment("Increases the time required for Netherwart to grow (e.g. at 2.0 Netherwart will take twice to grow).\nSetting this to 0 will prevent Netherwart from growing naturally.\n1.0 will make Netherwart grow like normal.")
				.defineInRange("Netherwart Growth Speed Multiplier", netherwartGrowthMultiplier, 0.0d, 128d);
		chorusPlantGrowthMultiplierConfig = Config.builder
				.comment("Increases the time required for Chorus Plants to grow (e.g. at 2.0 Chorus Plants will take twice to grow).\nSetting this to 0 will prevent Chorus Plants from growing naturally.\n1.0 will make Chorus Plants grow like normal.")
				.defineInRange("Chorus Plants Growth Speed Multiplier", chorusPlantGrowthMultiplier, 0.0d, 128d);
		saplingGrowthMultiplierConfig = Config.builder
				.comment("Increases the time required for Saplings to grow (e.g. at 2.0 Saplings will take twice to grow).\nSetting this to 0 will prevent Saplings from growing naturally.\n1.0 will make Saplings grow like normal.")
				.defineInRange("Saplings Growth Speed Multiplier", saplingGrowthMultiplier, 0.0d, 128d);
		stemGrowthMultiplierConfig = Config.builder
				.comment("Increases the time required for Pumpkin & Melon to grow (e.g. at 2.0 Pumpkin & Melon will take twice to grow).\nSetting this to 0 will prevent Pumpkin & Melon from growing naturally.\n1.0 will make Pumpkin & Melon grow like normal.")
				.defineInRange("Pumpkin & Melon Growth Speed Multiplier", stemGrowthMultiplier, 0.0d, 128d);
		berryBushGrowthMultiplierConfig = Config.builder
				.comment("Increases the time required for Berry Bushes to grow (e.g. at 2.0 Berry Bushes will take twice to grow).\nSetting this to 0 will prevent Berry Bushes from growing naturally.\n1.0 will make Berry Bushes grow like normal.")
				.defineInRange("Berry Bushes Growth Speed Multiplier", berryBushGrowthMultiplier, 0.0d, 128d);
		kelpGrowthMultiplierConfig = Config.builder
				.comment("Increases the time required for Kelp to grow (e.g. at 2.0 Kelp will take twice to grow).\nSetting this to 0 will prevent Kelp from growing naturally.\n1.0 will make Kelp grow like normal.")
				.defineInRange("Saplings Growth Speed Multiplier", kelpGrowthMultiplier, 0.0d, 128d);
		bambooGrowthMultiplierConfig = Config.builder
				.comment("Increases the time required for Bamboo to grow (e.g. at 2.0 Bamboo will take twice to grow).\nSetting this to 0 will prevent Bamboo from growing naturally.\n1.0 will make Bamboo grow like normal.")
				.defineInRange("Bamboo Growth Speed Multiplier", bambooGrowthMultiplier, 0.0d, 128d);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.sugarCanesGrowthMultiplier = this.sugarCanesGrowthMultiplierConfig.get();
		this.cactusGrowthMultiplier = this.cactusGrowthMultiplierConfig.get();
		this.cocoaBeansGrowthMultiplier = this.cocoaBeansGrowthMultiplierConfig.get();
		this.netherwartGrowthMultiplier = this.netherwartGrowthMultiplierConfig.get();
		this.chorusPlantGrowthMultiplier = this.chorusPlantGrowthMultiplierConfig.get();
		this.saplingGrowthMultiplier = this.saplingGrowthMultiplierConfig.get();
		this.stemGrowthMultiplier = this.stemGrowthMultiplierConfig.get();
		this.berryBushGrowthMultiplier = this.berryBushGrowthMultiplierConfig.get();
		this.kelpGrowthMultiplier = this.kelpGrowthMultiplierConfig.get();
		this.bambooGrowthMultiplier = this.bambooGrowthMultiplierConfig.get();
	}

	@SubscribeEvent
	public void cropGrowPost(BlockEvent.CropGrowEvent.Post event) {
		if (!this.isEnabled())
			return;
		plantGrowthMultiplier(event, SugarCaneBlock.class, this.sugarCanesGrowthMultiplier);
		plantGrowthMultiplier(event, CactusBlock.class, this.cactusGrowthMultiplier);
		plantGrowthMultiplier(event, CocoaBlock.class, this.cocoaBeansGrowthMultiplier);
		plantGrowthMultiplier(event, NetherWartBlock.class, this.netherwartGrowthMultiplier);
		plantGrowthMultiplier(event, ChorusPlantBlock.class, this.chorusPlantGrowthMultiplier);
		plantGrowthMultiplier(event, SaplingBlock.class, this.saplingGrowthMultiplier);
		plantGrowthMultiplier(event, StemBlock.class, this.stemGrowthMultiplier);
		plantGrowthMultiplier(event, SweetBerryBushBlock.class, this.berryBushGrowthMultiplier);
		plantGrowthMultiplier(event, KelpTopBlock.class, this.kelpGrowthMultiplier);
		plantGrowthMultiplier(event, BambooBlock.class, this.bambooGrowthMultiplier);
	}

	public static void plantGrowthMultiplier(BlockEvent.CropGrowEvent.Post event, Class<? extends Block> blockClass, double multiplier) {
		if (multiplier == 1.0d)
			return;
		IWorld world = event.getWorld();
		BlockState state = event.getOriginalState();
		if (!(state.getBlock().getClass().isInstance(blockClass)))
			return;
		double chance;
		if (multiplier == 0.0d)
			chance = -1d;
		else
			chance = 1d / multiplier;
		if (event.getWorld().getRandom().nextDouble() > chance)
			world.setBlockState(event.getPos(), state, 2);
	}
}
