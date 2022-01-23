package insane96mcp.iguanatweaksreborn.module.sleeprespawn.utils;

import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public class EffectOnWakeUp {
	public static MobEffectInstance parseLine(String line) {
		String[] split = line.split(",");
		if (split.length != 3) {
			LogHelper.warn("Invalid line \"%s\" for Effects on WakeUp. Format must be modid:potion_id,duration_ticks,amplifier", line);
			return null;
		}
		if (!NumberUtils.isParsable(split[1])) {
			LogHelper.warn(String.format("Invalid duration \"%s\" for Effects on WakeUp", split[1]));
			return null;
		}
		int duration = Integer.parseInt(split[1]);
		if (!NumberUtils.isParsable(split[2])) {
			LogHelper.warn(String.format("Invalid amplifier \"%s\" for Effects on WakeUp", split[1]));
			return null;
		}
		int amplifier = Integer.parseInt(split[2]);
		ResourceLocation mobEffect = ResourceLocation.tryParse(split[0]);
		if (mobEffect == null) {
			LogHelper.warn("%s mobEffect for Effects on WakeUp is not valid", line);
			return null;
		}
		if (!ForgeRegistries.MOB_EFFECTS.containsKey(mobEffect)) {
			LogHelper.warn(String.format("%s mobEffect for Effects on WakeUp seems to not exist", line));
			return null;
		}

		MobEffectInstance mobEffectInstance = new MobEffectInstance(ForgeRegistries.MOB_EFFECTS.getValue(mobEffect), duration, amplifier, false, false, true);
		mobEffectInstance.setCurativeItems(new ArrayList<>());
		return mobEffectInstance;
	}

	public static ArrayList<MobEffectInstance> parseStringList(List<? extends String> list) {
		ArrayList<MobEffectInstance> mobEffectInstances = new ArrayList<>();
		for (String line : list) {
			MobEffectInstance mobEffectInstance = EffectOnWakeUp.parseLine(line);
			if (mobEffectInstance != null)
				mobEffectInstances.add(mobEffectInstance);
		}
		return mobEffectInstances;
	}
}
