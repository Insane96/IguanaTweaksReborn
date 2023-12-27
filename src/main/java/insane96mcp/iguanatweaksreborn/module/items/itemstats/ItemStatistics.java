package insane96mcp.iguanatweaksreborn.module.items.itemstats;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.iguanatweaksreborn.utils.ITRGsonHelper;
import insane96mcp.insanelib.base.JsonFeature;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
