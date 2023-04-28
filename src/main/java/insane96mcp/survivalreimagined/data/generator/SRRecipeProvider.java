package insane96mcp.survivalreimagined.data.generator;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.experience.feature.EnchantmentsFeature;
import insane96mcp.survivalreimagined.module.hungerhealth.feature.FoodDrinks;
import insane96mcp.survivalreimagined.module.items.feature.*;
import insane96mcp.survivalreimagined.module.movement.feature.Minecarts;
import insane96mcp.survivalreimagined.module.world.feature.BeegVeins;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SRRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public SRRecipeProvider(PackOutput p_248933_) {
        super(p_248933_);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> writer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, FlintExpansion.AXE.get())
                .pattern("ff")
                .pattern("fs")
                .pattern(" s")
                .define('f', Items.FLINT)
                .define('s', Items.STICK)
                .unlockedBy("has_stick", has(Items.STICK))
                .unlockedBy("has_flint", has(Items.FLINT))
                .save(writer);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, FlintExpansion.SHOVEL.get())
                .pattern("f")
                .pattern("s")
                .pattern("s")
                .define('f', Items.FLINT)
                .define('s', Items.STICK)
                .unlockedBy("has_stick", has(Items.STICK))
                .unlockedBy("has_flint", has(Items.FLINT))
                .save(writer);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, FlintExpansion.PICKAXE.get())
                .pattern("fff")
                .pattern(" s ")
                .pattern(" s ")
                .define('f', Items.FLINT)
                .define('s', Items.STICK)
                .unlockedBy("has_stick", has(Items.STICK))
                .unlockedBy("has_flint", has(Items.FLINT))
                .save(writer);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, FlintExpansion.HOE.get())
                .pattern("ff")
                .pattern(" s")
                .pattern(" s")
                .define('f', Items.FLINT)
                .define('s', Items.STICK)
                .unlockedBy("has_stick", has(Items.STICK))
                .unlockedBy("has_flint", has(Items.FLINT))
                .save(writer);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, FlintExpansion.SWORD.get())
                .pattern("f")
                .pattern("f")
                .pattern("s")
                .define('f', Items.FLINT)
                .define('s', Items.STICK)
                .unlockedBy("has_stick", has(Items.STICK))
                .unlockedBy("has_flint", has(Items.FLINT))
                .save(writer);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, FlintExpansion.SHIELD.get())
                .pattern(" f ")
                .pattern("fLf")
                .pattern(" f ")
                .define('f', Items.FLINT)
                .define('L', ItemTags.LOGS)
                .unlockedBy("has_flint", has(Items.FLINT))
                .save(writer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, FlintExpansion.FLINT_BLOCK_ITEM.get())
                .requires(Items.FLINT, 9)
                .unlockedBy("has_flint", has(Items.FLINT))
                .save(writer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.FLINT, 9)
                .requires(FlintExpansion.FLINT_BLOCK_ITEM.get(), 1)
                .unlockedBy("has_flint_block_item", has(FlintExpansion.FLINT_BLOCK_ITEM.get()))
                .save(writer);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, FlintExpansion.POLISHED_FLINT_BLOCK_ITEM.get())
                .pattern("ff")
                .pattern("ff")
                .define('f', FlintExpansion.FLINT_BLOCK_ITEM.get())
                .unlockedBy("has_flint_block", has(FlintExpansion.FLINT_BLOCK_ITEM.get()))
                .save(writer);
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(FlintExpansion.FLINT_BLOCK_ITEM.get()), RecipeCategory.BUILDING_BLOCKS, FlintExpansion.POLISHED_FLINT_BLOCK_ITEM.get())
                .unlockedBy("has_flint_block", has(FlintExpansion.FLINT_BLOCK_ITEM.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "stonecutter_polished_flint_block");

        ConditionalRecipe.builder()
                .addCondition(not(modLoaded("tconstruct")))
                .addRecipe(
                    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.FLINT)
                            .requires(Items.GRAVEL, 3)
                            .unlockedBy("has_gravel", has(Items.GRAVEL))
                            ::save
                )
                .build(writer, SurvivalReimagined.MOD_ID, "flint_from_gravel");

        //Chained Copper Armor
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ChainedCopperArmor.HELMET.get())
                .pattern("cmc")
                .pattern("m m")
                .define('c', Items.CHAIN)
                .define('m', Items.COPPER_INGOT)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
                .unlockedBy("has_chain", has(Items.CHAIN))
                .save(writer);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ChainedCopperArmor.CHESTPLATE.get())
                .pattern("c c")
                .pattern("mcm")
                .pattern("mmm")
                .define('c', Items.CHAIN)
                .define('m', Items.COPPER_INGOT)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
                .unlockedBy("has_chain", has(Items.CHAIN))
                .save(writer);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ChainedCopperArmor.LEGGINGS.get())
                .pattern("ccc")
                .pattern("m m")
                .pattern("m m")
                .define('c', Items.CHAIN)
                .define('m', Items.COPPER_INGOT)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
                .unlockedBy("has_chain", has(Items.CHAIN))
                .save(writer);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ChainedCopperArmor.BOOTS.get())
                .pattern("c c")
                .pattern("m m")
                .define('c', Items.CHAIN)
                .define('m', Items.COPPER_INGOT)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
                .unlockedBy("has_chain", has(Items.CHAIN))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, Crate.BLOCK_ITEM.get())
                .pattern("nnn")
                .pattern("ibi")
                .pattern("nnn")
                .define('n', Mithril.NUGGET.get())
                .define('i', Items.IRON_INGOT)
                .define('b', Items.BARREL)
                .unlockedBy("has_mithril_nugget", has(Mithril.NUGGET.get()))
                .unlockedBy("has_barrel", has(Items.BARREL))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Altimeter.ITEM.get())
                .pattern(" i ")
                .pattern("frf")
                .pattern(" f ")
                .define('f', Mithril.INGOT.get())
                .define('i', Items.IRON_INGOT)
                .define('r', Items.REDSTONE)
                .unlockedBy("has_mithril_nugget", has(Mithril.NUGGET.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, Minecarts.NETHER_INFUSED_POWERED_RAIL_ITEM.get(), 4)
                .requires(Items.POWERED_RAIL, 4)
                .requires(Items.NETHER_BRICK, 4)
                .requires(Items.FIRE_CHARGE, 1)
                .unlockedBy("has_powered_rail", has(Items.POWERED_RAIL))
                .unlockedBy("has_nether_brick", has(Items.NETHER_BRICK))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AncientLapis.ANCIENT_LAPIS.get(), 1)
                .requires(EnchantmentsFeature.CLEANSED_LAPIS.get(), 1)
                .requires(Items.NETHERITE_SCRAP, 1)
                .requires(Items.EXPERIENCE_BOTTLE, 1)
                .unlockedBy("has_netherite_scrap", has(Items.NETHERITE_SCRAP))
                .unlockedBy("has_ancient_lapis", has(AncientLapis.ANCIENT_LAPIS.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Mithril.BLOCK_ITEM.get(), 1)
                .requires(Mithril.INGOT.get(), 9)
                .unlockedBy("has_ingot", has(Mithril.INGOT.get()))
                .save(writer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Mithril.INGOT.get(), 9)
                .requires(Mithril.BLOCK_ITEM.get(), 1)
                .unlockedBy("has_ingot", has(Mithril.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "mithril_ingot_from_block");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Mithril.INGOT.get(), 1)
                .requires(Mithril.NUGGET.get(), 9)
                .unlockedBy("has_nuggets", has(Mithril.NUGGET.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "mithril_ingot_from_nuggets");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Mithril.NUGGET.get(), 9)
                .requires(Mithril.INGOT.get(), 1)
                .unlockedBy("has_ingot", has(Mithril.INGOT.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, ExplosiveBarrel.BLOCK_ITEM.get())
                .requires(Items.TNT, 1)
                .requires(Items.BARREL, 1)
                .requires(Items.GUNPOWDER, 2)
                .unlockedBy("has_tnt", has(Items.TNT))
                .unlockedBy("has_barrel", has(Items.BARREL))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, FoodDrinks.BROWN_MUSHROOM_STEW.get())
                .requires(Items.BOWL, 1)
                .requires(Items.BROWN_MUSHROOM, 2)
                .unlockedBy("has_bowl", has(Items.BOWL))
                .unlockedBy("has_mushroom", has(Items.BROWN_MUSHROOM))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, FoodDrinks.RED_MUSHROOM_STEW.get())
                .requires(Items.BOWL, 1)
                .requires(Items.RED_MUSHROOM, 2)
                .unlockedBy("has_bowl", has(Items.BOWL))
                .unlockedBy("has_mushroom", has(Items.RED_MUSHROOM))
                .save(writer);

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(BeegVeins.COPPER_ORE_ROCK_ITEM.get()),
                        RecipeCategory.MISC,
                        Items.COPPER_INGOT,
                        0.7f,
                        800
                )
                .unlockedBy("has_copper_ore_rock", has(BeegVeins.COPPER_ORE_ROCK_ITEM.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "copper_ingot_from_smelting_rock");
        SimpleCookingRecipeBuilder.blasting(
                        Ingredient.of(BeegVeins.COPPER_ORE_ROCK_ITEM.get()),
                        RecipeCategory.MISC,
                        Items.COPPER_INGOT,
                        0.7f,
                        200
                )
                .unlockedBy("has_copper_ore_rock", has(BeegVeins.COPPER_ORE_ROCK_ITEM.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "copper_ingot_from_blasting_rock");

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(BeegVeins.IRON_ORE_ROCK_ITEM.get()),
                        RecipeCategory.MISC,
                        Items.IRON_INGOT,
                        0.8f,
                        800
                )
                .unlockedBy("has_iron_ore_rock", has(BeegVeins.IRON_ORE_ROCK_ITEM.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "iron_ingot_from_smelting_rock");
        SimpleCookingRecipeBuilder.blasting(
                        Ingredient.of(BeegVeins.IRON_ORE_ROCK_ITEM.get()),
                        RecipeCategory.MISC,
                        Items.IRON_INGOT,
                        0.8f,
                        200
                )
                .unlockedBy("has_iron_ore_rock", has(BeegVeins.IRON_ORE_ROCK_ITEM.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "iron_ingot_from_blasting_rock");

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(BeegVeins.GOLD_ORE_ROCK_ITEM.get()),
                        RecipeCategory.MISC,
                        Items.GOLD_INGOT,
                        1f,
                        800
                )
                .unlockedBy("has_gold_ore_rock", has(BeegVeins.GOLD_ORE_ROCK_ITEM.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "gold_ingot_from_smelting_rock");
        SimpleCookingRecipeBuilder.blasting(
                        Ingredient.of(BeegVeins.GOLD_ORE_ROCK_ITEM.get()),
                        RecipeCategory.MISC,
                        Items.GOLD_INGOT,
                        1f,
                        200
                )
                .unlockedBy("has_gold_ore_rock", has(BeegVeins.GOLD_ORE_ROCK_ITEM.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "gold_ingot_from_blasting_rock");

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ChainedCopperArmor.HELMET.get()),
                        RecipeCategory.MISC,
                        Items.IRON_NUGGET,
                        0,
                        200
                )
                .unlockedBy("has_chained_armor", has(ChainedCopperArmor.HELMET.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "smelting_chained_copper_helmet");
        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ChainedCopperArmor.CHESTPLATE.get()),
                        RecipeCategory.MISC,
                        Items.IRON_NUGGET,
                        0,
                        200
                )
                .unlockedBy("has_chained_armor", has(ChainedCopperArmor.CHESTPLATE.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "smelting_chained_copper_chestplate");
        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ChainedCopperArmor.LEGGINGS.get()),
                        RecipeCategory.MISC,
                        Items.IRON_NUGGET,
                        0,
                        200
                )
                .unlockedBy("has_chained_armor", has(ChainedCopperArmor.LEGGINGS.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "smelting_chained_copper_leggings");
        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ChainedCopperArmor.BOOTS.get()),
                        RecipeCategory.MISC,
                        Items.IRON_NUGGET,
                        0,
                        200
                )
                .unlockedBy("has_chained_armor", has(ChainedCopperArmor.BOOTS.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "smelting_chained_copper_boots");
        SimpleCookingRecipeBuilder.blasting(
                        Ingredient.of(ChainedCopperArmor.HELMET.get()),
                        RecipeCategory.MISC,
                        Items.IRON_NUGGET,
                        0,
                        100
                )
                .unlockedBy("has_chained_armor", has(ChainedCopperArmor.HELMET.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "blasting_chained_copper_helmet");
        SimpleCookingRecipeBuilder.blasting(
                        Ingredient.of(ChainedCopperArmor.CHESTPLATE.get()),
                        RecipeCategory.MISC,
                        Items.IRON_NUGGET,
                        0,
                        100
                )
                .unlockedBy("has_chained_armor", has(ChainedCopperArmor.CHESTPLATE.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "blasting_chained_copper_chestplate");
        SimpleCookingRecipeBuilder.blasting(
                        Ingredient.of(ChainedCopperArmor.LEGGINGS.get()),
                        RecipeCategory.MISC,
                        Items.IRON_NUGGET,
                        0,
                        100
                )
                .unlockedBy("has_chained_armor", has(ChainedCopperArmor.LEGGINGS.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "blasting_chained_copper_leggings");
        SimpleCookingRecipeBuilder.blasting(
                        Ingredient.of(ChainedCopperArmor.BOOTS.get()),
                        RecipeCategory.MISC,
                        Items.IRON_NUGGET,
                        0,
                        100
                )
                .unlockedBy("has_chained_armor", has(ChainedCopperArmor.BOOTS.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "blasting_chained_copper_boots");

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(Mithril.ORE.get()),
                        RecipeCategory.MISC,
                        Mithril.NUGGET.get(),
                        0.8f,
                        800
                )
                .unlockedBy("has_mithril_ore", has(Mithril.ORE.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "mithril_nugget_from_smelting_ore");

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(Mithril.DEEPSLATE_ORE.get()),
                        RecipeCategory.MISC,
                        Mithril.NUGGET.get(),
                        0.8f,
                        800
                )
                .unlockedBy("has_mithril_ore", has(Mithril.DEEPSLATE_ORE.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "mithril_nugget_from_smelting_deepslate_ore");

        SimpleCookingRecipeBuilder.blasting(
                        Ingredient.of(Mithril.ORE.get()),
                        RecipeCategory.MISC,
                        Mithril.NUGGET.get(),
                        0.8f,
                        200
                )
                .unlockedBy("has_mithril_ore", has(Mithril.ORE.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "mithril_nugget_from_blasting_ore");

        SimpleCookingRecipeBuilder.blasting(
                        Ingredient.of(Mithril.DEEPSLATE_ORE.get()),
                        RecipeCategory.MISC,
                        Mithril.NUGGET.get(),
                        0.8f,
                        200
                )
                .unlockedBy("has_mithril_ore", has(Mithril.DEEPSLATE_ORE.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "mithril_nugget_from_blasting_deepslate_ore");

        LegacyUpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_AXE), Ingredient.of(Mithril.INGOT.get()), RecipeCategory.TOOLS, Mithril.AXE.get())
                .unlocks("has_mithril", has(Mithril.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "mithril_axe");
        LegacyUpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_PICKAXE), Ingredient.of(Mithril.INGOT.get()), RecipeCategory.TOOLS, Mithril.PICKAXE.get())
                .unlocks("has_mithril", has(Mithril.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "mithril_pickaxe");
        LegacyUpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_SHOVEL), Ingredient.of(Mithril.INGOT.get()), RecipeCategory.TOOLS, Mithril.SHOVEL.get())
                .unlocks("has_mithril", has(Mithril.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "mithril_shovel");
        LegacyUpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_HOE), Ingredient.of(Mithril.INGOT.get()), RecipeCategory.TOOLS, Mithril.HOE.get())
                .unlocks("has_mithril", has(Mithril.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "mithril_how");
        LegacyUpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_SWORD), Ingredient.of(Mithril.INGOT.get()), RecipeCategory.COMBAT, Mithril.SWORD.get())
                .unlocks("has_mithril", has(Mithril.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "mithril_sword");
        LegacyUpgradeRecipeBuilder.smithing(Ingredient.of(Items.SHIELD), Ingredient.of(Mithril.INGOT.get()), RecipeCategory.COMBAT, Mithril.SHIELD.get())
                .unlocks("has_mithril", has(Mithril.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "mithril_shield");
        LegacyUpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_HELMET), Ingredient.of(Mithril.INGOT.get()), RecipeCategory.COMBAT, Mithril.HELMET.get())
                .unlocks("has_mithril", has(Mithril.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "mithril_helmet");
        LegacyUpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_CHESTPLATE), Ingredient.of(Mithril.INGOT.get()), RecipeCategory.COMBAT, Mithril.CHESTPLATE.get())
                .unlocks("has_mithril", has(Mithril.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "mithril_chestplate");
        LegacyUpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_LEGGINGS), Ingredient.of(Mithril.INGOT.get()), RecipeCategory.COMBAT, Mithril.LEGGINGS.get())
                .unlocks("has_mithril", has(Mithril.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "mithril_leggings");
        LegacyUpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_BOOTS), Ingredient.of(Mithril.INGOT.get()), RecipeCategory.COMBAT, Mithril.BOOTS.get())
                .unlocks("has_mithril", has(Mithril.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "mithril_boots");
    }


}