package insane96mcp.survivalreimagined.module.mining;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.item.ILItemTier;
import insane96mcp.shieldsplus.world.item.SPShieldItem;
import insane96mcp.shieldsplus.world.item.SPShieldMaterial;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.base.SimpleBlockWithItem;
import insane96mcp.survivalreimagined.data.lootmodifier.InjectLootTableModifier;
import insane96mcp.survivalreimagined.item.SRArmorMaterial;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.setup.SRRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.List;

@Label(name = "Soul Steel", description = "Add Soul Steel, a new metal made by alloying Iron, Soul Sand and Hellish Coal")
@LoadFeature(module = Modules.Ids.MINING)
public class SoulSteel extends Feature {
	public static final SimpleBlockWithItem BLOCK = SimpleBlockWithItem.register("soul_steel_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK)), new Item.Properties().fireResistant());

	public static final RegistryObject<Item> INGOT = SRRegistries.ITEMS.register("soul_steel_ingot", () -> new Item(new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> NUGGET = SRRegistries.ITEMS.register("soul_steel_nugget", () -> new Item(new Item.Properties().fireResistant()));

	private static final ChatFormatting TITLE_FORMAT = ChatFormatting.GRAY;
	private static final ChatFormatting DESCRIPTION_FORMAT = ChatFormatting.BLUE;
	private static final Component UPGRADE = Component.translatable(Util.makeDescriptionId("upgrade", new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "soul_steel_upgrade"))).withStyle(TITLE_FORMAT);
	private static final Component UPGRADE_APPLIES_TO = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "smithing_template.soul_steel_upgrade.applies_to"))).withStyle(DESCRIPTION_FORMAT);
	private static final Component UPGRADE_INGREDIENTS = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "smithing_template.soul_steel_upgrade.ingredients"))).withStyle(DESCRIPTION_FORMAT);
	private static final Component UPGRADE_BASE_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "smithing_template.soul_steel_upgrade.base_slot_description")));
	private static final Component UPGRADE_ADDITIONS_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "smithing_template.soul_steel_upgrade.additions_slot_description")));
	private static final ResourceLocation EMPTY_SLOT_HELMET = new ResourceLocation("item/empty_armor_slot_helmet");
	private static final ResourceLocation EMPTY_SLOT_CHESTPLATE = new ResourceLocation("item/empty_armor_slot_chestplate");
	private static final ResourceLocation EMPTY_SLOT_LEGGINGS = new ResourceLocation("item/empty_armor_slot_leggings");
	private static final ResourceLocation EMPTY_SLOT_BOOTS = new ResourceLocation("item/empty_armor_slot_boots");
	private static final ResourceLocation EMPTY_SLOT_HOE = new ResourceLocation("item/empty_slot_hoe");
	private static final ResourceLocation EMPTY_SLOT_AXE = new ResourceLocation("item/empty_slot_axe");
	private static final ResourceLocation EMPTY_SLOT_SWORD = new ResourceLocation("item/empty_slot_sword");
	private static final ResourceLocation EMPTY_SLOT_SHOVEL = new ResourceLocation("item/empty_slot_shovel");
	private static final ResourceLocation EMPTY_SLOT_PICKAXE = new ResourceLocation("item/empty_slot_pickaxe");
	private static final ResourceLocation EMPTY_SLOT_INGOT = new ResourceLocation("item/empty_slot_ingot");
	public static final RegistryObject<Item> UPGRADE_SMITHING_TEMPLATE = SRRegistries.ITEMS.register("soul_steel_upgrade_smithing_template", () -> new SmithingTemplateItem(UPGRADE_APPLIES_TO, UPGRADE_INGREDIENTS, UPGRADE, UPGRADE_BASE_SLOT_DESCRIPTION, UPGRADE_ADDITIONS_SLOT_DESCRIPTION, createUpgradeIconList(), createUpgradeMaterialList()));

    public static final ILItemTier ITEM_TIER = new ILItemTier(4, 2356, 8f, 3.0f, 17, () -> Ingredient.of(INGOT.get()));

	public static final RegistryObject<Item> SWORD = SRRegistries.ITEMS.register("soul_steel_sword", () -> new SwordItem(ITEM_TIER, 3, -2.4F, new Item.Properties().fireResistant()));
	public static final RegistryObject<Item> SHOVEL = SRRegistries.ITEMS.register("soul_steel_shovel", () -> new ShovelItem(ITEM_TIER, 1.5F, -3.0F, new Item.Properties().fireResistant()));
	public static final RegistryObject<Item> PICKAXE = SRRegistries.ITEMS.register("soul_steel_pickaxe", () -> new PickaxeItem(ITEM_TIER, 1, -2.8F, new Item.Properties().fireResistant()));
	public static final RegistryObject<Item> AXE = SRRegistries.ITEMS.register("soul_steel_axe", () -> new AxeItem(ITEM_TIER, 5.0F, -3.1F, new Item.Properties().fireResistant()));
	public static final RegistryObject<Item> HOE = SRRegistries.ITEMS.register("soul_steel_hoe", () -> new HoeItem(ITEM_TIER, -2, -1.0F, new Item.Properties().fireResistant()));

	private static final SRArmorMaterial ARMOR_MATERIAL = new SRArmorMaterial("survivalreimagined:soul_steel", 35, Util.make(new EnumMap<>(ArmorItem.Type.class), (p_266652_) -> {
		p_266652_.put(ArmorItem.Type.BOOTS, 4);
		p_266652_.put(ArmorItem.Type.LEGGINGS, 5);
		p_266652_.put(ArmorItem.Type.CHESTPLATE, 6);
		p_266652_.put(ArmorItem.Type.HELMET, 3);
	}), 18, SoundEvents.ARMOR_EQUIP_IRON, 1f, 0.05f, () -> Ingredient.of(INGOT.get()));

	public static final RegistryObject<Item> HELMET = SRRegistries.ITEMS.register("soul_steel_helmet", () -> new ArmorItem(ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties().fireResistant()));
	public static final RegistryObject<Item> CHESTPLATE = SRRegistries.ITEMS.register("soul_steel_chestplate", () -> new ArmorItem(ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties().fireResistant()));
	public static final RegistryObject<Item> LEGGINGS = SRRegistries.ITEMS.register("soul_steel_leggings", () -> new ArmorItem(ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Properties().fireResistant()));
	public static final RegistryObject<Item> BOOTS = SRRegistries.ITEMS.register("soul_steel_boots", () -> new ArmorItem(ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Properties().fireResistant()));

	public static final SPShieldMaterial SHIELD_MATERIAL = new SPShieldMaterial("soul_steel", 6.5d, 756, INGOT, 3, Rarity.COMMON);

	public static final RegistryObject<SPShieldItem> SHIELD = SRRegistries.registerShield("soul_steel_shield", SHIELD_MATERIAL, true);

	public SoulSteel(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	private static List<ResourceLocation> createUpgradeIconList() {
		return List.of(EMPTY_SLOT_HELMET, EMPTY_SLOT_SWORD, EMPTY_SLOT_CHESTPLATE, EMPTY_SLOT_PICKAXE, EMPTY_SLOT_LEGGINGS, EMPTY_SLOT_AXE, EMPTY_SLOT_BOOTS, EMPTY_SLOT_HOE, EMPTY_SLOT_SHOVEL);
	}

	private static List<ResourceLocation> createUpgradeMaterialList() {
		return List.of(EMPTY_SLOT_INGOT);
	}

	private static final String path = "mining/soul_steel/";

	public static void addGlobalLoot(GlobalLootModifierProvider provider) {
		provider.add(path + "chests/upgrade_template_in_fortress", new InjectLootTableModifier(new ResourceLocation("minecraft:chests/nether_bridge"), new ResourceLocation("survivalreimagined:chests/soul_steel_upgrade_template")));
	}
}