package insane96mcp.iguanatweaksreborn.modules.experience;

import insane96mcp.iguanatweaksreborn.base.ITModule;
import insane96mcp.iguanatweaksreborn.modules.experience.feature.BlockExperienceFeature;
import insane96mcp.iguanatweaksreborn.modules.experience.feature.GlobalExperienceFeature;
import insane96mcp.iguanatweaksreborn.modules.experience.feature.SpawnerMobsExperienceFeature;
import insane96mcp.iguanatweaksreborn.setup.Config;

public class ExperienceModule extends ITModule {

    public GlobalExperienceFeature globalExperienceFeature;
    public BlockExperienceFeature blockExperienceFeature;
    public SpawnerMobsExperienceFeature spawnerMobsExperienceFeature;

    public ExperienceModule() {
        super("Experience", "");
        Config.builder.comment(this.getDescription()).push(this.getName());
        globalExperienceFeature = new GlobalExperienceFeature(this);
        blockExperienceFeature = new BlockExperienceFeature(this);
        spawnerMobsExperienceFeature = new SpawnerMobsExperienceFeature(this);
        Config.builder.pop();
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        globalExperienceFeature.loadConfig();
        blockExperienceFeature.loadConfig();
        spawnerMobsExperienceFeature.loadConfig();
    }
}
