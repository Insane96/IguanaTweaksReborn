package insane96mcp.iguanatweaksreborn.module.combat.utils;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.iguanatweaksreborn.utils.AttributeModifierOperation;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

@JsonAdapter(ItemAttributeModifier.Serializer.class)
public class ItemAttributeModifier extends IdTagMatcher {
	public Class<? extends Item> itemClass;
	public EquipmentSlot slot;
	public Attribute attribute;
	public double amount;
	public AttributeModifier.Operation operation;

	public ItemAttributeModifier(IdTagMatcher.Type type, String id) {
		super(type, id);
	}

	public ItemAttributeModifier(IdTagMatcher.Type type, String id, EquipmentSlot slot, Attribute attribute, double amount, AttributeModifier.Operation operation) {
		super(type, id);
		this.slot = slot;
		this.attribute = attribute;
		this.amount = amount;
		this.operation = operation;
	}

	public ItemAttributeModifier(Class<? extends Item> itemClass, EquipmentSlot slot, Attribute attribute, double amount, AttributeModifier.Operation operation) {
		super(Type.ID, "minecraft:air");
		this.itemClass = itemClass;
		this.slot = slot;
		this.attribute = attribute;
		this.amount = amount;
		this.operation = operation;
	}

	public boolean matches(Item item) {
		if (this.itemClass != null)
			return item.getClass().equals(this.itemClass);
		else
			return this.matchesItem(item);
	}

	public static class Serializer implements JsonDeserializer<ItemAttributeModifier>, JsonSerializer<ItemAttributeModifier> {
		@Override
		public ItemAttributeModifier deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String id = GsonHelper.getAsString(json.getAsJsonObject(), "id", "");
			String tag = GsonHelper.getAsString(json.getAsJsonObject(), "tag", "");

			if (!id.equals("") && !ResourceLocation.isValidResourceLocation(id)) {
				throw new JsonParseException("Invalid id for ItemAttributeModifier: %s".formatted(id));
			}
			if (!tag.equals("") && !ResourceLocation.isValidResourceLocation(id)) {
				throw new JsonParseException("Invalid tag for ItemAttributeModifier: %s".formatted(tag));
			}

			ItemAttributeModifier itemAttributeModifier;
			if (!id.equals("") && !tag.equals("")){
				throw new JsonParseException("Invalid ItemAttributeModifier containing both tag (%s) and id (%s)".formatted(tag, id));
			}
			else if (!id.equals("")) {
				itemAttributeModifier = new ItemAttributeModifier(Type.ID, id);
			}
			else if (!tag.equals("")){
				itemAttributeModifier = new ItemAttributeModifier(Type.TAG, id);
			}
			else {
				throw new JsonParseException("Invalid ItemAttributeModifier missing either tag and id");
			}

			String dimension = GsonHelper.getAsString(json.getAsJsonObject(), "dimension", "");
			if (!dimension.equals("") && !ResourceLocation.isValidResourceLocation(dimension)) {
				throw new JsonParseException("Invalid dimension for ItemAttributeModifier: %s".formatted(dimension));
			}
			itemAttributeModifier.dimension = ResourceLocation.tryParse(dimension);

			String sEquipSlot = GsonHelper.getAsString(json.getAsJsonObject(), "slot");
			itemAttributeModifier.slot = EquipmentSlot.byName(sEquipSlot);

			String sAttribute = GsonHelper.getAsString(json.getAsJsonObject(), "attribute");
			Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.tryParse(sAttribute));
			if (attribute == null) {
				throw new JsonParseException("Invalid attribute for ItemAttributeModifier: %s".formatted(sAttribute));
			}
			itemAttributeModifier.attribute = attribute;

			itemAttributeModifier.amount = GsonHelper.getAsDouble(json.getAsJsonObject(), "amount");

			AttributeModifierOperation operation = context.deserialize(json.getAsJsonObject().get("operation"), AttributeModifierOperation.class);
			itemAttributeModifier.operation = operation.get();

			return itemAttributeModifier;
		}

		@Override
		public JsonElement serialize(ItemAttributeModifier src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			if (src.type == Type.ID) {
				jsonObject.addProperty("id", src.location.toString());
			}
			else if (src.type == Type.TAG) {
				jsonObject.addProperty("tag", src.location.toString());
			}
			if (src.dimension != null) {
				jsonObject.addProperty("dimension", src.dimension.toString());
			}
			jsonObject.addProperty("slot", src.slot.getName());
			jsonObject.addProperty("attribute", ForgeRegistries.ATTRIBUTES.getKey(src.attribute).toString());
			jsonObject.addProperty("amount", src.amount);
			jsonObject.addProperty("operation", AttributeModifierOperation.getNameFromOperation(src.operation));
			return jsonObject;
		}
	}
}
