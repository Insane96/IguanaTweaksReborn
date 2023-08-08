package insane96mcp.survivalreimagined.module.client;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.ClientModules;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Label(name = "Sounds & Music", description = "Changes to sounds and music")
@LoadFeature(module = ClientModules.Ids.CLIENT, enabledByDefault = false)
public class Sound extends Feature {

    @Config
    @Label(name = "Music delay multiplier", description = "Multiplies the time it takes for music to play by this value (in vanilla, normal music plays each 10 to 20 minutes).")
    public static Double musicDelayMultiplier = 0.5d;

    public Sound(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @Override
    public void readConfig(ModConfigEvent event) {
        super.readConfig(event);

        Musics.END = new Music(SoundEvents.MUSIC_END, (int) (6000 * getMusicDelayMultiplier()), (int) (24000 * getMusicDelayMultiplier()), true);
    }

    public static double getMusicDelayMultiplier() {
        return !isEnabled(Sound.class) ? 1d : musicDelayMultiplier;
    }
}
