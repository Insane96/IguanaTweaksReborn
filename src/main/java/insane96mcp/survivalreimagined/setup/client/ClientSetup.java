package insane96mcp.survivalreimagined.setup.client;

import com.google.common.collect.ImmutableList;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.combat.client.FletchingScreen;
import insane96mcp.survivalreimagined.module.combat.client.SRArrowRenderer;
import insane96mcp.survivalreimagined.module.combat.feature.Fletching;
import insane96mcp.survivalreimagined.module.experience.feature.AncientLapis;
import insane96mcp.survivalreimagined.module.experience.feature.EnchantmentsFeature;
import insane96mcp.survivalreimagined.module.farming.feature.BoneMeal;
import insane96mcp.survivalreimagined.module.farming.feature.Crops;
import insane96mcp.survivalreimagined.module.hungerhealth.feature.FoodDrinks;
import insane96mcp.survivalreimagined.module.items.feature.*;
import insane96mcp.survivalreimagined.module.mining.client.ForgeScreen;
import insane96mcp.survivalreimagined.module.mining.client.MiningChargeRenderer;
import insane96mcp.survivalreimagined.module.mining.client.MultiBlockBlastFurnaceScreen;
import insane96mcp.survivalreimagined.module.mining.client.MultiBlockSoulBlastFurnaceScreen;
import insane96mcp.survivalreimagined.module.mining.feature.*;
import insane96mcp.survivalreimagined.module.movement.feature.Minecarts;
import insane96mcp.survivalreimagined.module.sleeprespawn.feature.Cloth;
import insane96mcp.survivalreimagined.module.sleeprespawn.feature.Death;
import insane96mcp.survivalreimagined.module.sleeprespawn.feature.Respawn;
import insane96mcp.survivalreimagined.module.world.feature.CoalFire;
import insane96mcp.survivalreimagined.module.world.feature.CyanFlower;
import insane96mcp.survivalreimagined.module.world.feature.OreGeneration;
import insane96mcp.survivalreimagined.setup.SREntityTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

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
            event.accept(Durium.AXE.get());
            event.accept(Durium.PICKAXE.get());
            event.accept(Durium.SHOVEL.get());
            event.accept(Durium.HOE.get());
            event.accept(SoulSteel.AXE.get());
            event.accept(SoulSteel.PICKAXE.get());
            event.accept(SoulSteel.SHOVEL.get());
            event.accept(SoulSteel.HOE.get());
            event.accept(CoalFire.FIRESTARTER.get());

            event.accept(Forging.STONE_HAMMER.get());
            event.accept(Forging.FLINT_HAMMER.get());
            event.accept(Forging.COPPER_HAMMER.get());
            event.accept(Forging.GOLDEN_HAMMER.get());
            event.accept(Forging.IRON_HAMMER.get());
            event.accept(Forging.DURIUM_HAMMER.get());
            event.accept(Forging.DIAMOND_HAMMER.get());
            event.accept(Forging.SOUL_STEEL_HAMMER.get());
            event.accept(Forging.NETHERITE_HAMMER.get());
        }
        else if (event.getTab() == CreativeModeTabs.COMBAT) {
            event.accept(FlintExpansion.SWORD.get());
            event.accept(FlintExpansion.SHIELD.get());
            event.accept(CopperTools.SWORD.get());

            event.accept(ChainedCopperArmor.HELMET.get());
            event.accept(ChainedCopperArmor.CHESTPLATE.get());
            event.accept(ChainedCopperArmor.LEGGINGS.get());
            event.accept(ChainedCopperArmor.BOOTS.get());

            event.accept(Durium.HELMET.get());
            event.accept(Durium.CHESTPLATE.get());
            event.accept(Durium.LEGGINGS.get());
            event.accept(Durium.BOOTS.get());
            event.accept(Durium.SWORD.get());
            event.accept(Durium.SHIELD.get());

            event.accept(SoulSteel.HELMET.get());
            event.accept(SoulSteel.CHESTPLATE.get());
            event.accept(SoulSteel.LEGGINGS.get());
            event.accept(SoulSteel.BOOTS.get());
            event.accept(SoulSteel.SWORD.get());
            event.accept(SoulSteel.SHIELD.get());

            event.accept(Fletching.QUARTZ_ARROW_ITEM.get());
            event.accept(Fletching.DIAMOND_ARROW_ITEM.get());
            event.accept(Fletching.EXPLOSIVE_ARROW_ITEM.get());
            event.accept(Fletching.TORCH_ARROW_ITEM.get());
        }
        else if (event.getTab() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(Durium.SCRAP_BLOCK.item().get());
            event.accept(Durium.BLOCK.item().get());
            event.accept(SoulSteel.BLOCK.item().get());
            event.accept(FlintExpansion.FLINT_BLOCK.item().get());
            event.accept(FlintExpansion.POLISHED_FLINT_BLOCK.item().get());
            event.accept(CoalFire.CHARCOAL_LAYER.item().get());
            event.accept(Death.GRAVE.item().get());
        }
        else if (event.getTab() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(Respawn.RESPAWN_OBELISK.item().get());
            event.accept(Crate.ITEM.get());
            event.accept(MultiBlockFurnaces.BLAST_FURNACE.item().get());
            event.accept(MultiBlockFurnaces.SOUL_BLAST_FURNACE.item().get());
            event.accept(Forging.FORGE.item().get());
            event.accept(Fletching.FLETCHING_TABLE.item().get());
        }
        else if (event.getTab() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(Minecarts.COPPER_POWERED_RAIL.item().get());
            event.accept(Minecarts.GOLDEN_POWERED_RAIL.item().get());
            event.accept(Minecarts.NETHER_INFUSED_POWERED_RAIL.item().get());
            event.accept(ExplosiveBarrel.BLOCK.item().get());
            event.accept(MiningCharge.MINING_CHARGE.item().get());
        }
        else if (event.getTab() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(Durium.ORE.item().get());
            event.accept(Durium.DEEPSLATE_ORE.item().get());
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
        else if (event.getTab() == CreativeModeTabs.INGREDIENTS) {
            event.accept(Durium.SCRAP_PIECE.get());
            event.accept(Durium.INGOT.get());
            event.accept(Durium.NUGGET.get());
            event.accept(SoulSteel.INGOT.get());
            event.accept(SoulSteel.NUGGET.get());
            event.accept(AncientLapis.ANCIENT_LAPIS.get());
            event.accept(EnchantmentsFeature.CLEANSED_LAPIS.get());
            event.accept(CoalFire.HELLISH_COAL.get());
            event.accept(Cloth.CLOTH.get());
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

        MenuScreens.register(MultiBlockFurnaces.BLAST_FURNACE_MENU_TYPE.get(), MultiBlockBlastFurnaceScreen::new);
        MenuScreens.register(MultiBlockFurnaces.SOUL_BLAST_FURNACE_MENU_TYPE.get(), MultiBlockSoulBlastFurnaceScreen::new);
        MenuScreens.register(Forging.FORGE_MENU_TYPE.get(), ForgeScreen::new);
        MenuScreens.register(Fletching.FLETCHING_MENU_TYPE.get(), FletchingScreen::new);
    }

    public static void entityRenderEvent(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SREntityTypes.PILABLE_FALLING_LAYER.get(), FallingBlockRenderer::new);
        event.registerEntityRenderer(Fletching.QUARTZ_ARROW.get(), SRArrowRenderer::new);
        event.registerEntityRenderer(Fletching.DIAMOND_ARROW.get(), SRArrowRenderer::new);
        event.registerEntityRenderer(Fletching.EXPLOSIVE_ARROW.get(), SRArrowRenderer::new);
        event.registerEntityRenderer(Fletching.TORCH_ARROW.get(), SRArrowRenderer::new);
        event.registerEntityRenderer(MiningCharge.PRIMED_MINING_CHARGE.get(), MiningChargeRenderer::new);
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

    public static void onRegisterRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
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
}
