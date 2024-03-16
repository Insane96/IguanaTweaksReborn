package insane96mcp.iguanatweaksreborn.module.hungerhealth.nohunger;

import com.mojang.blaze3d.systems.RenderSystem;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.hungerhealth.fooddrinks.FoodDrinks;
import insane96mcp.iguanatweaksreborn.module.hungerhealth.healthregen.HealthRegen;
import insane96mcp.iguanatweaksreborn.network.NetworkHandler;
import insane96mcp.iguanatweaksreborn.utils.ClientUtils;
import insane96mcp.iguanatweaksreborn.utils.Utils;
import insane96mcp.insanelib.InsaneLib;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.base.config.MinMax;
import insane96mcp.insanelib.event.CakeEatEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkDirection;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

@Label(name = "No Hunger", description = "Remove hunger and get back to the Beta 1.7.3 days. Use the gamerule iguanatweaks:no_hunger to re-enable hunger (and thus, disable this feature)")
@LoadFeature(module = Modules.Ids.HUNGER_HEALTH, canBeDisabled = false)
public class NoHunger extends Feature {

    public static final GameRules.Key<GameRules.BooleanValue> RULE_NOHUNGER = GameRules.register("iguanatweaks:noHunger", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true, (server, booleanValue) -> {
        Module.getFeature(NoHunger.class).setEnabled(booleanValue.get());
        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            NoHungerSync.sync(booleanValue.get(), serverPlayer);
        }
    }));
    private static final String PASSIVE_REGEN_TICK = IguanaTweaksReborn.RESOURCE_PREFIX + "passive_regen_ticks";
    private static final String FOOD_REGEN_LEFT = IguanaTweaksReborn.RESOURCE_PREFIX + "food_regen_left";
    private static final String FOOD_REGEN_STRENGTH = IguanaTweaksReborn.RESOURCE_PREFIX + "food_regen_strength";

    private static final String HEALTH_LANG = IguanaTweaksReborn.MOD_ID + ".tooltip.health";
    private static final String MISSING_HEALTH_LANG = IguanaTweaksReborn.MOD_ID + ".tooltip.missing_health";
    private static final String SEC_LANG = IguanaTweaksReborn.MOD_ID + ".tooltip.sec";

    @Config
    @Label(name = "Passive Health Regen.Enable", description = "If true, Passive Regeneration is enabled")
    public static Boolean enablePassiveRegen = false;
    @Config
    @Label(name = "Passive Health Regen.Regen Speed", description = "Min represents how many ticks the regeneration of 1 HP takes when health is 100%, Max how many ticks when health is 0%")
    public static MinMax passiveRegenerationTime = new MinMax(120, 3600);
    @Config(min = 0d)
    @Label(name = "Food Heal.Over Time", description = "The formula to calculate the health regenerated when eating a food. Leave empty to disable. Variables as hunger, saturation_modifier, effectiveness as numbers and fast_food as boolean can be used. This is evaluated with EvalEx https://ezylang.github.io/EvalEx/concepts/parsing_evaluation.html.")
    public static String healOverTime = "(hunger^1.55)*0.25";
    @Config
    @Label(name = "Food Heal.Over time Strength", description = "How much HP does food regen each second? Variables as hunger, saturation_modifier, effectiveness as numbers and fast_food as boolean can be used. This is evaluated with EvalEx https://ezylang.github.io/EvalEx/concepts/parsing_evaluation.html. Default is 60% of the saturation modifier, down to a minimum of 0.15/s")
    public static String healOverTimeStrength = "MAX(0.15, 0.6 * saturation_modifier)";
    @Config(min = 0d)
    @Label(name = "Food Heal.Instant Heal", description = "The formula to calculate the health restored instantly when eating. Leave empty to disable. To have the same effect as pre-Beta 1.8 food just use \"hunger\". Variables as hunger, saturation_modifier, effectiveness as numbers and fast_food as boolean can be used. This is evaluated with EvalEx https://ezylang.github.io/EvalEx/concepts/parsing_evaluation.html.")
    public static String instantHeal = "(hunger^1.3)*0.4";
    @Config(min = 0d)
    @Label(name = "Food Heal.Saturation threshold", description = "Foods below this saturation will instantly heal, foods above this threshold will have overtime heal.")
    public static Double instantHealSaturationThreshold = 4d;
    @Config
    @Label(name = "Raw food.Heal Multiplier", description = "If true, raw food will heal by this percentage (this is applied after 'Food Heal.Health Multiplier'). Raw food is defined in the iguanatweaksreborn:raw_food tag")
    public static Double rawFoodHealPercentage = 1d;

    @Config
    @Label(name = "Convert Hunger to Weakness", description = "If true, Hunger effect is replaced by Weakness")
    public static Boolean convertHungerToWeakness = true;

    @Config
    @Label(name = "Convert Saturation to Haste", description = "If true, Saturation effect is replaced by Haste")
    public static Boolean convertSaturationToHaste = true;

    @Config
    @Label(name = "Render armor at Hunger", description = "(Client Only) Armor is rendered in the place of Hunger bar")
    public static Boolean renderArmorAtHunger = true;

    @Config
    @Label(name = "Buff cakes", description = "Make cakes restore 40% missing health")
    public static Boolean buffCakes = true;

    public NoHunger(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    private static final int PASSIVE_REGEN_TICK_RATE = 10;
    private static final int FOOD_REGEN_TICK_RATE = 10;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!this.isEnabled()
                || event.player.level().isClientSide
                || event.phase.equals(TickEvent.Phase.START))
            return;

        if (HealthRegen.isPlayerHurt(event.player))
            event.player.getFoodData().foodLevel = 15;
        else
            event.player.getFoodData().foodLevel = 20;

        if (event.player.tickCount % PASSIVE_REGEN_TICK_RATE == 1 && enablePassiveRegen && event.player.isHurt()) {
            incrementPassiveRegenTick(event.player);
            int passiveRegen = getPassiveRegenSpeed(event.player);

            if (getPassiveRegenTick(event.player) > passiveRegen) {
                float heal = 1.0f;
                event.player.heal(heal);
                resetPassiveRegenTick(event.player);
            }
        }

        if (event.player.hasEffect(MobEffects.HUNGER) && convertHungerToWeakness) {
            MobEffectInstance effect = event.player.getEffect(MobEffects.HUNGER);
            //noinspection ConstantConditions; Checking with hasEffect
            event.player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, effect.getDuration() + 1, effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
            event.player.removeEffect(MobEffects.HUNGER);
        }
        if (event.player.hasEffect(MobEffects.SATURATION) && convertSaturationToHaste) {
            MobEffectInstance effect = event.player.getEffect(MobEffects.SATURATION);
            //noinspection ConstantConditions; Checking with hasEffect
            event.player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, (effect.getDuration() + 1) * 20, effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
            event.player.removeEffect(MobEffects.SATURATION);
        }

        if (event.player.tickCount % FOOD_REGEN_TICK_RATE == 0 && getFoodRegenLeft(event.player) > 0f)
            consumeAndHealFromFoodRegen(event.player);
    }

    @SubscribeEvent
    public void onPlayerEat(LivingEntityUseItemEvent.Finish event) {
        if (!this.isEnabled()
                || event.getItem().getItem().getFoodProperties() == null
                || !(event.getEntity() instanceof Player player)
                || event.getEntity().level().isClientSide)
            return;

        Item item = event.getItem().getItem();
        healOnEat(player, item, item.getFoodProperties(event.getItem(), player));
    }

    private static final FoodProperties CAKE_FOOD_PROPERTIES = new FoodProperties.Builder().nutrition(2).saturationMod(0.8f).build();

    @SubscribeEvent
    public void onCakeEat(CakeEatEvent event) {
        if (!this.isEnabled()
                || ((Level)event.getLevel()).isClientSide)
            return;

        healOnEat(event.getEntity(), null, CAKE_FOOD_PROPERTIES);
    }

    /**
     * item is null when eating cakes
     */
    @SuppressWarnings("ConstantConditions")
    public void healOnEat(Player player, @Nullable Item item, FoodProperties foodProperties) {
        boolean isRawFood = item != null && FoodDrinks.isRawFood(item);
        if (Utils.getFoodSaturationRestored(foodProperties) > instantHealSaturationThreshold)
            onEatHealOverTime(player, item, foodProperties, isRawFood);
        else
            onEatInstantHeal(player, item, foodProperties, isRawFood);
    }

    public void onEatHealOverTime(Player player, @Nullable Item item, FoodProperties foodProperties, boolean isRawFood) {
        if (!doesHealOverTime())
            return;

        float heal = Utils.computeFoodFormula(foodProperties, healOverTime);
        if (heal <= 0f)
            return;
        if (buffCakes && item == null)
            heal = Math.max((player.getMaxHealth() - player.getHealth()) * 0.4f, 1f);
        if (isRawFood && rawFoodHealPercentage != 1d)
            heal *= rawFoodHealPercentage;

        float strength = Utils.computeFoodFormula(foodProperties, healOverTimeStrength) / 20f;
        setHealOverTime(player, heal, strength);
    }

    public static boolean doesHealOverTime() {
        return !StringUtils.isBlank(healOverTime) && !StringUtils.isBlank(healOverTimeStrength);
    }

    private void onEatInstantHeal(Player player, @Nullable Item item, FoodProperties foodProperties, boolean isRawFood) {
        if (!doesHealInstantly())
            return;

        float heal = buffCakes && item == null
                ? Math.max((player.getMaxHealth() - player.getHealth()) * 0.2f, 1f)
                : getInstantHealAmount(foodProperties, isRawFood);
        player.heal(heal);
    }

    public static float getInstantHealAmount(FoodProperties foodProperties, boolean isRawFood) {
        float heal = Utils.computeFoodFormula(foodProperties, instantHeal);
        if (isRawFood && rawFoodHealPercentage != 1d)
            heal *= rawFoodHealPercentage;
        return heal;
    }

    public static boolean doesHealInstantly() {
        return !StringUtils.isBlank(instantHeal);
    }

    private static int getPassiveRegenSpeed(Player player) {
        float healthPerc = 1f - (player.getHealth() / player.getMaxHealth());
        float secs = (float) ((passiveRegenerationTime.max - passiveRegenerationTime.min) * healthPerc + passiveRegenerationTime.min);
        if (player.level().getDifficulty().equals(Difficulty.HARD))
            secs *= 1.5f;
        if (player.hasEffect(HealthRegen.VIGOUR.get())) {
            MobEffectInstance vigour = player.getEffect(HealthRegen.VIGOUR.get());
            //noinspection ConstantConditions
            secs *= 1 - (((vigour.getAmplifier() + 1) * 0.4f));
        }
        return (int) (secs * 20);
    }

    private static int getPassiveRegenTick(Player player) {
        return player.getPersistentData().getInt(PASSIVE_REGEN_TICK);
    }

    private static void incrementPassiveRegenTick(Player player) {
        player.getPersistentData().putInt(PASSIVE_REGEN_TICK, getPassiveRegenTick(player) + FOOD_REGEN_TICK_RATE);
    }

    private static void resetPassiveRegenTick(Player player) {
        player.getPersistentData().putInt(PASSIVE_REGEN_TICK, 0);
    }

    private static float getFoodRegenLeft(Player player) {
        return player.getPersistentData().getFloat(FOOD_REGEN_LEFT);
    }

    public static void setHealOverTime(Player player, float amount, float strength) {
        player.getPersistentData().putFloat(FOOD_REGEN_LEFT, amount);
        player.getPersistentData().putFloat(FOOD_REGEN_STRENGTH, strength);
        if (player instanceof ServerPlayer serverPlayer) {
            Object msg = new FoodRegenSync(amount, strength);
            NetworkHandler.CHANNEL.sendTo(msg, serverPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    private static void consumeAndHealFromFoodRegen(Player player) {
        float regenLeft = getFoodRegenLeft(player);
        float regenStrength = getFoodRegenStrength(player);
        if (player.getHealth() == player.getMaxHealth()) {
            regenLeft -= regenStrength * 0.02f * FOOD_REGEN_TICK_RATE;
        }
        else {
            float healAmount = regenStrength * FOOD_REGEN_TICK_RATE;
            if (regenLeft < healAmount)
                healAmount = regenLeft;
            if (player.getMaxHealth() - player.getHealth() < healAmount)
                healAmount = player.getMaxHealth() - player.getHealth();
            player.heal(healAmount);
            regenLeft -= healAmount;
            if (regenLeft <= 0f)
                regenLeft = 0f;
        }
        setHealOverTime(player, regenLeft, regenStrength);
    }

    private static float getFoodRegenStrength(Player player) {
        return player.getPersistentData().getFloat(FOOD_REGEN_STRENGTH);
    }

    //Render before Regenerating absorption
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void removeFoodBar(final RenderGuiOverlayEvent.Pre event) {
        if (!this.isEnabled())
            return;

        if (event.getOverlay().equals(VanillaGuiOverlay.FOOD_LEVEL.type()))
            event.setCanceled(true);
        //Remove armor bar to render it on the right
        if (event.getOverlay().equals(VanillaGuiOverlay.ARMOR_LEVEL.type()) && renderArmorAtHunger) {
            event.setCanceled(true);
            Minecraft mc = Minecraft.getInstance();
            ForgeGui gui = (ForgeGui) mc.gui;
            if (!mc.options.hideGui && gui.shouldDrawSurvivalElements())
                renderArmor(event.getGuiGraphics(), event.getWindow().getGuiScaledWidth(), event.getWindow().getGuiScaledHeight());
        }
    }

    protected static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");
    @OnlyIn(Dist.CLIENT)
    protected void renderArmor(GuiGraphics guiGraphics, int width, int height) {
        Minecraft mc = Minecraft.getInstance();
        ForgeGui gui = (ForgeGui) mc.gui;
        mc.getProfiler().push("armor");

        RenderSystem.enableBlend();
        int left = width / 2 + 82;
        int top = height - gui.rightHeight;

        int level = mc.player.getArmorValue();
        for (int i = 1; level > 0 && i < 20; i += 2)
        {
            if (i < level)
                guiGraphics.blit(GUI_ICONS_LOCATION, left, top, 34, 9, 9, 9, 256, 256);
            else if (i == level)
                ClientUtils.blitVericallyMirrored(GUI_ICONS_LOCATION, guiGraphics, left, top, 25, 9, 9, 9, 256, 256);
            else
                guiGraphics.blit(GUI_ICONS_LOCATION, left, top, 16, 9, 9, 9, 256, 256);
            left -= 8;
        }
        if (level > 0)
            gui.rightHeight += 10;

        RenderSystem.disableBlend();
        mc.getProfiler().pop();
    }

    protected static final ResourceLocation OT_REGEN_LOCATION = new ResourceLocation(IguanaTweaksReborn.MOD_ID, "textures/gui/ot_regen.png");

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerGui(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.PLAYER_HEALTH.id(), "ot_regen", (gui, guiGraphics, partialTicks, screenWidth, screenHeight) -> {
            if (!Feature.isEnabled(NoHunger.class)
                    || !gui.shouldDrawSurvivalElements())
                return;

            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player == null)
                return;

            int right = screenWidth / 2 - 91;
            int top = screenHeight - gui.leftHeight + 16;
            float regenLeft = Math.min(20, getFoodRegenLeft(player));
            float regenStrength = getFoodRegenStrength(player) * 20 * 2;
            if (regenStrength == 0f)
                return;
            ClientUtils.setRenderColor(1.2f - (regenStrength / 1.2f), 0.78f, 0.17f, 1f);
            guiGraphics.blit(OT_REGEN_LOCATION, right, top, 0f, 0f, (int) (regenLeft / 2f * 9), 3, 90, 3);
            ClientUtils.resetRenderColor();
        });
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        if (!this.isEnabled()
                || event.getItemStack().getItem().getFoodProperties() == null)
            return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null)
            return;

        if (mc.options.reducedDebugInfo().get() || !mc.options.advancedItemTooltips)
            return;

        FoodProperties food = event.getItemStack().getItem().getFoodProperties(event.getItemStack(), event.getEntity());

        if (Utils.getFoodSaturationRestored(food) < instantHealSaturationThreshold && doesHealInstantly()) {
            boolean isRawFood = FoodDrinks.isRawFood(event.getItemStack().getItem());
            //noinspection ConstantConditions
            float heal = getInstantHealAmount(food, isRawFood);
            MutableComponent component = Component.literal(InsaneLib.ONE_DECIMAL_FORMATTER.format(heal))
                    .append(" ")
                    .append(Component.translatable(HEALTH_LANG))
                    .withStyle(ChatFormatting.GRAY)
                    .withStyle(ChatFormatting.ITALIC);
            event.getToolTip().add(component);
        }

        if (Utils.getFoodSaturationRestored(food) >= instantHealSaturationThreshold && doesHealOverTime()) {
            //noinspection ConstantConditions
            float heal = Utils.computeFoodFormula(food, healOverTime);
            //Half heart per second by default
            float strength = Utils.computeFoodFormula(food, healOverTimeStrength);
            MutableComponent component = Component.literal(InsaneLib.ONE_DECIMAL_FORMATTER.format(heal))
                    .append(" ")
                    .append(Component.translatable(HEALTH_LANG))
                    .append(" / ")
                    .append(InsaneLib.ONE_DECIMAL_FORMATTER.format(heal / strength))
                    .append(" ")
                    .append(Component.translatable(SEC_LANG))
                    .withStyle(ChatFormatting.GRAY)
                    .withStyle(ChatFormatting.ITALIC);
            event.getToolTip().add(component);
        }
    }

}
