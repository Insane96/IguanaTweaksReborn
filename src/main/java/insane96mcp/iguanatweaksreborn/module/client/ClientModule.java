package insane96mcp.iguanatweaksreborn.module.client;

import insane96mcp.iguanatweaksreborn.module.client.feature.Fog;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@Label(name = "Client", description = "Various client side changes")
public class ClientModule extends Module {
    public static Fog fog;

    public ClientModule() {
        super(Config.builder);
        pushConfig(Config.builder);
        fog = new Fog(this);
        Config.builder.pop();
    }

    public void loadConfig() {
        fog.loadConfig();
    }
}
