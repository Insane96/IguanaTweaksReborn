package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.client.feature.Sound;
import net.minecraft.sounds.Musics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Musics.class)
public class MusicsMixin {
    @ModifyConstant(method = "createGameMusic", constant = { @Constant(intValue = 12000), @Constant(intValue = 24000) })
    private static int onCreateGameMusicDelay(int delay) {
        return (int) (delay * Sound.getMusicDelayMultiplier());
    }
}
