package insane96mcp.iguanatweaksreborn.module.misc.utils;

import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.utils.IdTagMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ToolDurabilityModifier extends IdTagMatcher {
	public int durability;

	public ToolDurabilityModifier(@Nullable ResourceLocation item, int durability) {
		super(item, null);
		this.durability = durability;
	}

	@Nullable
	public static ToolDurabilityModifier parseLine(String line) {
		String[] split = line.split(",");
		if (split.length != 2) {
			LogHelper.warn("Invalid line \"%s\" for Tool Durability Modifier", line);
			return null;
		}
		ResourceLocation item = ResourceLocation.tryParse(split[0]);
		if (item == null) {
			LogHelper.warn("%s item for Tool Durability Modifier is not valid", split[0]);
			return null;
		}
		if (!ForgeRegistries.ITEMS.containsKey(item)) {
			LogHelper.warn(String.format("%s item for Tool Durability Modifier seems to not exist", split[0]));
			return null;
		}

		if (!NumberUtils.isParsable(split[1])) {
			LogHelper.warn(String.format("Invalid durability \"%s\" for Tool Durability Modifier", line));
			return null;
		}
		int durability = Integer.parseInt(split[1]);

		return new ToolDurabilityModifier(item, durability);
	}

	public static ArrayList<? extends IdTagMatcher> parseStringList(List<? extends String> list) {
		ArrayList<ToolDurabilityModifier> toolDurabilityModifiers = new ArrayList<>();
		for (String line : list) {
			ToolDurabilityModifier toolDurabilityModifier = ToolDurabilityModifier.parseLine(line);
			if (toolDurabilityModifier != null)
				toolDurabilityModifiers.add(toolDurabilityModifier);
		}
		return toolDurabilityModifiers;
	}
}