package insane96mcp.iguanatweaksreborn.modules.mining.classutils;


import insane96mcp.iguanatweaksreborn.common.classutils.IdTagMatcher;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;

public class BlockHardness extends IdTagMatcher {
    public double hardness;
    public boolean has0Hardness;

    public BlockHardness(@Nullable ResourceLocation block, @Nullable ResourceLocation tag, Double hardness, ResourceLocation dimension) {
        super(block, tag, dimension);
        this.hardness = hardness;
    }

    @Nullable
    public static BlockHardness parseLine(String line) {
        String[] split = line.split(",");
        if (split.length < 2 || split.length > 3) {
            LogHelper.warn("Invalid line \"%s\" for Custom Hardnesses", line);
            return null;
        }
        if (!NumberUtils.isParsable(split[1])) {
            LogHelper.warn(String.format("Invalid hardness \"%s\" for Custom Hardnesses", line));
            return null;
        }
        double hardness = Double.parseDouble(split[1]);
		ResourceLocation dimension = AnyRL;
        if (split.length == 3) {
            dimension = ResourceLocation.tryCreate(split[2]);
            if (dimension == null) {
                LogHelper.warn(String.format("Invalid dimension \"%s\" for Custom Hardnesses. Ignoring it", split[2]));
                dimension = AnyRL;
            }
        }
        if (split[0].startsWith("#")) {
            String replaced = split[0].replace("#", "");
            ResourceLocation tag = ResourceLocation.tryCreate(replaced);
            if (tag == null) {
                LogHelper.warn("%s tag for Custom Hardneses is not valid", replaced);
                return null;
            }
            return new BlockHardness(null, tag, hardness, dimension);
        }
        else {
            ResourceLocation block = ResourceLocation.tryCreate(split[0]);
            if (block == null) {
                LogHelper.warn("%s block for Custom Hardneses is not valid", split[0]);
                return null;
            }
            if (ForgeRegistries.BLOCKS.containsKey(block)) {
                return new BlockHardness(block, null, hardness, dimension);
            }
            else {
                LogHelper.warn(String.format("%s block for Custom Hardnesses seems to not exist", split[0]));
                return null;
            }
        }
    }
}