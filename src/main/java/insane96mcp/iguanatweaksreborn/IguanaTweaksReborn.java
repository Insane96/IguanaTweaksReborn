package insane96mcp.iguanatweaksreborn;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.misc.capability.SpawnerProvider;
import insane96mcp.iguanatweaksreborn.network.SyncHandler;
import insane96mcp.iguanatweaksreborn.setup.ITClientConfig;
import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.iguanatweaksreborn.setup.ITMobEffects;
import insane96mcp.iguanatweaksreborn.utils.Weights;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.resource.PathPackResources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Mod("iguanatweaksreborn")
public class IguanaTweaksReborn
{
	public static final String MOD_ID = "iguanatweaksreborn";
	public static final String RESOURCE_PREFIX = MOD_ID + ":";
    public static final Logger LOGGER = LogManager.getLogger();

    public IguanaTweaksReborn() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ITCommonConfig.CONFIG_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ITClientConfig.CONFIG_SPEC);
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::addPackFinders);
        ITMobEffects.MOB_EFFECTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Weights.initMaterialWeight();
    }

    @SubscribeEvent
    public void attachCapBlockEntity(final AttachCapabilitiesEvent<BlockEntity> event)
    {
        if (event.getObject() instanceof SpawnerBlockEntity)
            event.addCapability(SpawnerProvider.IDENTIFIER, new SpawnerProvider());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        SyncHandler.init();
    }

    public void clientSetup(final FMLClientSetupEvent event) {
        Modules.sleepRespawn.tiredness.registerGui();
    }

    public void addPackFinders(AddPackFindersEvent event)
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
    }
}
