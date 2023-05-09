package insane96mcp.survivalreimagined.data.generator;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.experience.feature.EnchantmentsFeature;
import insane96mcp.survivalreimagined.module.hungerhealth.feature.FoodDrinks;
import insane96mcp.survivalreimagined.module.items.feature.*;
import insane96mcp.survivalreimagined.module.mining.feature.Mithril;
import insane96mcp.survivalreimagined.module.mining.feature.SoulSteel;
import insane96mcp.survivalreimagined.module.world.feature.CoalFire;
import insane96mcp.survivalreimagined.module.world.feature.OreGeneration;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.ForgeRegistries;
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
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, FlintExpansion.FLINT_BLOCK.item().get())
                .requires(Items.FLINT, 9)
                .unlockedBy("has_flint", has(Items.FLINT))
                .save(writer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.FLINT, 9)
                .requires(FlintExpansion.FLINT_BLOCK.item().get(), 1)
                .unlockedBy("has_flint_block_item", has(FlintExpansion.FLINT_BLOCK.item().get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "flint_from_block");
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, FlintExpansion.POLISHED_FLINT_BLOCK.item().get(), 4)
                .pattern("ff")
                .pattern("ff")
                .define('f', FlintExpansion.FLINT_BLOCK.item().get())
                .unlockedBy("has_flint_block", has(FlintExpansion.FLINT_BLOCK.item().get()))
                .save(writer);
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(FlintExpansion.FLINT_BLOCK.item().get()), RecipeCategory.BUILDING_BLOCKS, FlintExpansion.POLISHED_FLINT_BLOCK.item().get())
                .unlockedBy("has_flint_block", has(FlintExpansion.FLINT_BLOCK.item().get()))
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

        //Chainmail Armor
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.CHAINMAIL_HELMET)
                .pattern("ccc")
                .pattern("clc")
                .define('c', Items.CHAIN)
                .define('l', Items.LEATHER_HELMET)
                .unlockedBy("has_leather_armor", has(Items.LEATHER_HELMET))
                .unlockedBy("has_chain", has(Items.CHAIN))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "chainmail_helmet");
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.CHAINMAIL_CHESTPLATE)
                .pattern("clc")
                .pattern("ccc")
                .pattern("ccc")
                .define('c', Items.CHAIN)
                .define('l', Items.LEATHER_CHESTPLATE)
                .unlockedBy("has_leather_armor", has(Items.LEATHER_CHESTPLATE))
                .unlockedBy("has_chain", has(Items.CHAIN))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "chainmail_chestplate");
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.CHAINMAIL_LEGGINGS)
                .pattern("ccc")
                .pattern("clc")
                .pattern("c c")
                .define('c', Items.CHAIN)
                .define('l', Items.LEATHER_LEGGINGS)
                .unlockedBy("has_leather_armor", has(Items.LEATHER_LEGGINGS))
                .unlockedBy("has_chain", has(Items.CHAIN))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "chainmail_leggings");
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.CHAINMAIL_BOOTS)
                .pattern("clc")
                .pattern("c c")
                .define('c', Items.CHAIN)
                .define('l', Items.LEATHER_BOOTS)
                .unlockedBy("has_leather_armor", has(Items.LEATHER_BOOTS))
                .unlockedBy("has_chain", has(Items.CHAIN))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "chainmail_boots");

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, Crate.BLOCK.item().get())
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

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AncientLapis.ANCIENT_LAPIS.get(), 1)
                .requires(EnchantmentsFeature.CLEANSED_LAPIS.get(), 1)
                .requires(Items.NETHERITE_SCRAP, 1)
                .requires(Items.EXPERIENCE_BOTTLE, 1)
                .unlockedBy("has_netherite_scrap", has(Items.NETHERITE_SCRAP))
                .unlockedBy("has_ancient_lapis", has(AncientLapis.ANCIENT_LAPIS.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Mithril.BLOCK.item().get(), 1)
                .requires(Mithril.INGOT.get(), 9)
                .unlockedBy("has_ingot", has(Mithril.INGOT.get()))
                .save(writer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Mithril.INGOT.get(), 9)
                .requires(Mithril.BLOCK.item().get(), 1)
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

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, SoulSteel.BLOCK.block().get(), 1)
                .requires(SoulSteel.INGOT.get(), 9)
                .unlockedBy("has_ingot", has(SoulSteel.INGOT.get()))
                .save(writer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SoulSteel.INGOT.get(), 9)
                .requires(SoulSteel.BLOCK.block().get(), 1)
                .unlockedBy("has_ingot", has(SoulSteel.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "soul_steel_ingot_from_block");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SoulSteel.INGOT.get(), 1)
                .requires(SoulSteel.NUGGET.get(), 9)
                .unlockedBy("has_nuggets", has(SoulSteel.NUGGET.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "soul_steel_ingot_from_nuggets");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SoulSteel.NUGGET.get(), 9)
                .requires(SoulSteel.INGOT.get(), 1)
                .unlockedBy("has_ingot", has(SoulSteel.INGOT.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, ExplosiveBarrel.BLOCK.item().get())
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

        SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(Items.EGG), RecipeCategory.FOOD, FoodDrinks.OVER_EASY_EGG.get(), 0.35f, 600)
                .unlockedBy("has_egg", has(Items.EGG))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "over_easy_egg_from_campfire");
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.EGG), RecipeCategory.FOOD, FoodDrinks.OVER_EASY_EGG.get(), 0.35f, 200)
                .unlockedBy("has_egg", has(Items.EGG))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "over_easy_egg_from_smelting");
        SimpleCookingRecipeBuilder.smoking(Ingredient.of(Items.EGG), RecipeCategory.FOOD, FoodDrinks.OVER_EASY_EGG.get(), 0.35f, 100)
                .unlockedBy("has_egg", has(Items.EGG))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "over_easy_egg_from_smoking");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, CoalFire.FIRESTARTER.get())
                .requires(Items.FLINT, 2)
                .requires(Items.IRON_INGOT, 1)
                .unlockedBy("has_iron_nugget", has(Items.IRON_NUGGET))
                .save(writer);

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(OreGeneration.COPPER_ORE_ROCK.item().get()),
                        RecipeCategory.MISC,
                        Items.COPPER_INGOT,
                        0.7f,
                        800
                )
                .unlockedBy("has_copper_ore_rock", has(OreGeneration.COPPER_ORE_ROCK.item().get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "copper_ingot_from_smelting_rock");
        SimpleCookingRecipeBuilder.blasting(
                        Ingredient.of(OreGeneration.COPPER_ORE_ROCK.item().get()),
                        RecipeCategory.MISC,
                        Items.COPPER_INGOT,
                        0.7f,
                        200
                )
                .unlockedBy("has_copper_ore_rock", has(OreGeneration.COPPER_ORE_ROCK.item().get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "copper_ingot_from_blasting_rock");

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(OreGeneration.IRON_ORE_ROCK.item().get()),
                        RecipeCategory.MISC,
                        Items.IRON_INGOT,
                        0.8f,
                        800
                )
                .unlockedBy("has_iron_ore_rock", has(OreGeneration.IRON_ORE_ROCK.item().get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "iron_ingot_from_smelting_rock");
        SimpleCookingRecipeBuilder.blasting(
                        Ingredient.of(OreGeneration.IRON_ORE_ROCK.item().get()),
                        RecipeCategory.MISC,
                        Items.IRON_INGOT,
                        0.8f,
                        200
                )
                .unlockedBy("has_iron_ore_rock", has(OreGeneration.IRON_ORE_ROCK.item().get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "iron_ingot_from_blasting_rock");

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(OreGeneration.GOLD_ORE_ROCK.item().get()),
                        RecipeCategory.MISC,
                        Items.GOLD_INGOT,
                        1f,
                        800
                )
                .unlockedBy("has_gold_ore_rock", has(OreGeneration.GOLD_ORE_ROCK.item().get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "gold_ingot_from_smelting_rock");
        SimpleCookingRecipeBuilder.blasting(
                        Ingredient.of(OreGeneration.GOLD_ORE_ROCK.item().get()),
                        RecipeCategory.MISC,
                        Items.GOLD_INGOT,
                        1f,
                        200
                )
                .unlockedBy("has_gold_ore_rock", has(OreGeneration.GOLD_ORE_ROCK.item().get()))
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
                        Ingredient.of(Mithril.ORE.item().get()),
                        RecipeCategory.MISC,
                        Mithril.NUGGET.get(),
                        1f,
                        800
                )
                .unlockedBy("has_mithril_ore", has(Mithril.ORE.item().get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "mithril_nugget_from_smelting_ore");

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(Mithril.DEEPSLATE_ORE.item().get()),
                        RecipeCategory.MISC,
                        Mithril.NUGGET.get(),
                        1f,
                        800
                )
                .unlockedBy("has_mithril_ore", has(Mithril.DEEPSLATE_ORE.item().get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "mithril_nugget_from_smelting_deepslate_ore");

        SimpleCookingRecipeBuilder.blasting(
                        Ingredient.of(Mithril.ORE.item().get()),
                        RecipeCategory.MISC,
                        Mithril.NUGGET.get(),
                        1f,
                        200
                )
                .unlockedBy("has_mithril_ore", has(Mithril.ORE.item().get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "mithril_nugget_from_blasting_ore");

        SimpleCookingRecipeBuilder.blasting(
                        Ingredient.of(Mithril.DEEPSLATE_ORE.item().get()),
                        RecipeCategory.MISC,
                        Mithril.NUGGET.get(),
                        1f,
                        200
                )
                .unlockedBy("has_mithril_ore", has(Mithril.DEEPSLATE_ORE.item().get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "mithril_nugget_from_blasting_deepslate_ore");

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(CoalFire.SOUL_SAND_HELLISH_COAL_ORE.item().get()),
                        RecipeCategory.MISC,
                        CoalFire.HELLISH_COAL.get(),
                        1.2f,
                        200
                )
                .unlockedBy("has_hellish_coal_ore", has(CoalFire.SOUL_SAND_HELLISH_COAL_ORE.item().get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "hellish_coal_from_smelting_soul_sand_ore");
        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(CoalFire.SOUL_SOIL_HELLISH_COAL_ORE.item().get()),
                        RecipeCategory.MISC,
                        CoalFire.HELLISH_COAL.get(),
                        1.2f,
                        200
                )
                .unlockedBy("has_hellish_coal_ore", has(CoalFire.SOUL_SOIL_HELLISH_COAL_ORE.item().get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "hellish_coal_from_smelting_soul_soil_ore");
        SimpleCookingRecipeBuilder.blasting(
                        Ingredient.of(CoalFire.SOUL_SAND_HELLISH_COAL_ORE.item().get()),
                        RecipeCategory.MISC,
                        CoalFire.HELLISH_COAL.get(),
                        1.2f,
                        100
                )
                .unlockedBy("has_hellish_coal_ore", has(CoalFire.SOUL_SAND_HELLISH_COAL_ORE.item().get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "hellish_coal_from_blasting_soul_sand_ore");
        SimpleCookingRecipeBuilder.blasting(
                        Ingredient.of(CoalFire.SOUL_SOIL_HELLISH_COAL_ORE.item().get()),
                        RecipeCategory.MISC,
                        CoalFire.HELLISH_COAL.get(),
                        1.2f,
                        100
                )
                .unlockedBy("has_hellish_coal_ore", has(CoalFire.SOUL_SOIL_HELLISH_COAL_ORE.item().get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "hellish_coal_from_blasting_soul_soil_ore");

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

        addPoorRichOreRecipes(writer, OreGeneration.POOR_RICH_IRON_ORE, Items.IRON_INGOT, 0.8f);
        addPoorRichOreRecipes(writer, OreGeneration.POOR_RICH_COPPER_ORE, Items.COPPER_INGOT, 0.7f);
        addPoorRichOreRecipes(writer, OreGeneration.POOR_RICH_GOLD_ORE, Items.GOLD_INGOT, 1f);
    }

    private void addPoorRichOreRecipes(Consumer<FinishedRecipe> writer, OreGeneration.PoorRichOre poorRichOre, Item smeltOutput, float experience) {
        for (Item item : poorRichOre.getAllItems()) {
            String name = ForgeRegistries.ITEMS.getKey(item).getPath();
            SimpleCookingRecipeBuilder.smelting(
                            Ingredient.of(item),
                            RecipeCategory.MISC,
                            smeltOutput,
                            experience,
                            800
                    )
                    .unlockedBy("has_" + name, has(item))
                    .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "gold_ingot_from_smelting_" + name);
            SimpleCookingRecipeBuilder.blasting(
                            Ingredient.of(item),
                            RecipeCategory.MISC,
                            smeltOutput,
                            experience,
                            200
                    )
                    .unlockedBy("has_" + name, has(item))
                    .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "gold_ingot_from_blasting_" + name);
        }
    }
}
