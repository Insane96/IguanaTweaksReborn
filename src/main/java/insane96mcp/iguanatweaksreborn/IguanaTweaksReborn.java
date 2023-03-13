package insane96mcp.iguanatweaksreborn;

import insane96mcp.iguanatweaksreborn.data.ITDataReloadListener;
import insane96mcp.iguanatweaksreborn.module.misc.capability.SpawnerProvider;
import insane96mcp.iguanatweaksreborn.module.sleeprespawn.feature.Tiredness;
import insane96mcp.iguanatweaksreborn.network.NetworkHandler;
import insane96mcp.iguanatweaksreborn.setup.*;
import insane96mcp.iguanatweaksreborn.utils.Weights;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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

    public static final String CONFIG_FOLDER = "config/" + MOD_ID;

    public static final ResourceLocation GUI_ICONS = new ResourceLocation(IguanaTweaksReborn.MOD_ID, "textures/gui/icons.png");

    public IguanaTweaksReborn() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ITClientConfig.CONFIG_SPEC, MOD_ID + "/client.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ITCommonConfig.CONFIG_SPEC, MOD_ID + "/common.toml");
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(this::addPackFinders);
        FMLJavaModLoadingContext.get().getModEventBus().register(Tiredness.class);
        ITSoundEvents.SOUND_EVENTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITMobEffects.MOB_EFFECTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
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

    public void clientSetup(final FMLClientSetupEvent event) {

    }

    /*public void addPackFinders(AddPackFindersEvent event)
    {
        for (IntegratedDataPack dp : IntegratedDataPack.INTEGRATED_DATA_PACKS) {
            if (event.getPackType() != dp.packType)
                continue;

            try
            {
                Path resourcePath = ModList.get().getModFileById(MOD_ID).getFile().findResource("integrated_packs/" + dp.path);
                try (PathPackResources pack = new PathPackResources(ModList.get().getModFileById(MOD_ID).getFile().getFileName() + ":" + resourcePath, resourcePath)) {
                    PackMetadataSection metadataSection = pack.getMetadataSection(PackMetadataSection.SERIALIZER);
                    if (metadataSection != null) {
                        event.addRepositorySource((packConsumer, packConstructor) ->
                                packConsumer.accept(packConstructor.create(
                                        MOD_ID + "/" + dp.path, dp.description, false,
                                        () -> pack, metadataSection, Pack.Position.BOTTOM, PackSource.BUILT_IN, false)));
                    }
                }
            }
            catch(IOException ex)
            {
                throw new RuntimeException(ex);
            }
        }
    }

    private static class IntegratedDataPack {
        PackType packType;
        String path;
        Component description;

        public static final List<IntegratedDataPack> INTEGRATED_DATA_PACKS = List.of(
                //new IntegratedDataPack(PackType.SERVER_DATA, "vanilla_tweaks", new TextComponent("IT Reborn Vanilla Tweaks")),
                //new IntegratedDataPack(PackType.SERVER_DATA, "cheaper_chains", new TextComponent("IT Reborn Cheaper Chains"))
        );

        public IntegratedDataPack(PackType packType, String path, Component description) {
            this.packType = packType;
            this.path = path;
            this.description = description;
        }
    }*/
}
