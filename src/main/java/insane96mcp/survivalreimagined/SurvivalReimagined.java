package insane96mcp.survivalreimagined;

import insane96mcp.survivalreimagined.data.SRDataReloadListener;
import insane96mcp.survivalreimagined.data.generator.SRBlockTagsProvider;
import insane96mcp.survivalreimagined.data.generator.SRGlobalLootModifierProvider;
import insane96mcp.survivalreimagined.data.generator.SRItemTagsProvider;
import insane96mcp.survivalreimagined.data.generator.SRRecipeProvider;
import insane96mcp.survivalreimagined.data.generator.client.SRBlockModelsProvider;
import insane96mcp.survivalreimagined.data.generator.client.SRBlockStatesProvider;
import insane96mcp.survivalreimagined.data.generator.client.SRItemModelsProvider;
import insane96mcp.survivalreimagined.module.combat.PiercingPickaxes;
import insane96mcp.survivalreimagined.module.combat.fletching.Fletching;
import insane96mcp.survivalreimagined.module.combat.fletching.dispenser.SRArrowDispenseBehaviour;
import insane96mcp.survivalreimagined.module.experience.anvils.AnvilRecipeReloadListener;
import insane96mcp.survivalreimagined.module.items.copper.ElectrocutionSparkParticle;
import insane96mcp.survivalreimagined.module.sleeprespawn.tiredness.Tiredness;
import insane96mcp.survivalreimagined.module.world.spawners.capability.SpawnerData;
import insane96mcp.survivalreimagined.module.world.spawners.capability.SpawnerDataAttacher;
import insane96mcp.survivalreimagined.network.NetworkHandler;
import insane96mcp.survivalreimagined.setup.*;
import insane96mcp.survivalreimagined.setup.client.ClientSetup;
import insane96mcp.survivalreimagined.setup.client.SRClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.concurrent.CompletableFuture;

@Mod("survivalreimagined")
public class SurvivalReimagined
{
	public static final String MOD_ID = "survivalreimagined";
	public static final String RESOURCE_PREFIX = MOD_ID + ":";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final String CONFIG_FOLDER = "config/" + MOD_ID;

    public static final ResourceLocation GUI_ICONS = new ResourceLocation(SurvivalReimagined.MOD_ID, "textures/gui/icons.png");

    public static DecimalFormat ONE_DECIMAL_FORMATTER;
    public static final RecipeBookType MULTI_ITEM_BLASTING_RECIPE_BOOK_TYPE = RecipeBookType.create(SurvivalReimagined.RESOURCE_PREFIX + "multi_item_blasting");
    public static final RecipeBookType MULTI_ITEM_SOUL_BLASTING_RECIPE_BOOK_TYPE = RecipeBookType.create(SurvivalReimagined.RESOURCE_PREFIX + "multi_item_soul_blasting");
    public static final RecipeBookType FORGING_RECIPE_BOOK_TYPE = RecipeBookType.create(SurvivalReimagined.RESOURCE_PREFIX + "forging");
    public static final RecipeBookType FLETCHING_RECIPE_BOOK_TYPE = RecipeBookType.create(SurvivalReimagined.RESOURCE_PREFIX + "fletching");

