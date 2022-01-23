package insane96mcp.iguanatweaksreborn.module.combat.utils;

import com.google.common.base.Enums;
import insane96mcp.insanelib.utils.LogHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemAttributeModifier {
	public ResourceLocation itemId;
	public ResourceLocation itemTag;
	public Class<? extends Item> itemClass;
	public EquipmentSlot slot;
	public Attribute attribute;
	public double amount;
	public AttributeModifier.Operation operation;

	public ItemAttributeModifier(@Nullable ResourceLocation itemId, @Nullable ResourceLocation itemTag, EquipmentSlot slot, Attribute attribute, double amount, AttributeModifier.Operation operation) {
		this.itemId = itemId;
		this.itemTag = itemTag;
		this.slot = slot;
		this.attribute = attribute;
		this.amount = amount;
		this.operation = operation;
	}

	public ItemAttributeModifier(Class<? extends Item> itemClass, EquipmentSlot slot, Attribute attribute, double amount, AttributeModifier.Operation operation) {
		this.itemClass = itemClass;
		this.slot = slot;
		this.attribute = attribute;
		this.amount = amount;
		this.operation = operation;
	}

	@Nullable
	public static ItemAttributeModifier parseLine(String line) {
		String[] split = line.split(",");
		if (split.length != 5) {
			LogHelper.warn("Invalid line \"%s\" for Item Modifier", line);
			return null;
		}
		ResourceLocation tag = null;
		ResourceLocation id = null;
		if (split[0].startsWith("#")) {
			String replaced = split[0].replace("#", "");
			tag = ResourceLocation.tryParse(replaced);
			if (tag == null) {
				LogHelper.warn("%s tag is not a valid resource location", split[0]);
				return null;
			}
		}
		else {
			id = ResourceLocation.tryParse(split[0]);
			if (id == null) {
				LogHelper.warn("%s id is not a valid resource location", split[0]);
				return null;
			}
			if (!ForgeRegistries.ITEMS.containsKey(id)) {
				LogHelper.warn("%s id seems to not exist", line);
				return null;
			}
		}

		EquipmentSlot slot = Enums.getIfPresent(EquipmentSlot.class, split[1]).orNull();
		if (slot == null) {
			LogHelper.warn("%s slot is not valid", split[1]);
			return null;
		}

		ResourceLocation attributeRL = ResourceLocation.tryParse(split[2]);
		if (attributeRL == null) {
			LogHelper.warn("%s attribute is not a valid resource location", split[2]);
			return null;
		}
		if (!ForgeRegistries.ATTRIBUTES.containsKey(attributeRL)) {
			LogHelper.warn("%s attribute seems to not exist", attributeRL);
			return null;
		}
		Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeRL);

		if (!NumberUtils.isCreatable(split[3])) {
			LogHelper.warn("%s  seems to not be a number", split[3]);
			return null;
		}
		double amount = Double.parseDouble(split[3]);

		AttributeModifier.Operation operation = Enums.getIfPresent(AttributeModifier.Operation.class, split[4]).orNull();
		if (operation == null) {
			LogHelper.warn("%s operation is not valid", split[4]);
			return null;
		}

		return new ItemAttributeModifier(id, tag, slot, attribute, amount, operation);
	}

	public static ArrayList<ItemAttributeModifier> parseStringList(List<? extends String> list) {
		ArrayList<ItemAttributeModifier> itemAttributeModifiers = new ArrayList<>();
		for (String line : list) {
			ItemAttributeModifier itemAttributeModifier = ItemAttributeModifier.parseLine(line);
			if (itemAttributeModifier != null)
				itemAttributeModifiers.add(itemAttributeModifier);
		}
		return itemAttributeModifiers;
	}

	public boolean matchesItem(Item item) {
		if (this.itemTag != null)
			return ItemTags.getAllTags().getTag(this.itemTag).contains(item);
		if (this.itemId != null)
			return item.getRegistryName().equals(this.itemId);
		return item.getClass().equals(this.itemClass);
	}
}
