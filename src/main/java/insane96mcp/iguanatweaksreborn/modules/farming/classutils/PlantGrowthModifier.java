package insane96mcp.iguanatweaksreborn.modules.farming.classutils;

import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.utils.IdTagMatcher;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
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

	public PlantGrowthModifier(@Nullable ResourceLocation plantId, @Nullable ResourceLocation plantTag) {
		this.plantId = new IdTagMatcher(plantId, plantTag);
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
	 * @param block
	 * @param world
	 * @param pos
	 * @return
	 */
	public double getMultiplier(Block block, World world, BlockPos pos) {
		if (!this.plantId.matchesBlock(block))
			return -1d;
		double multiplier = this.growthMultiplier;
		int skyLight = world.getLightFor(LightType.SKY, pos);
		if (skyLight < this.minSunlightRequired)
			multiplier *= this.noSunlightGrowthMultiplier;
		int dayTime = (int) (world.func_241851_ab() % 24000);
		if (dayTime >= 12786 && dayTime < 23216)
			multiplier *= this.nightTimeGrowthMultiplier;

		return multiplier;
	}

	/**
	 * Parses a simple id,multiplier or tag,multiplier line to a PlantGrowthModifier
	 * @param line
	 * @return
	 */
	@Nullable
	public static PlantGrowthModifier parseSimpleLine(String line) {
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
			ResourceLocation tag = ResourceLocation.tryCreate(replaced);
			if (tag == null) {
				LogHelper.warn("%s tag for Plant Growth Modification is not valid", replaced);
				return null;
			}
			return new PlantGrowthModifier(null, tag).growthMultiplier(multiplier);
		}
		else {
			ResourceLocation block = ResourceLocation.tryCreate(split[0]);
			if (block == null) {
				LogHelper.warn("%s block for Plant Growth Modification is not valid", split[0]);
				return null;
			}
			if (ForgeRegistries.BLOCKS.containsKey(block)) {
				return new PlantGrowthModifier(block, null).growthMultiplier(multiplier);
			}
			else {
				LogHelper.warn(String.format("%s block for Plant Growth Modification seems to not exist", split[0]));
				return null;
			}
		}
	}
}