    public SurvivalReimagined() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SRClientConfig.CONFIG_SPEC, MOD_ID + "/client.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SRCommonConfig.CONFIG_SPEC, MOD_ID + "/common.toml");
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(SpawnerDataAttacher.class);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(ClientSetup::entityRenderEvent);
        modEventBus.addListener(ClientSetup::onRegisterRecipeBookCategories);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(ClientSetup::onBuildCreativeModeTabContents);
        modEventBus.addListener(this::addPackFinders);
        modEventBus.addListener(this::registerParticleFactories);
        modEventBus.addListener(PiercingPickaxes::piercingDamageAttribute);
        modEventBus.register(Tiredness.class);
        modEventBus.register(SpawnerData.class);
        SRSoundEvents.REGISTRY.register(modEventBus);
        SRParticles.REGISTRY.register(modEventBus);
        SRMobEffects.REGISTRY.register(modEventBus);
        SRAttributes.REGISTRY.register(modEventBus);
        SRBlocks.REGISTRY.register(modEventBus);
        SRBlockEntityTypes.REGISTRY.register(modEventBus);
        SRItems.REGISTRY.register(modEventBus);
        SRFeatureType.REGISTRY.register(modEventBus);
        SRRuleTestType.REGISTRY.register(modEventBus);
        SRRecipeTypes.REGISTRY.register(modEventBus);
        SRRecipeSerializers.REGISTRY.register(modEventBus);
        SRMenuType.REGISTRY.register(modEventBus);
        SREnchantments.REGISTRY.register(modEventBus);
        SREntityTypes.REGISTRY.register(modEventBus);
        SRGlobalLootModifiers.REGISTRY.register(modEventBus);
        SRLootItemConditions.REGISTRY.register(modEventBus);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onAddReloadListener(AddReloadListenerEvent event) {
        SRDataReloadListener.reloadContext = event.getConditionContext();
        event.addListener(SRDataReloadListener.INSTANCE);
        event.addListener(AnvilRecipeReloadListener.INSTANCE);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        NetworkHandler.init();

        event.enqueueWork(() -> {
            DispenserBlock.registerBehavior(Fletching.QUARTZ_ARROW_ITEM.get(), new SRArrowDispenseBehaviour());
            DispenserBlock.registerBehavior(Fletching.DIAMOND_ARROW_ITEM.get(), new SRArrowDispenseBehaviour());
            DispenserBlock.registerBehavior(Fletching.EXPLOSIVE_ARROW_ITEM.get(), new SRArrowDispenseBehaviour());
            DispenserBlock.registerBehavior(Fletching.TORCH_ARROW_ITEM.get(), new SRArrowDispenseBehaviour());
        });
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ClientSetup.init(event);
    }

    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(event.includeServer(), new SRRecipeProvider(generator.getPackOutput()));
        generator.addProvider(event.includeServer(), new SRGlobalLootModifierProvider(generator.getPackOutput(), SurvivalReimagined.MOD_ID));
        SRBlockTagsProvider blockTags = new SRBlockTagsProvider(generator.getPackOutput(), lookupProvider, SurvivalReimagined.MOD_ID, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new SRItemTagsProvider(generator.getPackOutput(), lookupProvider, blockTags.contentsGetter(), SurvivalReimagined.MOD_ID, existingFileHelper));
        generator.addProvider(event.includeClient(), new SRBlockStatesProvider(generator.getPackOutput(), SurvivalReimagined.MOD_ID, existingFileHelper));
        generator.addProvider(event.includeClient(), new SRBlockModelsProvider(generator.getPackOutput(), SurvivalReimagined.MOD_ID, existingFileHelper));
        generator.addProvider(event.includeClient(), new SRItemModelsProvider(generator.getPackOutput(), SurvivalReimagined.MOD_ID, existingFileHelper));
    }

    public void addPackFinders(AddPackFindersEvent event)
    {
        for (IntegratedDataPack dataPack : IntegratedDataPack.INTEGRATED_DATA_PACKS) {
            if (event.getPackType() != dataPack.getPackType())
                continue;

            Path resourcePath = ModList.get().getModFileById(MOD_ID).getFile().findResource("integrated_packs/" + dataPack.getPath());
            var pack = Pack.readMetaAndCreate(SurvivalReimagined.RESOURCE_PREFIX + dataPack.getPath(), dataPack.getDescription(), dataPack.shouldBeEnabled(),
                    (path) -> new PathPackResources(path, resourcePath, false), PackType.SERVER_DATA, Pack.Position.TOP, dataPack.shouldBeEnabled() ? PackSource.DEFAULT : SRPackSource.DISABLED);
            event.addRepositorySource((packConsumer) -> packConsumer.accept(pack));
        }
    }

    public void registerParticleFactories(RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(SRParticles.ELECTROCUTION_SPARKS.get(), ElectrocutionSparkParticle.Provider::new);
    }

}
