package insane96mcp.iguanatweaksreborn.modules.sleeprespawn.feature;

import com.google.common.collect.Lists;
import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.base.ITModule;
import insane96mcp.iguanatweaksreborn.modules.sleeprespawn.classutils.EffectOnWakeUp;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SleepingEffectsFeature extends ITFeature {

    private final ForgeConfigSpec.ConfigValue<Integer> hungerDepletedOnWakeUpConfig;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> effectsOnWakeUpConfig;
    private final ForgeConfigSpec.ConfigValue<Boolean> noSleepIfHungryConfig;

    private final List<String> effectsOnWakeUpDefault = Lists.newArrayList("minecraft:slowness,400,1", "minecraft:regeneration,200,1", "minecraft:weakness,300,1", "minecraft:mining_fatigue,300,1");

    public int hungerDepletedOnWakeUp = 11;
    public ArrayList<EffectOnWakeUp> effectsOnWakeUp;
    public boolean noSleepIfHungry = true;

    public SleepingEffectsFeature(ITModule module) {
        super("Sleeping Effects", "Prevents the player from sleeping if has not enough Hunger and gives him effects on wake up.", module);

        Config.builder.comment(this.getDescription()).push(this.getName());
        hungerDepletedOnWakeUpConfig = Config.builder
                .comment("How much the hunger bar is depleted when you wake up in the morning. Saturation depleted is based off this value times 2. Setting to 0 will disable this feature.")
                .defineInRange("Hunger Depleted on Wake Up", this.hungerDepletedOnWakeUp, -20, 20);
        effectsOnWakeUpConfig = Config.builder
                .comment("A list of effects to apply to the player when he wakes up.\nThe format is modid:potion_id,duration_in_ticks,amplifier\nE.g. 'minecraft:slowness,240,1' will apply Slowness II for 12 seconds to the player.")
                .defineList("Effects on Wake Up", this.effectsOnWakeUpDefault, o -> o instanceof String);
        noSleepIfHungryConfig = Config.builder
                .comment("If the player's hunger bar is below 'Hunger Depleted on Wake Up' he can't sleep.")
                .define("No Sleep If Hungry", true);
        Config.builder.pop();
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        this.hungerDepletedOnWakeUp = this.hungerDepletedOnWakeUpConfig.get();
        this.effectsOnWakeUp = parseEffectsOnWakeUp(this.effectsOnWakeUpConfig.get());
        this.noSleepIfHungry = this.noSleepIfHungryConfig.get();
    }

    @SubscribeEvent
    public void wakeUpHungerAndEffects(SleepFinishedTimeEvent event) {
        if (!this.isModuleEnabled())
            return;
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

    @SubscribeEvent
    public void tooHungryToSleep(PlayerSleepInBedEvent event) {
        if (!this.isModuleEnabled())
            return;
        if (!this.isEnabled())
            return;
        if (!this.noSleepIfHungry)
            return;
        if (this.hungerDepletedOnWakeUp == 0)
            return;
        if (event.getPlayer().getFoodStats().getFoodLevel() >= this.hungerDepletedOnWakeUp)
            return;
        event.setResult(PlayerEntity.SleepResult.OTHER_PROBLEM);
        event.getPlayer().sendStatusMessage(new TranslationTextComponent(Strings.Translatable.NO_FOOD_FOR_SLEEP), true);
    }

    private static ArrayList<EffectOnWakeUp> parseEffectsOnWakeUp(List<? extends String> list) {
        ArrayList<EffectOnWakeUp> effectsOnWakeUp = new ArrayList<>();
        for (String line : list) {
            EffectOnWakeUp effectOnWakeUp = EffectOnWakeUp.parseLine(line);
            if (effectOnWakeUp != null) effectsOnWakeUp.add(effectOnWakeUp);
        }
        return effectsOnWakeUp;
    }
}
