package insane96mcp.survivalreimagined.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;

public class ClientUtils {
    public static void blitVericallyMirrored(PoseStack poseStack, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
        GuiComponent.innerBlit(poseStack.last().pose(), x, x + width, y, y + height, 0, (u + (float)width) / (float)textureWidth, (u + 0.0F) / (float)textureWidth, (v + 0.0F) / (float)textureHeight, (v + (float)height) / (float)textureHeight);
    }
}
