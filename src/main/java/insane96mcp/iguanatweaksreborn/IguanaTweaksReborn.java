package insane96mcp.iguanatweaksreborn;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.setup.ITEffects;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
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
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, Config.COMMON_SPEC);
        //MinecraftForge.EVENT_BUS.register(this);
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        //Reflection.init();
        //WeightFeature.initMaterialWeight();
        ITEffects.EFFECTS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public void attachCapabilitiesEntity(final AttachCapabilitiesEvent<BlockEntity> event) {
        //if (event.getObject() instanceof MobSpawnerTileEntity) {
        //    SpawnerCapability spawnerCapability = new SpawnerCapability();
        //    event.addCapability(new ResourceLocation(Strings.Tags.TEMPORARY_SPAWNER), spawnerCapability);
        //    event.addListener(spawnerCapability::invalidate);
        //}
    }

    private void setup(final FMLCommonSetupEvent event) {
        //SpawnerCapability.register();
        //SyncHandler.init();
    }
}
