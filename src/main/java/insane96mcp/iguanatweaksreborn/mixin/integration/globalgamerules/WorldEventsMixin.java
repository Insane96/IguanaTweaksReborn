package insane96mcp.iguanatweaksreborn.mixin.integration.globalgamerules;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraftforge.event.level.LevelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import se.gory_moon.globalgamerules.WorldEvents;

@Mixin(WorldEvents.class)
public class WorldEventsMixin {
    //!(locked || hardcore)
    @ModifyExpressionValue(method = "onWorldLoad", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/PrimaryLevelData;isDifficultyLocked()Z"))
    private static boolean iguanatweaksreborn$checkIfForceHardcore(boolean value, LevelEvent.Load event) {
        return value || event.getLevel().getLevelData().isHardcore();
    }
}
