package insane96mcp.iguanatweaksreborn.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import insane96mcp.iguanatweaksreborn.module.client.Misc;
import insane96mcp.insanelib.base.Feature;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DeathScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin {

	@WrapOperation(method = "setButtonsActive", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/Button;active:Z"))
	public void onSetButtonsActive(Button instance, boolean value, Operation<Void> original) {
		instance.visible = value;
		original.call(instance, value);
	}

	@ModifyExpressionValue(method = "tick", at = @At(value = "CONSTANT", args = "intValue=20"))
	public int onTick(int original) {
		if (!Feature.isEnabled(Misc.class)
				|| !Misc.thirdPersonOnDeath)
			return original;
		return 50;
	}
}
