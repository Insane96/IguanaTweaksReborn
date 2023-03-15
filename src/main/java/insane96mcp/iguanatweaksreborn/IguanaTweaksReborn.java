package insane96mcp.iguanatweaksreborn;

import insane96mcp.iguanatweaksreborn.data.ITDataReloadListener;
import insane96mcp.iguanatweaksreborn.data.ITGlobalLootModifierProvider;
import insane96mcp.iguanatweaksreborn.data.ITRecipeProvider;
import insane96mcp.iguanatweaksreborn.module.misc.capability.SpawnerProvider;
import insane96mcp.iguanatweaksreborn.module.sleeprespawn.feature.Tiredness;
import insane96mcp.iguanatweaksreborn.network.NetworkHandler;
import insane96mcp.iguanatweaksreborn.setup.*;
import insane96mcp.iguanatweaksreborn.setup.client.ClientSetup;
import insane96mcp.iguanatweaksreborn.setup.client.ITClientConfig;
import insane96mcp.iguanatweaksreborn.utils.Weights;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

@Mod("iguanatweaksreborn")
public class IguanaTweaksReborn
{
	public static final String MOD_ID = "iguanatweaksreborn";
	public static final String RESOURCE_PREFIX = MOD_ID + ":";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final String CONFIG_FOLDER = "config/" + MOD_ID;

    public static final ResourceLocation GUI_ICONS = new ResourceLocation(IguanaTweaksReborn.MOD_ID, "textures/gui/icons.png");

    public IguanaTweaksReborn() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ITClientConfig.CONFIG_SPEC, MOD_ID + "/client.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ITCommonConfig.CONFIG_SPEC, MOD_ID + "/common.toml");
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(ClientSetup::creativeTabsBuildContents);
        modEventBus.addListener(this::addPackFinders);
        modEventBus.register(Tiredness.class);
        ITSoundEvents.SOUND_EVENTS.register(modEventBus);
        ITMobEffects.MOB_EFFECTS.register(modEventBus);
        ITItems.ITEMS.register(modEventBus);
        ITGlobalLootModifiers.LOOT_MODIFIERS.register(modEventBus);
        Weights.initMaterialWeight();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onAddReloadListener(AddReloadListenerEvent event) {
        ITDataReloadListener.reloadContext = event.getConditionContext();
        event.addListener(ITDataReloadListener.INSTANCE);
    }

    @SubscribeEvent
    public void attachCapBlockEntity(final AttachCapabilitiesEvent<BlockEntity> event)
    {
        if (event.getObject() instanceof SpawnerBlockEntity)
            event.addCapability(SpawnerProvider.IDENTIFIER, new SpawnerProvider());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void attachCapBlockEntity(final RenderGuiOverlayEvent.Pre event)
    {
        if (event.getOverlay().equals(VanillaGuiOverlay.FOOD_LEVEL.type()))
            event.setCanceled(true);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        NetworkHandler.init();
    }

    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        generator.addProvider(event.includeServer(), new ITRecipeProvider(generator.getPackOutput()));
        generator.addProvider(event.includeServer(), new ITGlobalLootModifierProvider(generator.getPackOutput(), IguanaTweaksReborn.MOD_ID));
    }

    public void addPackFinders(AddPackFindersEvent event)
    {
        for (IntegratedDataPacks.IntegratedDataPack dataPack : IntegratedDataPacks.INTEGRATED_DATA_PACKS) {
            if (event.getPackType() != dataPack.getPackType())
                continue;

            Path resourcePath = ModList.get().getModFileById(MOD_ID).getFile().findResource("integrated_packs/" + dataPack.getPath());
            var pack = Pack.readMetaAndCreate(IguanaTweaksReborn.RESOURCE_PREFIX + dataPack.getPath(), dataPack.getDescription(), dataPack.shouldBeEnabled(),
                    (path) -> new PathPackResources(path, resourcePath, false), PackType.SERVER_DATA, Pack.Position.BOTTOM, dataPack.shouldBeEnabled() ? PackSource.DEFAULT : ITPackSource.DISABLED);
            event.addRepositorySource((packConsumer) -> packConsumer.accept(pack));
        }
    }


}
