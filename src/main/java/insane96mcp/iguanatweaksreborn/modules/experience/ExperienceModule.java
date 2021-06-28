package insane96mcp.iguanatweaksreborn.modules.experience;

import insane96mcp.iguanatweaksreborn.modules.experience.feature.BlockExperienceFeature;
import insane96mcp.iguanatweaksreborn.modules.experience.feature.GlobalExperienceFeature;
import insane96mcp.iguanatweaksreborn.modules.experience.feature.SpawnerMobsExperienceFeature;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Experience")
public class ExperienceModule extends Module {

    public GlobalExperienceFeature globalExperience;
    public BlockExperienceFeature blockExperience;
    public SpawnerMobsExperienceFeature spawnerMobsExperience;

    public ExperienceModule() {
        super(Config.builder);
        pushConfig(Config.builder);
        globalExperience = new GlobalExperienceFeature(this);
        blockExperience = new BlockExperienceFeature(this);
        spawnerMobsExperience = new SpawnerMobsExperienceFeature(this);
        Config.builder.pop();
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        globalExperience.loadConfig();
        blockExperience.loadConfig();
        spawnerMobsExperience.loadConfig();
    }
}
