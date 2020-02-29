package insane96mcp.iguanatweaksreborn;

import insane96mcp.iguanatweaksreborn.setup.ModConfig;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;

@Mod("iguanatweaksreborn")
public class IguanaTweaksReborn
{
	public static final String MOD_ID = "iguanatweaksreborn";
	public static final String RESOURCE_PREFIX = MOD_ID + ":";
    public static final Logger LOGGER = LogManager.getLogger();

    public IguanaTweaksReborn() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
		ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, ModConfig.SPEC);
		ModConfig.init(Paths.get("config", MOD_ID + ".toml"));
    }


	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class RegistryEvents {
        @SubscribeEvent
		public static void onWorldJoin(PlayerEvent.PlayerLoggedInEvent event) {
            /*Collection<Block> blocks = BlockTags.getCollection().get(new ResourceLocation("minecraft:carpets")).getAllElements();

            for (Block block : blocks) {
                LOGGER.info(block.getRegistryName());
            }*/
		}
	}

}
