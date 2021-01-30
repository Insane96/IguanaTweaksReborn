package insane96mcp.iguanatweaksreborn.base;

import insane96mcp.iguanatweaksreborn.modules.experience.ExperienceModule;
import insane96mcp.iguanatweaksreborn.modules.hungerhealth.HungerHealthModule;
import insane96mcp.iguanatweaksreborn.modules.mining.MiningModule;
import insane96mcp.iguanatweaksreborn.modules.sleeprespawn.SleepRespawnModule;
import insane96mcp.iguanatweaksreborn.modules.stacksize.StackSizeModule;

public class Modules {
    public static SleepRespawnModule sleepRespawnModule;
    public static ExperienceModule experienceModule;
    public static MiningModule miningModule;
    public static HungerHealthModule hungerHealthModule;
    public static StackSizeModule stackSizeModule;

    public static void init() {
        sleepRespawnModule = new SleepRespawnModule();
        experienceModule = new ExperienceModule();
        miningModule = new MiningModule();
        hungerHealthModule = new HungerHealthModule();
        stackSizeModule = new StackSizeModule();
    }
}
