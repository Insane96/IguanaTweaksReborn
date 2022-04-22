package insane96mcp.iguanatweaksreborn.module.stacksize.utils;

import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemStackSize extends IdTagMatcher {
	public int stackSize;

	public ItemStackSize(@Nullable ResourceLocation id, @Nullable ResourceLocation tag, int stackSize) {
		super(id, tag);
		this.stackSize = stackSize;
	}

	@Nullable
	public static ItemStackSize parseLine(String line) {
		String[] split = line.split(",");
		if (split.length != 2) {
			LogHelper.warn("Invalid line \"%s\" for Custom Stack Size", line);
			return null;
		}
		if (!NumberUtils.isParsable(split[1])) {
			LogHelper.warn(String.format("Invalid stackSize \"%s\" for Custom Stack Size", line));
			return null;
		}
		int stackSize = Integer.parseInt(split[1]);
		if (split[0].startsWith("#")) {
			String replaced = split[0].replace("#", "");
			ResourceLocation tag = ResourceLocation.tryParse(replaced);
			if (tag == null) {
				LogHelper.warn("%s tag for Custom Stack Size is not valid", replaced);
				return null;
			}
			return new ItemStackSize(null, tag, stackSize);
		}
		else {
			ResourceLocation item = ResourceLocation.tryParse(split[0]);
			if (item == null) {
				LogHelper.warn("%s item for Custom Stack Size is not valid", split[0]);
				return null;
			}
			if (!ForgeRegistries.ITEMS.containsKey(item)) {
				LogHelper.warn(String.format("%s item for Custom Stack Size seems to not exist", split[0]));
				return null;
			}
			return new ItemStackSize(item, null, stackSize);
		}
	}

	public static ArrayList<ItemStackSize> parseStringList(List<? extends String> list) {
		ArrayList<ItemStackSize> stackSizes = new ArrayList<>();
		for (String line : list) {
			ItemStackSize customStackSize = ItemStackSize.parseLine(line);
			if (customStackSize != null)
				stackSizes.add(customStackSize);
		}
		return stackSizes;
	}

}