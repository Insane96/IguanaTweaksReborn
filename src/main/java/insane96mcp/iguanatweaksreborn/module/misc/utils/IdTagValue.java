package insane96mcp.iguanatweaksreborn.module.misc.utils;

import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.util.IdTagMatcher;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class IdTagValue {
	public IdTagMatcher idTagMatcher;
	public double value;

	public IdTagValue(IdTagMatcher idTagMatcher, double value) {
		this.idTagMatcher = idTagMatcher;
		this.value = value;
	}

	@Override
	public String toString() {
		return String.format("IdTagValue{idTagMatcher: %s, value: %f}", this.idTagMatcher, this.value);
	}

	@Nullable
	public static IdTagValue parseLine(String line) {
		//Split
		String[] split = line.split(",");
		if (split.length != 2) {
			LogHelper.warn("Invalid line \"%s\" for IdTagValue", line);
			return null;
		}

		IdTagMatcher idTagMatcher = IdTagMatcher.parseLine(split[0]);
		if (idTagMatcher == null) {
			LogHelper.warn(String.format("Invalid block/tag \"%s\" for IdTagValue", line));
			return null;
		}
		if (!NumberUtils.isParsable(split[1])) {
			LogHelper.warn(String.format("Invalid value \"%s\" for IdTagValue", line));
			return null;
		}
		double value = Double.parseDouble(split[1]);

		return new IdTagValue(idTagMatcher, value);
	}

	public static ArrayList<IdTagValue> parseStringList(List<? extends String> list) {
		ArrayList<IdTagValue> deBuffs = new ArrayList<>();
		for (String line : list) {
			IdTagValue deBuff = IdTagValue.parseLine(line);
			if (deBuff != null)
				deBuffs.add(deBuff);
		}

		return deBuffs;
	}
}