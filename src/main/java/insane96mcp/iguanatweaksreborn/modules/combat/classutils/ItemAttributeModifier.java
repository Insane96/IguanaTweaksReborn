package insane96mcp.iguanatweaksreborn.modules.combat.classutils;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;

public class ItemAttributeModifier {
	public ResourceLocation itemId;
	public EquipmentSlotType slot;
	public Attribute attribute;
	public double amount;
	public AttributeModifier.Operation operation;

	public ItemAttributeModifier(ResourceLocation itemId, EquipmentSlotType slot, Attribute attribute, double amount, AttributeModifier.Operation operation) {
		this.itemId = itemId;
		this.slot = slot;
		this.attribute = attribute;
		this.amount = amount;
		this.operation = operation;
	}

	public ItemAttributeModifier(String itemId, EquipmentSlotType slot, Attribute attribute, double amount, AttributeModifier.Operation operation) {
		this(new ResourceLocation(itemId), slot, attribute, amount, operation);
	}
}
