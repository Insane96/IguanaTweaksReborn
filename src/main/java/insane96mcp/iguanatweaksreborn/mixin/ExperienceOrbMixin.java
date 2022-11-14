package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.experience.feature.OtherExperience;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExperienceOrb.class)
public class ExperienceOrbMixin {
	//TODO move to https://github.com/VazkiiMods/Quark/blob/c235dfed0b218f96ea514889949ff05fb5e3a0dc/src/main/java/vazkii/quark/content/experimental/module/GameNerfsModule.java#L75
	@Inject(at = @At("HEAD"), method = "repairPlayerItems", cancellable = true)
	public void repairPlayerItems(Player player, int p_147094_, CallbackInfoReturnable<Integer> callbackInfo) {
		if (OtherExperience.isUnmendingEnabled())
			callbackInfo.setReturnValue(p_147094_);
	}
}
