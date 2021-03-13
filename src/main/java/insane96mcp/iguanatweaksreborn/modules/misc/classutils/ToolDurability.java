package insane96mcp.iguanatweaksreborn.modules.misc.classutils;


import insane96mcp.iguanatweaksreborn.common.classutils.IdTagMatcher;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;

public class ToolDurability extends IdTagMatcher {
	public int durability;

	public ToolDurability(@Nullable ResourceLocation item, int durability) {
		super(item, null);
		this.durability = durability;
	}

	@Nullable
	public static ToolDurability parseLine(String line) {
		String[] split = line.split(",");
		if (split.length != 2) {
			LogHelper.warn("Invalid line \"%s\" for Custom Tool Durability", line);
			return null;
		}
		ResourceLocation item = ResourceLocation.tryCreate(split[0]);
		if (item == null) {
			LogHelper.warn("%s item for Tool Durability is not valid", split[0]);
			return null;
		}
		if (!ForgeRegistries.ITEMS.containsKey(item)) {
			LogHelper.warn(String.format("%s item for Tool Durability seems to not exist", split[0]));
			return null;
		}
		if (!NumberUtils.isParsable(split[1])) {
			LogHelper.warn(String.format("Invalid durability \"%s\" for Tool Durability", line));
			return null;
		}
		int durability = Integer.parseInt(split[1]);
		return new ToolDurability(item, durability);
	}
}