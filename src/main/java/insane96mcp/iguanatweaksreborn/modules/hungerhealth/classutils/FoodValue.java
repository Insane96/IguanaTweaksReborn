package insane96mcp.iguanatweaksreborn.modules.hungerhealth.classutils;

import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;

public class FoodValue {
    public ResourceLocation id;
    public int hunger;
    public float saturation;

    public FoodValue(ResourceLocation id, int hunger, float saturation) {
        this.id = id;
        this.hunger = hunger;
        this.saturation = saturation;
    }

    @Nullable
    public static FoodValue parseLine(String line) {
        String[] split = line.split(",");
        if (split.length < 2 || split.length > 3) {
            LogHelper.warn("Invalid line \"%s\" for Custom Food Value", line);
            return null;
        }
        if (!NumberUtils.isParsable(split[1])) {
            LogHelper.warn(String.format("Invalid hunger \"%s\" for Custom Food Value", line));
            return null;
        }
        int hunger = Integer.parseInt(split[1]);
        float saturation = -1f;
        if (split.length == 3) {
            if (!NumberUtils.isParsable(split[2])) {
                LogHelper.warn(String.format("Invalid saturation \"%s\" for Custom Food Value", line));
                return null;
            }
            saturation = Float.parseFloat(split[2]);
        }
        ResourceLocation item = ResourceLocation.tryCreate(split[0]);
        if (item == null) {
            LogHelper.warn("%s item for Custom Food Value is not valid", split[0]);
            return null;
        }
        if (!ForgeRegistries.ITEMS.containsKey(item) || !ForgeRegistries.ITEMS.getValue(item).isFood()) {
            LogHelper.warn(String.format("%s item for Custom Food Value seems to not exist or is not a food", split[0]));
            return null;
        }
        return new FoodValue(item, hunger, saturation);
    }

    @Override
    public String toString() {
        return "FoodValue{" +
                "id=" + id +
                ", hunger=" + hunger +
                ", saturation=" + saturation +
                '}';
    }
}