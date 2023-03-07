package insane96mcp.iguanatweaksreborn.module.hungerhealth.feature;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.base.config.MinMax;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "No Hunger", description = "Remove hunger and get back to the Beta 1.7.3 days.")
@LoadFeature(module = Modules.Ids.HUNGER_HEALTH)
public class NoHunger extends Feature {

    @Config
    @Label(name = "Disable Hunger", description = "Completely disables the entire hunger system, from the hunger bar, to the health regen that comes with it.")
    public static Boolean disableHunger = true;
    @Config
    @Label(name = "Passive Health Regen.Enable Passive Health Regen", description = "If true, Passive Regeneration is enabled")
    public static Boolean enablePassiveRegen = true;
    @Config
    @Label(name = "Passive Health Regen.Passive Regeneration Speed Easy", description = "Min represents how many seconds the regeneration of 1 HP takes when health is 100%, Max how many seconds when health is 0%. This applies for easy and peaceful difficulty")
    public static MinMax passiveRegenerationTimeEasy = new MinMax(5, 30);
    @Config
    @Label(name = "Passive Health Regen.Passive Regeneration Speed Normal", description = "Min represents how many seconds the regeneration of 1 HP takes when health is 100%, Max how many seconds when health is 0%. This applies for normal difficulty")
    public static MinMax passiveRegenerationTimeNormal = new MinMax(7.5, 45);
    @Config
    @Label(name = "Passive Health Regen.Passive Regeneration Speed Hard", description = "Min represents how many seconds the regeneration of 1 HP takes when health is 100%, Max how many seconds when health is 0%. This applies for hard difficulty")
    public static MinMax passiveRegenerationTimeHard = new MinMax(10, 60);

    public NoHunger(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    private static int getPassiveRegenSpeed(Player player) {
        float healthPerc = 1 - (player.getHealth() / player.getMaxHealth());
        int secs;
        if (player.level.getDifficulty().equals(Difficulty.HARD)) {
            secs = (int) ((passiveRegenerationTimeHard.max - passiveRegenerationTimeHard.min) * healthPerc + passiveRegenerationTimeHard.min);
        }
        else if (player.level.getDifficulty().equals(Difficulty.NORMAL)) {
            secs = (int) ((passiveRegenerationTimeNormal.max - passiveRegenerationTimeNormal.min) * healthPerc + passiveRegenerationTimeNormal.min);
        }
        else {
            secs = (int) ((passiveRegenerationTimeEasy.max - passiveRegenerationTimeEasy.min) * healthPerc + passiveRegenerationTimeEasy.min);
        }
        return secs * 20;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!this.isEnabled()
                || !disableHunger)
            return;

        event.player.getFoodData().foodLevel = 15;

        if (enablePassiveRegen && event.player.isHurt()) {
            //TODO Change into a custom tick var
            ++event.player.getFoodData().tickTimer;
            int passiveRegen = getPassiveRegenSpeed(event.player);

            if (event.player.getFoodData().tickTimer > passiveRegen) {
                event.player.heal(1.0F);
                event.player.getFoodData().tickTimer = 0;
            }
        }
    }

    public static class PassiveHealthRegen {

    }
}
