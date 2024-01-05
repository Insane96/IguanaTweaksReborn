package insane96mcp.iguanatweaksreborn.setup.client;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.misc.beaconconduit.BeaconConduit;
import insane96mcp.iguanatweaksreborn.module.misc.beaconconduit.ITRBeaconRenderer;
import insane96mcp.iguanatweaksreborn.module.misc.beaconconduit.ITRBeaconScreen;
import insane96mcp.iguanatweaksreborn.module.sleeprespawn.death.Death;
import insane96mcp.iguanatweaksreborn.module.world.CyanFlower;
import insane96mcp.iguanatweaksreborn.setup.ITRRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.function.Supplier;

public class ClientSetup {
    public static void onBuildCreativeModeTabContents(final BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            addAfter(event, Items.BEACON, BeaconConduit.BEACON.item());
        }
        else if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            addAfter(event, Items.CHAIN, Death.GRAVE.item());
        }
        else if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            addAfter(event, Items.POPPY, CyanFlower.FLOWER.item());
        }
    }

    public static void addBefore(BuildCreativeModeTabContentsEvent event, Item before, ItemLike itemToAdd) {
        event.getEntries().putBefore(new ItemStack(before), new ItemStack(itemToAdd), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    public static void addAfter(BuildCreativeModeTabContentsEvent event, Item after, ItemLike itemToAdd) {
        event.getEntries().putAfter(new ItemStack(after), new ItemStack(itemToAdd), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    public static void addBefore(BuildCreativeModeTabContentsEvent event, Item before, Supplier<? extends ItemLike> itemToAdd) {
        addBefore(event, before, itemToAdd.get());
    }

    public static void addAfter(BuildCreativeModeTabContentsEvent event, Item after, Supplier<? extends ItemLike> itemToAdd) {
        addAfter(event, after, itemToAdd.get());
    }

    public static void init(FMLClientSetupEvent event) {
        DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS = new DecimalFormatSymbols(Minecraft.getInstance().getLocale());
        IguanaTweaksReborn.ONE_DECIMAL_FORMATTER = new DecimalFormat("#.#", DECIMAL_FORMAT_SYMBOLS);

        MenuScreens.register(BeaconConduit.BEACON_MENU_TYPE.get(), ITRBeaconScreen::new);
    }

    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ITRRegistries.PILABLE_FALLING_LAYER.get(), FallingBlockRenderer::new);
        event.registerBlockEntityRenderer(BeaconConduit.BEACON_BLOCK_ENTITY_TYPE.get(), ITRBeaconRenderer::new);
    }
}
