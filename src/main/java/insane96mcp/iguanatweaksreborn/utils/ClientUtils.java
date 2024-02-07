package insane96mcp.iguanatweaksreborn.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ClientUtils {

	public static void setRenderColor(float r, float g, float b, float alpha) {
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(r, g, b, alpha);
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	public static void resetRenderColor() {
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public static void blitVericallyMirrored(ResourceLocation texture, GuiGraphics guiGraphics, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
		guiGraphics.innerBlit(texture, x, x + width, y, y + height, 0, (u + (float)width) / (float)textureWidth, (u + 0.0F) / (float)textureWidth, (v + 0.0F) / (float)textureHeight, (v + (float)height) / (float)textureHeight);
	}
}
