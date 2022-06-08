package insane96mcp.iguanatweaksreborn.module;

import insane96mcp.iguanatweaksreborn.module.client.ClientModule;

public class ClientModules {
    public static ClientModule client;

    public static void init() {
        client = new ClientModule();
    }

    public static void loadConfig() {
        client.loadConfig();
    }
}
