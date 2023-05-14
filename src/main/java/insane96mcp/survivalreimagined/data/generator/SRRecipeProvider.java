package insane96mcp.survivalreimagined.data.generator;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.experience.feature.EnchantmentsFeature;
import insane96mcp.survivalreimagined.module.hungerhealth.feature.FoodDrinks;
import insane96mcp.survivalreimagined.module.items.feature.*;
import insane96mcp.survivalreimagined.module.mining.data.MultiItemSmeltingRecipeBuilder;
import insane96mcp.survivalreimagined.module.mining.feature.Durium;
import insane96mcp.survivalreimagined.module.mining.feature.MultiBlockFurnaces;
import insane96mcp.survivalreimagined.module.mining.feature.SoulSteel;
import insane96mcp.survivalreimagined.module.world.feature.CoalFire;
import insane96mcp.survivalreimagined.module.world.feature.OreGeneration;
import net.minecraft.core.NonNullList;
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
                .define('n', Durium.NUGGET.get())
                .define('i', Items.IRON_INGOT)
                .define('b', Items.BARREL)
                .unlockedBy("has_durium_nugget", has(Durium.NUGGET.get()))
                .unlockedBy("has_barrel", has(Items.BARREL))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Altimeter.ITEM.get())
                .pattern(" i ")
                .pattern("frf")
                .pattern(" f ")
                .define('f', Durium.INGOT.get())
                .define('i', Items.IRON_INGOT)
                .define('r', Items.REDSTONE)
                .unlockedBy("has_durium_nugget", has(Durium.NUGGET.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AncientLapis.ANCIENT_LAPIS.get(), 1)
                .requires(EnchantmentsFeature.CLEANSED_LAPIS.get(), 1)
                .requires(Items.NETHERITE_SCRAP, 1)
                .requires(Items.EXPERIENCE_BOTTLE, 1)
                .unlockedBy("has_netherite_scrap", has(Items.NETHERITE_SCRAP))
                .unlockedBy("has_ancient_lapis", has(AncientLapis.ANCIENT_LAPIS.get()))
                .save(writer);

        //Durium Block, Ingot, Nugget, Scrap
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Durium.BLOCK.item().get(), 1)
                .requires(Durium.INGOT.get(), 9)
                .unlockedBy("has_ingot", has(Durium.INGOT.get()))
                .save(writer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Durium.INGOT.get(), 9)
                .requires(Durium.BLOCK.item().get(), 1)
                .unlockedBy("has_ingot", has(Durium.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_ingot_from_block");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Durium.INGOT.get(), 1)
                .requires(Durium.NUGGET.get(), 9)
                .unlockedBy("has_nuggets", has(Durium.NUGGET.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_ingot_from_nuggets");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Durium.NUGGET.get(), 9)
                .requires(Durium.INGOT.get(), 1)
                .unlockedBy("has_ingot", has(Durium.INGOT.get()))
                .save(writer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Durium.SCRAP_BLOCK.item().get(), 1)
                .requires(Durium.SCRAP_PIECE.get(), 9)
                .unlockedBy("has_piece", has(Durium.SCRAP_PIECE.get()))
                .save(writer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Durium.SCRAP_PIECE.get(), 9)
                .requires(Durium.SCRAP_BLOCK.item().get(), 1)
                .unlockedBy("has_piece", has(Durium.SCRAP_PIECE.get()))
                .save(writer);
        MultiItemSmeltingRecipeBuilder.blasting(
                        NonNullList.of(Ingredient.EMPTY, Ingredient.of(Durium.SCRAP_BLOCK.item().get()), Ingredient.of(ItemTags.SAND), Ingredient.of(Items.CLAY_BALL)),
                        RecipeCategory.MISC,
                        Durium.INGOT.get(),
                        800
                )
                .unlockedBy("has_scrap_block", has(Durium.SCRAP_BLOCK.item().get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_ingot_from_blasting");
        MultiItemSmeltingRecipeBuilder.soulBlasting(
                        NonNullList.of(Ingredient.EMPTY, Ingredient.of(Durium.SCRAP_BLOCK.item().get()), Ingredient.of(ItemTags.SAND), Ingredient.of(Items.CLAY_BALL)),
                        RecipeCategory.MISC,
                        Durium.INGOT.get(),
                        400
                )
                .doubleOutputChance(0.2f)
                .unlockedBy("has_scrap_block", has(Durium.SCRAP_BLOCK.item().get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_ingot_from_soul_blasting");

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
        MultiItemSmeltingRecipeBuilder.blasting(
                        NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.IRON_INGOT), Ingredient.of(Items.IRON_INGOT), Ingredient.of(CoalFire.HELLISH_COAL.get()), Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL)),
                        RecipeCategory.MISC,
                        SoulSteel.INGOT.get(),
                        800
                )
                .unlockedBy("has_hellish_coal", has(CoalFire.HELLISH_COAL.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "soul_steel_ingot_from_blasting");
        MultiItemSmeltingRecipeBuilder.soulBlasting(
                        NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.IRON_INGOT), Ingredient.of(Items.IRON_INGOT), Ingredient.of(CoalFire.HELLISH_COAL.get()), Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL)),
                        RecipeCategory.MISC,
                        SoulSteel.INGOT.get(),
                        400
                )
                .doubleOutputChance(0.2f)
                .unlockedBy("has_hellish_coal", has(CoalFire.HELLISH_COAL.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "soul_steel_ingot_from_soul_blasting");

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

        //Soul Blast Furnace
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, MultiBlockFurnaces.SOUL_BLAST_FURNACE.item().get())
                .pattern("GNG")
                .pattern("GFG")
                .pattern("BBB")
                .define('G', Items.GOLD_INGOT)
                .define('N', Items.NETHERITE_INGOT)
                .define('F', MultiBlockFurnaces.BLAST_FURNACE.item().get())
                .define('B', Items.BLACKSTONE)
                .unlockedBy("has_netherite", has(Items.NETHERITE_INGOT))
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

        // Chained Copper Armor
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

        //Durium
        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(Durium.ORE.item().get()),
                        RecipeCategory.MISC,
                        Durium.SCRAP_PIECE.get(),
                        1f,
                        200
                )
                .unlockedBy("has_durium_ore", has(Durium.ORE.item().get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_scrap_piece_from_smelting_ore");

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(Durium.DEEPSLATE_ORE.item().get()),
                        RecipeCategory.MISC,
                        Durium.SCRAP_PIECE.get(),
                        1f,
                        200
                )
                .unlockedBy("has_durium_ore", has(Durium.DEEPSLATE_ORE.item().get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_scrap_piece_from_smelting_deepslate_ore");

        SimpleCookingRecipeBuilder.blasting(
                        Ingredient.of(Durium.ORE.item().get()),
                        RecipeCategory.MISC,
                        Durium.SCRAP_PIECE.get(),
                        1f,
                        100
                )
                .unlockedBy("has_durium_ore", has(Durium.ORE.item().get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_scrap_piece_from_blasting_ore");

        SimpleCookingRecipeBuilder.blasting(
                        Ingredient.of(Durium.DEEPSLATE_ORE.item().get()),
                        RecipeCategory.MISC,
                        Durium.SCRAP_PIECE.get(),
                        1f,
                        100
                )
                .unlockedBy("has_durium_ore", has(Durium.DEEPSLATE_ORE.item().get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_scrap_piece_from_blasting_deepslate_ore");

        //Hellish Coal
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

        //Recycle recipes
        recycleArmorBlasting(writer, ChainedCopperArmor.HELMET.get(), Items.IRON_NUGGET, 200, 6);
        recycleArmorBlasting(writer, ChainedCopperArmor.CHESTPLATE.get(), Items.IRON_NUGGET, 200, 9);
        recycleArmorBlasting(writer, ChainedCopperArmor.LEGGINGS.get(), Items.IRON_NUGGET, 200, 9);
        recycleArmorBlasting(writer, ChainedCopperArmor.BOOTS.get(), Items.IRON_NUGGET, 200, 6);
        recycleArmorBlasting(writer, Items.IRON_HELMET, Items.IRON_NUGGET, 200, 45);
        recycleArmorBlasting(writer, Items.IRON_CHESTPLATE, Items.IRON_NUGGET, 200, 72);
        recycleArmorBlasting(writer, Items.IRON_LEGGINGS, Items.IRON_NUGGET, 200, 63);
        recycleArmorBlasting(writer, Items.IRON_BOOTS, Items.IRON_NUGGET, 200, 36);
        recycleArmorBlasting(writer, Durium.HELMET.get(), Durium.NUGGET.get(), 200, 9);
        recycleArmorBlasting(writer, Durium.CHESTPLATE.get(), Durium.NUGGET.get(), 200, 9);
        recycleArmorBlasting(writer, Durium.LEGGINGS.get(), Durium.NUGGET.get(), 200, 9);
        recycleArmorBlasting(writer, Durium.BOOTS.get(), Durium.NUGGET.get(), 200, 9);
        recycleArmorBlasting(writer, Items.GOLDEN_HELMET, Items.GOLD_NUGGET, 200, 45);
        recycleArmorBlasting(writer, Items.GOLDEN_CHESTPLATE, Items.GOLD_NUGGET, 200, 72);
        recycleArmorBlasting(writer, Items.GOLDEN_LEGGINGS, Items.GOLD_NUGGET, 200, 63);
        recycleArmorBlasting(writer, Items.GOLDEN_BOOTS, Items.GOLD_NUGGET, 200, 36);
        recycleArmorBlasting(writer, SoulSteel.HELMET.get(), SoulSteel.NUGGET.get(), 200, 45);
        recycleArmorBlasting(writer, SoulSteel.CHESTPLATE.get(), SoulSteel.NUGGET.get(), 200, 72);
        recycleArmorBlasting(writer, SoulSteel.LEGGINGS.get(), SoulSteel.NUGGET.get(), 200, 63);
        recycleArmorBlasting(writer, SoulSteel.BOOTS.get(), SoulSteel.NUGGET.get(), 200, 36);
        recycleArmorBlasting(writer, Items.CHAINMAIL_HELMET, Items.IRON_NUGGET, 200, 15);
        recycleArmorBlasting(writer, Items.CHAINMAIL_CHESTPLATE, Items.IRON_NUGGET, 200, 24);
        recycleArmorBlasting(writer, Items.CHAINMAIL_LEGGINGS, Items.IRON_NUGGET, 200, 21);
        recycleArmorBlasting(writer, Items.CHAINMAIL_BOOTS, Items.IRON_NUGGET, 200, 12);
        recycleArmorBlasting(writer, Items.DIAMOND_HELMET, Items.DIAMOND, 200, 5);
        recycleArmorBlasting(writer, Items.DIAMOND_CHESTPLATE, Items.DIAMOND, 200, 8);
        recycleArmorBlasting(writer, Items.DIAMOND_LEGGINGS, Items.DIAMOND, 200, 7);
        recycleArmorBlasting(writer, Items.DIAMOND_BOOTS, Items.DIAMOND, 200, 4);
        recycleArmorBlasting(writer, Items.NETHERITE_HELMET, Items.NETHERITE_INGOT, 200, 1);
        recycleArmorBlasting(writer, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_INGOT, 200, 1);
        recycleArmorBlasting(writer, Items.NETHERITE_LEGGINGS, Items.NETHERITE_INGOT, 200, 1);
        recycleArmorBlasting(writer, Items.NETHERITE_BOOTS, Items.NETHERITE_INGOT, 200, 1);

        recycleArmorBlasting(writer, CopperTools.PICKAXE.get(), Items.COPPER_INGOT, 200, 3);
        recycleArmorBlasting(writer, CopperTools.AXE.get(), Items.COPPER_INGOT, 200, 3);
        recycleArmorBlasting(writer, CopperTools.SHOVEL.get(), Items.COPPER_INGOT, 200, 1);
        recycleArmorBlasting(writer, CopperTools.HOE.get(), Items.COPPER_INGOT, 200, 2);
        recycleArmorBlasting(writer, CopperTools.SWORD.get(), Items.COPPER_INGOT, 200, 2);
        recycleArmorBlasting(writer, Items.IRON_PICKAXE, Items.IRON_NUGGET, 200, 27);
        recycleArmorBlasting(writer, Items.IRON_AXE, Items.IRON_NUGGET, 200, 27);
        recycleArmorBlasting(writer, Items.IRON_SHOVEL, Items.IRON_NUGGET, 200, 9);
        recycleArmorBlasting(writer, Items.IRON_HOE, Items.IRON_NUGGET, 200, 18);
        recycleArmorBlasting(writer, Items.IRON_SWORD, Items.IRON_NUGGET, 200, 18);
        recycleArmorBlasting(writer, Durium.PICKAXE.get(), Durium.NUGGET.get(), 200, 9);
        recycleArmorBlasting(writer, Durium.AXE.get(), Durium.NUGGET.get(), 200, 9);
        recycleArmorBlasting(writer, Durium.SHOVEL.get(), Durium.NUGGET.get(), 200, 9);
        recycleArmorBlasting(writer, Durium.HOE.get(), Durium.NUGGET.get(), 200, 9);
        recycleArmorBlasting(writer, Durium.SWORD.get(), Durium.NUGGET.get(), 200, 9);
        recycleArmorBlasting(writer, Items.GOLDEN_PICKAXE, Items.GOLD_NUGGET, 200, 27);
        recycleArmorBlasting(writer, Items.GOLDEN_AXE, Items.GOLD_NUGGET, 200, 27);
        recycleArmorBlasting(writer, Items.GOLDEN_SHOVEL, Items.GOLD_NUGGET, 200, 9);
        recycleArmorBlasting(writer, Items.GOLDEN_HOE, Items.GOLD_NUGGET, 200, 18);
        recycleArmorBlasting(writer, Items.GOLDEN_SWORD, Items.GOLD_NUGGET, 200, 18);
        recycleArmorBlasting(writer, SoulSteel.PICKAXE.get(), SoulSteel.NUGGET.get(), 200, 27);
        recycleArmorBlasting(writer, SoulSteel.AXE.get(), SoulSteel.NUGGET.get(), 200, 27);
        recycleArmorBlasting(writer, SoulSteel.SHOVEL.get(), SoulSteel.NUGGET.get(), 200, 9);
        recycleArmorBlasting(writer, SoulSteel.HOE.get(), SoulSteel.NUGGET.get(), 200, 18);
        recycleArmorBlasting(writer, SoulSteel.SWORD.get(), SoulSteel.NUGGET.get(), 200, 18);
        recycleArmorBlasting(writer, Items.DIAMOND_PICKAXE, Items.DIAMOND, 200, 3);
        recycleArmorBlasting(writer, Items.DIAMOND_AXE, Items.DIAMOND, 200, 3);
        recycleArmorBlasting(writer, Items.DIAMOND_SHOVEL, Items.DIAMOND, 200, 1);
        recycleArmorBlasting(writer, Items.DIAMOND_HOE, Items.DIAMOND, 200, 2);
        recycleArmorBlasting(writer, Items.DIAMOND_SWORD, Items.DIAMOND, 200, 2);
        recycleArmorBlasting(writer, Items.NETHERITE_PICKAXE, Items.NETHERITE_INGOT, 200, 1);
        recycleArmorBlasting(writer, Items.NETHERITE_AXE, Items.NETHERITE_INGOT, 200, 1);
        recycleArmorBlasting(writer, Items.NETHERITE_SHOVEL, Items.NETHERITE_INGOT, 200, 1);
        recycleArmorBlasting(writer, Items.NETHERITE_HOE, Items.NETHERITE_INGOT, 200, 1);
        recycleArmorBlasting(writer, Items.NETHERITE_SWORD, Items.NETHERITE_INGOT, 200, 1);

        LegacyUpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_AXE), Ingredient.of(Durium.INGOT.get()), RecipeCategory.TOOLS, Durium.AXE.get())
                .unlocks("has_durium", has(Durium.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_axe");
        LegacyUpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_PICKAXE), Ingredient.of(Durium.INGOT.get()), RecipeCategory.TOOLS, Durium.PICKAXE.get())
                .unlocks("has_durium", has(Durium.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_pickaxe");
        LegacyUpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_SHOVEL), Ingredient.of(Durium.INGOT.get()), RecipeCategory.TOOLS, Durium.SHOVEL.get())
                .unlocks("has_durium", has(Durium.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_shovel");
        LegacyUpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_HOE), Ingredient.of(Durium.INGOT.get()), RecipeCategory.TOOLS, Durium.HOE.get())
                .unlocks("has_durium", has(Durium.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_how");
        LegacyUpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_SWORD), Ingredient.of(Durium.INGOT.get()), RecipeCategory.COMBAT, Durium.SWORD.get())
                .unlocks("has_durium", has(Durium.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_sword");
        LegacyUpgradeRecipeBuilder.smithing(Ingredient.of(Items.SHIELD), Ingredient.of(Durium.INGOT.get()), RecipeCategory.COMBAT, Durium.SHIELD.get())
                .unlocks("has_durium", has(Durium.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_shield");
        LegacyUpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_HELMET), Ingredient.of(Durium.INGOT.get()), RecipeCategory.COMBAT, Durium.HELMET.get())
                .unlocks("has_durium", has(Durium.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_helmet");
        LegacyUpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_CHESTPLATE), Ingredient.of(Durium.INGOT.get()), RecipeCategory.COMBAT, Durium.CHESTPLATE.get())
                .unlocks("has_durium", has(Durium.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_chestplate");
        LegacyUpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_LEGGINGS), Ingredient.of(Durium.INGOT.get()), RecipeCategory.COMBAT, Durium.LEGGINGS.get())
                .unlocks("has_durium", has(Durium.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_leggings");
        LegacyUpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_BOOTS), Ingredient.of(Durium.INGOT.get()), RecipeCategory.COMBAT, Durium.BOOTS.get())
                .unlocks("has_durium", has(Durium.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_boots");

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

    private void recycleArmorBlasting(Consumer<FinishedRecipe> writer, Item itemToRecycle, Item output, int cookingTime, int amountAtMaxDurability) {
        MultiItemSmeltingRecipeBuilder.blasting(
                        NonNullList.of(Ingredient.EMPTY, Ingredient.of(itemToRecycle)),
                        RecipeCategory.COMBAT,
                        output,
                        cookingTime)
                .recycle(amountAtMaxDurability, 0.75f)
                .unlockedBy("has_armor", has(itemToRecycle))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "blast_furnace/recycle_" + ForgeRegistries.ITEMS.getKey(itemToRecycle).getPath());

        MultiItemSmeltingRecipeBuilder.soulBlasting(
                        NonNullList.of(Ingredient.EMPTY, Ingredient.of(itemToRecycle)),
                        RecipeCategory.COMBAT,
                        output,
                        cookingTime)
                .recycle(amountAtMaxDurability)
                .unlockedBy("has_armor", has(itemToRecycle))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "soul_furnace/recycle_" + ForgeRegistries.ITEMS.getKey(itemToRecycle).getPath());
    }
}
