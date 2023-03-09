package insane96mcp.iguanatweaksreborn.module.hungerhealth.feature;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.setup.ITMobEffects;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.base.config.MinMax;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
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
    public static MinMax passiveRegenerationTimeHard = new MinMax(10, 45);
    @Config
    @Label(name = "Food Gives Fed", description = "If true, food gives the 'Fed' effect that increases passive health regen.")
    public static Boolean foodGivesFed = true;
    @Config(min = 0d, max = 1f)
    @Label(name = "Food Heal Multiplier", description = "When eating you'll get healed by this percentage hunger restored. (Set to 1 to have the same effect as pre-beta 1.8 food")
    public static Double foodHealMultiplier = 0d;

    public NoHunger(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!this.isEnabled()
                || !disableHunger
                || event.player.level.isClientSide)
            return;

        event.player.getFoodData().foodLevel = 15;

        if (enablePassiveRegen && event.player.isHurt()) {
            incrementPassiveRegenTick(event.player);
            int passiveRegen = getPassiveRegenSpeed(event.player);

            if (getPassiveRegenTick(event.player) > passiveRegen) {
                event.player.heal(1.0F);
                resetPassiveRegenTick(event.player);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerEat(LivingEntityUseItemEvent.Finish event) {
        if (!this.isEnabled()
                || !event.getItem().isEdible()
                || !(event.getEntity() instanceof Player)
                || event.getEntity().level.isClientSide)
            return;

        applyFedEffect(event);
        healOnEat(event);
    }

    public void applyFedEffect(LivingEntityUseItemEvent.Finish event) {
        if (!foodGivesFed) return;
        FoodProperties food = event.getItem().getItem().getFoodProperties(event.getItem(), event.getEntity());
        //noinspection ConstantConditions
        int duration = food.nutrition * 50;
        int amplifier = (int) ((food.saturationModifier * 2 * food.nutrition) / 4 - 1);
        event.getEntity().addEffect(new MobEffectInstance(ITMobEffects.FED.get(), duration, amplifier, true, false, true));
    }

    public void healOnEat(LivingEntityUseItemEvent.Finish event) {
        if (foodHealMultiplier == 0d)
            return;
        FoodProperties food = event.getItem().getItem().getFoodProperties(event.getItem(), event.getEntity());
        //noinspection ConstantConditions
        double heal = food.getNutrition() * foodHealMultiplier;
        event.getEntity().heal((float) heal);
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
        LogHelper.info("secs pre Fed: %d", secs);
        if (player.hasEffect(ITMobEffects.FED.get())) {
            MobEffectInstance fed = player.getEffect(ITMobEffects.FED.get());
            //noinspection ConstantConditions
            secs *= 1 - (((fed.getAmplifier() + 1) * 0.2d));
        }
        LogHelper.info("secs: %d", secs);
        return secs * 20;
    }

    private static int getPassiveRegenTick(Player player) {
        return player.getPersistentData().getInt(Strings.Tags.PASSIVE_REGEN_TICK);
    }

    private static void incrementPassiveRegenTick(Player player) {
        player.getPersistentData().putInt(Strings.Tags.PASSIVE_REGEN_TICK, player.getPersistentData().getInt(Strings.Tags.PASSIVE_REGEN_TICK) + 1);
    }

    private static void resetPassiveRegenTick(Player player) {
        player.getPersistentData().putInt(Strings.Tags.PASSIVE_REGEN_TICK, 0);
    }
}
