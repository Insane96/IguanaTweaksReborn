package insane96mcp.iguanatweaksreborn.modules.farming.classutils;

import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.utils.IdTagMatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;

public class PlantGrowthMultiplier extends IdTagMatcher {
	public double multiplier;

	public PlantGrowthMultiplier(@Nullable ResourceLocation item, @Nullable ResourceLocation tag, double multiplier) {
		super(item, tag);
		this.multiplier = multiplier;
	}

	@Nullable
	public static PlantGrowthMultiplier parseLine(String line) {
		String[] split = line.split(",");
		if (split.length != 2) {
			LogHelper.warn("Invalid line \"%s\" for Plant Growth Multiplier", line);
			return null;
		}
		if (!NumberUtils.isParsable(split[1])) {
			LogHelper.warn(String.format("Invalid multiplier \"%s\" for Plant Growth Multiplier", line));
			return null;
		}
		double multiplier = Double.parseDouble(split[1]);
		if (split[0].startsWith("#")) {
			String replaced = split[0].replace("#", "");
			ResourceLocation tag = ResourceLocation.tryCreate(replaced);
			if (tag == null) {
				LogHelper.warn("%s tag for Plant Growth Multiplier is not valid", replaced);
				return null;
			}
			return new PlantGrowthMultiplier(null, tag, multiplier);
		}
		else {
			ResourceLocation block = ResourceLocation.tryCreate(split[0]);
			if (block == null) {
				LogHelper.warn("%s block for Plant Growth Multiplier is not valid", split[0]);
				return null;
			}
			if (ForgeRegistries.BLOCKS.containsKey(block)) {
				return new PlantGrowthMultiplier(block, null, multiplier);
			}
			else {
				LogHelper.warn(String.format("%s item for Plant Growth Multiplier seems to not exist", split[0]));
				return null;
			}
		}
	}
}
