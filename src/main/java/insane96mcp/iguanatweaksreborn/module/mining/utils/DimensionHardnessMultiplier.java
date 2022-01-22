package insane96mcp.iguanatweaksreborn.module.mining.utils;

import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DimensionHardnessMultiplier {
	public ResourceLocation dimension;
	public double multiplier;

	public DimensionHardnessMultiplier(ResourceLocation dimension, double multiplier) {
		this.dimension = dimension;
		this.multiplier = multiplier;
	}

	@Nullable
	public static DimensionHardnessMultiplier parseLine(String line) {
		String[] split = line.split(",");
		if (split.length < 1 || split.length > 2) {
			LogHelper.warn("Invalid line \"%s\" for Dimension multiplier. Format must be modid:dimensionId,hardness", line);
			return null;
		}
		ResourceLocation dimension = ResourceLocation.tryParse(split[0]);
		if (dimension == null) {
			LogHelper.warn(String.format("Invalid dimension \"%s\" for Dimension multiplier", split[0]));
			return null;
		}
		if (!NumberUtils.isParsable(split[1])) {
			LogHelper.warn(String.format("Invalid hardness \"%s\" for Dimension Multiplier", split[1]));
			return null;
		}
		double hardness = Double.parseDouble(split[1]);

		return new DimensionHardnessMultiplier(dimension, hardness);
	}

	public static ArrayList<? extends DimensionHardnessMultiplier> parseStringList(List<? extends String> list) {
		ArrayList<DimensionHardnessMultiplier> dimensionHardnessMultipliers = new ArrayList<>();
		for (String line : list) {
			DimensionHardnessMultiplier dimensionHardnessMultiplier = DimensionHardnessMultiplier.parseLine(line);
			if (dimensionHardnessMultiplier != null)
				dimensionHardnessMultipliers.add(dimensionHardnessMultiplier);
		}

		return dimensionHardnessMultipliers;
	}
}