package insane96mcp.survivalreimagined.setup.client;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.experience.feature.EnchantmentsFeature;
import insane96mcp.survivalreimagined.module.farming.feature.BoneMeal;
import insane96mcp.survivalreimagined.module.farming.feature.Crops;
import insane96mcp.survivalreimagined.module.hungerhealth.feature.FoodDrinks;
import insane96mcp.survivalreimagined.module.items.feature.*;
import insane96mcp.survivalreimagined.module.mining.feature.Mithril;
import insane96mcp.survivalreimagined.module.mining.feature.SoulSteel;
import insane96mcp.survivalreimagined.module.movement.feature.Minecarts;
import insane96mcp.survivalreimagined.module.sleeprespawn.feature.Death;
import insane96mcp.survivalreimagined.module.sleeprespawn.feature.Respawn;
import insane96mcp.survivalreimagined.module.world.feature.CoalFire;
import insane96mcp.survivalreimagined.module.world.feature.OreGeneration;
import insane96mcp.survivalreimagined.setup.SREntityTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class ClientSetup {
    public static void creativeTabsBuildContents(final CreativeModeTabEvent.BuildContents event)
    {
        if (event.getTab() == CreativeModeTabs.TOOLS_AND_UTILITIES)
        {
            event.accept(FlintExpansion.AXE.get());
            event.accept(FlintExpansion.PICKAXE.get());
            event.accept(FlintExpansion.SHOVEL.get());
            event.accept(FlintExpansion.HOE.get());
            event.accept(CopperTools.AXE.get());
            event.accept(CopperTools.PICKAXE.get());
            event.accept(CopperTools.SHOVEL.get());
            event.accept(CopperTools.HOE.get());
            event.accept(Mithril.AXE.get());
            event.accept(Mithril.PICKAXE.get());
            event.accept(Mithril.SHOVEL.get());
            event.accept(Mithril.HOE.get());
            event.accept(SoulSteel.AXE.get());
            event.accept(SoulSteel.PICKAXE.get());
            event.accept(SoulSteel.SHOVEL.get());
            event.accept(SoulSteel.HOE.get());
        }
        else if (event.getTab() == CreativeModeTabs.COMBAT) {
            event.accept(FlintExpansion.SWORD.get());
            event.accept(FlintExpansion.SHIELD.get());
            event.accept(CopperTools.SWORD.get());

            event.accept(ChainedCopperArmor.HELMET.get());
            event.accept(ChainedCopperArmor.CHESTPLATE.get());
            event.accept(ChainedCopperArmor.LEGGINGS.get());
            event.accept(ChainedCopperArmor.BOOTS.get());

            event.accept(Mithril.HELMET.get());
            event.accept(Mithril.CHESTPLATE.get());
            event.accept(Mithril.LEGGINGS.get());
            event.accept(Mithril.BOOTS.get());
            event.accept(Mithril.SWORD.get());
            event.accept(Mithril.SHIELD.get());

            event.accept(SoulSteel.HELMET.get());
            event.accept(SoulSteel.CHESTPLATE.get());
            event.accept(SoulSteel.LEGGINGS.get());
            event.accept(SoulSteel.BOOTS.get());
            event.accept(SoulSteel.SWORD.get());
            event.accept(SoulSteel.SHIELD.get());
        }
        else if (event.getTab() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(Mithril.BLOCK.item().get());
            event.accept(SoulSteel.BLOCK.item().get());
            event.accept(FlintExpansion.FLINT_BLOCK.item().get());
            event.accept(FlintExpansion.POLISHED_FLINT_BLOCK.item().get());
            event.accept(CoalFire.CHARCOAL_LAYER.item().get());
            event.accept(Death.GRAVE.item().get());
        }
        else if (event.getTab() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(Respawn.RESPAWN_OBELISK.item().get());
            event.accept(Crate.BLOCK.item().get());
        }
        else if (event.getTab() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(Minecarts.NETHER_INFUSED_POWERED_RAIL.item().get());
            event.accept(ExplosiveBarrel.BLOCK.item().get());
        }
        else if (event.getTab() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(Mithril.ORE.item().get());
            event.accept(Mithril.DEEPSLATE_ORE.item().get());
            event.accept(Crops.CARROT_SEEDS.get());
            event.accept(Crops.POTATO_SEEDS.get());
            event.accept(OreGeneration.COPPER_ORE_ROCK.item().get());
            event.accept(OreGeneration.IRON_ORE_ROCK.item().get());
            event.accept(OreGeneration.GOLD_ORE_ROCK.item().get());
            event.accept(OreGeneration.POOR_RICH_IRON_ORE.poorOre().item().get());
            event.accept(OreGeneration.POOR_RICH_IRON_ORE.poorDeepslateOre().item().get());
            event.accept(OreGeneration.POOR_RICH_IRON_ORE.richOre().item().get());
            event.accept(OreGeneration.POOR_RICH_IRON_ORE.richDeepslateOre().item().get());
            event.accept(OreGeneration.POOR_RICH_COPPER_ORE.poorOre().item().get());
            event.accept(OreGeneration.POOR_RICH_COPPER_ORE.poorDeepslateOre().item().get());
            event.accept(OreGeneration.POOR_RICH_COPPER_ORE.richOre().item().get());
            event.accept(OreGeneration.POOR_RICH_COPPER_ORE.richDeepslateOre().item().get());
            event.accept(OreGeneration.POOR_RICH_GOLD_ORE.poorOre().item().get());
            event.accept(OreGeneration.POOR_RICH_GOLD_ORE.poorDeepslateOre().item().get());
            event.accept(OreGeneration.POOR_RICH_GOLD_ORE.richOre().item().get());
            event.accept(OreGeneration.POOR_RICH_GOLD_ORE.richDeepslateOre().item().get());
            event.accept(BoneMeal.RICH_FARMLAND.item().get());
        }
        else if (event.getTab() == CreativeModeTabs.INGREDIENTS) {
            event.accept(Mithril.INGOT.get());
            event.accept(Mithril.NUGGET.get());
            event.accept(SoulSteel.INGOT.get());
            event.accept(SoulSteel.NUGGET.get());
            event.accept(AncientLapis.ANCIENT_LAPIS.get());
            event.accept(EnchantmentsFeature.CLEANSED_LAPIS.get());
        }
        else if (event.getTab() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(FoodDrinks.BROWN_MUSHROOM_STEW.get());
            event.accept(FoodDrinks.RED_MUSHROOM_STEW.get());
        }
    }

    public static void init(FMLClientSetupEvent event) {
        DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS = new DecimalFormatSymbols(Minecraft.getInstance().getLocale());
        SurvivalReimagined.ONE_DECIMAL_FORMATTER = new DecimalFormat("#.#", DECIMAL_FORMAT_SYMBOLS);

        event.enqueueWork(() ->
                ItemProperties.register(Altimeter.ITEM.get(), new ResourceLocation(SurvivalReimagined.MOD_ID, "y"), (stack, clientLevel, livingEntity, entityId) -> {
                    if (livingEntity == null)
                        return 96f;
                    return (float) livingEntity.getY();
                }));
    }

    public static void entityRenderEvent(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SREntityTypes.PILABLE_FALLING_LAYER.get(), FallingBlockRenderer::new);
    }
}
