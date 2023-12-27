package insane96mcp.iguanatweaksreborn.module.combat.stats.data;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.iguanatweaksreborn.data.AttributeModifierOperation;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Supplier;

@JsonAdapter(ItemAttributeModifier.Serializer.class)
public class ItemAttributeModifier {
	public IdTagMatcher item;
	public UUID uuid;
	public EquipmentSlot slot;
	//Use supplier due to default modifiers loading at runtime and modded attributes are not yet registered
	public Supplier<Attribute> attribute;
	public double amount;
	public AttributeModifier.Operation operation;

	public ItemAttributeModifier(IdTagMatcher item, UUID uuid, EquipmentSlot slot, Supplier<Attribute> attribute, double amount, AttributeModifier.Operation operation) {
		this.item = item;
		this.slot = slot;
		this.uuid = uuid;
		this.attribute = attribute;
		this.amount = amount;
		this.operation = operation;
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<ItemAttributeModifier>>(){}.getType();

	public static class Serializer implements JsonDeserializer<ItemAttributeModifier>, JsonSerializer<ItemAttributeModifier> {
		@Override
		public ItemAttributeModifier deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jObject = json.getAsJsonObject();
			IdTagMatcher item = context.deserialize(jObject.get("item"), IdTagMatcher.class);

			String sUUID = GsonHelper.getAsString(jObject, "uuid");
			UUID uuid;
			try {
				uuid = UUID.fromString(sUUID);
			}
			catch (Exception ex) {
				throw new JsonParseException("uuid %s is not valid".formatted(sUUID));
			}

			EquipmentSlot slot = EquipmentSlot.byName(GsonHelper.getAsString(jObject, "slot"));

			String sAttribute = GsonHelper.getAsString(jObject, "attribute");
			Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.tryParse(sAttribute));
			if (attribute == null) {
				throw new JsonParseException("Invalid attribute: %s".formatted(sAttribute));
			}

			double amount = GsonHelper.getAsDouble(jObject, "amount");
			AttributeModifierOperation operation = context.deserialize(jObject.get("operation"), AttributeModifierOperation.class);

			return new ItemAttributeModifier(item, uuid, slot, () -> attribute, amount, operation.get());
		}

		@Override
		public JsonElement serialize(ItemAttributeModifier src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jObject = new JsonObject();
			jObject.add("item", context.serialize(src.item));
			jObject.addProperty("uuid", src.uuid.toString());
			jObject.addProperty("slot", src.slot.getName());
			jObject.addProperty("attribute", ForgeRegistries.ATTRIBUTES.getKey(src.attribute.get()).toString());
			jObject.addProperty("amount", src.amount);
			jObject.addProperty("operation", AttributeModifierOperation.getNameFromOperation(src.operation));
			return jObject;
		}
	}
}
