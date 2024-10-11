package insane96mcp.iguanatweaksreborn.mixin.client;

import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Options.class)
public class OptionsMixin {

	/*@ModifyReturnValue(method = "getEffectiveRenderDistance", at = @At("RETURN"))
	public int renderDistance(int original) {
		return ClientWeather.getRenderDistance(original);
	}*/
}
