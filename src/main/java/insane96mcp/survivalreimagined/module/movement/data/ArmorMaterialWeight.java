package insane96mcp.survivalreimagined.module.movement.data;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Optional;

import static insane96mcp.survivalreimagined.module.movement.feature.WeightedEquipment.armorDurabilityRatio;

@JsonAdapter(ArmorMaterialWeight.Serializer.class)
public class ArmorMaterialWeight {
	public String materialName;
	public double totalWeight;

	public ArmorMaterialWeight(String materialName) {
		this.materialName = materialName;
	}

	public ArmorMaterialWeight(String materialName, double totalWeight) {
		this.materialName = materialName;
		this.totalWeight = totalWeight;
	}

	public Optional<Double> getStackWeight(ItemStack stack) {
		ArmorItem armor = (ArmorItem) stack.getItem();
		if (!armor.getMaterial().getName().equals(this.materialName))
			return Optional.empty();
		EquipmentSlot slot = armor.getEquipmentSlot();
		double armorPieceSlowdown = this.totalWeight * armorDurabilityRatio.get(slot);
		return Optional.of(-armorPieceSlowdown);
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<ArmorMaterialWeight>>(){}.getType();
	public static class Serializer implements JsonDeserializer<ArmorMaterialWeight>, JsonSerializer<ArmorMaterialWeight> {
		@Override
		public ArmorMaterialWeight deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String id = GsonHelper.getAsString(json.getAsJsonObject(), "id");
			ArmorMaterialWeight armorMaterialWeight = new ArmorMaterialWeight(id);
			armorMaterialWeight.totalWeight = GsonHelper.getAsDouble(json.getAsJsonObject(), "total_weight");

			return armorMaterialWeight;
		}

		@Override
		public JsonElement serialize(ArmorMaterialWeight src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("id", src.materialName);
			jsonObject.addProperty("total_weight", src.totalWeight);

			return jsonObject;
		}
	}
}
