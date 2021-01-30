package insane96mcp.iguanatweaksreborn.modules.stacksize.classutils;


import insane96mcp.iguanatweaksreborn.common.classutils.IdTagMatcher;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class CustomStackSize extends IdTagMatcher {
    public int stackSize;

    public CustomStackSize(@Nullable ResourceLocation id, @Nullable ResourceLocation tag, int stackSize) {
        super(id, tag);
        this.stackSize = stackSize;
    }
}