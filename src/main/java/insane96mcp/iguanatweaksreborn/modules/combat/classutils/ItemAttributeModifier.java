package insane96mcp.iguanatweaksreborn.modules.combat.classutils;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class ItemAttributeModifier {
	public ResourceLocation itemId;
	public Class<? extends Item> itemClass;
	public EquipmentSlotType slot;
	public Attribute attribute;
	public double amount;
	public AttributeModifier.Operation operation;

	public ItemAttributeModifier(@Nullable ResourceLocation itemId, @Nullable Class<? extends Item> itemClass, EquipmentSlotType slot, Attribute attribute, double amount, AttributeModifier.Operation operation) {
		if (itemId == null && itemClass == null)
			throw new NullPointerException("itemId and itemClass can't be both null");
		this.itemId = itemId;
		this.itemClass = itemClass;
		this.slot = slot;
		this.attribute = attribute;
		this.amount = amount;
		this.operation = operation;
	}

	public ItemAttributeModifier(@Nullable String itemId, @Nullable Class<? extends Item> itemClass, EquipmentSlotType slot, Attribute attribute, double amount, AttributeModifier.Operation operation) {
		this(new ResourceLocation(itemId), itemClass, slot, attribute, amount, operation);
	}
}
