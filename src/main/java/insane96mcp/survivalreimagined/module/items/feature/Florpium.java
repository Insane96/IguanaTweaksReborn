package insane96mcp.survivalreimagined.module.items.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.item.ILItemTier;
import insane96mcp.shieldsplus.world.item.SPShieldItem;
import insane96mcp.shieldsplus.world.item.SPShieldMaterial;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.items.utils.SRArmorMaterial;
import insane96mcp.survivalreimagined.setup.SRBlocks;
import insane96mcp.survivalreimagined.setup.SRItems;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;

@Label(name = "Florpium", description = "Add Irirum, a new ore scattered everywhere in the Overworld in small quantities and can be used to upgrade Iron Equipment")
@LoadFeature(module = Modules.Ids.ITEMS)
public class Florpium extends Feature {

	public static final RegistryObject<Block> BLOCK = SRBlocks.REGISTRY.register("florpium_block", () -> new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 7.0F).sound(SoundType.METAL)));
	public static final RegistryObject<Block> ORE = SRBlocks.REGISTRY.register("florpium_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), UniformInt.of(2, 5)));
	public static final RegistryObject<Block> DEEPSLATE_ORE = SRBlocks.REGISTRY.register("deepslate_florpium_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(ORE.get()).color(MaterialColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE), UniformInt.of(2, 5)));

	public static final RegistryObject<Item> INGOT = SRItems.REGISTRY.register("florpium_ingot", () -> new Item(new Item.Properties().rarity(Rarity.COMMON)));

	public static final RegistryObject<Item> NUGGET = SRItems.REGISTRY.register("florpium_nugget", () -> new Item(new Item.Properties().rarity(Rarity.COMMON)));

	public static final RegistryObject<Item> BLOCK_ITEM = SRItems.REGISTRY.register("florpium_block", () -> new BlockItem(BLOCK.get(), new Item.Properties().rarity(Rarity.COMMON)));
	public static final RegistryObject<Item> ORE_ITEM = SRItems.REGISTRY.register("florpium_ore", () -> new BlockItem(ORE.get(), new Item.Properties().rarity(Rarity.COMMON)));
	public static final RegistryObject<Item> DEEPSLATE_ORE_ITEM = SRItems.REGISTRY.register("deepslate_florpium_ore", () -> new BlockItem(DEEPSLATE_ORE.get(), new Item.Properties().rarity(Rarity.COMMON)));

	private static final ILItemTier ITEM_TIER = new ILItemTier(2, 585, 6.5f, 2.5f, 12, () -> Ingredient.of(INGOT.get()));

	public static final RegistryObject<Item> SWORD = SRItems.REGISTRY.register("florpium_sword", () -> new SwordItem(ITEM_TIER, 3, -2.4F, new Item.Properties()));
	public static final RegistryObject<Item> SHOVEL = SRItems.REGISTRY.register("florpium_shovel", () -> new ShovelItem(ITEM_TIER, 1.5F, -3.0F, new Item.Properties()));
	public static final RegistryObject<Item> PICKAXE = SRItems.REGISTRY.register("florpium_pickaxe", () -> new PickaxeItem(ITEM_TIER, 1, -2.8F, new Item.Properties()));
	public static final RegistryObject<Item> AXE = SRItems.REGISTRY.register("florpium_axe", () -> new AxeItem(ITEM_TIER, 6.0F, -3.2F, new Item.Properties()));
	public static final RegistryObject<Item> HOE = SRItems.REGISTRY.register("florpium_hoe", () -> new HoeItem(ITEM_TIER, -2, -1.0F, new Item.Properties()));

	private static final SRArmorMaterial ARMOR_MATERIAL = new SRArmorMaterial("survivalreimagined:florpium", 20, Util.make(new EnumMap<>(ArmorItem.Type.class), (p_266652_) -> {
		p_266652_.put(ArmorItem.Type.BOOTS, 3);
		p_266652_.put(ArmorItem.Type.LEGGINGS, 5);
		p_266652_.put(ArmorItem.Type.CHESTPLATE, 6);
		p_266652_.put(ArmorItem.Type.HELMET, 2);
	}), 6, SoundEvents.ARMOR_EQUIP_IRON, 1f, 0.04f, () -> Ingredient.of(INGOT.get()));

	public static final RegistryObject<Item> HELMET = SRItems.REGISTRY.register("florpium_helmet", () -> new ArmorItem(ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties()));
	public static final RegistryObject<Item> CHESTPLATE = SRItems.REGISTRY.register("florpium_chestplate", () -> new ArmorItem(ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
	public static final RegistryObject<Item> LEGGINGS = SRItems.REGISTRY.register("florpium_leggings", () -> new ArmorItem(ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Properties()));
	public static final RegistryObject<Item> BOOTS = SRItems.REGISTRY.register("florpium_boots", () -> new ArmorItem(ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Properties()));

	public static final SPShieldMaterial SHIELD_MATERIAL = new SPShieldMaterial("florpium", 5.5d, 452, INGOT, 9, Rarity.COMMON);

	public static final RegistryObject<SPShieldItem> SHIELD = SRItems.registerShield("florpium_shield", SHIELD_MATERIAL);

	public Florpium(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}
}