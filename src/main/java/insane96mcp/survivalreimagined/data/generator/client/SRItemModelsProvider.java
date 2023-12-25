package insane96mcp.survivalreimagined.data.generator.client;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.combat.fletching.Fletching;
import insane96mcp.survivalreimagined.module.experience.Lapis;
import insane96mcp.survivalreimagined.module.farming.crops.Crops;
import insane96mcp.survivalreimagined.module.hungerhealth.fooddrinks.FoodDrinks;
import insane96mcp.survivalreimagined.module.items.ChainedCopperArmor;
import insane96mcp.survivalreimagined.module.items.copper.CopperToolsExpansion;
import insane96mcp.survivalreimagined.module.items.flintexpansion.FlintExpansion;
import insane96mcp.survivalreimagined.module.items.recallidol.RecallIdol;
import insane96mcp.survivalreimagined.module.items.solarium.Solarium;
import insane96mcp.survivalreimagined.module.mining.Durium;
import insane96mcp.survivalreimagined.module.mining.SoulSteel;
import insane96mcp.survivalreimagined.module.mining.forging.Forging;
import insane96mcp.survivalreimagined.module.mining.keego.Keego;
import insane96mcp.survivalreimagined.module.movement.minecarts.Minecarts;
import insane96mcp.survivalreimagined.module.sleeprespawn.Cloth;
import insane96mcp.survivalreimagined.module.world.CyanFlower;
import insane96mcp.survivalreimagined.module.world.coalfire.CoalFire;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class SRItemModelsProvider extends ItemModelProvider {
    private static LinkedHashMap<ResourceKey<TrimMaterial>, Float> trimMaterials = new LinkedHashMap<>();
    static {
        trimMaterials.put(TrimMaterials.QUARTZ, 0.1F);
        trimMaterials.put(TrimMaterials.IRON, 0.2F);
        trimMaterials.put(TrimMaterials.NETHERITE, 0.3F);
        trimMaterials.put(TrimMaterials.REDSTONE, 0.4F);
        trimMaterials.put(TrimMaterials.COPPER, 0.5F);
        trimMaterials.put(TrimMaterials.GOLD, 0.6F);
        trimMaterials.put(TrimMaterials.EMERALD, 0.7F);
        trimMaterials.put(TrimMaterials.DIAMOND, 0.8F);
        trimMaterials.put(TrimMaterials.LAPIS, 0.9F);
        trimMaterials.put(TrimMaterials.AMETHYST, 1.0F);
    }

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
        trimmedArmorItem(SoulSteel.BOOTS);
        trimmedArmorItem(SoulSteel.LEGGINGS);
        trimmedArmorItem(SoulSteel.CHESTPLATE);
        trimmedArmorItem(SoulSteel.HELMET);
        basicItem(SoulSteel.INGOT.get());
        basicItem(SoulSteel.NUGGET.get());
        basicItem(SoulSteel.UPGRADE_SMITHING_TEMPLATE.get());
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
        basicItem(Crops.ROOTED_POTATO.get());

        trimmedArmorItem(ChainedCopperArmor.BOOTS);
        trimmedArmorItem(ChainedCopperArmor.LEGGINGS);
        trimmedArmorItem(ChainedCopperArmor.CHESTPLATE);
        trimmedArmorItem(ChainedCopperArmor.HELMET);

        handHeld(CopperToolsExpansion.COPPER_AXE.get());
        handHeld(CopperToolsExpansion.COPPER_PICKAXE.get());
        handHeld(CopperToolsExpansion.COPPER_SHOVEL.get());
        handHeld(CopperToolsExpansion.COPPER_HOE.get());
        handHeld(CopperToolsExpansion.COPPER_SWORD.get());

        handHeld(CopperToolsExpansion.COATED_AXE.get());
        handHeld(CopperToolsExpansion.COATED_PICKAXE.get());
        handHeld(CopperToolsExpansion.COATED_SHOVEL.get());
        handHeld(CopperToolsExpansion.COATED_HOE.get());
        handHeld(CopperToolsExpansion.COATED_SWORD.get());
        shield(CopperToolsExpansion.COATED_SHIELD.get());

        withExistingParent("charcoal_layer", new ResourceLocation(SurvivalReimagined.MOD_ID, "block/charcoal_layer/height_2"));

        withExistingParent("crate", new ResourceLocation(SurvivalReimagined.MOD_ID, "block/crate"));

        handHeld(Solarium.AXE.get());
        handHeld(Solarium.PICKAXE.get());
        handHeld(Solarium.SHOVEL.get());
        handHeld(Solarium.HOE.get());
        handHeld(Solarium.SWORD.get());
        trimmedArmorItem(Solarium.BOOTS);
        trimmedArmorItem(Solarium.LEGGINGS);
        trimmedArmorItem(Solarium.CHESTPLATE);
        trimmedArmorItem(Solarium.HELMET);
        basicItem(Solarium.SOLARIUM_BALL.get());
        shield(Solarium.SHIELD.get());

        handHeld(Keego.AXE.get());
        handHeld(Keego.PICKAXE.get());
        handHeld(Keego.SHOVEL.get());
        handHeld(Keego.HOE.get());
        handHeld(Keego.SWORD.get());
        trimmedArmorItem(Keego.BOOTS);
        trimmedArmorItem(Keego.LEGGINGS);
        trimmedArmorItem(Keego.CHESTPLATE);
        trimmedArmorItem(Keego.HELMET);
        basicItem(Keego.GEM.get());
        shield(Keego.SHIELD.get());
        withExistingParent("keego_ore", new ResourceLocation(SurvivalReimagined.MOD_ID, "block/keego_ore"));
        withExistingParent("keego_block", new ResourceLocation(SurvivalReimagined.MOD_ID, "block/keego_block"));

        handHeld(Durium.AXE.get());
        handHeld(Durium.PICKAXE.get());
        handHeld(Durium.SHOVEL.get());
        handHeld(Durium.HOE.get());
        handHeld(Durium.SWORD.get());
        trimmedArmorItem(Durium.BOOTS);
        trimmedArmorItem(Durium.LEGGINGS);
        trimmedArmorItem(Durium.CHESTPLATE);
        trimmedArmorItem(Durium.HELMET);
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

        handHeld(Forging.WOODEN_HAMMER.get());
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

    // Shoutout to El_Redstoniano for making this
    private void trimmedArmorItem(RegistryObject<Item> itemRegistryObject) {
        final String MOD_ID = SurvivalReimagined.MOD_ID; // Change this to your mod id

        if (itemRegistryObject.get() instanceof ArmorItem armorItem) {
            trimMaterials.entrySet().forEach(entry -> {

                ResourceKey<TrimMaterial> trimMaterial = entry.getKey();
                float trimValue = entry.getValue();

                String armorType = switch (armorItem.getEquipmentSlot()) {
                    case HEAD -> "helmet";
                    case CHEST -> "chestplate";
                    case LEGS -> "leggings";
                    case FEET -> "boots";
                    default -> "";
                };

                String armorItemPath = "item/" + armorItem;
                String trimPath = "trims/items/" + armorType + "_trim_" + trimMaterial.location().getPath();
                String currentTrimName = armorItemPath + "_" + trimMaterial.location().getPath() + "_trim";
                ResourceLocation armorItemResLoc = new ResourceLocation(MOD_ID, armorItemPath);
                ResourceLocation trimResLoc = new ResourceLocation(trimPath); // minecraft namespace
                ResourceLocation trimNameResLoc = new ResourceLocation(MOD_ID, currentTrimName);

                // This is used for making the ExistingFileHelper acknowledge that this texture exist, so this will
                // avoid an IllegalArgumentException
                existingFileHelper.trackGenerated(trimResLoc, PackType.CLIENT_RESOURCES, ".png", "textures");

                // Trimmed armorItem files
                getBuilder(currentTrimName)
                        .parent(new ModelFile.UncheckedModelFile("item/generated"))
                        .texture("layer0", armorItemResLoc)
                        .texture("layer1", trimResLoc);

                // Non-trimmed armorItem file (normal variant)
                this.withExistingParent(itemRegistryObject.getId().getPath(),
                                mcLoc("item/generated"))
                        .override()
                        .model(new ModelFile.UncheckedModelFile(trimNameResLoc))
                        .predicate(mcLoc("trim_type"), trimValue).end()
                        .texture("layer0",
                                new ResourceLocation(MOD_ID,
                                        "item/" + itemRegistryObject.getId().getPath()));
            });
        }
    }
}
