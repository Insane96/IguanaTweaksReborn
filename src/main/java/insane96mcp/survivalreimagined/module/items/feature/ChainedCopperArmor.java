package insane96mcp.survivalreimagined.module.items.feature;

import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.items.utils.SRArmorMaterial;
import insane96mcp.survivalreimagined.setup.SRItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Chained Copper Armor", description = "Add Chained Copper Armor")
@LoadFeature(module = Modules.Ids.ITEMS)
public class ChainedCopperArmor extends SRFeature {

	private static final SRArmorMaterial CHAINED_COPPER = new SRArmorMaterial("survivalreimagined:chained_copper", 10, new int[] {1, 3, 4, 1}, 13, SoundEvents.ARMOR_EQUIP_CHAIN, 0f, 0f, () -> Ingredient.of(Items.COPPER_INGOT));

	public static final RegistryObject<Item> HELMET = SRItems.ITEMS.register("chained_copper_helmet", () -> new ArmorItem(CHAINED_COPPER, EquipmentSlot.HEAD, new Item.Properties()));
	public static final RegistryObject<Item> CHESTPLATE = SRItems.ITEMS.register("chained_copper_chestplate", () -> new ArmorItem(CHAINED_COPPER, EquipmentSlot.CHEST, new Item.Properties()));
	public static final RegistryObject<Item> LEGGINGS = SRItems.ITEMS.register("chained_copper_leggings", () -> new ArmorItem(CHAINED_COPPER, EquipmentSlot.LEGS, new Item.Properties()));
	public static final RegistryObject<Item> BOOTS = SRItems.ITEMS.register("chained_copper_boots", () -> new ArmorItem(CHAINED_COPPER, EquipmentSlot.FEET, new Item.Properties()));


	public ChainedCopperArmor(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}
}