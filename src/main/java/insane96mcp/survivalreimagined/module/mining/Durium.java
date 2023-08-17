package insane96mcp.survivalreimagined.module.mining;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.item.ILItemTier;
import insane96mcp.shieldsplus.world.item.SPShieldItem;
import insane96mcp.shieldsplus.world.item.SPShieldMaterial;
import insane96mcp.survivalreimagined.base.SimpleBlockWithItem;
import insane96mcp.survivalreimagined.item.SRArmorMaterial;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.DataPacks;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import insane96mcp.survivalreimagined.setup.SRItems;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;

@Label(name = "Durium", description = "Add Durium, a new metal made by alloying Durium Scrap (found as scrap pieces in ores in the Overworld) and can be used to upgrade Iron Equipment")
@LoadFeature(module = Modules.Ids.MINING)
public class Durium extends Feature {

	public static final SimpleBlockWithItem ORE = SimpleBlockWithItem.register("durium_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F), UniformInt.of(2, 4)));
	public static final SimpleBlockWithItem DEEPSLATE_ORE = SimpleBlockWithItem.register("deepslate_durium_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(ORE.block().get()).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE), UniformInt.of(2, 4)));
	public static final SimpleBlockWithItem SCRAP_BLOCK = SimpleBlockWithItem.register("durium_scrap_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 7.0F).sound(SoundType.METAL)));
	public static final RegistryObject<Item> SCRAP_PIECE = SRItems.REGISTRY.register("durium_scrap_piece", () -> new Item(new Item.Properties()));

	public static final SimpleBlockWithItem BLOCK = SimpleBlockWithItem.register("durium_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 7.0F).sound(SoundType.METAL)));

	public static final RegistryObject<Item> INGOT = SRItems.REGISTRY.register("durium_ingot", () -> new Item(new Item.Properties()));

	public static final RegistryObject<Item> NUGGET = SRItems.REGISTRY.register("durium_nugget", () -> new Item(new Item.Properties()));

	public static final ILItemTier ITEM_TIER = new ILItemTier(2, 570, 6.5f, 2f, 11, () -> Ingredient.of(INGOT.get()));

	public static final RegistryObject<Item> SWORD = SRItems.REGISTRY.register("durium_sword", () -> new SwordItem(ITEM_TIER, 3, -2.4F, new Item.Properties()));
	public static final RegistryObject<Item> SHOVEL = SRItems.REGISTRY.register("durium_shovel", () -> new ShovelItem(ITEM_TIER, 1.5F, -3.0F, new Item.Properties()));
	public static final RegistryObject<Item> PICKAXE = SRItems.REGISTRY.register("durium_pickaxe", () -> new PickaxeItem(ITEM_TIER, 1, -2.8F, new Item.Properties()));
	public static final RegistryObject<Item> AXE = SRItems.REGISTRY.register("durium_axe", () -> new AxeItem(ITEM_TIER, 6.0F, -3.2F, new Item.Properties()));
	public static final RegistryObject<Item> HOE = SRItems.REGISTRY.register("durium_hoe", () -> new HoeItem(ITEM_TIER, -2, -1.0F, new Item.Properties()));

	private static final SRArmorMaterial ARMOR_MATERIAL = new SRArmorMaterial("survivalreimagined:durium", 20, Util.make(new EnumMap<>(ArmorItem.Type.class), (p_266652_) -> {
		p_266652_.put(ArmorItem.Type.BOOTS, 3);
		p_266652_.put(ArmorItem.Type.LEGGINGS, 5);
		p_266652_.put(ArmorItem.Type.CHESTPLATE, 6);
		p_266652_.put(ArmorItem.Type.HELMET, 2);
	}), 6, SoundEvents.ARMOR_EQUIP_IRON, 1f, 0.03f, () -> Ingredient.of(INGOT.get()));

	public static final RegistryObject<Item> HELMET = SRItems.REGISTRY.register("durium_helmet", () -> new ArmorItem(ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties()));
	public static final RegistryObject<Item> CHESTPLATE = SRItems.REGISTRY.register("durium_chestplate", () -> new ArmorItem(ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
	public static final RegistryObject<Item> LEGGINGS = SRItems.REGISTRY.register("durium_leggings", () -> new ArmorItem(ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Properties()));
	public static final RegistryObject<Item> BOOTS = SRItems.REGISTRY.register("durium_boots", () -> new ArmorItem(ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Properties()));

	public static final SPShieldMaterial SHIELD_MATERIAL = new SPShieldMaterial("durium", 5.5d, 452, INGOT, 9, Rarity.COMMON);

	public static final RegistryObject<SPShieldItem> SHIELD = SRItems.registerShield("durium_shield", SHIELD_MATERIAL);

	@Config
	@Label(name = "Durium Lodestone", description = "Enables a data pack that makes Lodestone require Durium instead of Netherite.")
	public static Boolean duriumLodestone = true;

	public Durium(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "durium_lodestone", Component.literal("Survival Reimagined Durium Lodestone"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && duriumLodestone));
	}
}