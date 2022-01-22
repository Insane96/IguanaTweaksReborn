package insane96mcp.iguanatweaksreborn.module.movement.utils;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;

public class Armor {
	public static double getTotalDamageReduction(ArmorMaterial material) {
		double total = material.getDefenseForSlot(EquipmentSlot.HEAD);
		total += material.getDefenseForSlot(EquipmentSlot.CHEST);
		total += material.getDefenseForSlot(EquipmentSlot.LEGS);
		total += material.getDefenseForSlot(EquipmentSlot.FEET);
		return total;
	}
}
