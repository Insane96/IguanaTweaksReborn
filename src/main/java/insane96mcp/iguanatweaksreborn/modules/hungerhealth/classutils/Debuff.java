package insane96mcp.iguanatweaksreborn.modules.hungerhealth.classutils;

import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.iguanatweaksreborn.utils.Utils;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;

public class Debuff {
    public Stat stat;
    public double min, max;
    public Effect effect;
    public int amplifier;

    public Debuff(Stat stat, double min, double max, Effect effect, int amplifier) {
        this.stat = stat;
        this.min = min;
        this.max = max;
        this.effect = effect;
        this.amplifier = amplifier;
    }

    @Override
    public String toString() {
        return String.format("Debuff{stat: %s, min: %f, max: %f, effect: %s, amplifier: %d}", stat, min, max, effect.getRegistryName(), amplifier);
    }

    public enum Stat {
        HUNGER,
        HEALTH,
        EXPERIENCE_LEVEL,
        TIREDNESS
    }

    @Nullable
    public static Debuff parseLine(String line) {
        //Split
        String[] split = line.split(",");
        if (split.length != 4) {
            LogHelper.Warn("Invalid line \"%s\" for Debuff", line);
            return null;
        }
        //Stat
        Debuff.Stat stat = Utils.searchEnum(Debuff.Stat.class, split[0]);
        if (stat == null) {
            LogHelper.Warn(String.format("Invalid stat name \"%s\" for Debuff", line));
            return null;
        }

        //Range
        double min = -Double.MAX_VALUE, max = Double.MAX_VALUE;
        if (split[1].contains("..")) {
            String[] rangeSplit = split[1].split("\\.\\.");
            if (rangeSplit.length < 1 || rangeSplit.length > 2) {
                LogHelper.Warn(String.format("Invalid range \"%s\" for Debuff", line));
                return null;
            }
            if (rangeSplit[0].length() > 0) {
                if (!NumberUtils.isParsable(rangeSplit[0])) {
                    LogHelper.Warn(String.format("Invalid range \"%s\" for Debuff", line));
                    return null;
                }
                min = Double.parseDouble(rangeSplit[0]);
            }
            if (rangeSplit.length == 2 && rangeSplit[1].length() > 0) {
                if (!NumberUtils.isParsable(rangeSplit[1])) {
                    LogHelper.Warn(String.format("Invalid range \"%s\" for Debuff", line));
                    return null;
                }
                max = Double.parseDouble(rangeSplit[1]);
            }
        }
        else {
            if (!NumberUtils.isParsable(split[1])) {
                LogHelper.Warn(String.format("Invalid range \"%s\" for Debuff", line));
                return null;
            }
            double value = Double.parseDouble(split[1]);
            min = value;
            max = value;
        }

        //Potion effect
        ResourceLocation effectRL = ResourceLocation.tryCreate(split[2]);
        if (effectRL == null) {
            LogHelper.Warn("%s potion effect for Debuff is not valid", split[2]);
            return null;
        }
        if (!ForgeRegistries.POTIONS.containsKey(effectRL)) {
            LogHelper.Warn("%s potion effect for Debuff seems to not exist", split[2]);
            return null;
        }
        Effect effect = ForgeRegistries.POTIONS.getValue(effectRL);

        //Amplifier
        if (!NumberUtils.isParsable(split[3])) {
            LogHelper.Warn(String.format("Invalid amplifier \"%s\" for Debuff", line));
            return null;
        }
        int amplifier = Integer.parseInt(split[3]);

        return new Debuff(stat, min, max, effect, amplifier);
    }
}