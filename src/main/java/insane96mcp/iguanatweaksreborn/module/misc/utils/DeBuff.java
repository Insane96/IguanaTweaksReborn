package insane96mcp.iguanatweaksreborn.module.misc.utils;

import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DeBuff {
	public Stat stat;
	public double min, max;
	public MobEffect effect;
	public int amplifier;

	public DeBuff(Stat stat, double min, double max, MobEffect effect, int amplifier) {
		this.stat = stat;
		this.min = min;
		this.max = max;
		this.effect = effect;
		this.amplifier = amplifier;
	}

	@Override
	public String toString() {
		return String.format("DeBuff{stat: %s, min: %f, max: %f, effect: %s, amplifier: %d}", stat, min, max, effect.getDisplayName(), amplifier);
	}

	public enum Stat {
		HUNGER,
		HEALTH,
		EXPERIENCE_LEVEL
	}

	@Nullable
	public static DeBuff parseLine(String line) {
		//Split
		String[] split = line.split(",");
		if (split.length != 4) {
			LogHelper.warn("Invalid line \"%s\" for Debuff", line);
			return null;
		}
		//Stat
		DeBuff.Stat stat = Utils.searchEnum(DeBuff.Stat.class, split[0]);
		if (stat == null) {
			LogHelper.warn(String.format("Invalid stat name \"%s\" for Debuff", line));
			return null;
		}

		//Range
		double min = -Double.MAX_VALUE, max = Double.MAX_VALUE;
		if (split[1].contains("..")) {
			String[] rangeSplit = split[1].split("\\.\\.");
			if (rangeSplit.length < 1 || rangeSplit.length > 2) {
				LogHelper.warn(String.format("Invalid range \"%s\" for Debuff", line));
				return null;
			}
			if (rangeSplit[0].length() > 0) {
				if (!NumberUtils.isParsable(rangeSplit[0])) {
					LogHelper.warn(String.format("Invalid range \"%s\" for Debuff", line));
					return null;
				}
				min = Double.parseDouble(rangeSplit[0]);
			}
			if (rangeSplit.length == 2 && rangeSplit[1].length() > 0) {
				if (!NumberUtils.isParsable(rangeSplit[1])) {
					LogHelper.warn(String.format("Invalid range \"%s\" for Debuff", line));
					return null;
				}
				max = Double.parseDouble(rangeSplit[1]);
			}
		}
		else {
			if (!NumberUtils.isParsable(split[1])) {
				LogHelper.warn(String.format("Invalid range \"%s\" for Debuff", line));
				return null;
			}
			double value = Double.parseDouble(split[1]);
			min = value;
			max = value;
		}

		//Potion effect
		ResourceLocation effectRL = ResourceLocation.tryParse(split[2]);
		if (effectRL == null) {
			LogHelper.warn("%s potion effect for Debuff is not valid", split[2]);
			return null;
		}
		if (!ForgeRegistries.MOB_EFFECTS.containsKey(effectRL)) {
			LogHelper.warn("%s potion effect for Debuff seems to not exist", split[2]);
			return null;
		}
		MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(effectRL);

		//Amplifier
		if (!NumberUtils.isParsable(split[3])) {
			LogHelper.warn(String.format("Invalid amplifier \"%s\" for Debuff", line));
			return null;
		}
		int amplifier = Integer.parseInt(split[3]);

		return new DeBuff(stat, min, max, effect, amplifier);
	}

	public static ArrayList<? extends DeBuff> parseStringList(List<? extends String> list) {
		ArrayList<DeBuff> deBuffs = new ArrayList<>();
		for (String line : list) {
			DeBuff deBuff = DeBuff.parseLine(line);
			if (deBuff != null)
				deBuffs.add(deBuff);
		}

		return deBuffs;
	}
}