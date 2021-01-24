package insane96mcp.iguanatweaksreborn.base;

import insane96mcp.iguanatweaksreborn.modules.experience.ExperienceModule;
import insane96mcp.iguanatweaksreborn.modules.mining.MiningModule;
import insane96mcp.iguanatweaksreborn.modules.sleeprespawn.SleepRespawnModule;

public class Modules {
    public static SleepRespawnModule sleepRespawnModule;
    public static ExperienceModule experienceModule;
    public static MiningModule miningModule;

    public static void init() {
        sleepRespawnModule = new SleepRespawnModule();
        experienceModule = new ExperienceModule();
        miningModule = new MiningModule();
    }
}
