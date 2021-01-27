package insane96mcp.iguanatweaksreborn.modules.experience.feature;

import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.base.ITModule;
import insane96mcp.iguanatweaksreborn.base.Label;
import insane96mcp.iguanatweaksreborn.setup.Config;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Experience From Blocks", description = "Decrease (or Increase) experience dropped by blocks broken")
public class BlockExperienceFeature extends ITFeature {

    private final ForgeConfigSpec.ConfigValue<Double> oreMultiplierConfig;

    public double oreMultiplier = 2.5d;

    public BlockExperienceFeature(ITModule module) {
        super(module, true);
        Config.builder.comment(this.getDescription()).push(this.getName());
        oreMultiplierConfig = Config.builder
                .comment("Experience dropped by blocks (Ores and Spawners) will be multiplied by this multiplier. Experience dropped by blocks are still affected by 'Global Experience Multiplier'\nCan be set to 0 to make blocks drop no experience")
                .defineInRange("Experience from Blocks Multiplier", this.oreMultiplier, 0.0d, 1000d);
        Config.builder.pop();
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        this.oreMultiplier = this.oreMultiplierConfig.get();
    }

    @SubscribeEvent
    public void onBlockXPDrop(BlockEvent.BreakEvent event) {
        if (!this.isEnabled())
            return;
        if (this.oreMultiplier == 1.0d)
            return;

        int xpToDrop = event.getExpToDrop();
        xpToDrop *= this.oreMultiplier;
        event.setExpToDrop(xpToDrop);
    }
}
