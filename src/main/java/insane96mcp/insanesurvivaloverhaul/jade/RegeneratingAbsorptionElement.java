package insane96mcp.insanesurvivaloverhaul.jade;

import insane96mcp.insanesurvivaloverhaul.module.combat.regeneratingabsorption.RegeneratingAbsorptionClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.ui.Element;

public class RegeneratingAbsorptionElement extends Element {
    private static final ResourceLocation ICON = RegeneratingAbsorptionClient.GUI_ICONS;
    private static final int ICONS_PER_LINE = 10;

    private final int points;
    private final int iconCount;
    private final int iconsPerLine;
    private final int lineCount;

    public RegeneratingAbsorptionElement(float absorption) {
        // Mirrors RegeneratingAbsorptionClient's HUD bar: points is a whole count of raw absorption points
        // (not halved), and each tile pairs up two points via the texture's two complementary frames.
        points = Mth.ceil(absorption);
        iconCount = (points + 1) / 2;
        iconsPerLine = Math.min(ICONS_PER_LINE, iconCount);
        lineCount = Mth.ceil((float) iconCount / ICONS_PER_LINE);
    }

    @Override
    public Vec2 getSize() {
        return new Vec2(8 * iconsPerLine + 1, 5 + 4 * lineCount);
    }

    @Override
    public void render(GuiGraphics guiGraphics, float x, float y, float maxX, float maxY) {
        int xOffset = (iconCount - 1) % iconsPerLine * 8;
        int yOffset = lineCount * 4 - 4;
        for (int tile = iconCount; tile > 0; tile--) {
            int xPos = (int) (x + xOffset);
            int yPos = (int) (y + yOffset);
            guiGraphics.blit(ICON, xPos, yPos, 9, 0, 9, 9, 18, 18);
            if (tile * 2 <= points)
                guiGraphics.blit(ICON, xPos, yPos, 0, 0, 9, 9, 18, 18);

            xOffset -= 8;
            if (xOffset < 0) {
                xOffset = iconsPerLine * 8 - 8;
                yOffset -= 4;
            }
        }
    }
}
