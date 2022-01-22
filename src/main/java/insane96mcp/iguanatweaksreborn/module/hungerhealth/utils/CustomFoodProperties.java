package insane96mcp.iguanatweaksreborn.module.hungerhealth.utils;

import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CustomFoodProperties {
	public ResourceLocation id;
	public int nutrition;
	public float saturationModifier;

	public CustomFoodProperties(ResourceLocation id, int nutrition, float saturationModifier) {
		this.id = id;
		this.nutrition = nutrition;
		this.saturationModifier = saturationModifier;
	}

	@Nullable
	public static CustomFoodProperties parseLine(String line) {
		String[] split = line.split(",");
		if (split.length < 2 || split.length > 3) {
			LogHelper.warn("Invalid line \"%s\" for Custom Food Value", line);
			return null;
		}
		if (!NumberUtils.isParsable(split[1])) {
			LogHelper.warn(String.format("Invalid nutrition \"%s\" for Custom Food Properties", line));
			return null;
		}
		int nutrition = Integer.parseInt(split[1]);
		float saturationModifier = -1f;
		if (split.length == 3) {
			if (!NumberUtils.isParsable(split[2])) {
				LogHelper.warn(String.format("Invalid saturation modifier \"%s\" for Custom Food Properties", line));
				return null;
			}
			saturationModifier = Float.parseFloat(split[2]);
		}
		ResourceLocation item = ResourceLocation.tryParse(split[0]);
		if (item == null) {
			LogHelper.warn("%s item for Custom Food Properties is not valid", split[0]);
			return null;
		}
		if (!ForgeRegistries.ITEMS.containsKey(item) || !ForgeRegistries.ITEMS.getValue(item).isEdible()) {
			LogHelper.warn(String.format("%s item for Custom Food Properties seems to not exist or is not a food", split[0]));
			return null;
		}
		return new CustomFoodProperties(item, nutrition, saturationModifier);
	}

	public static ArrayList<CustomFoodProperties> parseStringList(List<? extends String> list) {
		ArrayList<CustomFoodProperties> foodValues = new ArrayList<>();
		for (String line : list) {
			CustomFoodProperties customFoodValue = CustomFoodProperties.parseLine(line);
			if (customFoodValue != null)
				foodValues.add(customFoodValue);
		}
		return foodValues;
	}

	@Override
	public String toString() {
		return "FoodValue{id: %s, nutrition: %d, saturationModifier: %d}".formatted(id, nutrition, saturationModifier);
	}
}
