package insane96mcp.iguanatweaksreborn.mixin.client;

import insane96mcp.iguanatweaksreborn.module.client.Misc;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

	@ModifyVariable(at = @At(value = "STORE"), method = "renderWorldBorder", ordinal = 4)
	private double onWorldBorderHeight(double value) {
		if (Misc.shouldShortenWorldBorder())
			return Math.min(Misc.capWorldBorderHeight, value / 4d);
		return value;
	}

	@ModifyVariable(at = @At(value = "STORE", ordinal = 2), method = "renderWorldBorder", ordinal = 1)
	private double onWorldBorderAlpha(double value) {
		return value * Misc.getWorldBorderTransparencyMultiplier();
	}
}
