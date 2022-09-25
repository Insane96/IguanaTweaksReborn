package insane96mcp.iguanatweaksreborn.module.sleeprespawn.utils;

import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EnergyBoostItem extends IdTagMatcher {
    public int duration;
    public int amplifier;

    public EnergyBoostItem(Type type, ResourceLocation location) {
        super(type, location);
        this.duration = 0;
        this.amplifier = 0;
    }

    public EnergyBoostItem(Type type, ResourceLocation location, int duration, int amplifier) {
        super(type, location);
        this.duration = duration;
        this.amplifier = amplifier;
    }

    @Nullable
    public static EnergyBoostItem parseLine(String line) {
        String[] split = line.split(",");
        if (split.length != 1 && split.length != 3) {
            LogHelper.warn("Invalid line \"%s\" for Energy Boost Item", line);
            return null;
        }
        Type type;
        ResourceLocation resourceLocation;
        if (split[0].startsWith("#")) {
            String replaced = split[0].replace("#", "");
            ResourceLocation tag = ResourceLocation.tryParse(replaced);
            if (tag == null) {
                LogHelper.warn("%s tag for Energy Boost Item is not valid", replaced);
                return null;
            }
            type = Type.TAG;
            resourceLocation = tag;
        }
        else {
            ResourceLocation item = ResourceLocation.tryParse(split[0]);
            if (item == null) {
                LogHelper.warn("%s item for Energy Boost Item is not valid", split[0]);
                return null;
            }
            if (!ForgeRegistries.ITEMS.containsKey(item)) {
                LogHelper.warn(String.format("%s item for Energy Boost Item seems to not exist", split[0]));
                return null;
            }
            type = Type.ID;
            resourceLocation = item;
        }
        if (split.length == 1) {
            return new EnergyBoostItem(type, resourceLocation);
        }
        else {
            if (!NumberUtils.isParsable(split[1])) {
                LogHelper.warn(String.format("Invalid duration \"%s\" for Energy Boost Item", line));
                return null;
            }
            int duration = Integer.parseInt(split[1]);

            if (!NumberUtils.isParsable(split[2])) {
                LogHelper.warn(String.format("Invalid amplifier \"%s\" for Energy Boost Item", line));
                return null;
            }
            int amplifier = Integer.parseInt(split[2]);
            return new EnergyBoostItem(type, resourceLocation, duration, amplifier);
        }
    }

    public static ArrayList<EnergyBoostItem> parseStringList(List<? extends String> list) {
        ArrayList<EnergyBoostItem> stackSizes = new ArrayList<>();
        for (String line : list) {
            EnergyBoostItem energyBoostItem = EnergyBoostItem.parseLine(line);
            if (energyBoostItem != null)
                stackSizes.add(energyBoostItem);
        }
        return stackSizes;
    }

}