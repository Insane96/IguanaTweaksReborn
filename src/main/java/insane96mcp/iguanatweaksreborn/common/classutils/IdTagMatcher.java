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
            throw new NullPointerException("'id' and 'tag' can't be both null");

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
            LogHelper.Warn("Invalid line \"%s\". Format must be modid:item_or_block_id,modid:dimension", line);
            return null;
        }
        ResourceLocation dimension = MCUtils.AnyRL;
        if (split.length == 2) {
            dimension = ResourceLocation.tryCreate(split[1]);
            if (dimension == null) {
                LogHelper.Warn(String.format("Invalid dimension \"%s\". Ignoring it", split[1]));
                dimension = MCUtils.AnyRL;
            }
        }
        if (split[0].startsWith("#")) {
            String replaced = split[0].replace("#", "");
            ResourceLocation tag = ResourceLocation.tryCreate(replaced);
            if (tag == null) {
                LogHelper.Warn("%s tag is not valid", replaced);
                return null;
            }
            return new IdTagMatcher(null, tag, dimension);
        }
        else {
            ResourceLocation id = ResourceLocation.tryCreate(split[0]);
            if (id == null) {
                LogHelper.Warn("%s id is not valid", line);
                return null;
            }
            if (ForgeRegistries.BLOCKS.containsKey(id) || ForgeRegistries.ITEMS.containsKey(id)) {
                return new IdTagMatcher(id, null, dimension);
            }
            else {
                LogHelper.Warn(String.format("%s id seems to not exist", line));
                return null;
            }
        }
    }
}