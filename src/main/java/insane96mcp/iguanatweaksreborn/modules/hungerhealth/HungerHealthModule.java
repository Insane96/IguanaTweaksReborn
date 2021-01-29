package insane96mcp.iguanatweaksreborn.modules.hungerhealth;

import insane96mcp.iguanatweaksreborn.base.ITModule;
import insane96mcp.iguanatweaksreborn.base.Label;
import insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature.DebuffFeature;
import insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature.ExaustionIncreaseFeature;
import insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature.FoodFeature;
import insane96mcp.iguanatweaksreborn.setup.Config;

@Label(name = "Hunger")
public class HungerHealthModule extends ITModule {

    public FoodFeature foodFeature;
    public DebuffFeature debuffFeature;
    public ExaustionIncreaseFeature exaustionIncreaseFeature;

    public HungerHealthModule() {
        super();
        Config.builder.comment(this.getDescription()).push(this.getName());
        foodFeature = new FoodFeature(this);
        debuffFeature = new DebuffFeature(this);
        exaustionIncreaseFeature = new ExaustionIncreaseFeature(this);
        Config.builder.pop();
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        foodFeature.loadConfig();
        debuffFeature.loadConfig();
        exaustionIncreaseFeature.loadConfig();
    }

}
