package insane96mcp.iguanatweaksreborn.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import insane96mcp.iguanatweaksreborn.module.client.Misc;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public class GuiMixin {
    @ModifyExpressionValue(method = "renderHotbar", at = @At(value = "CONSTANT", args = "intValue=22", ordinal = 0))
    public int hotBarShift(int original) {
        if (!Misc.shouldRaiseHotbar())
            return original;
        return original + Misc.floatyHotbar;
    }
    @ModifyExpressionValue(method = "renderHotbar", at = @At(value = "CONSTANT", args = "intValue=22", ordinal = 2))
    public int selectedHotBarShift(int original) {
        if (!Misc.shouldRaiseHotbar())
            return original;
        return original + Misc.floatyHotbar;
    }
    @ModifyExpressionValue(method = "renderHotbar", at = @At(value = "CONSTANT", args = "intValue=22", ordinal = 4))
    public int renderWholeSelectedHotBar(int original) {
        if (!Misc.shouldRaiseHotbar())
            return original;
        return 24;
    }
    @ModifyExpressionValue(method = "renderHotbar", at = @At(value = "CONSTANT", args = "intValue=23", ordinal = 0))
    public int offHandHotbarShift(int original) {
        if (!Misc.shouldRaiseHotbar())
            return original;
        return original + Misc.floatyHotbar;
    }
    @ModifyExpressionValue(method = "renderHotbar", at = @At(value = "CONSTANT", args = "intValue=23", ordinal = 1))
    public int selectedOffHandHotbarShift(int original) {
        if (!Misc.shouldRaiseHotbar())
            return original;
        return original + Misc.floatyHotbar;
    }
    @ModifyExpressionValue(method = "renderHotbar", at = @At(value = "CONSTANT", args = "intValue=16", ordinal = 0))
    public int stacksShift(int original) {
        if (!Misc.shouldRaiseHotbar())
            return original;
        return original + Misc.floatyHotbar;
    }
    @ModifyExpressionValue(method = "renderHotbar", at = @At(value = "CONSTANT", args = "intValue=16", ordinal = 1))
    public int offHandStacksShift(int original) {
        if (!Misc.shouldRaiseHotbar())
            return original;
        return original + Misc.floatyHotbar;
    }
    @ModifyExpressionValue(method = "renderHotbar", at = @At(value = "CONSTANT", args = "intValue=20", ordinal = 2))
    public int attackIndicatorShift(int original) {
        if (!Misc.shouldRaiseHotbar())
            return original;
        return original + Misc.floatyHotbar;
    }
    @ModifyExpressionValue(method = "renderHotbar", at = @At(value = "CONSTANT", args = "intValue=18", ordinal = 2))
    public int attackIndicatorShift2(int original) {
        if (!Misc.shouldRaiseHotbar())
            return original;
        return original + Misc.floatyHotbar;
    }
    @ModifyExpressionValue(method = "renderExperienceBar", at = @At(value = "CONSTANT", args = "intValue=32", ordinal = 0))
    public int experienceBarShift(int original) {
        if (!Misc.shouldRaiseHotbar())
            return original;
        return original + Misc.floatyHotbar;
    }
    @ModifyExpressionValue(method = "renderExperienceBar", at = @At(value = "CONSTANT", args = "intValue=31", ordinal = 0))
    public int experienceBarLevelsShift(int original) {
        if (!Misc.shouldRaiseHotbar())
            return original;
        return original + Misc.floatyHotbar;
    }
    @ModifyExpressionValue(method = "renderJumpMeter", at = @At(value = "CONSTANT", args = "intValue=32", ordinal = 0))
    public int jumpMeterShift(int original) {
        if (!Misc.shouldRaiseHotbar())
            return original;
        return original + Misc.floatyHotbar;
    }
}
