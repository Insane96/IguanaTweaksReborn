package insane96mcp.iguanatweaksreborn.modules.movement.classutils;

import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ArmorMaterialWeight {
	public String id;
	public double totalWeight;

	public ArmorMaterialWeight(String id, double totalWeight) {
		this.id = id;
		this.totalWeight = totalWeight;
	}

	@Nullable
	public static ArmorMaterialWeight parseLine(String line) {
		String[] split = line.split(",");
		if (split.length != 2) {
			LogHelper.Warn("Invalid ArmorMaterialWeight \"%s\". Format must be material,total_weight", line);
			return null;
		}
		String id = split[0];
		if (!NumberUtils.isParsable(split[1])) {
			LogHelper.Warn(String.format("Invalid Total Weight \"%s\" for ArmorMaterialWeight", line));
			return null;
		}
		double totalWeight = Double.parseDouble(split[1]);
		return new ArmorMaterialWeight(id, totalWeight);
	}

	public static ArrayList<ArmorMaterialWeight> parseStringList(List<? extends String> list) {
		ArrayList<ArmorMaterialWeight> armorMaterialWeights = new ArrayList<>();
		for (String line : list) {
			ArmorMaterialWeight armorMaterialWeight = ArmorMaterialWeight.parseLine(line);
			if (armorMaterialWeight != null)
				armorMaterialWeights.add(armorMaterialWeight);
		}
		return armorMaterialWeights;
	}
}
