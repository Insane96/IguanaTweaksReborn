package insane96mcp.survivalreimagined.data.generator.client;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.combat.feature.Fletching;
import insane96mcp.survivalreimagined.module.experience.feature.Lapis;
import insane96mcp.survivalreimagined.module.farming.feature.Crops;
import insane96mcp.survivalreimagined.module.hungerhealth.feature.FoodDrinks;
import insane96mcp.survivalreimagined.module.items.feature.ChainedCopperArmor;
import insane96mcp.survivalreimagined.module.items.feature.CopperToolsExtension;
import insane96mcp.survivalreimagined.module.items.feature.FlintExpansion;
import insane96mcp.survivalreimagined.module.items.feature.RecallIdol;
import insane96mcp.survivalreimagined.module.mining.feature.*;
import insane96mcp.survivalreimagined.module.movement.feature.Minecarts;
import insane96mcp.survivalreimagined.module.sleeprespawn.feature.Cloth;
import insane96mcp.survivalreimagined.module.world.feature.CoalFire;
import insane96mcp.survivalreimagined.module.world.feature.CyanFlower;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;

public class SRItemModelsProvider extends ItemModelProvider {
    public SRItemModelsProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    List<String> poorRichOres = List.of("iron", "gold", "copper");

    @Override
    protected void registerModels() {
        for (String poorRichOre : poorRichOres) {
            withExistingParent("poor_%s_ore".formatted(poorRichOre), new ResourceLocation(SurvivalReimagined.MOD_ID, "block/poor_%s_ore".formatted(poorRichOre)));
            withExistingParent("rich_%s_ore".formatted(poorRichOre), new ResourceLocation(SurvivalReimagined.MOD_ID, "block/rich_%s_ore".formatted(poorRichOre)));
            withExistingParent("poor_deepslate_%s_ore".formatted(poorRichOre), new ResourceLocation(SurvivalReimagined.MOD_ID, "block/poor_deepslate_%s_ore".formatted(poorRichOre)));
            withExistingParent("rich_deepslate_%s_ore".formatted(poorRichOre), new ResourceLocation(SurvivalReimagined.MOD_ID, "block/rich_deepslate_%s_ore".formatted(poorRichOre)));
        }

        handHeld(SoulSteel.AXE.get());
        handHeld(SoulSteel.PICKAXE.get());
        handHeld(SoulSteel.SHOVEL.get());
        handHeld(SoulSteel.HOE.get());
        handHeld(SoulSteel.SWORD.get());
        basicItem(SoulSteel.BOOTS.get());
        basicItem(SoulSteel.LEGGINGS.get());
        basicItem(SoulSteel.CHESTPLATE.get());
        basicItem(SoulSteel.HELMET.get());
        basicItem(SoulSteel.INGOT.get());
        basicItem(SoulSteel.NUGGET.get());
        shield(SoulSteel.SHIELD.get());
        withExistingParent("soul_steel_block", new ResourceLocation(SurvivalReimagined.MOD_ID, "block/soul_steel_block"));
        withExistingParent("blast_furnace", new ResourceLocation("block/blast_furnace"));
        withExistingParent("soul_blast_furnace", new ResourceLocation(SurvivalReimagined.MOD_ID, "block/soul_blast_furnace"));

        handHeld(FlintExpansion.AXE.get());
        handHeld(FlintExpansion.PICKAXE.get());
        handHeld(FlintExpansion.SHOVEL.get());
        handHeld(FlintExpansion.HOE.get());
        handHeld(FlintExpansion.SWORD.get());
        shield(FlintExpansion.SHIELD.get());
        withExistingParent("flint_block", new ResourceLocation(SurvivalReimagined.MOD_ID, "block/flint_block"));
        withExistingParent("polished_flint_block", new ResourceLocation(SurvivalReimagined.MOD_ID, "block/polished_flint_block"));

        basicItem(Lapis.CLEANSED_LAPIS.get());
        basicItem(Lapis.ANCIENT_LAPIS.get());

        basicItem(Crops.CARROT_SEEDS.get());
        basicItem(Crops.POTATO_SEEDS.get());

        basicItem(ChainedCopperArmor.BOOTS.get());
        basicItem(ChainedCopperArmor.LEGGINGS.get());
        basicItem(ChainedCopperArmor.CHESTPLATE.get());
        basicItem(ChainedCopperArmor.HELMET.get());

        handHeld(CopperToolsExtension.COPPER_AXE.get());
        handHeld(CopperToolsExtension.COPPER_PICKAXE.get());
        handHeld(CopperToolsExtension.COPPER_SHOVEL.get());
        handHeld(CopperToolsExtension.COPPER_HOE.get());
        handHeld(CopperToolsExtension.COPPER_SWORD.get());

        handHeld(CopperToolsExtension.COATED_AXE.get());
        handHeld(CopperToolsExtension.COATED_PICKAXE.get());
        handHeld(CopperToolsExtension.COATED_SHOVEL.get());
        handHeld(CopperToolsExtension.COATED_HOE.get());
        handHeld(CopperToolsExtension.COATED_SWORD.get());
        shield(CopperToolsExtension.COATED_SHIELD.get());

        withExistingParent("charcoal_layer", new ResourceLocation(SurvivalReimagined.MOD_ID, "block/charcoal_layer/height_2"));

        withExistingParent("crate", new ResourceLocation(SurvivalReimagined.MOD_ID, "block/crate"));

        handHeld(Solarium.AXE.get());
        handHeld(Solarium.PICKAXE.get());
        handHeld(Solarium.SHOVEL.get());
        handHeld(Solarium.HOE.get());
        handHeld(Solarium.SWORD.get());
        basicItem(Solarium.BOOTS.get());
        basicItem(Solarium.LEGGINGS.get());
        basicItem(Solarium.CHESTPLATE.get());
        basicItem(Solarium.HELMET.get());
        basicItem(Solarium.SOLARIUM_BALL.get());
        shield(Solarium.SHIELD.get());

        handHeld(Keego.AXE.get());
        handHeld(Keego.PICKAXE.get());
        handHeld(Keego.SHOVEL.get());
        handHeld(Keego.HOE.get());
        handHeld(Keego.SWORD.get());
        basicItem(Keego.BOOTS.get());
        basicItem(Keego.LEGGINGS.get());
        basicItem(Keego.CHESTPLATE.get());
        basicItem(Keego.HELMET.get());
        basicItem(Keego.SHARD.get());
        basicItem(Keego.GEM.get());
        shield(Keego.SHIELD.get());
        withExistingParent("keego_ore", new ResourceLocation(SurvivalReimagined.MOD_ID, "block/keego_ore"));
        withExistingParent("keego_block", new ResourceLocation(SurvivalReimagined.MOD_ID, "block/keego_block"));

        handHeld(Durium.AXE.get());
        handHeld(Durium.PICKAXE.get());
        handHeld(Durium.SHOVEL.get());
        handHeld(Durium.HOE.get());
        handHeld(Durium.SWORD.get());
        basicItem(Durium.BOOTS.get());
        basicItem(Durium.LEGGINGS.get());
        basicItem(Durium.CHESTPLATE.get());
        basicItem(Durium.HELMET.get());
        basicItem(Durium.INGOT.get());
        basicItem(Durium.NUGGET.get());
        basicItem(Durium.SCRAP_PIECE.get());
        shield(Durium.SHIELD.get());
        withExistingParent("durium_block", new ResourceLocation(SurvivalReimagined.MOD_ID, "block/durium_block"));
        withExistingParent("deepslate_durium_ore", new ResourceLocation(SurvivalReimagined.MOD_ID, "block/deepslate_durium_ore"));
        withExistingParent("durium_ore", new ResourceLocation(SurvivalReimagined.MOD_ID, "block/durium_ore"));
        withExistingParent("durium_scrap_block", new ResourceLocation(SurvivalReimagined.MOD_ID, "block/durium_scrap_block"));

        basicItemWithTexture(Minecarts.COPPER_POWERED_RAIL.item().get(), new ResourceLocation(SurvivalReimagined.MOD_ID, "block/copper_powered_rail"));
        withExistingParent("golden_powered_rail", new ResourceLocation("item/powered_rail"));
        basicItemWithTexture(Minecarts.NETHER_INFUSED_POWERED_RAIL.item().get(), new ResourceLocation(SurvivalReimagined.MOD_ID, "block/nether_infused_powered_rail"));

        basicItem(FoodDrinks.OVER_EASY_EGG.get());
        basicItem(FoodDrinks.BROWN_MUSHROOM_STEW.get());
        basicItem(FoodDrinks.RED_MUSHROOM_STEW.get());
        basicItem(FoodDrinks.PUMPKIN_PULP.get());

        withExistingParent("explosive_barrel", new ResourceLocation(SurvivalReimagined.MOD_ID, "block/explosive_barrel"));

        withExistingParent("respawn_obelisk", new ResourceLocation(SurvivalReimagined.MOD_ID, "block/respawn_obelisk_disabled"));

        withExistingParent("rich_farmland", new ResourceLocation(SurvivalReimagined.MOD_ID, "block/rich_farmland"));

        basicItem(CoalFire.FIRESTARTER.get());
        basicItem(CoalFire.HELLISH_COAL.get());
        withExistingParent("soul_sand_hellish_coal_ore", new ResourceLocation(SurvivalReimagined.MOD_ID, "block/soul_sand_hellish_coal_ore"));
        withExistingParent("soul_soil_hellish_coal_ore", new ResourceLocation(SurvivalReimagined.MOD_ID, "block/soul_soil_hellish_coal_ore"));

        handHeld(Forging.STONE_HAMMER.get());
        handHeld(Forging.FLINT_HAMMER.get());
        handHeld(Forging.COPPER_HAMMER.get());
        handHeld(Forging.GOLDEN_HAMMER.get());
        handHeld(Forging.IRON_HAMMER.get());
        handHeld(Forging.SOLARIUM_HAMMER.get());
        handHeld(Forging.DURIUM_HAMMER.get());
        handHeld(Forging.COATED_COPPER_HAMMER.get());
        handHeld(Forging.DIAMOND_HAMMER.get());
        handHeld(Forging.SOUL_STEEL_HAMMER.get());
        handHeld(Forging.NETHERITE_HAMMER.get());
        handHeld(Forging.KEEGO_HAMMER.get());

        basicItem(Cloth.CLOTH.get());

        basicItem(Fletching.QUARTZ_ARROW_ITEM.get());
        basicItem(Fletching.DIAMOND_ARROW_ITEM.get());
        basicItem(Fletching.EXPLOSIVE_ARROW_ITEM.get());
        basicItem(Fletching.TORCH_ARROW_ITEM.get());

        basicItem(RecallIdol.ITEM.get());

        basicItemWithTexture(CyanFlower.FLOWER.item().get(), new ResourceLocation(SurvivalReimagined.MOD_ID, "block/cyan_flower"));
    }

    private ItemModelBuilder handHeld(Item item) {
        return handHeld(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)));
    }

    private ItemModelBuilder handHeld(ResourceLocation item) {
        return getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", new ResourceLocation(item.getNamespace(), "item/" + item.getPath()));
    }

    private ItemModelBuilder shield(Item item) {
        return shield(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)));
    }

    private ItemModelBuilder shield(ResourceLocation item) {
        return getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("shieldsplus:item/wooden_shield"))
                .override().predicate(new ResourceLocation("blocking"), 1)
                .model(new ModelFile.UncheckedModelFile("shieldsplus:item/wooden_shield_blocking"))
                .end();
    }

    public ItemModelBuilder basicItemWithTexture(Item item, ResourceLocation texture)
    {
        return basicItemWithTexture(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)), texture);
    }

    public ItemModelBuilder basicItemWithTexture(ResourceLocation item, ResourceLocation texture)
    {
        return getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", texture);
    }
}
