package insane96mcp.survivalreimagined.module.mining;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.item.ILItemTier;
import insane96mcp.shieldsplus.world.item.SPShieldItem;
import insane96mcp.shieldsplus.world.item.SPShieldMaterial;
import insane96mcp.survivalreimagined.base.SimpleBlockWithItem;
import insane96mcp.survivalreimagined.item.SRArmorMaterial;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.setup.SRRegistries;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;

@Label(name = "Soul Steel", description = "Add Soul Steel, a new metal made by alloying Iron, Soul Sand and Hellish Coal")
@LoadFeature(module = Modules.Ids.MINING)
public class SoulSteel extends Feature {
	public static final SimpleBlockWithItem BLOCK = SimpleBlockWithItem.register("soul_steel_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 9.0F).sound(SoundType.METAL)), new Item.Properties().fireResistant());

	public static final RegistryObject<Item> INGOT = SRRegistries.ITEMS.register("soul_steel_ingot", () -> new Item(new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> NUGGET = SRRegistries.ITEMS.register("soul_steel_nugget", () -> new Item(new Item.Properties().fireResistant()));

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
}