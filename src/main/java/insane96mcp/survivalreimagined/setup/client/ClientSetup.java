package insane96mcp.survivalreimagined.setup.client;

import com.google.common.collect.ImmutableList;
import insane96mcp.shieldsplus.setup.SPItems;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.combat.fletching.Fletching;
import insane96mcp.survivalreimagined.module.combat.fletching.client.FletchingScreen;
import insane96mcp.survivalreimagined.module.combat.fletching.client.SRArrowRenderer;
import insane96mcp.survivalreimagined.module.experience.Lapis;
import insane96mcp.survivalreimagined.module.experience.enchanting.EnchantingFeature;
import insane96mcp.survivalreimagined.module.experience.enchanting.SREnchantingTableRenderer;
import insane96mcp.survivalreimagined.module.experience.enchanting.SREnchantingTableScreen;
import insane96mcp.survivalreimagined.module.farming.bonemeal.BoneMeal;
import insane96mcp.survivalreimagined.module.farming.crops.Crops;
import insane96mcp.survivalreimagined.module.hungerhealth.fooddrinks.FoodDrinks;
import insane96mcp.survivalreimagined.module.items.ChainedCopperArmor;
import insane96mcp.survivalreimagined.module.items.altimeter.Altimeter;
import insane96mcp.survivalreimagined.module.items.copper.CopperToolsExpansion;
import insane96mcp.survivalreimagined.module.items.copper.ElectrocutionSparkParticle;
import insane96mcp.survivalreimagined.module.items.crate.Crate;
import insane96mcp.survivalreimagined.module.items.explosivebarrel.ExplosiveBarrel;
import insane96mcp.survivalreimagined.module.items.flintexpansion.FlintExpansion;
import insane96mcp.survivalreimagined.module.items.recallidol.RecallIdol;
import insane96mcp.survivalreimagined.module.items.solarium.Solarium;
import insane96mcp.survivalreimagined.module.mining.Durium;
import insane96mcp.survivalreimagined.module.mining.SoulSteel;
import insane96mcp.survivalreimagined.module.mining.forging.ForgeRenderer;
import insane96mcp.survivalreimagined.module.mining.forging.ForgeScreen;
import insane96mcp.survivalreimagined.module.mining.forging.Forging;
import insane96mcp.survivalreimagined.module.mining.keego.Keego;
import insane96mcp.survivalreimagined.module.mining.miningcharge.MiningCharge;
import insane96mcp.survivalreimagined.module.mining.miningcharge.MiningChargeRenderer;
import insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.MultiBlockFurnaces;
import insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.client.MultiBlockBlastFurnaceScreen;
import insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.client.MultiBlockSoulBlastFurnaceScreen;
import insane96mcp.survivalreimagined.module.misc.beaconconduit.BeaconConduit;
import insane96mcp.survivalreimagined.module.misc.beaconconduit.SRBeaconRenderer;
import insane96mcp.survivalreimagined.module.misc.beaconconduit.SRBeaconScreen;
import insane96mcp.survivalreimagined.module.movement.minecarts.Minecarts;
import insane96mcp.survivalreimagined.module.sleeprespawn.Cloth;
import insane96mcp.survivalreimagined.module.sleeprespawn.death.Death;
import insane96mcp.survivalreimagined.module.sleeprespawn.respawn.Respawn;
import insane96mcp.survivalreimagined.module.world.CyanFlower;
import insane96mcp.survivalreimagined.module.world.coalfire.CoalFire;
import insane96mcp.survivalreimagined.module.world.oregeneration.OreGeneration;
import insane96mcp.survivalreimagined.setup.SRRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class ClientSetup {
    public static void onBuildCreativeModeTabContents(final BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES)
        {
            addAfter(event, Items.STONE_HOE, FlintExpansion.HOE.get());
            addAfter(event, Items.STONE_HOE, FlintExpansion.AXE.get());
            addAfter(event, Items.STONE_HOE, FlintExpansion.PICKAXE.get());
            addAfter(event, Items.STONE_HOE, FlintExpansion.SHOVEL.get());
            addAfter(event, FlintExpansion.HOE.get(), CopperToolsExpansion.COPPER_HOE.get());
            addAfter(event, FlintExpansion.HOE.get(), CopperToolsExpansion.COPPER_AXE.get());
            addAfter(event, FlintExpansion.HOE.get(), CopperToolsExpansion.COPPER_PICKAXE.get());
            addAfter(event, FlintExpansion.HOE.get(), CopperToolsExpansion.COPPER_SHOVEL.get());
            addAfter(event, Items.IRON_HOE, Solarium.HOE.get());
            addAfter(event, Items.IRON_HOE, Solarium.AXE.get());
            addAfter(event, Items.IRON_HOE, Solarium.PICKAXE.get());
            addAfter(event, Items.IRON_HOE, Solarium.SHOVEL.get());
            addAfter(event, Items.IRON_HOE, Durium.HOE.get());
            addAfter(event, Items.IRON_HOE, Durium.AXE.get());
            addAfter(event, Items.IRON_HOE, Durium.PICKAXE.get());
            addAfter(event, Items.IRON_HOE, Durium.SHOVEL.get());
            addAfter(event, Items.DIAMOND_HOE, Keego.HOE.get());
            addAfter(event, Items.DIAMOND_HOE, Keego.AXE.get());
            addAfter(event, Items.DIAMOND_HOE, Keego.PICKAXE.get());
            addAfter(event, Items.DIAMOND_HOE, Keego.SHOVEL.get());
            addAfter(event, Items.DIAMOND_HOE, CopperToolsExpansion.COATED_HOE.get());
            addAfter(event, Items.DIAMOND_HOE, CopperToolsExpansion.COATED_AXE.get());
            addAfter(event, Items.DIAMOND_HOE, CopperToolsExpansion.COATED_PICKAXE.get());
            addAfter(event, Items.DIAMOND_HOE, CopperToolsExpansion.COATED_SHOVEL.get());
            addAfter(event, Items.NETHERITE_HOE, SoulSteel.HOE.get());
            addAfter(event, Items.NETHERITE_HOE, SoulSteel.AXE.get());
            addAfter(event, Items.NETHERITE_HOE, SoulSteel.PICKAXE.get());
            addAfter(event, Items.NETHERITE_HOE, SoulSteel.SHOVEL.get());
            addBefore(event, Items.FLINT_AND_STEEL, CoalFire.FIRESTARTER.get());

            addAfter(event, Items.WOODEN_HOE, Forging.WOODEN_HAMMER.get());
            addAfter(event, Items.STONE_HOE, Forging.STONE_HAMMER.get());
            addAfter(event, FlintExpansion.HOE.get(), Forging.FLINT_HAMMER.get());
            addAfter(event, CopperToolsExpansion.COPPER_HOE.get(), Forging.COPPER_HAMMER.get());
            addAfter(event, CopperToolsExpansion.COATED_HOE.get(), Forging.COATED_COPPER_HAMMER.get());
            addAfter(event, Items.GOLDEN_HOE, Forging.GOLDEN_HAMMER.get());
            addAfter(event, Items.IRON_HOE, Forging.IRON_HAMMER.get());
            addAfter(event, Solarium.HOE.get(), Forging.SOLARIUM_HAMMER.get());
            addAfter(event, Durium.HOE.get(), Forging.DURIUM_HAMMER.get());
            addAfter(event, Items.DIAMOND_HOE, Forging.DIAMOND_HAMMER.get());
            addAfter(event, SoulSteel.HOE.get(), Forging.SOUL_STEEL_HAMMER.get());
            addAfter(event, Items.NETHERITE_HOE, Forging.NETHERITE_HAMMER.get());
            addAfter(event, Keego.HOE.get(), Forging.KEEGO_HAMMER.get());

            addAfter(event, Items.RECOVERY_COMPASS, Altimeter.ITEM.get());
            addAfter(event, Items.ENDER_EYE, RecallIdol.ITEM.get());

            addAfter(event, Items.RAIL, Minecarts.NETHER_INFUSED_POWERED_RAIL.item().get());
            addAfter(event, Items.RAIL, Minecarts.GOLDEN_POWERED_RAIL.item().get());
            addAfter(event, Items.RAIL, Minecarts.COPPER_POWERED_RAIL.item().get());
        }
        else if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            addAfter(event, Items.STONE_SWORD, FlintExpansion.SWORD.get());
            addAfter(event, FlintExpansion.SWORD.get(), CopperToolsExpansion.COPPER_SWORD.get());
            addAfter(event, Items.IRON_SWORD, Solarium.SWORD.get());
            addAfter(event, Items.IRON_SWORD, Durium.SWORD.get());
            addAfter(event, Items.DIAMOND_SWORD, Keego.SWORD.get());
            addAfter(event, Items.DIAMOND_SWORD, CopperToolsExpansion.COATED_SWORD.get());
            addAfter(event, Items.NETHERITE_SWORD, SoulSteel.SWORD.get());

            addAfter(event, Items.STONE_AXE, FlintExpansion.AXE.get());
            addAfter(event, FlintExpansion.AXE.get(), CopperToolsExpansion.COPPER_AXE.get());
            addAfter(event, Items.IRON_AXE, Solarium.AXE.get());
            addAfter(event, Items.IRON_AXE, Durium.AXE.get());
            addAfter(event, Items.DIAMOND_AXE, Keego.AXE.get());
            addAfter(event, Items.DIAMOND_AXE, CopperToolsExpansion.COATED_AXE.get());
            addAfter(event, Items.NETHERITE_AXE, SoulSteel.AXE.get());

            addAfter(event, SPItems.STONE_SHIELD.get(), FlintExpansion.SHIELD.get());
            addAfter(event, FlintExpansion.SHIELD.get(), CopperToolsExpansion.COPPER_SHIELD.get());
            addAfter(event, Items.SHIELD, Solarium.SHIELD.get());
            addAfter(event, Items.SHIELD, Durium.SHIELD.get());
            addAfter(event, SPItems.DIAMOND_SHIELD.get(), Keego.SHIELD.get());
            addAfter(event, SPItems.DIAMOND_SHIELD.get(), CopperToolsExpansion.COATED_SHIELD.get());
            addAfter(event, SPItems.NETHERITE_SHIELD.get(), SoulSteel.SHIELD.get());

            addAfter(event, Items.LEATHER_BOOTS, ChainedCopperArmor.BOOTS.get());
            addAfter(event, Items.LEATHER_BOOTS, ChainedCopperArmor.LEGGINGS.get());
            addAfter(event, Items.LEATHER_BOOTS, ChainedCopperArmor.CHESTPLATE.get());
            addAfter(event, Items.LEATHER_BOOTS, ChainedCopperArmor.HELMET.get());
            addAfter(event, Items.IRON_BOOTS, Solarium.BOOTS.get());
            addAfter(event, Items.IRON_BOOTS, Solarium.LEGGINGS.get());
            addAfter(event, Items.IRON_BOOTS, Solarium.CHESTPLATE.get());
            addAfter(event, Items.IRON_BOOTS, Solarium.HELMET.get());
            addAfter(event, Items.IRON_BOOTS, Durium.BOOTS.get());
            addAfter(event, Items.IRON_BOOTS, Durium.LEGGINGS.get());
            addAfter(event, Items.IRON_BOOTS, Durium.CHESTPLATE.get());
            addAfter(event, Items.IRON_BOOTS, Durium.HELMET.get());
            addAfter(event, Items.DIAMOND_BOOTS, Keego.BOOTS.get());
            addAfter(event, Items.DIAMOND_BOOTS, Keego.LEGGINGS.get());
            addAfter(event, Items.DIAMOND_BOOTS, Keego.CHESTPLATE.get());
            addAfter(event, Items.DIAMOND_BOOTS, Keego.HELMET.get());
            addAfter(event, Items.NETHERITE_BOOTS, SoulSteel.BOOTS.get());
            addAfter(event, Items.NETHERITE_BOOTS, SoulSteel.LEGGINGS.get());
            addAfter(event, Items.NETHERITE_BOOTS, SoulSteel.CHESTPLATE.get());
            addAfter(event, Items.NETHERITE_BOOTS, SoulSteel.HELMET.get());

            addAfter(event, Items.ARROW, Fletching.TORCH_ARROW_ITEM.get());
            addAfter(event, Items.ARROW, Fletching.EXPLOSIVE_ARROW_ITEM.get());
            addAfter(event, Items.ARROW, Fletching.DIAMOND_ARROW_ITEM.get());
            addAfter(event, Items.ARROW, Fletching.QUARTZ_ARROW_ITEM.get());
        }
        else if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(Durium.SCRAP_BLOCK.item().get());
            event.accept(Durium.BLOCK.item().get());
            event.accept(SoulSteel.BLOCK.item().get());
            event.accept(Keego.BLOCK.item().get());
            event.accept(FlintExpansion.FLINT_BLOCK.item().get());
            event.accept(FlintExpansion.POLISHED_FLINT_BLOCK.item().get());
            event.accept(CoalFire.CHARCOAL_LAYER.item().get());
            event.accept(Death.GRAVE.item().get());
        }
        else if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(Respawn.RESPAWN_OBELISK.item().get());
            event.accept(Crate.ITEM.get());
            event.accept(MultiBlockFurnaces.BLAST_FURNACE.item().get());
            event.accept(MultiBlockFurnaces.SOUL_BLAST_FURNACE.item().get());
            event.accept(Forging.FORGE.item().get());
            event.accept(Fletching.FLETCHING_TABLE.item().get());
            event.accept(BeaconConduit.BEACON.item().get());
        }
        else if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            addAfter(event, Items.RAIL, Minecarts.NETHER_INFUSED_POWERED_RAIL.item().get());
            addAfter(event, Items.RAIL, Minecarts.GOLDEN_POWERED_RAIL.item().get());
            addAfter(event, Items.RAIL, Minecarts.COPPER_POWERED_RAIL.item().get());
            event.accept(ExplosiveBarrel.BLOCK.item().get());
            event.accept(MiningCharge.MINING_CHARGE.item().get());
        }
        else if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(Durium.ORE.item().get());
            event.accept(Durium.DEEPSLATE_ORE.item().get());
            event.accept(Keego.ORE.item().get());
            event.accept(Solarium.SOLIUM_MOSS.item().get());
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
            event.accept(CoalFire.SOUL_SAND_HELLISH_COAL_ORE.item().get());
            event.accept(CoalFire.SOUL_SOIL_HELLISH_COAL_ORE.item().get());
            event.accept(FlintExpansion.FLINT_ROCK.item().get());
            event.accept(CyanFlower.FLOWER.item().get());
        }
        else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(Durium.SCRAP_PIECE.get());
            event.accept(Durium.INGOT.get());
            event.accept(Durium.NUGGET.get());
            event.accept(Solarium.SOLARIUM_BALL.get());
            event.accept(SoulSteel.INGOT.get());
            event.accept(SoulSteel.NUGGET.get());
            event.accept(SoulSteel.UPGRADE_SMITHING_TEMPLATE.get());
            event.accept(Keego.GEM.get());
            event.accept(Lapis.CLEANSED_LAPIS.get());
            event.accept(Lapis.ANCIENT_LAPIS.get());
            event.accept(CoalFire.HELLISH_COAL.get());
            event.accept(Cloth.CLOTH.get());
        }
        else if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(FoodDrinks.BROWN_MUSHROOM_STEW.get());
            event.accept(FoodDrinks.RED_MUSHROOM_STEW.get());
        }
    }

    private static void addBefore(BuildCreativeModeTabContentsEvent event, Item before, Item itemToAdd) {
        event.getEntries().putBefore(new ItemStack(before), new ItemStack(itemToAdd), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    private static void addAfter(BuildCreativeModeTabContentsEvent event, Item after, Item itemToAdd) {
        event.getEntries().putAfter(new ItemStack(after), new ItemStack(itemToAdd), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
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

        MenuScreens.register(MultiBlockFurnaces.BLAST_FURNACE_MENU_TYPE.get(), MultiBlockBlastFurnaceScreen::new);
        MenuScreens.register(MultiBlockFurnaces.SOUL_BLAST_FURNACE_MENU_TYPE.get(), MultiBlockSoulBlastFurnaceScreen::new);
        MenuScreens.register(Forging.FORGE_MENU_TYPE.get(), ForgeScreen::new);
        MenuScreens.register(EnchantingFeature.ENCHANTING_TABLE_MENU_TYPE.get(), SREnchantingTableScreen::new);
        MenuScreens.register(Fletching.FLETCHING_MENU_TYPE.get(), FletchingScreen::new);
        MenuScreens.register(BeaconConduit.BEACON_MENU_TYPE.get(), SRBeaconScreen::new);
    }

    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SRRegistries.PILABLE_FALLING_LAYER.get(), FallingBlockRenderer::new);
        event.registerEntityRenderer(Fletching.QUARTZ_ARROW.get(), SRArrowRenderer::new);
        event.registerEntityRenderer(Fletching.DIAMOND_ARROW.get(), SRArrowRenderer::new);
        event.registerEntityRenderer(Fletching.EXPLOSIVE_ARROW.get(), SRArrowRenderer::new);
        event.registerEntityRenderer(Fletching.TORCH_ARROW.get(), SRArrowRenderer::new);
        event.registerEntityRenderer(MiningCharge.PRIMED_MINING_CHARGE.get(), MiningChargeRenderer::new);

        event.registerBlockEntityRenderer(Forging.FORGE_BLOCK_ENTITY_TYPE.get(), ForgeRenderer::new);
        event.registerBlockEntityRenderer(EnchantingFeature.ENSORCELLER_BLOCK_ENTITY_TYPE.get(), SREnchantingTableRenderer::new);

        event.registerBlockEntityRenderer(BeaconConduit.BEACON_BLOCK_ENTITY_TYPE.get(), SRBeaconRenderer::new);
    }

    static RecipeBookCategories BLAST_FURNACE_SEARCH = RecipeBookCategories.create(SurvivalReimagined.RESOURCE_PREFIX + "blast_furnace_search", new ItemStack(Items.COMPASS));
    static RecipeBookCategories BLAST_FURNACE_MISC = RecipeBookCategories.create(SurvivalReimagined.RESOURCE_PREFIX + "blast_furnace_misc", new ItemStack(MultiBlockFurnaces.BLAST_FURNACE.item().get()));
    public static final List<RecipeBookCategories> BLAST_FURNACE_CATEGORIES = ImmutableList.of(BLAST_FURNACE_SEARCH, BLAST_FURNACE_MISC);
    static RecipeBookCategories SOUL_BLAST_FURNACE_SEARCH = RecipeBookCategories.create(SurvivalReimagined.RESOURCE_PREFIX + "soul_blast_furnace_search", new ItemStack(Items.COMPASS));
    static RecipeBookCategories SOUL_BLAST_FURNACE_MISC = RecipeBookCategories.create(SurvivalReimagined.RESOURCE_PREFIX + "soul_blast_furnace_misc", new ItemStack(MultiBlockFurnaces.SOUL_BLAST_FURNACE.item().get()));
    public static final List<RecipeBookCategories> SOUL_BLAST_FURNACE_CATEGORIES = ImmutableList.of(SOUL_BLAST_FURNACE_SEARCH, SOUL_BLAST_FURNACE_MISC);
    static RecipeBookCategories FORGE_SEARCH = RecipeBookCategories.create("forge_search", new ItemStack(Items.COMPASS));
    static RecipeBookCategories FORGE_MISC = RecipeBookCategories.create("forge_misc", new ItemStack(Forging.FORGE.item().get()));
    public static final List<RecipeBookCategories> FORGE_CATEGORIES = ImmutableList.of(FORGE_SEARCH, FORGE_MISC);
    static RecipeBookCategories FLETCHING_SEARCH = RecipeBookCategories.create("fletching_search", new ItemStack(Items.COMPASS));
    static RecipeBookCategories FLETCHING_MISC = RecipeBookCategories.create("fletching_misc", new ItemStack(Items.FLETCHING_TABLE));
    public static final List<RecipeBookCategories> FLETCHING_CATEGORIES = ImmutableList.of(FLETCHING_SEARCH, FLETCHING_MISC);

    public static void registerRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
        event.registerBookCategories(SurvivalReimagined.MULTI_ITEM_BLASTING_RECIPE_BOOK_TYPE, BLAST_FURNACE_CATEGORIES);
        event.registerAggregateCategory(BLAST_FURNACE_SEARCH, ImmutableList.of(BLAST_FURNACE_MISC));
        event.registerRecipeCategoryFinder(MultiBlockFurnaces.BLASTING_RECIPE_TYPE.get(), r -> BLAST_FURNACE_MISC);

        event.registerBookCategories(SurvivalReimagined.MULTI_ITEM_SOUL_BLASTING_RECIPE_BOOK_TYPE, SOUL_BLAST_FURNACE_CATEGORIES);
        event.registerAggregateCategory(SOUL_BLAST_FURNACE_SEARCH, ImmutableList.of(SOUL_BLAST_FURNACE_MISC));
        event.registerRecipeCategoryFinder(MultiBlockFurnaces.SOUL_BLASTING_RECIPE_TYPE.get(), r -> SOUL_BLAST_FURNACE_MISC);

        event.registerBookCategories(SurvivalReimagined.FORGING_RECIPE_BOOK_TYPE, FORGE_CATEGORIES);
        event.registerAggregateCategory(FORGE_SEARCH, ImmutableList.of(FORGE_MISC));
        event.registerRecipeCategoryFinder(Forging.FORGE_RECIPE_TYPE.get(), r -> FORGE_MISC);

        event.registerBookCategories(SurvivalReimagined.FLETCHING_RECIPE_BOOK_TYPE, FLETCHING_CATEGORIES);
        event.registerAggregateCategory(FLETCHING_SEARCH, ImmutableList.of(FLETCHING_MISC));
        event.registerRecipeCategoryFinder(Fletching.FLETCHING_RECIPE_TYPE.get(), r -> FLETCHING_MISC);
    }

    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(CopperToolsExpansion.ELECTROCUTION_SPARKS.get(), ElectrocutionSparkParticle.Provider::new);
    }
}
