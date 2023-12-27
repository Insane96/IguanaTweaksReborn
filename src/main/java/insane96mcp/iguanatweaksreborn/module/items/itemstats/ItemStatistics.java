package insane96mcp.iguanatweaksreborn.module.items.itemstats;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.iguanatweaksreborn.utils.ITRGsonHelper;
import insane96mcp.insanelib.base.JsonFeature;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

@JsonAdapter(ItemStatistics.Serializer.class)
public record ItemStatistics(IdTagMatcher item, @Nullable Integer durability, @Nullable Double efficiency,
							 @Nullable Double baseAttackDamage, @Nullable Double baseAttackSpeed,
							 @Nullable Double baseArmor, @Nullable Double baseArmorToughness,
							 @Nullable Double baseKnockbackResistance) {
	public ItemStatistics(@NotNull IdTagMatcher item, @Nullable Integer durability, @Nullable Double efficiency, @Nullable Double baseAttackDamage, @Nullable Double baseAttackSpeed, @Nullable Double baseArmor, @Nullable Double baseArmorToughness, @Nullable Double baseKnockbackResistance) {
		this.item = item;
		this.durability = durability;
		this.efficiency = efficiency;
		this.baseAttackDamage = baseAttackDamage;
		this.baseAttackSpeed = baseAttackSpeed;
		this.baseArmor = baseArmor;
		this.baseArmorToughness = baseArmorToughness;
		this.baseKnockbackResistance = baseKnockbackResistance;
	}

	public void applyStats(boolean isClientSide) {
		List<Item> items = JsonFeature.getAllItems(this.item, isClientSide);
		for (Item item : items) {
			if (this.durability != null)
				item.maxDamage = this.durability;
			if (this.efficiency != null && item instanceof DiggerItem diggerItem) {
				diggerItem.speed = this.efficiency().floatValue();
			}
		}
	}

	static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	@SuppressWarnings("unchecked")
	private static final EnumMap<ArmorItem.Type, UUID> ARMOR_MODIFIER_UUID_PER_TYPE = (EnumMap) Util.make(new EnumMap(ArmorItem.Type.class), (enumMap) -> {
		enumMap.put(ArmorItem.Type.BOOTS, UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"));
		enumMap.put(ArmorItem.Type.LEGGINGS, UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"));
		enumMap.put(ArmorItem.Type.CHESTPLATE, UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"));
		enumMap.put(ArmorItem.Type.HELMET, UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"));
	});

	public void applyAttributes(ItemAttributeModifierEvent event, ItemStack stack, Multimap<Attribute, AttributeModifier> modifiers) {
		if (!this.item.matchesItem(stack))
			return;


		Multimap<Attribute, AttributeModifier> toAdd = HashMultimap.create();
		Multimap<Attribute, AttributeModifier> toRemove = HashMultimap.create();
		for (var entry : modifiers.entries()) {
			if (this.baseAttackDamage != null && entry.getValue().getId().equals(BASE_ATTACK_DAMAGE_UUID) && entry.getKey().equals(Attributes.ATTACK_DAMAGE)) {
				if (this.baseAttackDamage > 0d) {
					double materialAd = 0d;
					if (stack.getItem() instanceof TieredItem tieredItem)
						materialAd = tieredItem.getTier().getAttackDamageBonus();
					toAdd.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", this.baseAttackDamage + materialAd, AttributeModifier.Operation.ADDITION));
				}
				toRemove.put(entry.getKey(), entry.getValue());
			}
			if (this.baseAttackSpeed != null && entry.getValue().getId().equals(BASE_ATTACK_SPEED_UUID) && entry.getKey().equals(Attributes.ATTACK_SPEED)) {
				if (this.baseAttackSpeed > 0d)
					toAdd.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -(4d - this.baseAttackSpeed), AttributeModifier.Operation.ADDITION));
				toRemove.put(entry.getKey(), entry.getValue());
			}
			if (this.baseArmor != null && stack.getItem() instanceof ArmorItem armorItem && entry.getValue().getId().equals(ARMOR_MODIFIER_UUID_PER_TYPE.get(armorItem.getType())) && entry.getKey().equals(Attributes.ARMOR)) {
				if (this.baseArmor > 0d)
					toAdd.put(Attributes.ARMOR, new AttributeModifier(ARMOR_MODIFIER_UUID_PER_TYPE.get(armorItem.getType()), "Armor modifier", this.baseArmor, AttributeModifier.Operation.ADDITION));
				toRemove.put(entry.getKey(), entry.getValue());
			}
			if (this.baseArmorToughness != null && stack.getItem() instanceof ArmorItem armorItem && entry.getValue().getId().equals(ARMOR_MODIFIER_UUID_PER_TYPE.get(armorItem.getType())) && entry.getKey().equals(Attributes.ARMOR_TOUGHNESS)) {
				if (this.baseArmorToughness > 0d)
					toAdd.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ARMOR_MODIFIER_UUID_PER_TYPE.get(armorItem.getType()), "Armor toughness", this.baseArmorToughness, AttributeModifier.Operation.ADDITION));
				toRemove.put(entry.getKey(), entry.getValue());
			}
			//TODO If it doesn't have it, it doesn't apply it
			if (this.baseKnockbackResistance != null && stack.getItem() instanceof ArmorItem armorItem && entry.getValue().getId().equals(ARMOR_MODIFIER_UUID_PER_TYPE.get(armorItem.getType())) && entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE)) {
				if (this.baseKnockbackResistance > 0d)
					toAdd.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ARMOR_MODIFIER_UUID_PER_TYPE.get(armorItem.getType()), "Armor knockback resistance", this.baseKnockbackResistance, AttributeModifier.Operation.ADDITION));
				toRemove.put(entry.getKey(), entry.getValue());
			}
		}
		toRemove.forEach(event::removeModifier);
		toAdd.forEach(event::addModifier);
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<ItemStatistics>>() {}.getType();

	public static class Serializer implements JsonDeserializer<ItemStatistics>, JsonSerializer<ItemStatistics> {
		@Override
		public ItemStatistics deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jObject = json.getAsJsonObject();
			IdTagMatcher item = context.deserialize(jObject.get("item"), IdTagMatcher.class);
			Integer durability = ITRGsonHelper.getAsInt(jObject, "durability");
			Double efficiency = ITRGsonHelper.getAsDouble(jObject, "efficiency");
			Double baseAttackDamage = ITRGsonHelper.getAsDouble(jObject, "attack_damage");
			Double baseAttackSpeed = ITRGsonHelper.getAsDouble(jObject, "attack_speed");
			Double baseArmor = ITRGsonHelper.getAsDouble(jObject, "armor");
			Double baseToughness = ITRGsonHelper.getAsDouble(jObject, "armor_toughness");
			Double baseKnockbackResistance = ITRGsonHelper.getAsDouble(jObject, "knockback_resistance");
			return new ItemStatistics(item, durability, efficiency, baseAttackDamage, baseAttackSpeed, baseArmor, baseToughness, baseKnockbackResistance);
		}

		@Override
		public JsonElement serialize(ItemStatistics src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jObject = new JsonObject();
			JsonElement item = context.serialize(src.item, IdTagMatcher.class);
			jObject.add("item", item);
			if (src.durability != null)
				jObject.addProperty("durability", src.durability);
			if (src.efficiency != null)
				jObject.addProperty("efficiency", src.efficiency);
			if (src.baseAttackDamage != null)
				jObject.addProperty("attack_damage", src.baseAttackDamage);
			if (src.baseAttackSpeed != null)
				jObject.addProperty("attack_speed", src.baseAttackSpeed);
			if (src.baseArmor != null)
				jObject.addProperty("armor", src.baseArmor);
			if (src.baseArmorToughness != null)
				jObject.addProperty("armor_toughness", src.baseArmorToughness);
			if (src.baseKnockbackResistance != null)
				jObject.addProperty("knockback_resistance", src.baseKnockbackResistance);
			return jObject;
		}
	}

	public static ItemStatistics fromNetwork(FriendlyByteBuf byteBuf) {
		String utf = byteBuf.readUtf();
		IdTagMatcher item = IdTagMatcher.parseLine(utf);
		if (item == null)
			throw new NullPointerException("Parsing item from %s for Item Statistics returned null".formatted(utf));
		Integer durability = byteBuf.readNullable(FriendlyByteBuf::readInt);
		Double efficiency = byteBuf.readNullable(FriendlyByteBuf::readDouble);
		Double baseAttackDamage = byteBuf.readNullable(FriendlyByteBuf::readDouble);
		Double baseAttackSpeed = byteBuf.readNullable(FriendlyByteBuf::readDouble);
		Double baseArmor = byteBuf.readNullable(FriendlyByteBuf::readDouble);
		Double baseToughness = byteBuf.readNullable(FriendlyByteBuf::readDouble);
		Double baseKnockbackResistance = byteBuf.readNullable(FriendlyByteBuf::readDouble);
		return new ItemStatistics(item, durability, efficiency, baseAttackDamage, baseAttackSpeed, baseArmor, baseToughness, baseKnockbackResistance);
	}

	public void toNetwork(FriendlyByteBuf byteBuf) {
		byteBuf.writeUtf(this.item.getSerializedName());
		byteBuf.writeNullable(this.durability, FriendlyByteBuf::writeInt);
		byteBuf.writeNullable(this.efficiency, FriendlyByteBuf::writeDouble);
		byteBuf.writeNullable(this.baseAttackDamage, FriendlyByteBuf::writeDouble);
		byteBuf.writeNullable(this.baseAttackSpeed, FriendlyByteBuf::writeDouble);
		byteBuf.writeNullable(this.baseArmor, FriendlyByteBuf::writeDouble);
		byteBuf.writeNullable(this.baseArmorToughness, FriendlyByteBuf::writeDouble);
		byteBuf.writeNullable(this.baseKnockbackResistance, FriendlyByteBuf::writeDouble);
	}
}
