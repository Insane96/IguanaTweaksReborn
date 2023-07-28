package insane96mcp.survivalreimagined.utils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class ClientUtils {
    public static void blitVericallyMirrored(ResourceLocation texture, GuiGraphics guiGraphics, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
        guiGraphics.innerBlit(texture, x, x + width, y, y + height, 0, (u + (float)width) / (float)textureWidth, (u + 0.0F) / (float)textureWidth, (v + 0.0F) / (float)textureHeight, (v + (float)height) / (float)textureHeight);
    }
}
