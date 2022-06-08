package insane96mcp.iguanatweaksreborn.setup;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.ClientModules;
import insane96mcp.iguanatweaksreborn.module.Modules;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ITClientConfig {
	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final ClientConfig CLIENT;

	public static final ForgeConfigSpec.Builder builder;

	static {
		builder = new ForgeConfigSpec.Builder();
		final Pair<ClientConfig, ForgeConfigSpec> specPair = builder.configure(ClientConfig::new);
		CLIENT = specPair.getLeft();
		CLIENT_SPEC = specPair.getRight();
	}

	public static class ClientConfig {
		public ClientConfig(final ForgeConfigSpec.Builder builder) {
			ClientModules.init();
		}
	}

	@SubscribeEvent
	public static void onModConfigEvent(final ModConfigEvent event) {
		ClientModules.loadConfig();
	}
}
