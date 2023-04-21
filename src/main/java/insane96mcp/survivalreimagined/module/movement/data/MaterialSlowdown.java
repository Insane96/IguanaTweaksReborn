package insane96mcp.survivalreimagined.module.movement.data;

import insane96mcp.survivalreimagined.utils.LogHelper;
import insane96mcp.survivalreimagined.utils.Weights;
import net.minecraft.world.level.material.Material;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MaterialSlowdown {
	public Material material;
	public double slowdown;

	public MaterialSlowdown(Material material, double slowdown) {
		this.material = material;
		this.slowdown = slowdown;
	}

	@Nullable
	public static MaterialSlowdown parseLine(String line) {
		String[] split = line.split(",");
		if (split.length != 2) {
			LogHelper.warn("Invalid line \"%s\" for Material Slowdown", line);
			return null;
		}

		Material material = Weights.getMaterialFromString(split[0]);
		if (material == null) {
			LogHelper.warn("%s material for Material Slowdown is not valid", split[0]);
			return null;
		}

		if (!NumberUtils.isParsable(split[1])) {
			LogHelper.warn(String.format("Invalid slowness \"%s\" for Material Slowdown", line));
			return null;
		}
		double slowness = Double.parseDouble(split[1]);
		if (slowness < 0d || slowness > 1d)
			LogHelper.warn(String.format("Slowness \"%s\" for Material Slowdown has been clamped between 0.0 and 1.0", line));

		return new MaterialSlowdown(material, slowness);
	}

	public static ArrayList<MaterialSlowdown> parseStringList(List<? extends String> list) {
		ArrayList<MaterialSlowdown> materialSlowdowns = new ArrayList<>();
		for (String line : list) {
			MaterialSlowdown materialSlowdown = MaterialSlowdown.parseLine(line);
			if (materialSlowdown != null)
				materialSlowdowns.add(materialSlowdown);
		}
		return materialSlowdowns;
	}
}
