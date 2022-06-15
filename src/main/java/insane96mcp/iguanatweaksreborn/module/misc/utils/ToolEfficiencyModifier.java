package insane96mcp.iguanatweaksreborn.module.misc.utils;

import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ToolEfficiencyModifier extends IdTagMatcher {
	public float efficiencyMultiplier;

	public ToolEfficiencyModifier(ResourceLocation location, float efficiencyMultiplier) {
		super(Type.ID, location);
		this.efficiencyMultiplier = efficiencyMultiplier;
	}

	@Nullable
	public static ToolEfficiencyModifier parseLine(String line) {
		String[] split = line.split(",");
		if (split.length != 2) {
			LogHelper.warn("Invalid line \"%s\" for Tool Efficiency Modifier", line);
			return null;
		}
		ResourceLocation item = ResourceLocation.tryParse(split[0]);
		if (item == null) {
			LogHelper.warn("%s item for Tool Efficiency Modifier is not valid", split[0]);
			return null;
		}
		if (!ForgeRegistries.ITEMS.containsKey(item)) {
			LogHelper.warn(String.format("%s item for Tool Efficiency Modifier seems to not exist", split[0]));
			return null;
		}

		if (!NumberUtils.isParsable(split[1])) {
			LogHelper.warn(String.format("Invalid efficiency \"%s\" for Tool Efficiency Modifier", line));
			return null;
		}
		float efficiencyMultiplier = Float.parseFloat(split[1]);

		return new ToolEfficiencyModifier(item, efficiencyMultiplier);
	}

	public static ArrayList<ToolEfficiencyModifier> parseStringList(List<? extends String> list) {
		ArrayList<ToolEfficiencyModifier> toolEfficiencyModifiers = new ArrayList<>();
		for (String line : list) {
			ToolEfficiencyModifier toolEfficiencyModifier = ToolEfficiencyModifier.parseLine(line);
			if (toolEfficiencyModifier != null)
				toolEfficiencyModifiers.add(toolEfficiencyModifier);
		}
		return toolEfficiencyModifiers;
	}
}