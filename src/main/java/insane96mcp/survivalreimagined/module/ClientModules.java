package insane96mcp.survivalreimagined.module;

import insane96mcp.insanelib.base.Module;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.setup.client.SRClientConfig;
import net.minecraftforge.fml.config.ModConfig;

public class ClientModules {
    public static Module client;

    public static void init() {
        client = Module.Builder.create(Ids.CLIENT, "Client", ModConfig.Type.CLIENT, SRClientConfig.builder).build();
    }

    public static class Ids {
        public static final String CLIENT = SurvivalReimagined.RESOURCE_PREFIX + "client";
    }
}
