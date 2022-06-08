package insane96mcp.iguanatweaksreborn.module;

import insane96mcp.iguanatweaksreborn.module.client.ClientModule;
import insane96mcp.iguanatweaksreborn.module.combat.Combat;
import insane96mcp.iguanatweaksreborn.module.experience.Experience;
import insane96mcp.iguanatweaksreborn.module.farming.Farming;
import insane96mcp.iguanatweaksreborn.module.hungerhealth.HungerHealth;
import insane96mcp.iguanatweaksreborn.module.mining.Mining;
import insane96mcp.iguanatweaksreborn.module.misc.Misc;
import insane96mcp.iguanatweaksreborn.module.movement.Movement;
import insane96mcp.iguanatweaksreborn.module.sleeprespawn.SleepRespawn;
import insane96mcp.iguanatweaksreborn.module.stacksize.StackSize;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientModules {
    public static ClientModule client;

    public static void init() {
        client = new ClientModule();
    }

    public static void loadConfig() {
        client.loadConfig();
    }
}
