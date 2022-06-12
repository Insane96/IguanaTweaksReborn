package insane96mcp.iguanatweaksreborn;

import insane96mcp.iguanatweaksreborn.module.misc.capability.SpawnerProvider;
import insane96mcp.iguanatweaksreborn.network.SyncHandler;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.setup.ITClientConfig;
import insane96mcp.iguanatweaksreborn.setup.ITMobEffects;
import insane96mcp.iguanatweaksreborn.utils.Weights;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("iguanatweaksreborn")
public class IguanaTweaksReborn
{
	public static final String MOD_ID = "iguanatweaksreborn";
	public static final String RESOURCE_PREFIX = MOD_ID + ":";
    public static final Logger LOGGER = LogManager.getLogger();

    public IguanaTweaksReborn() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.CONFIG_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ITClientConfig.CONFIG_SPEC);
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        ITMobEffects.MOB_EFFECTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Weights.initMaterialWeight();
    }

    @SubscribeEvent
    public void attachCapBlockEntity(final AttachCapabilitiesEvent<BlockEntity> event)
    {
        if (event.getObject() instanceof SpawnerBlockEntity)
            event.addCapability(SpawnerProvider.IDENTIFIER, new SpawnerProvider());
    }

    private void setup(final FMLCommonSetupEvent event) {
        //SpawnerCapability.register();
        SyncHandler.init();
    }
}
