package insane96mcp.iguanatweaksreborn.modules.stacksize.classutils;


import insane96mcp.iguanatweaksreborn.common.classutils.IdTagMatcher;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;

public class CustomStackSize extends IdTagMatcher {
    public int stackSize;

    public CustomStackSize(@Nullable ResourceLocation id, @Nullable ResourceLocation tag, int stackSize) {
        super(id, tag);
        this.stackSize = stackSize;
    }

    @Nullable
    public static CustomStackSize parseLine(String line) {
        String[] split = line.split(",");
        if (split.length != 2) {
            LogHelper.Warn("Invalid line \"%s\" for Custom Stack Size", line);
            return null;
        }
        if (!NumberUtils.isParsable(split[1])) {
            LogHelper.Warn(String.format("Invalid stackSize \"%s\" for Custom Stack Size", line));
            return null;
        }
        int stackSize = Integer.parseInt(split[1]);
        if (split[0].startsWith("#")) {
            String replaced = split[0].replace("#", "");
            ResourceLocation tag = ResourceLocation.tryCreate(replaced);
            if (tag == null) {
                LogHelper.Warn("%s tag for Custom Stack Size is not valid", replaced);
                return null;
            }
            return new CustomStackSize(null, tag, stackSize);
        }
        else {
            ResourceLocation item = ResourceLocation.tryCreate(split[0]);
            if (item == null) {
                LogHelper.Warn("%s item for Custom Stack Size is not valid", split[0]);
                return null;
            }
            if (!ForgeRegistries.ITEMS.containsKey(item)) {
                LogHelper.Warn(String.format("%s item for Custom Stack Size seems to not exist", split[0]));
                return null;
            }

            return new CustomStackSize(item, null, stackSize);
        }
    }
}