package insane96mcp.iguanatweaksreborn.setup;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ITClientConfig {
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
			//ClientModules.init();
		}
	}
}
