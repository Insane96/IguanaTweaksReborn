package insane96mcp.iguanatweaksreborn;

import com.google.common.collect.Lists;
import insane96mcp.iguanatweaksreborn.command.ITRCommand;
import insane96mcp.iguanatweaksreborn.data.criterion.ITRTriggers;
import insane96mcp.iguanatweaksreborn.data.generator.ITRBlockTagsProvider;
import insane96mcp.iguanatweaksreborn.data.generator.ITRDamageTypeTagsProvider;
import insane96mcp.iguanatweaksreborn.data.generator.ITRItemTagsProvider;
import insane96mcp.iguanatweaksreborn.data.generator.client.ITRBlockModelsProvider;
import insane96mcp.iguanatweaksreborn.data.generator.client.ITRBlockStatesProvider;
import insane96mcp.iguanatweaksreborn.data.generator.client.ITRItemModelsProvider;
import insane96mcp.iguanatweaksreborn.module.combat.PiercingPickaxes;
import insane96mcp.iguanatweaksreborn.module.combat.RegeneratingAbsorption;
import insane96mcp.iguanatweaksreborn.module.experience.anvils.AnvilRepairReloadListener;
import insane96mcp.iguanatweaksreborn.module.items.itemstats.ItemStatsReloadListener;
import insane96mcp.iguanatweaksreborn.module.movement.stamina.Stamina;
import insane96mcp.iguanatweaksreborn.module.sleeprespawn.tiredness.Tiredness;
import insane96mcp.iguanatweaksreborn.module.world.spawners.capability.SpawnerData;
import insane96mcp.iguanatweaksreborn.module.world.spawners.capability.SpawnerDataAttacher;
import insane96mcp.iguanatweaksreborn.network.NetworkHandler;
import insane96mcp.iguanatweaksreborn.setup.ITRCommonConfig;
import insane96mcp.iguanatweaksreborn.setup.ITRPackSource;
import insane96mcp.iguanatweaksreborn.setup.ITRRegistries;
import insane96mcp.iguanatweaksreborn.setup.IntegratedDataPack;
import insane96mcp.iguanatweaksreborn.setup.client.ClientSetup;
import insane96mcp.iguanatweaksreborn.setup.client.ITRClientConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Mod("iguanatweaksreborn")
public class IguanaTweaksReborn
{
	public static final String MOD_ID = "iguanatweaksreborn";
	public static final String RESOURCE_PREFIX = MOD_ID + ":";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final String CONFIG_FOLDER = "config/" + MOD_ID;

    public static final ResourceLocation GUI_ICONS = new ResourceLocation(IguanaTweaksReborn.MOD_ID, "textures/gui/icons.png");

    public static DecimalFormat ONE_DECIMAL_FORMATTER;

    public IguanaTweaksReborn() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ITRClientConfig.CONFIG_SPEC, MOD_ID + "/client.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ITRCommonConfig.CONFIG_SPEC, MOD_ID + "/common.toml");
        MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(SpawnerDataAttacher.class);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(this::addPackFinders);
        modEventBus.addListener(PiercingPickaxes::piercingDamageAttribute);
        modEventBus.addListener(RegeneratingAbsorption::regeneratingAbsorptionAttribute);
        modEventBus.register(Stamina.class);
		modEventBus.register(SpawnerData.class);
        modEventBus.register(Tiredness.class);
        ITRRegistries.REGISTRIES.forEach(register -> register.register(modEventBus));

        ITRTriggers.init();

