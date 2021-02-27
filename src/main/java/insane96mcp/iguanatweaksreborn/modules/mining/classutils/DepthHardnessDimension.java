package insane96mcp.iguanatweaksreborn.modules.mining.classutils;

import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;

/**
 * In this case the {@link DepthHardnessDimension#multiplier} field is used per block below the {@link DepthHardnessDimension#applyBelowY} level
 */
public class DepthHardnessDimension extends DimensionHardnessMultiplier {

    public int applyBelowY;
    public int capY;

    public DepthHardnessDimension(ResourceLocation dimension, double multiplier, int applyBelowY, int capY) {
        super(dimension, multiplier);
        this.applyBelowY = applyBelowY;
        this.capY = capY;
    }

    @Nullable
    public static DepthHardnessDimension parseLine(String line) {
        String[] split = line.split(",");
        if (split.length != 4) {
            LogHelper.Warn("Invalid line \"%s\" for Depth Hardness Dimension. Format must be modid:dimensionId,hardness,applyBelowY,capY", line);
            return null;
        }
        ResourceLocation dimension = ResourceLocation.tryCreate(split[0]);
        if (dimension == null) {
            LogHelper.Warn(String.format("Invalid dimension \"%s\" for Depth Hardness Dimension", split[0]));
            return null;
        }

        if (!NumberUtils.isParsable(split[1])) {
            LogHelper.Warn(String.format("Invalid hardness \"%s\" for Depth Hardness Dimension", split[1]));
            return null;
        }
        double hardness = Double.parseDouble(split[1]);

        if (!NumberUtils.isParsable(split[2])) {
            LogHelper.Warn(String.format("Invalid Y Level \"%s\" for Depth Hardness Dimension", split[2]));
            return null;
        }
        int applyBelowY = Integer.parseInt(split[2]);

        if (!NumberUtils.isParsable(split[3])) {
            LogHelper.Warn(String.format("Invalid Y cap \"%s\" for Depth Hardness Dimension", split[3]));
            return null;
        }
        int capY = Integer.parseInt(split[3]);

        return new DepthHardnessDimension(dimension, hardness, applyBelowY, capY);
    }
}