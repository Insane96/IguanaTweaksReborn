package insane96mcp.iguanatweaksreborn.common.classutils;


import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.iguanatweaksreborn.utils.MCUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class IdTagMatcher {
    public ResourceLocation id;
    public ResourceLocation tag;
    public ResourceLocation dimension;

    public IdTagMatcher(@Nullable ResourceLocation id, @Nullable ResourceLocation tag, ResourceLocation dimension) {
        if (id == null && tag == null)
            throw new NullPointerException("'block' and 'tag' can't be both null");

        this.id = id;
        this.tag = tag;
        this.dimension = dimension;
    }

    public IdTagMatcher(@Nullable ResourceLocation id, @Nullable ResourceLocation tag) {
        this(id, tag, MCUtils.AnyRL);
    }

    @Nullable
    public static IdTagMatcher parseLine(String line) {
        String[] split = line.split(",");
        if (split.length < 1 || split.length > 2) {
            LogHelper.Warn("Invalid line \"%s\" for Hardnesses Blacklist. Format must be modid:blockid,modid:dimension", line);
            return null;
        }
        ResourceLocation dimension = MCUtils.AnyRL;
        if (split.length == 2) {
            dimension = ResourceLocation.tryCreate(split[1]);
            if (dimension == null) {
                LogHelper.Warn(String.format("Invalid dimension \"%s\" for Hardness Blacklist. Ignoring it", split[1]));
                dimension = MCUtils.AnyRL;
            }
        }
        if (split[0].startsWith("#")) {
            String replaced = split[0].replace("#", "");
            ResourceLocation tag = ResourceLocation.tryCreate(replaced);
            if (tag == null) {
                LogHelper.Warn("%s tag for Hardness Blacklist is not valid", replaced);
                return null;
            }
            IdTagMatcher hardness = new IdTagMatcher(null, tag, dimension);
            return hardness;
        }
        else {
            ResourceLocation block = ResourceLocation.tryCreate(split[0]);
            if (block == null) {
                LogHelper.Warn("%s block for Hardness Blacklist is not valid", line);
                return null;
            }
            if (ForgeRegistries.BLOCKS.containsKey(block)) {
                IdTagMatcher hardness = new IdTagMatcher(block, null, dimension);
                return hardness;
            }
            else {
                LogHelper.Warn(String.format("%s block for Hardness Blacklist seems to not exist", line));
                return null;
            }
        }
    }
}