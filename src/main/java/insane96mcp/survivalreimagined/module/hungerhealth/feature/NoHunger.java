package insane96mcp.survivalreimagined.module.hungerhealth.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.base.config.MinMax;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.setup.SRMobEffects;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "No Hunger", description = "Remove hunger and get back to the Beta 1.7.3 days.")
@LoadFeature(module = Modules.Ids.HUNGER_HEALTH)
public class NoHunger extends Feature {

    //TODO Make food regenerate health overtime. Higher saturation = faster regen. Maybe with this remove Well Fed
    public static final String PASSIVE_REGEN_TICK = SurvivalReimagined.RESOURCE_PREFIX + "passive_regen_ticks";

    private static final ResourceLocation RAW_FOOD = new ResourceLocation(SurvivalReimagined.MOD_ID, "raw_food");

    @Config
    @Label(name = "Disable Hunger", description = "Completely disables the entire hunger system, from the hunger bar, to the health regen that comes with it.")
    public static Boolean disableHunger = true;
    @Config
    @Label(name = "Passive Health Regen.Enable Passive Health Regen", description = "If true, Passive Regeneration is enabled")
    public static Boolean enablePassiveRegen = true;
    @Config
    @Label(name = "Passive Health Regen.Regen Speed Easy", description = "Min represents how many seconds the regeneration of 1 HP takes when health is 100%, Max how many seconds when health is 0%. This applies for easy and peaceful difficulty")
    public static MinMax passiveRegenerationTimeEasy = new MinMax(15, 30);
    @Config
    @Label(name = "Passive Health Regen.Regen Speed Normal", description = "Min represents how many seconds the regeneration of 1 HP takes when health is 100%, Max how many seconds when health is 0%. This applies for normal difficulty")
    public static MinMax passiveRegenerationTimeNormal = new MinMax(20, 45);
    @Config
    @Label(name = "Passive Health Regen.Regen Speed Hard", description = "Min represents how many seconds the regeneration of 1 HP takes when health is 100%, Max how many seconds when health is 0%. This applies for hard difficulty")
    public static MinMax passiveRegenerationTimeHard = new MinMax(25, 45);
    @Config(min = -1)
    @Label(name = "Food Gives Well Fed when Saturation Modifier >", description = "When saturation modifier of the food eaten is higher than this value, the Well Fed effect is given. Set to -1 to disable the effect.\n" +
            "Well Fed increases passive health regen speed by 40%")
    public static Double foodGivesWellFedWhenSaturationModifier = 0.5d;
    @Config(min = 0d, max = 1f)
    @Label(name = "Food Heal Multiplier", description = "When eating you'll get healed by this percentage hunger restored. (Set to 1 to have the same effect as pre-beta 1.8 food")
    public static Double foodHealMultiplier = 1d;
    //TODO Make this a multiplier for the heal
    @Config
    @Label(name = "Raw food.No Heal", description = "If true, raw food doesn't heal. Raw food is defined in the survivalreimagined:raw_food tag")
    public static Boolean rawFoodDoesntHeal = true;
    @Config(min = 0d, max = 1f)
    @Label(name = "Raw food.Poison Chance", description = "Raw food has this chance to poison the player. Raw food is defined in the survivalreimagined:raw_food tag")
    public static Double rawFoodPoisonChance = 0.8d;

    @Config
    @Label(name = "Convert Hunger to Nausea", description = "If true, Hunger effect is replaced by Nausea")
    public static Boolean convertHungerToNausea = true;

    public NoHunger(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!this.isEnabled()
                || !disableHunger
                || event.player.level.isClientSide
                || event.phase.equals(TickEvent.Phase.START))
            return;

        event.player.getFoodData().foodLevel = 15;

        if (enablePassiveRegen && event.player.isHurt()) {
            incrementPassiveRegenTick(event.player);
            int passiveRegen = getPassiveRegenSpeed(event.player);

            if (getPassiveRegenTick(event.player) > passiveRegen) {
                float heal = 1.0f;
                event.player.heal(heal);
                resetPassiveRegenTick(event.player);
            }
        }

        if (event.player.hasEffect(MobEffects.HUNGER) && convertHungerToNausea) {
            MobEffectInstance effect = event.player.getEffect(MobEffects.HUNGER);
            //noinspection ConstantConditions; Checking with hasEffect
            event.player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
            event.player.removeEffect(MobEffects.HUNGER);
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
        if (foodGivesWellFedWhenSaturationModifier < 0d) return;
        FoodProperties food = event.getItem().getItem().getFoodProperties(event.getItem(), event.getEntity());
        //noinspection ConstantConditions
        if (food.saturationModifier < foodGivesWellFedWhenSaturationModifier)
            return;
        int duration = (int) (food.getNutrition() * food.getSaturationModifier() * 2 * 20 * 10);
        //int amplifier = (int) ((food.saturationModifier * 2 * food.nutrition) / 4 - 1);
        event.getEntity().addEffect(new MobEffectInstance(SRMobEffects.WELL_FED.get(), duration, 0, true, false, true));
    }

    @SuppressWarnings("ConstantConditions")
    public void healOnEat(LivingEntityUseItemEvent.Finish event) {
        if (foodHealMultiplier == 0d)
            return;
        FoodProperties food = event.getItem().getItem().getFoodProperties(event.getItem(), event.getEntity());
        boolean isRawFood = isRawFood(event.getItem().getItem());
        if (event.getEntity().getRandom().nextDouble() < rawFoodPoisonChance && isRawFood) {
            event.getEntity().addEffect(new MobEffectInstance(MobEffects.POISON, food.getNutrition() * 20 * 4));
        }
        else if (!isRawFood || !rawFoodDoesntHeal) {
            double heal = food.getNutrition() * foodHealMultiplier;
            event.getEntity().heal((float) heal);
        }
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
        if (player.hasEffect(SRMobEffects.WELL_FED.get())) {
            MobEffectInstance wellFed = player.getEffect(SRMobEffects.WELL_FED.get());
            //noinspection ConstantConditions
            secs *= 1 - (((wellFed.getAmplifier() + 1) * 0.4d));
        }
        return secs * 20;
    }

    private static int getPassiveRegenTick(Player player) {
        return player.getPersistentData().getInt(PASSIVE_REGEN_TICK);
    }

    private static void incrementPassiveRegenTick(Player player) {
        player.getPersistentData().putInt(PASSIVE_REGEN_TICK, getPassiveRegenTick(player) + 1);
    }

    private static void resetPassiveRegenTick(Player player) {
        player.getPersistentData().putInt(PASSIVE_REGEN_TICK, 0);
    }

    public static boolean isRawFood(Item item) {
        return Utils.isItemInTag(item, RAW_FOOD);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void removeFoodBar(final RenderGuiOverlayEvent.Pre event)
    {
        if (event.getOverlay().equals(VanillaGuiOverlay.FOOD_LEVEL.type()))
            event.setCanceled(true);
    }
}
