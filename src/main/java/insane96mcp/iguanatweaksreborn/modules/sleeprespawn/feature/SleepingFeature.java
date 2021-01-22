package insane96mcp.iguanatweaksreborn.modules.sleeprespawn.feature;

import com.google.common.collect.Lists;
import insane96mcp.iguanatweaksreborn.base.ConfigInt;
import insane96mcp.iguanatweaksreborn.base.ITConfig;
import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SleepingFeature extends ITFeature {

    @ConfigInt(min = -20, max = 20)
    @ITConfig(name = "Hunger Depleted on Wake Up", description = "How much the hunger bar is depleted when you wake up in the morning. Saturation depleted is based off this value times 2. Setting to 0 will disable this feature.")
    static int hungerDepletedOnWakeUp = 11;

    @ITConfig(name = "Effects on Wake Up", description = "A list of effects to apply to the player when he wakes up.\nThe format is modid:potion_id,duration_in_ticks,amplifier\nE.g. 'minecraft:slowness,240,1' will apply Slowness II for 12 seconds on wake up.")
    static ArrayList<String> effectsOnWakeUpConfig = Lists.newArrayList("minecraft:slowness,400,1", "minecraft:regeneration,200,1", "minecraft:weakness,300,1", "minecraft:mining_fatigue,300,1");

    static ArrayList<EffectOnWakeUp> effectsOnWakeUp;

    public SleepingFeature() {
        super("Too Hungry To Sleep", "Prevents the player from sleeping if has not enough Hunger.");
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        effectsOnWakeUp = parseEffectsOnWakeUp(effectsOnWakeUpConfig);
    }

    @SubscribeEvent
    public void wakeUpHungerAndEffects(SleepFinishedTimeEvent event) {
        if (!this.isEnabled())
            return;
        if (hungerDepletedOnWakeUp == 0 && effectsOnWakeUp.isEmpty())
            return;
        event.getWorld().getPlayers().stream().filter(LivingEntity::isSleeping).collect(Collectors.toList()).forEach((player) -> {
            player.getFoodStats().addStats(-hungerDepletedOnWakeUp, 1.0f);
            //For some reasons saturation can go below 0 so I get it back up to 0
            if (player.getFoodStats().getSaturationLevel() < 0.0f)
                player.getFoodStats().addStats(1, -player.getFoodStats().getSaturationLevel() / 2f);
            for (EffectOnWakeUp effectOnWakeUp : effectsOnWakeUp) {
                EffectInstance effectInstance = new EffectInstance(ForgeRegistries.POTIONS.getValue(effectOnWakeUp.potionId), effectOnWakeUp.duration, effectOnWakeUp.amplifier, true, true);
                player.addPotionEffect(effectInstance);
            }
        });
    }

    public static class EffectOnWakeUp {
        public ResourceLocation potionId;
        public int duration;
        public int amplifier;

        public EffectOnWakeUp(ResourceLocation potionId, int duration, int amplifier) {
            this.potionId = potionId;
            this.duration = duration;
            this.amplifier = amplifier;
        }
    }

    private static ArrayList<EffectOnWakeUp> parseEffectsOnWakeUp(List<? extends String> list) {
        ArrayList<EffectOnWakeUp> effectsOnWakeUp = new ArrayList<>();
        for (String line : list) {
            String[] split = line.split(",");
            if (split.length != 3) {
                LogHelper.Warn("Invalid line \"%s\" for Effects on WakeUp. Format must be modid:potion_id,duration_in_ticks,amplifier", line);
                continue;
            }
            if (!NumberUtils.isParsable(split[1])) {
                LogHelper.Warn(String.format("Invalid duration \"%s\" for Effects on WakeUp", split[1]));
                continue;
            }
            int duration = Integer.parseInt(split[1]);
            if (!NumberUtils.isParsable(split[2])) {
                LogHelper.Warn(String.format("Invalid amplifier \"%s\" for Effects on WakeUp", split[1]));
                continue;
            }
            int amplifier = Integer.parseInt(split[2]);
            ResourceLocation potion = ResourceLocation.tryCreate(split[0]);
            if (potion == null) {
                LogHelper.Warn("%s potion for Effects on WakeUp is not valid", line);
                continue;
            }
            if (ForgeRegistries.POTIONS.containsKey(potion)) {
                EffectOnWakeUp effectOnWakeUp = new EffectOnWakeUp(potion, duration, amplifier);
                effectsOnWakeUp.add(effectOnWakeUp);
            }
            else
                LogHelper.Warn(String.format("%s potion for Effects on WakeUp seems to not exist", line));
        }
        return effectsOnWakeUp;
    }
}
