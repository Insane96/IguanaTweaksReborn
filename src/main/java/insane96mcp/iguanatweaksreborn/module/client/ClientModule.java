package insane96mcp.iguanatweaksreborn.module.client;

import insane96mcp.iguanatweaksreborn.module.client.feature.Fog;
import insane96mcp.iguanatweaksreborn.module.client.feature.Light;
import insane96mcp.iguanatweaksreborn.setup.ITClientConfig;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Client")
public class ClientModule extends Module {
    public Fog fog;
    public Light light;

    public ClientModule() {
        super(ITClientConfig.builder);
        pushConfig(ITClientConfig.builder);
        fog = new Fog(this);
        light = new Light(this);
        ITClientConfig.builder.pop();
    }

    public void loadConfig() {
        super.loadConfig();
        fog.loadConfig();
        light.loadConfig();
    }
}
