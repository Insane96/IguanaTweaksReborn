package insane96mcp.survivalreimagined.data.generator;

import insane96mcp.shieldsplus.setup.SPItems;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.experience.feature.Lapis;
import insane96mcp.survivalreimagined.module.experience.feature.enchanting.EnchantingFeature;
import insane96mcp.survivalreimagined.module.hungerhealth.feature.FoodDrinks;
import insane96mcp.survivalreimagined.module.items.feature.*;
import insane96mcp.survivalreimagined.module.mining.data.ForgeRecipeBuilder;
import insane96mcp.survivalreimagined.module.mining.data.MultiItemSmeltingRecipeBuilder;
import insane96mcp.survivalreimagined.module.mining.feature.*;
import insane96mcp.survivalreimagined.module.sleeprespawn.feature.Cloth;
import insane96mcp.survivalreimagined.module.world.feature.CoalFire;
import insane96mcp.survivalreimagined.module.world.feature.CyanFlower;
import insane96mcp.survivalreimagined.module.world.feature.OreGeneration;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
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
                .pattern("fWf")
                .pattern(" f ")
                .define('f', Items.FLINT)
                .define('W', SPItems.WOODEN_SHIELD.get())
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

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, CopperToolsExpansion.COPPER_AXE.get())
                .pattern("ff")
                .pattern("fs")
                .pattern(" s")
                .define('f', Items.COPPER_INGOT)
                .define('s', Items.STICK)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
                .save(writer);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, CopperToolsExpansion.COPPER_SHOVEL.get())
                .pattern("f")
                .pattern("s")
                .pattern("s")
                .define('f', Items.COPPER_INGOT)
                .define('s', Items.STICK)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
                .save(writer);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, CopperToolsExpansion.COPPER_PICKAXE.get())
                .pattern("fff")
                .pattern(" s ")
                .pattern(" s ")
                .define('f', Items.COPPER_INGOT)
                .define('s', Items.STICK)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
                .save(writer);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, CopperToolsExpansion.COPPER_HOE.get())
                .pattern("ff")
                .pattern(" s")
                .pattern(" s")
                .define('f', Items.COPPER_INGOT)
                .define('s', Items.STICK)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
                .save(writer);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, CopperToolsExpansion.COPPER_SWORD.get())
                .pattern("f")
                .pattern("f")
                .pattern("s")
                .define('f', Items.COPPER_INGOT)
                .define('s', Items.STICK)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
                .save(writer);


        //Ensorcellator
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, EnchantingFeature.ENSORCELLER.item().get())
                .pattern(" c ")
                .pattern("iSi")
                .pattern("sss")
                .define('c', Cloth.CLOTH.get())
                .define('i', Items.IRON_INGOT)
                .define('S', Solarium.SOLARIUM_BALL.get())
                .define('s', Items.STONE_BRICKS)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(writer);

        //Coated Copper
        forgeRecipe(writer, Items.OBSIDIAN, 3, CopperToolsExpansion.COPPER_PICKAXE.get(), CopperToolsExpansion.COATED_PICKAXE.get(), 12, 6);
        forgeRecipe(writer, Items.OBSIDIAN, 3, CopperToolsExpansion.COPPER_AXE.get(), CopperToolsExpansion.COATED_AXE.get(), 12, 6);
        forgeRecipe(writer, Items.OBSIDIAN, 2, CopperToolsExpansion.COPPER_SWORD.get(), CopperToolsExpansion.COATED_SWORD.get(), 12, 6);
        forgeRecipe(writer, Items.OBSIDIAN, 2, CopperToolsExpansion.COPPER_HOE.get(), CopperToolsExpansion.COATED_HOE.get(), 12, 6);
        forgeRecipe(writer, Items.OBSIDIAN, 1, CopperToolsExpansion.COPPER_SHOVEL.get(), CopperToolsExpansion.COATED_SHOVEL.get(), 12, 6);
        forgeRecipe(writer, Items.OBSIDIAN, 4, SPItems.COPPER_SHIELD.get(), CopperToolsExpansion.COATED_SHIELD.get(), 12, 6);

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
                .define('c', Items.IRON_NUGGET)
                .define('m', Items.COPPER_INGOT)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
                .unlockedBy("has_chain", has(Items.IRON_NUGGET))
                .save(writer);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ChainedCopperArmor.CHESTPLATE.get())
                .pattern("c c")
                .pattern("mcm")
                .pattern("mmm")
                .define('c', Items.IRON_NUGGET)
                .define('m', Items.COPPER_INGOT)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
                .unlockedBy("has_chain", has(Items.IRON_NUGGET))
                .save(writer);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ChainedCopperArmor.LEGGINGS.get())
                .pattern("ccc")
                .pattern("m m")
                .pattern("m m")
                .define('c', Items.IRON_NUGGET)
                .define('m', Items.COPPER_INGOT)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
                .unlockedBy("has_chain", has(Items.IRON_NUGGET))
                .save(writer);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ChainedCopperArmor.BOOTS.get())
                .pattern("c c")
                .pattern("m m")
                .define('c', Items.IRON_NUGGET)
                .define('m', Items.COPPER_INGOT)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
                .unlockedBy("has_chain", has(Items.IRON_NUGGET))
                .save(writer);

        //Chainmail Armor
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.CHAINMAIL_HELMET)
                .pattern("CcC")
                .pattern("c c")
                .define('c', Items.CHAIN)
                .define('C', Cloth.CLOTH.get())
                .unlockedBy("has_cloth", has(Cloth.CLOTH.get()))
                .unlockedBy("has_chain", has(Items.CHAIN))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "chainmail_helmet");
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.CHAINMAIL_CHESTPLATE)
                .pattern("C C")
                .pattern("cCc")
                .pattern("ccc")
                .define('c', Items.CHAIN)
                .define('C', Cloth.CLOTH.get())
                .unlockedBy("has_cloth", has(Cloth.CLOTH.get()))
                .unlockedBy("has_chain", has(Items.CHAIN))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "chainmail_chestplate");
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.CHAINMAIL_LEGGINGS)
                .pattern("CCC")
                .pattern("c c")
                .pattern("c c")
                .define('c', Items.CHAIN)
                .define('C', Cloth.CLOTH.get())
                .unlockedBy("has_cloth", has(Cloth.CLOTH.get()))
                .unlockedBy("has_chain", has(Items.CHAIN))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "chainmail_leggings");
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.CHAINMAIL_BOOTS)
                .pattern("C C")
                .pattern("c c")
                .define('c', Items.CHAIN)
                .define('C', Cloth.CLOTH.get())
                .unlockedBy("has_cloth", has(Cloth.CLOTH.get()))
                .unlockedBy("has_chain", has(Items.CHAIN))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "chainmail_boots");

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, Crate.ITEM.get())
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

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Lapis.ANCIENT_LAPIS.get(), 1)
                .requires(Lapis.CLEANSED_LAPIS.get(), 1)
                .requires(Items.NETHERITE_SCRAP, 1)
                .requires(Items.EXPERIENCE_BOTTLE, 1)
                .unlockedBy("has_netherite_scrap", has(Items.NETHERITE_SCRAP))
                .unlockedBy("has_cleansed_lapis", has(Lapis.CLEANSED_LAPIS.get()))
                .save(writer);

        //Solarium
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Solarium.SOLARIUM_BALL.get())
                .requires(Solarium.SOLIUM_MOSS.item().get(), 9)
                .unlockedBy("has_solium_moss", has(Solarium.SOLIUM_MOSS.item().get()))
                .save(writer);
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.IRON_AXE), Ingredient.of(Solarium.SOLARIUM_BALL.get()), RecipeCategory.TOOLS, Solarium.AXE.get())
                .unlocks("has_solium_moss_ball", has(Solarium.SOLARIUM_BALL.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "solarium_axe");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.IRON_PICKAXE), Ingredient.of(Solarium.SOLARIUM_BALL.get()), RecipeCategory.TOOLS, Solarium.PICKAXE.get())
                .unlocks("has_solium_moss_ball", has(Solarium.SOLARIUM_BALL.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "solarium_pickaxe");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.IRON_SHOVEL), Ingredient.of(Solarium.SOLARIUM_BALL.get()), RecipeCategory.TOOLS, Solarium.SHOVEL.get())
                .unlocks("has_solium_moss_ball", has(Solarium.SOLARIUM_BALL.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "solarium_shovel");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.IRON_HOE), Ingredient.of(Solarium.SOLARIUM_BALL.get()), RecipeCategory.TOOLS, Solarium.HOE.get())
                .unlocks("has_solium_moss_ball", has(Solarium.SOLARIUM_BALL.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "solarium_hoe");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.IRON_SWORD), Ingredient.of(Solarium.SOLARIUM_BALL.get()), RecipeCategory.COMBAT, Solarium.SWORD.get())
                .unlocks("has_solium_moss_ball", has(Solarium.SOLARIUM_BALL.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "solarium_sword");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.SHIELD), Ingredient.of(Solarium.SOLARIUM_BALL.get()), RecipeCategory.COMBAT, Solarium.SHIELD.get())
                .unlocks("has_solium_moss_ball", has(Solarium.SOLARIUM_BALL.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "solarium_shield");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.IRON_HELMET), Ingredient.of(Solarium.SOLARIUM_BALL.get()), RecipeCategory.COMBAT, Solarium.HELMET.get())
                .unlocks("has_solium_moss_ball", has(Solarium.SOLARIUM_BALL.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "solarium_helmet");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.IRON_CHESTPLATE), Ingredient.of(Solarium.SOLARIUM_BALL.get()), RecipeCategory.COMBAT, Solarium.CHESTPLATE.get())
                .unlocks("has_solium_moss_ball", has(Solarium.SOLARIUM_BALL.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "solarium_chestplate");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.IRON_LEGGINGS), Ingredient.of(Solarium.SOLARIUM_BALL.get()), RecipeCategory.COMBAT, Solarium.LEGGINGS.get())
                .unlocks("has_solium_moss_ball", has(Solarium.SOLARIUM_BALL.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "solarium_leggings");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.IRON_BOOTS), Ingredient.of(Solarium.SOLARIUM_BALL.get()), RecipeCategory.COMBAT, Solarium.BOOTS.get())
                .unlocks("has_solium_moss_ball", has(Solarium.SOLARIUM_BALL.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "solarium_boots");

        //Durium Block, Ingot, Nugget, Scrap, smithing
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
                .experience(1f)
                .unlockedBy("has_scrap_block", has(Durium.SCRAP_BLOCK.item().get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_ingot_from_blasting");
        MultiItemSmeltingRecipeBuilder.soulBlasting(
                        NonNullList.of(Ingredient.EMPTY, Ingredient.of(Durium.SCRAP_BLOCK.item().get()), Ingredient.of(ItemTags.SAND), Ingredient.of(Items.CLAY_BALL)),
                        RecipeCategory.MISC,
                        Durium.INGOT.get(),
                        400
                )
                .experience(1f)
                .doubleOutputChance(0.2f)
                .unlockedBy("has_scrap_block", has(Durium.SCRAP_BLOCK.item().get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_ingot_from_soul_blasting");

        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.IRON_AXE), Ingredient.of(Durium.INGOT.get()), RecipeCategory.TOOLS, Durium.AXE.get())
                .unlocks("has_durium", has(Durium.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_axe");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.IRON_PICKAXE), Ingredient.of(Durium.INGOT.get()), RecipeCategory.TOOLS, Durium.PICKAXE.get())
                .unlocks("has_durium", has(Durium.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_pickaxe");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.IRON_SHOVEL), Ingredient.of(Durium.INGOT.get()), RecipeCategory.TOOLS, Durium.SHOVEL.get())
                .unlocks("has_durium", has(Durium.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_shovel");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.IRON_HOE), Ingredient.of(Durium.INGOT.get()), RecipeCategory.TOOLS, Durium.HOE.get())
                .unlocks("has_durium", has(Durium.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_hoe");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.IRON_SWORD), Ingredient.of(Durium.INGOT.get()), RecipeCategory.COMBAT, Durium.SWORD.get())
                .unlocks("has_durium", has(Durium.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_sword");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.SHIELD), Ingredient.of(Durium.INGOT.get()), RecipeCategory.COMBAT, Durium.SHIELD.get())
                .unlocks("has_durium", has(Durium.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_shield");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.IRON_HELMET), Ingredient.of(Durium.INGOT.get()), RecipeCategory.COMBAT, Durium.HELMET.get())
                .unlocks("has_durium", has(Durium.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_helmet");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.IRON_CHESTPLATE), Ingredient.of(Durium.INGOT.get()), RecipeCategory.COMBAT, Durium.CHESTPLATE.get())
                .unlocks("has_durium", has(Durium.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_chestplate");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.IRON_LEGGINGS), Ingredient.of(Durium.INGOT.get()), RecipeCategory.COMBAT, Durium.LEGGINGS.get())
                .unlocks("has_durium", has(Durium.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_leggings");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.IRON_BOOTS), Ingredient.of(Durium.INGOT.get()), RecipeCategory.COMBAT, Durium.BOOTS.get())
                .unlocks("has_durium", has(Durium.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_boots");

        //Soul Steel
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.DIAMOND_AXE), Ingredient.of(SoulSteel.INGOT.get()), RecipeCategory.TOOLS, SoulSteel.AXE.get())
                .unlocks("has_soul_steel", has(SoulSteel.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "soul_steel_axe");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.DIAMOND_PICKAXE), Ingredient.of(SoulSteel.INGOT.get()), RecipeCategory.TOOLS, SoulSteel.PICKAXE.get())
                .unlocks("has_soul_steel", has(SoulSteel.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "soul_steel_pickaxe");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.DIAMOND_SHOVEL), Ingredient.of(SoulSteel.INGOT.get()), RecipeCategory.TOOLS, SoulSteel.SHOVEL.get())
                .unlocks("has_soul_steel", has(SoulSteel.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "soul_steel_shovel");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.DIAMOND_HOE), Ingredient.of(SoulSteel.INGOT.get()), RecipeCategory.TOOLS, SoulSteel.HOE.get())
                .unlocks("has_soul_steel", has(SoulSteel.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "soul_steel_hoe");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.DIAMOND_SWORD), Ingredient.of(SoulSteel.INGOT.get()), RecipeCategory.COMBAT, SoulSteel.SWORD.get())
                .unlocks("has_soul_steel", has(SoulSteel.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "soul_steel_sword");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.SHIELD), Ingredient.of(SoulSteel.INGOT.get()), RecipeCategory.COMBAT, SoulSteel.SHIELD.get())
                .unlocks("has_soul_steel", has(SoulSteel.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "soul_steel_shield");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.DIAMOND_HELMET), Ingredient.of(SoulSteel.INGOT.get()), RecipeCategory.COMBAT, SoulSteel.HELMET.get())
                .unlocks("has_soul_steel", has(SoulSteel.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "soul_steel_helmet");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.DIAMOND_CHESTPLATE), Ingredient.of(SoulSteel.INGOT.get()), RecipeCategory.COMBAT, SoulSteel.CHESTPLATE.get())
                .unlocks("has_soul_steel", has(SoulSteel.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "soul_steel_chestplate");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.DIAMOND_LEGGINGS), Ingredient.of(SoulSteel.INGOT.get()), RecipeCategory.COMBAT, SoulSteel.LEGGINGS.get())
                .unlocks("has_soul_steel", has(SoulSteel.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "soul_steel_leggings");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Items.DIAMOND_BOOTS), Ingredient.of(SoulSteel.INGOT.get()), RecipeCategory.COMBAT, SoulSteel.BOOTS.get())
                .unlocks("has_soul_steel", has(SoulSteel.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "soul_steel_boots");
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
                        NonNullList.of(Ingredient.EMPTY, Ingredient.of(Durium.INGOT.get()), Ingredient.of(Durium.INGOT.get()), Ingredient.of(CoalFire.HELLISH_COAL.get()), Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL)),
                        RecipeCategory.MISC,
                        SoulSteel.INGOT.get(),
                        800
                )
                .experience(2f)
                .unlockedBy("has_hellish_coal", has(CoalFire.HELLISH_COAL.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "soul_steel_ingot_from_blasting");
        MultiItemSmeltingRecipeBuilder.soulBlasting(
                        NonNullList.of(Ingredient.EMPTY, Ingredient.of(Durium.INGOT.get()), Ingredient.of(Durium.INGOT.get()), Ingredient.of(CoalFire.HELLISH_COAL.get()), Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL)),
                        RecipeCategory.MISC,
                        SoulSteel.INGOT.get(),
                        400
                )
                .experience(2f)
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

        //Cyan flowers
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, Items.CYAN_DYE)
                .requires(CyanFlower.FLOWER.item().get())
                .unlockedBy("has_flower", has(CyanFlower.FLOWER.item().get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "cyan_dye_from_cyan_flower");

        //Mining Charge
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MiningCharge.MINING_CHARGE.item().get())
                .pattern("GTG")
                .pattern("CsC")
                .pattern("CCC")
                .define('G', Items.GUNPOWDER)
                .define('C', Durium.NUGGET.get())
                .define('T', Items.TNT)
                .define('s', Items.SLIME_BALL)
                .unlockedBy("has_tnt", has(Items.TNT))
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
                        400
                )
                .unlockedBy("has_copper_ore_rock", has(OreGeneration.COPPER_ORE_ROCK.item().get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "copper_ingot_from_smelting_rock");
        SimpleCookingRecipeBuilder.blasting(
                        Ingredient.of(OreGeneration.COPPER_ORE_ROCK.item().get()),
                        RecipeCategory.MISC,
                        Items.COPPER_INGOT,
                        0.7f,
                        100
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
        recycleGearBlasting(writer, ChainedCopperArmor.HELMET.get(), Items.IRON_NUGGET, 200, 6);
        recycleGearBlasting(writer, ChainedCopperArmor.CHESTPLATE.get(), Items.IRON_NUGGET, 200, 9);
        recycleGearBlasting(writer, ChainedCopperArmor.LEGGINGS.get(), Items.IRON_NUGGET, 200, 9);
        recycleGearBlasting(writer, ChainedCopperArmor.BOOTS.get(), Items.IRON_NUGGET, 200, 6);
        recycleGearBlasting(writer, Items.IRON_HELMET, Items.IRON_NUGGET, 200, 45);
        recycleGearBlasting(writer, Items.IRON_CHESTPLATE, Items.IRON_NUGGET, 200, 72);
        recycleGearBlasting(writer, Items.IRON_LEGGINGS, Items.IRON_NUGGET, 200, 63);
        recycleGearBlasting(writer, Items.IRON_BOOTS, Items.IRON_NUGGET, 200, 36);
        recycleGearBlasting(writer, Durium.HELMET.get(), Durium.NUGGET.get(), 200, 9);
        recycleGearBlasting(writer, Durium.CHESTPLATE.get(), Durium.NUGGET.get(), 200, 9);
        recycleGearBlasting(writer, Durium.LEGGINGS.get(), Durium.NUGGET.get(), 200, 9);
        recycleGearBlasting(writer, Durium.BOOTS.get(), Durium.NUGGET.get(), 200, 9);
        recycleGearBlasting(writer, Items.GOLDEN_HELMET, Items.GOLD_NUGGET, 200, 45);
        recycleGearBlasting(writer, Items.GOLDEN_CHESTPLATE, Items.GOLD_NUGGET, 200, 72);
        recycleGearBlasting(writer, Items.GOLDEN_LEGGINGS, Items.GOLD_NUGGET, 200, 63);
        recycleGearBlasting(writer, Items.GOLDEN_BOOTS, Items.GOLD_NUGGET, 200, 36);
        recycleGearBlasting(writer, Items.CHAINMAIL_HELMET, Items.IRON_NUGGET, 200, 15);
        recycleGearBlasting(writer, Items.CHAINMAIL_CHESTPLATE, Items.IRON_NUGGET, 200, 24);
        recycleGearBlasting(writer, Items.CHAINMAIL_LEGGINGS, Items.IRON_NUGGET, 200, 21);
        recycleGearBlasting(writer, Items.CHAINMAIL_BOOTS, Items.IRON_NUGGET, 200, 12);
        recycleGearBlasting(writer, Items.DIAMOND_HELMET, Items.DIAMOND, 200, 5);
        recycleGearBlasting(writer, Items.DIAMOND_CHESTPLATE, Items.DIAMOND, 200, 8);
        recycleGearBlasting(writer, Items.DIAMOND_LEGGINGS, Items.DIAMOND, 200, 7);
        recycleGearBlasting(writer, Items.DIAMOND_BOOTS, Items.DIAMOND, 200, 4);
        recycleGearBlasting(writer, Items.NETHERITE_HELMET, Items.NETHERITE_INGOT, 200, 1);
        recycleGearBlasting(writer, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_INGOT, 200, 1);
        recycleGearBlasting(writer, Items.NETHERITE_LEGGINGS, Items.NETHERITE_INGOT, 200, 1);
        recycleGearBlasting(writer, Items.NETHERITE_BOOTS, Items.NETHERITE_INGOT, 200, 1);
        recycleGearBlasting(writer, SoulSteel.HELMET.get(), SoulSteel.NUGGET.get(), 200, 9);
        recycleGearBlasting(writer, SoulSteel.CHESTPLATE.get(), SoulSteel.NUGGET.get(), 200, 9);
        recycleGearBlasting(writer, SoulSteel.LEGGINGS.get(), SoulSteel.NUGGET.get(), 200, 9);
        recycleGearBlasting(writer, SoulSteel.BOOTS.get(), SoulSteel.NUGGET.get(), 200, 9);
        recycleGearBlasting(writer, Keego.HELMET.get(), Keego.GEM.get(), 200, 1);
        recycleGearBlasting(writer, Keego.CHESTPLATE.get(), Keego.GEM.get(), 200, 1);
        recycleGearBlasting(writer, Keego.LEGGINGS.get(), Keego.GEM.get(), 200, 1);
        recycleGearBlasting(writer, Keego.BOOTS.get(), Keego.GEM.get(), 200, 1);

        recycleGearBlasting(writer, CopperToolsExpansion.COPPER_PICKAXE.get(), Items.COPPER_INGOT, 200, 3);
        recycleGearBlasting(writer, CopperToolsExpansion.COPPER_AXE.get(), Items.COPPER_INGOT, 200, 3);
        recycleGearBlasting(writer, CopperToolsExpansion.COPPER_SHOVEL.get(), Items.COPPER_INGOT, 200, 1);
        recycleGearBlasting(writer, CopperToolsExpansion.COPPER_HOE.get(), Items.COPPER_INGOT, 200, 2);
        recycleGearBlasting(writer, CopperToolsExpansion.COPPER_SWORD.get(), Items.COPPER_INGOT, 200, 2);
        recycleGearBlasting(writer, Items.IRON_PICKAXE, Items.IRON_NUGGET, 200, 27);
        recycleGearBlasting(writer, Items.IRON_AXE, Items.IRON_NUGGET, 200, 27);
        recycleGearBlasting(writer, Items.IRON_SHOVEL, Items.IRON_NUGGET, 200, 9);
        recycleGearBlasting(writer, Items.IRON_HOE, Items.IRON_NUGGET, 200, 18);
        recycleGearBlasting(writer, Items.IRON_SWORD, Items.IRON_NUGGET, 200, 18);
        recycleGearBlasting(writer, Durium.PICKAXE.get(), Durium.NUGGET.get(), 200, 9);
        recycleGearBlasting(writer, Durium.AXE.get(), Durium.NUGGET.get(), 200, 9);
        recycleGearBlasting(writer, Durium.SHOVEL.get(), Durium.NUGGET.get(), 200, 9);
        recycleGearBlasting(writer, Durium.HOE.get(), Durium.NUGGET.get(), 200, 9);
        recycleGearBlasting(writer, Durium.SWORD.get(), Durium.NUGGET.get(), 200, 9);
        recycleGearBlasting(writer, Items.GOLDEN_PICKAXE, Items.GOLD_NUGGET, 200, 27);
        recycleGearBlasting(writer, Items.GOLDEN_AXE, Items.GOLD_NUGGET, 200, 27);
        recycleGearBlasting(writer, Items.GOLDEN_SHOVEL, Items.GOLD_NUGGET, 200, 9);
        recycleGearBlasting(writer, Items.GOLDEN_HOE, Items.GOLD_NUGGET, 200, 18);
        recycleGearBlasting(writer, Items.GOLDEN_SWORD, Items.GOLD_NUGGET, 200, 18);
        recycleGearBlasting(writer, CopperToolsExpansion.COATED_PICKAXE.get(), Items.OBSIDIAN, 200, 3);
        recycleGearBlasting(writer, CopperToolsExpansion.COATED_AXE.get(), Items.OBSIDIAN, 200, 3);
        recycleGearBlasting(writer, CopperToolsExpansion.COATED_SHOVEL.get(), Items.OBSIDIAN, 200, 1);
        recycleGearBlasting(writer, CopperToolsExpansion.COATED_HOE.get(), Items.OBSIDIAN, 200, 2);
        recycleGearBlasting(writer, CopperToolsExpansion.COATED_SWORD.get(), Items.OBSIDIAN, 200, 2);
        recycleGearBlasting(writer, Items.DIAMOND_PICKAXE, Items.DIAMOND, 200, 3);
        recycleGearBlasting(writer, Items.DIAMOND_AXE, Items.DIAMOND, 200, 3);
        recycleGearBlasting(writer, Items.DIAMOND_SHOVEL, Items.DIAMOND, 200, 1);
        recycleGearBlasting(writer, Items.DIAMOND_HOE, Items.DIAMOND, 200, 2);
        recycleGearBlasting(writer, Items.DIAMOND_SWORD, Items.DIAMOND, 200, 2);
        recycleGearBlasting(writer, Items.NETHERITE_PICKAXE, Items.NETHERITE_INGOT, 200, 1);
        recycleGearBlasting(writer, Items.NETHERITE_AXE, Items.NETHERITE_INGOT, 200, 1);
        recycleGearBlasting(writer, Items.NETHERITE_SHOVEL, Items.NETHERITE_INGOT, 200, 1);
        recycleGearBlasting(writer, Items.NETHERITE_HOE, Items.NETHERITE_INGOT, 200, 1);
        recycleGearBlasting(writer, Items.NETHERITE_SWORD, Items.NETHERITE_INGOT, 200, 1);
        recycleGearBlasting(writer, SoulSteel.PICKAXE.get(), SoulSteel.NUGGET.get(), 200, 9);
        recycleGearBlasting(writer, SoulSteel.AXE.get(), SoulSteel.NUGGET.get(), 200, 9);
        recycleGearBlasting(writer, SoulSteel.SHOVEL.get(), SoulSteel.NUGGET.get(), 200, 9);
        recycleGearBlasting(writer, SoulSteel.HOE.get(), SoulSteel.NUGGET.get(), 200, 9);
        recycleGearBlasting(writer, SoulSteel.SWORD.get(), SoulSteel.NUGGET.get(), 200, 9);
        recycleGearBlasting(writer, Keego.PICKAXE.get(), Keego.GEM.get(), 200, 1);
        recycleGearBlasting(writer, Keego.AXE.get(), Keego.GEM.get(), 200, 1);
        recycleGearBlasting(writer, Keego.SHOVEL.get(), Keego.GEM.get(), 200, 1);
        recycleGearBlasting(writer, Keego.HOE.get(), Keego.GEM.get(), 200, 1);
        recycleGearBlasting(writer, Keego.SWORD.get(), Keego.GEM.get(), 200, 1);

        //Recycle Shields
        recycleGearBlasting(writer, SPItems.COPPER_SHIELD.get(), Items.COPPER_INGOT, 200, 4);
        recycleGearBlasting(writer, Items.SHIELD, Items.IRON_NUGGET, 200, 36);
        recycleGearBlasting(writer, Durium.SHIELD.get(), Durium.NUGGET.get(), 200, 9);
        recycleGearBlasting(writer, SPItems.GOLDEN_SHIELD.get(), Items.GOLD_NUGGET, 200, 36);
        recycleGearBlasting(writer, CopperToolsExpansion.COATED_SHIELD.get(), Items.OBSIDIAN, 200, 4);
        recycleGearBlasting(writer, SPItems.DIAMOND_SHIELD.get(), Items.DIAMOND, 200, 1);
        recycleGearBlasting(writer, SPItems.NETHERITE_SHIELD.get(), Items.NETHERITE_INGOT, 200, 1);
        recycleGearBlasting(writer, SoulSteel.SHIELD.get(), SoulSteel.NUGGET.get(), 200, 9);
        recycleGearBlasting(writer, Keego.SHIELD.get(), Keego.GEM.get(), 200, 1);

        //Forge recipes
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Forging.FORGE.item().get())
                .pattern("ISI")
                .pattern(" c ")
                .pattern("cCc")
                .define('S', Items.SMOOTH_STONE)
                .define('I', Items.IRON_BLOCK)
                .define('c', Items.COPPER_INGOT)
                .define('C', Items.COPPER_BLOCK)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
                .save(writer);

        hammerCraftingRecipe(writer, Forging.STONE_HAMMER.get(), ItemTags.STONE_CRAFTING_MATERIALS);
        hammerCraftingRecipe(writer, Forging.FLINT_HAMMER.get(), Items.FLINT);
        hammerCraftingRecipe(writer, Forging.COPPER_HAMMER.get(), Items.COPPER_INGOT);
        forgeRecipe(writer, Items.IRON_INGOT, 5, Forging.STONE_HAMMER.get(), Forging.IRON_HAMMER.get(), 10, 5f);
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Forging.IRON_HAMMER.get()), Ingredient.of(Solarium.SOLARIUM_BALL.get()), RecipeCategory.TOOLS, Forging.SOLARIUM_HAMMER.get())
                .unlocks("has_material", has(Solarium.SOLARIUM_BALL.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "solarium_hammer");
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Forging.IRON_HAMMER.get()), Ingredient.of(Durium.INGOT.get()), RecipeCategory.TOOLS, Forging.DURIUM_HAMMER.get())
                .unlocks("has_material", has(Durium.INGOT.get()))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "durium_hammer");
        forgeRecipe(writer, Items.GOLD_INGOT, 5, Forging.FLINT_HAMMER.get(), Forging.GOLDEN_HAMMER.get(), 4, 2f);
        forgeRecipe(writer, Items.OBSIDIAN, 5, Forging.COPPER_HAMMER.get(), Forging.COATED_COPPER_HAMMER.get(), 12, 6f);
        forgeRecipe(writer, Items.DIAMOND, 5, Forging.GOLDEN_HAMMER.get(), Forging.DIAMOND_HAMMER.get(), 16, 8f);
        forgeRecipe(writer, SoulSteel.INGOT.get(), 5, Forging.IRON_HAMMER.get(), Forging.SOUL_STEEL_HAMMER.get(), 20, 10f);
        SmithingTransformRecipeBuilder.smithing(Ingredient.EMPTY, Ingredient.of(Forging.SOUL_STEEL_HAMMER.get()), Ingredient.of(Items.NETHERITE_INGOT), RecipeCategory.TOOLS, Forging.NETHERITE_HAMMER.get())
                .unlocks("has_material", has(Items.NETHERITE_INGOT))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "netherite_hammer");

        addPoorRichOreRecipes(writer, OreGeneration.POOR_RICH_IRON_ORE, Items.IRON_INGOT, 0.8f);
        addPoorRichOreRecipes(writer, OreGeneration.POOR_RICH_COPPER_ORE, Items.COPPER_INGOT, 0.7f);
        addPoorRichOreRecipes(writer, OreGeneration.POOR_RICH_GOLD_ORE, Items.GOLD_INGOT, 1f);
    }

    private void addPoorRichOreRecipes(Consumer<FinishedRecipe> writer, OreGeneration.PoorRichOre poorRichOre, Item smeltOutput, float experience) {
        for (Item item : poorRichOre.getAllItems()) {
            String name = ForgeRegistries.ITEMS.getKey(item).getPath();
            String metal = name.split("_")[1];
            SimpleCookingRecipeBuilder.smelting(
                            Ingredient.of(item),
                            RecipeCategory.MISC,
                            smeltOutput,
                            experience,
                            800
                    )
                    .unlockedBy("has_" + name, has(item))
                    .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "ingot_from_smelting_" + name);
            SimpleCookingRecipeBuilder.blasting(
                            Ingredient.of(item),
                            RecipeCategory.MISC,
                            smeltOutput,
                            experience,
                            200
                    )
                    .unlockedBy("has_" + name, has(item))
                    .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "ingot_from_blasting_" + name);
        }
    }

    private void recycleGearBlasting(Consumer<FinishedRecipe> writer, Item itemToRecycle, Item output, int baseCookingTime, int amountAtMaxDurability) {
        MultiItemSmeltingRecipeBuilder.blasting(
                        NonNullList.of(Ingredient.EMPTY, Ingredient.of(itemToRecycle)),
                        RecipeCategory.COMBAT,
                        output,
                        baseCookingTime)
                .recycle(amountAtMaxDurability, 0.75f)
                .group("recycle_" + ForgeRegistries.ITEMS.getKey(output).getPath())
                .unlockedBy("has_armor", has(itemToRecycle))
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "blast_furnace/recycle_" + ForgeRegistries.ITEMS.getKey(itemToRecycle).getPath());

        MultiItemSmeltingRecipeBuilder.soulBlasting(
                        NonNullList.of(Ingredient.EMPTY, Ingredient.of(itemToRecycle)),
                        RecipeCategory.COMBAT,
                        output,
                        baseCookingTime / 2)
                .recycle(amountAtMaxDurability)
                .unlockedBy("has_armor", has(itemToRecycle))
                .group("recycle_" + ForgeRegistries.ITEMS.getKey(output).getPath())
                .save(writer, SurvivalReimagined.RESOURCE_PREFIX + "soul_furnace/recycle_" + ForgeRegistries.ITEMS.getKey(itemToRecycle).getPath());
    }

    private void hammerCraftingRecipe(Consumer<FinishedRecipe> writer, Item hammer, Item material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, hammer)
                .pattern("MMM")
                .pattern("MSM")
                .pattern(" S ")
                .define('S', Items.STICK)
                .define('M', material)
                .unlockedBy("has_material", has(material))
                .save(writer);
    }

    private void hammerCraftingRecipe(Consumer<FinishedRecipe> writer, Item hammer, TagKey<Item> materialTag) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, hammer)
                .pattern("MMM")
                .pattern("MSM")
                .pattern(" S ")
                .define('S', Items.STICK)
                .define('M', materialTag)
                .unlockedBy("has_material", has(materialTag))
                .save(writer);
    }
    private void forgeRecipe(Consumer<FinishedRecipe> writer, Item material, int amount, Item gear, Item result, int smashesRequired, float experience) {
        ForgeRecipeBuilder.forging(RecipeCategory.TOOLS, Ingredient.of(material), amount, Ingredient.of(gear), result, smashesRequired)
                .awardExperience(experience)
                .unlockedBy("has_material", has(material))
                .save(writer);
    }

    private void forgeRecipe(Consumer<FinishedRecipe> writer, TagKey<Item> materialTag, int amount, Item gear, Item result, int smashesRequired, float experience) {
        ForgeRecipeBuilder.forging(RecipeCategory.TOOLS, Ingredient.of(materialTag), amount, Ingredient.of(gear), result, smashesRequired)
                .awardExperience(experience)
                .unlockedBy("has_material", has(materialTag))
                .save(writer);
    }
}
