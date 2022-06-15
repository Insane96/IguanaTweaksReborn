package insane96mcp.iguanatweaksreborn.module.farming.utils;

import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlantGrowthModifier {
	public IdTagMatcher plantId;
	private double growthMultiplier = 1d;
	private double noSunlightGrowthMultiplier = 1d;
	private int minSunlightRequired = 0;
	private double nightTimeGrowthMultiplier = 1d;
	private List<BiomeDictionary.Type> biomes = new ArrayList<>();
	private double wrongBiomeMultiplier = 1d;

	public PlantGrowthModifier(IdTagMatcher.Type type, ResourceLocation location) {
		this.plantId = new IdTagMatcher(type, location);
	}

	public PlantGrowthModifier growthMultiplier(double growthMultiplier) {
		this.growthMultiplier = growthMultiplier;
		return this;
	}

	public PlantGrowthModifier noSunlightGrowthMultiplier(double noSunlightGrowthMultiplier) {
		this.noSunlightGrowthMultiplier = noSunlightGrowthMultiplier;
		return this;
	}

	public PlantGrowthModifier minSunlightRequired(int minSunlightRequired) {
		this.minSunlightRequired = minSunlightRequired;
		return this;
	}

	public PlantGrowthModifier nightTimeGrowthMultiplier(double nightTimeGrowthMultiplier) {
		this.nightTimeGrowthMultiplier = nightTimeGrowthMultiplier;
		return this;
	}

	public PlantGrowthModifier addBiomes(BiomeDictionary.Type... biomeType) {
		this.biomes.addAll(Arrays.asList(biomeType));
		return this;
	}

	public PlantGrowthModifier wrongBiomeMultiplier(double wrongBiomeMultiplier) {
		this.wrongBiomeMultiplier = wrongBiomeMultiplier;
		return this;
	}

	/**
	 * Returns -1 when the block doesn't match the PlantGrowthModifier
	 */
	public double getMultiplier(Block block, Level level, BlockPos pos) {
		if (!this.plantId.matchesBlock(block))
			return -1d;
		double multiplier = this.growthMultiplier;
		int skyLight = level.getBrightness(LightLayer.SKY, pos);
		if (skyLight < this.minSunlightRequired)
			multiplier *= this.noSunlightGrowthMultiplier;
		int dayTime = (int) (level.dayTime() % 24000);
		if (dayTime >= 12786 && dayTime < 23216)
			multiplier *= this.nightTimeGrowthMultiplier;

		return multiplier;
	}

	/**
	 * Parses a simple id,multiplier or tag,multiplier line to a PlantGrowthModifier
	 */
	@Nullable
	public static PlantGrowthModifier parseLine(String line) {
		String[] split = line.split(",");
		if (split.length != 2) {
			LogHelper.warn("Invalid line \"%s\" for Plant Growth Modification", line);
			return null;
		}
		if (!NumberUtils.isParsable(split[1])) {
			LogHelper.warn(String.format("Invalid multiplier \"%s\" for Plant Growth Modification", line));
			return null;
		}
		double multiplier = Double.parseDouble(split[1]);
		if (split[0].startsWith("#")) {
			String replaced = split[0].replace("#", "");
			ResourceLocation tag = ResourceLocation.tryParse(replaced);
			if (tag == null) {
				LogHelper.warn("%s tag for Plant Growth Modification is not valid", replaced);
				return null;
			}
			return new PlantGrowthModifier(IdTagMatcher.Type.TAG, tag).growthMultiplier(multiplier);
		}
		else {
			ResourceLocation block = ResourceLocation.tryParse(split[0]);
			if (block == null) {
				LogHelper.warn("%s block for Plant Growth Modification is not valid", split[0]);
				return null;
			}
			if (ForgeRegistries.BLOCKS.containsKey(block)) {
				return new PlantGrowthModifier(IdTagMatcher.Type.ID, block).growthMultiplier(multiplier);
			}
			else {
				LogHelper.warn(String.format("%s block for Plant Growth Modification seems to not exist", split[0]));
				return null;
			}
		}
	}
}