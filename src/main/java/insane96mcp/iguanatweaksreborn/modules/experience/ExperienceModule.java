package insane96mcp.iguanatweaksreborn.modules.experience;

import insane96mcp.iguanatweaksreborn.modules.experience.feature.BlockExperienceFeature;
import insane96mcp.iguanatweaksreborn.modules.experience.feature.GlobalExperienceFeature;
import insane96mcp.iguanatweaksreborn.modules.experience.feature.SpawnerMobsExperienceFeature;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Experience")
public class ExperienceModule extends Module {

    public GlobalExperienceFeature globalExperienceFeature;
    public BlockExperienceFeature blockExperienceFeature;
    public SpawnerMobsExperienceFeature spawnerMobsExperienceFeature;

    public ExperienceModule() {
        super(Config.builder);
        pushConfig(Config.builder);
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
