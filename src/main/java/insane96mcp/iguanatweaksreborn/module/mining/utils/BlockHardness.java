package insane96mcp.iguanatweaksreborn.module.mining.utils;

import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockHardness extends IdTagMatcher {
	public double hardness;
	public boolean has0Hardness;

	public BlockHardness(Type type, ResourceLocation location, Double hardness, ResourceLocation dimension) {
		super(type, location, dimension);
		this.hardness = hardness;
	}

	@Nullable
	public static BlockHardness parseLine(String line) {
		String[] split = line.split(",");
		if (split.length < 2 || split.length > 3) {
			LogHelper.warn("Invalid line \"%s\" for Custom Hardness", line);
			return null;
		}
		if (!NumberUtils.isParsable(split[1])) {
			LogHelper.warn(String.format("Invalid hardness \"%s\" for Custom Hardness", line));
			return null;
		}
		double hardness = Double.parseDouble(split[1]);
		ResourceLocation dimension = null;
		if (split.length == 3) {
			dimension = ResourceLocation.tryParse(split[2]);
			if (dimension == null) {
				LogHelper.info(String.format("Invalid dimension \"%s\" for Custom Hardness. Ignoring it", split[2]));
			}
		}
		if (split[0].startsWith("#")) {
			String replaced = split[0].replace("#", "");
			ResourceLocation tag = ResourceLocation.tryParse(replaced);
			if (tag == null) {
				LogHelper.warn("%s tag for Custom Hardness is not valid", replaced);
				return null;
			}
			return new BlockHardness(Type.TAG, tag, hardness, dimension);
		}
		else {
			ResourceLocation block = ResourceLocation.tryParse(split[0]);
			if (block == null) {
				LogHelper.warn("%s block for Custom Hardness is not valid", split[0]);
				return null;
			}
			if (ForgeRegistries.BLOCKS.containsKey(block)) {
				return new BlockHardness(Type.ID, block, hardness, dimension);
			}
			else {
				LogHelper.warn(String.format("%s block for Custom Hardness seems to not exist", split[0]));
				return null;
			}
		}
	}

	public static ArrayList<BlockHardness> parseStringList(List<? extends String> list) {
		ArrayList<BlockHardness> blockHardnesses = new ArrayList<>();
		for (String line : list) {
			BlockHardness blockHardness = BlockHardness.parseLine(line);
			if (blockHardness == null)
				continue;
			blockHardnesses.add(blockHardness);
		}

		return blockHardnesses;
	}
}