        if (FMLLoader.getDist().isClient()) {
            modEventBus.addListener(ClientSetup::onBuildCreativeModeTabContents);
            modEventBus.addListener(ClientSetup::registerEntityRenderers);
            //modEventBus.addListener(ClientSetup::registerRecipeBookCategories);
            //modEventBus.addListener(ClientSetup::registerParticleFactories);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(AnvilRepairReloadListener.INSTANCE);
        event.addListener(ItemStatsReloadListener.INSTANCE);
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        ITRCommand.register(event.getDispatcher());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        NetworkHandler.init();

        /*event.enqueueWork(() -> {
            DispenserBlock.registerBehavior(Fletching.QUARTZ_ARROW_ITEM.get(), new SRArrowDispenseBehaviour());
            DispenserBlock.registerBehavior(Fletching.DIAMOND_ARROW_ITEM.get(), new SRArrowDispenseBehaviour());
            DispenserBlock.registerBehavior(Fletching.EXPLOSIVE_ARROW_ITEM.get(), new SRArrowDispenseBehaviour());
            DispenserBlock.registerBehavior(Fletching.TORCH_ARROW_ITEM.get(), new SRArrowDispenseBehaviour());
        });*/
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ClientSetup.init(event);
    }

    public void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        /*generator.addProvider(event.includeServer(), new SRRecipeProvider(generator.getPackOutput()));
        generator.addProvider(event.includeServer(), new SRGlobalLootModifierProvider(generator.getPackOutput(), IguanaTweaksReborn.MOD_ID));*/
        ITRBlockTagsProvider blockTags = new ITRBlockTagsProvider(generator.getPackOutput(), lookupProvider, IguanaTweaksReborn.MOD_ID, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new ITRItemTagsProvider(generator.getPackOutput(), lookupProvider, blockTags.contentsGetter(), IguanaTweaksReborn.MOD_ID, existingFileHelper));
        generator.addProvider(event.includeServer(), new ITRDamageTypeTagsProvider(generator.getPackOutput(), lookupProvider, IguanaTweaksReborn.MOD_ID, existingFileHelper));
        generator.addProvider(event.includeClient(), new ITRBlockStatesProvider(generator.getPackOutput(), IguanaTweaksReborn.MOD_ID, existingFileHelper));
        generator.addProvider(event.includeClient(), new ITRBlockModelsProvider(generator.getPackOutput(), IguanaTweaksReborn.MOD_ID, existingFileHelper));
        generator.addProvider(event.includeClient(), new ITRItemModelsProvider(generator.getPackOutput(), IguanaTweaksReborn.MOD_ID, existingFileHelper));
    }

    public void addPackFinders(AddPackFindersEvent event)
    {
        for (IntegratedDataPack dataPack : IntegratedDataPack.INTEGRATED_DATA_PACKS) {
            if (event.getPackType() != dataPack.getPackType())
                continue;

            Path resourcePath = ModList.get().getModFileById(MOD_ID).getFile().findResource("integrated_packs/" + dataPack.getPath());
            var pack = Pack.readMetaAndCreate(IguanaTweaksReborn.RESOURCE_PREFIX + dataPack.getPath(), dataPack.getDescription(), dataPack.shouldBeEnabled(),
                    (path) -> new PathPackResources(path, resourcePath, false), PackType.SERVER_DATA, Pack.Position.TOP, dataPack.shouldBeEnabled() ? PackSource.DEFAULT : ITRPackSource.DISABLED);
            event.addRepositorySource((packConsumer) -> packConsumer.accept(pack));
        }
    }

    @SubscribeEvent
    public void onServerStartedEvent(ServerStartedEvent event)
    {
        boolean hasDisabledPack = false;
        PackRepository packRepository = event.getServer().getPackRepository();
        List<Pack> list = Lists.newArrayList(packRepository.getSelectedPacks());
        for (IntegratedDataPack dataPack : IntegratedDataPack.INTEGRATED_DATA_PACKS) {
            String dataPackId = IguanaTweaksReborn.RESOURCE_PREFIX + dataPack.getPath();
            Pack pack = packRepository.getPack(dataPackId);
            if (pack != null && !dataPack.shouldBeEnabled()) {
                list.remove(pack);
                hasDisabledPack = true;
            }
        }
        if (hasDisabledPack)
            event.getServer().reloadResources(list.stream().map(Pack::getId).collect(Collectors.toList()));
    }

}
