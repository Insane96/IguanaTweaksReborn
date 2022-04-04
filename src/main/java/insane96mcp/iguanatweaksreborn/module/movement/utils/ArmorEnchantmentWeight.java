package insane96mcp.iguanatweaksreborn.module.movement.utils;

import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ArmorEnchantmentWeight extends IdTagMatcher {
	public double slownessReductionPerLevel;
	public double flatSlownessReduction;

	public ArmorEnchantmentWeight(@Nullable ResourceLocation item, double slownessReductionPerLevel, double flatSlownessReduction) {
		super(item, null);
		this.slownessReductionPerLevel = slownessReductionPerLevel;
		this.flatSlownessReduction = flatSlownessReduction;
	}

	public ArmorEnchantmentWeight(@Nullable ResourceLocation item, double slownessReductionPerLevel) {
		this(item, slownessReductionPerLevel, 0d);
	}

	@Nullable
	public static ArmorEnchantmentWeight parseLine(String line) {
		String[] split = line.split(",");
		if (split.length < 2 || split.length > 3) {
			LogHelper.warn("Invalid line \"%s\" for Armor Enchantment Weight", line);
			return null;
		}
		ResourceLocation enchantment = ResourceLocation.tryParse(split[0]);
		if (enchantment == null) {
			LogHelper.warn("%s enchantment for Armor Enchantment Weight is not valid", split[0]);
			return null;
		}
		if (!ForgeRegistries.ENCHANTMENTS.containsKey(enchantment)) {
			LogHelper.warn(String.format("%s enchantment for Armor Enchantment Weight seems to not exist", split[0]));
			return null;
		}
		if (!NumberUtils.isParsable(split[1])) {
			LogHelper.warn(String.format("Invalid slowness reduction per level \"%s\" for Armor Enchantment Weight", line));
			return null;
		}
		double slownessReductionPerLevel = Double.parseDouble(split[1]);
		if (slownessReductionPerLevel < 0d || slownessReductionPerLevel > 1d)
			LogHelper.warn(String.format("Slowness reduction per level \"%s\" for Armor Enchantment Weight has been clamped between 0.0 and 1.0", line));
		slownessReductionPerLevel = Mth.clamp(slownessReductionPerLevel, 0d, 1d);
		double flatSlownessReduction = 0d;
		if (split.length >= 3) {
			if (!NumberUtils.isParsable(split[2])) {
				LogHelper.warn(String.format("Invalid flat slowness reduction \"%s\" for Armor Enchantment Weight", line));
				return null;
			}
			flatSlownessReduction = Double.parseDouble(split[2]);
			if (flatSlownessReduction < 0d || flatSlownessReduction > 1d)
				LogHelper.warn(String.format("Flat slowness reduction \"%s\" for Armor Enchantment Weight has been clamped between 0.0 and 1.0", line));
			flatSlownessReduction = Mth.clamp(flatSlownessReduction, 0d, 1d);
		}
		return new ArmorEnchantmentWeight(enchantment, slownessReductionPerLevel, flatSlownessReduction);
	}

	public static ArrayList<ArmorEnchantmentWeight> parseStringList(List<? extends String> list) {
		ArrayList<ArmorEnchantmentWeight> armorEnchantmentWeight = new ArrayList<>();
		for (String line : list) {
			ArmorEnchantmentWeight idTagMatcher = ArmorEnchantmentWeight.parseLine(line);
			if (idTagMatcher != null)
				armorEnchantmentWeight.add(idTagMatcher);
		}
		return armorEnchantmentWeight;
	}
}