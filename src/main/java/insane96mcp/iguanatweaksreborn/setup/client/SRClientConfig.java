package insane96mcp.iguanatweaksreborn.setup.client;

import insane96mcp.insanelib.base.Module;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.ClientModules;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class SRClientConfig {
	public static final ForgeConfigSpec CONFIG_SPEC;
	public static final ClientConfig CLIENT;

	public static final ForgeConfigSpec.Builder builder;

	static {
		builder = new ForgeConfigSpec.Builder();
		final Pair<ClientConfig, ForgeConfigSpec> specPair = builder.configure(ClientConfig::new);
		CLIENT = specPair.getLeft();
		CONFIG_SPEC = specPair.getRight();
	}

	public static class ClientConfig {
		public ClientConfig(final ForgeConfigSpec.Builder builder) {
			ClientModules.init();
			Module.loadFeatures(ModConfig.Type.CLIENT, IguanaTweaksReborn.MOD_ID, this.getClass().getClassLoader());
		}
	}
}
