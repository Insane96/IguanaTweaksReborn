package insane96mcp.iguanatweaksreborn.module;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.setup.ITClientConfig;
import insane96mcp.insanelib.base.Module;

public class ClientModules {
    public static Module client;

    public static void init() {
        client = Module.Builder.create(ITClientConfig.builder, Ids.CLIENT, "Client").build();
    }

    public static class Ids {
        public static final String CLIENT = IguanaTweaksReborn.RESOURCE_PREFIX + "client";
    }
}
