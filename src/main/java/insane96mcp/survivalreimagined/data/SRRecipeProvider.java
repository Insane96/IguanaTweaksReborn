package insane96mcp.survivalreimagined.data;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.items.feature.BoneClub;
import insane96mcp.survivalreimagined.module.items.feature.ChainedCopperArmor;
import insane96mcp.survivalreimagined.module.items.feature.FlintTools;
import insane96mcp.survivalreimagined.module.items.feature.Iridium;
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
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, FlintTools.AXE.get())
                .pattern("ff")
                .pattern("fs")
                .pattern(" s")
                .define('f', Items.FLINT)
                .define('s', Items.STICK)
                .unlockedBy("has_stick", has(Items.STICK))
                .unlockedBy("has_flint", has(Items.FLINT))
                .save(writer);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, FlintTools.SHOVEL.get())
                .pattern("f")
                .pattern("s")
                .pattern("s")
                .define('f', Items.FLINT)
                .define('s', Items.STICK)
                .unlockedBy("has_stick", has(Items.STICK))
                .unlockedBy("has_flint", has(Items.FLINT))
                .save(writer);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, FlintTools.PICKAXE.get())
                .pattern("fff")
                .pattern(" s ")
                .pattern(" s ")
                .define('f', Items.FLINT)
                .define('s', Items.STICK)
                .unlockedBy("has_stick", has(Items.STICK))
                .unlockedBy("has_flint", has(Items.FLINT))
                .save(writer);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, FlintTools.HOE.get())
                .pattern("ff")
                .pattern(" s")
                .pattern(" s")
                .define('f', Items.FLINT)
                .define('s', Items.STICK)
                .unlockedBy("has_stick", has(Items.STICK))
                .unlockedBy("has_flint", has(Items.FLINT))
                .save(writer);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, FlintTools.SWORD.get())
                .pattern("f")
                .pattern("f")
                .pattern("s")
                .define('f', Items.FLINT)
                .define('s', Items.STICK)
                .unlockedBy("has_stick", has(Items.STICK))
                .unlockedBy("has_flint", has(Items.FLINT))
                .save(writer);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, FlintTools.SHIELD.get())
                .pattern(" f ")
                .pattern("fLf")
                .pattern(" f ")
                .define('f', Items.FLINT)
                .define('L', ItemTags.LOGS)
                .unlockedBy("has_flint", has(Items.FLINT))
                .save(writer);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, BoneClub.BONE_CLUB.get())
                .pattern("  b")
                .pattern(" b ")
                .pattern("b  ")
                .define('b', Items.BONE)
                .unlockedBy("has_bone", has(Items.BONE))
                .save(writer);

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

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Iridium.BLOCK_ITEM.get(), 1)
                .requires(Iridium.INGOT.get(), 9)
                .unlockedBy("has_ingot", has(Iridium.INGOT.get()))
                .save(writer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Iridium.INGOT.get(), 9)
                .requires(Iridium.BLOCK_ITEM.get(), 1)
                .unlockedBy("has_ingot", has(Iridium.INGOT.get()))
                .save(writer, "iridium_ingot_from_block");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Iridium.INGOT.get(), 1)
                .requires(Iridium.NUGGET.get(), 9)
                .unlockedBy("has_nuggets", has(Iridium.NUGGET.get()))
                .save(writer, "iridium_ingot_from_nuggets");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Iridium.NUGGET.get(), 9)
                .requires(Iridium.INGOT.get(), 1)
                .unlockedBy("has_ingot", has(Iridium.INGOT.get()))
                .save(writer);

        UpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_AXE), Ingredient.of(Iridium.INGOT.get()), RecipeCategory.TOOLS, Iridium.AXE.get())
                .unlocks("has_iridium", has(Iridium.INGOT.get()))
                .save(writer, "iridium_axe");
        UpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_PICKAXE), Ingredient.of(Iridium.INGOT.get()), RecipeCategory.TOOLS, Iridium.PICKAXE.get())
                .unlocks("has_iridium", has(Iridium.INGOT.get()))
                .save(writer, "iridium_pickaxe");
        UpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_SHOVEL), Ingredient.of(Iridium.INGOT.get()), RecipeCategory.TOOLS, Iridium.SHOVEL.get())
                .unlocks("has_iridium", has(Iridium.INGOT.get()))
                .save(writer, "iridium_shovel");
        UpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_HOE), Ingredient.of(Iridium.INGOT.get()), RecipeCategory.TOOLS, Iridium.HOE.get())
                .unlocks("has_iridium", has(Iridium.INGOT.get()))
                .save(writer, "iridium_how");
        UpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_SWORD), Ingredient.of(Iridium.INGOT.get()), RecipeCategory.COMBAT, Iridium.SWORD.get())
                .unlocks("has_iridium", has(Iridium.INGOT.get()))
                .save(writer, "iridium_sword");
        /*UpgradeRecipeBuilder.smithing(Ingredient.of(Items.SHIELD), Ingredient.of(Iridium.INGOT.get()), RecipeCategory.COMBAT, Iridium.SHIELD.get())
                .unlocks("has_iridium", has(Iridium.INGOT.get()))
                .save(writer, "iridium_shield");*/
        UpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_HELMET), Ingredient.of(Iridium.INGOT.get()), RecipeCategory.COMBAT, Iridium.HELMET.get())
                .unlocks("has_iridium", has(Iridium.INGOT.get()))
                .save(writer, "iridium_helmet");
        UpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_CHESTPLATE), Ingredient.of(Iridium.INGOT.get()), RecipeCategory.COMBAT, Iridium.CHESTPLATE.get())
                .unlocks("has_iridium", has(Iridium.INGOT.get()))
                .save(writer, "iridium_chestplate");
        UpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_LEGGINGS), Ingredient.of(Iridium.INGOT.get()), RecipeCategory.COMBAT, Iridium.LEGGINGS.get())
                .unlocks("has_iridium", has(Iridium.INGOT.get()))
                .save(writer, "iridium_leggings");
        UpgradeRecipeBuilder.smithing(Ingredient.of(Items.IRON_BOOTS), Ingredient.of(Iridium.INGOT.get()), RecipeCategory.COMBAT, Iridium.BOOTS.get())
                .unlocks("has_iridium", has(Iridium.INGOT.get()))
                .save(writer, "iridium_boots");
    }


}
