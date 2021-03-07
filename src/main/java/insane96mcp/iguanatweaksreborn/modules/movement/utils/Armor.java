package insane96mcp.iguanatweaksreborn.modules.movement.utils;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;

public class Armor {
	public static double getTotalDamageReduction(IArmorMaterial material) {
		double total = material.getDamageReductionAmount(EquipmentSlotType.HEAD);
		total += material.getDamageReductionAmount(EquipmentSlotType.CHEST);
		total += material.getDamageReductionAmount(EquipmentSlotType.LEGS);
		total += material.getDamageReductionAmount(EquipmentSlotType.FEET);
		return total;
	}
}
