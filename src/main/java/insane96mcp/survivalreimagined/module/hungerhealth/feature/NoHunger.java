package insane96mcp.survivalreimagined.module.hungerhealth.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.base.config.MinMax;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.data.generator.SRItemTagsProvider;
import insane96mcp.survivalreimagined.event.CakeEatEvent;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.movement.feature.Stamina;
import insane96mcp.survivalreimagined.network.NetworkHandler;
import insane96mcp.survivalreimagined.network.message.MessageFoodRegenSync;
import insane96mcp.survivalreimagined.setup.SRMobEffects;
import insane96mcp.survivalreimagined.utils.ClientUtils;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.GuiOverlayManager;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkDirection;
import org.jetbrains.annotations.Nullable;

@Label(name = "No Hunger", description = "Remove hunger and get back to the Beta 1.7.3 days.")
@LoadFeature(module = Modules.Ids.HUNGER_HEALTH)
public class NoHunger extends Feature {

    private static final String PASSIVE_REGEN_TICK = SurvivalReimagined.RESOURCE_PREFIX + "passive_regen_ticks";
    private static final String FOOD_REGEN_LEFT = SurvivalReimagined.RESOURCE_PREFIX + "food_regen_left";
    private static final String FOOD_REGEN_STRENGTH = SurvivalReimagined.RESOURCE_PREFIX + "food_regen_strength";

    private static final String FOOD_STATS_LANG = SurvivalReimagined.MOD_ID + ".food_stats";
    private static final String FOOD_STATS_PERCENTAGE_LANG = SurvivalReimagined.MOD_ID + ".food_stats_percentage";

    public static final TagKey<Item> RAW_FOOD = SRItemTagsProvider.create("raw_food");

    @Config
    @Label(name = "Passive Health Regen.Enable Passive Health Regen", description = "If true, Passive Regeneration is enabled")
    public static Boolean enablePassiveRegen = false;
    @Config
    @Label(name = "Passive Health Regen.Regen Speed", description = "Min represents how many seconds the regeneration of 1 HP takes when health is 100%, Max how many seconds when health is 0%")
    public static MinMax passiveRegenerationTime = new MinMax(120, 180);
    @Config(min = 0d)
    @Label(name = "Food Heal.Health Multiplier", description = "When eating you'll get healed by hunger restored multiplied by this percentage. (Set to 1 to have the same effect as pre-beta 1.8 food")
    public static Double foodHealHealthMultiplier = 0.3d;
    @Config
    @Label(name = "Food Heal.Instant Heal", description = "If true, health is regenerated instantly instead of over time")
    public static Boolean foodHealInstantly = false;
    @Config
    @Label(name = "Raw food.Heal Multiplier", description = "If true, raw food will heal by this percentage (this is applied after 'Food Heal.Health Multiplier'). Raw food is defined in the survivalreimagined:raw_food tag")
    public static Double rawFoodHealPercentage = 1d;
    @Config(min = 0d, max = 1f)
    @Label(name = "Raw food.Poison Chance", description = "Raw food has this chance to poison the player. Raw food is defined in the survivalreimagined:raw_food tag")
    public static Double rawFoodPoisonChance = 0.6d;

    @Config
    @Label(name = "Convert Hunger to Weakness", description = "If true, Hunger effect is replaced by Weakness")
    public static Boolean convertHungerToWeakness = true;

    @Config
    @Label(name = "Render armor at Hunger", description = "(Client Only) Armor is rendered at the place of Hunger bar")
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
                || event.player.level.isClientSide
                || event.phase.equals(TickEvent.Phase.START))
            return;

        event.player.getFoodData().foodLevel = 15;

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
        if (event.player.hasEffect(MobEffects.SATURATION)) {
            MobEffectInstance effect = event.player.getEffect(MobEffects.SATURATION);
            //noinspection ConstantConditions; Checking with hasEffect
            event.player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, (effect.getDuration() + 1) * 20, effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
            event.player.removeEffect(MobEffects.SATURATION);
        }

        if (event.player.tickCount % FOOD_REGEN_TICK_RATE == 0 && getFoodRegenLeft(event.player) > 0f) {
            consumeAndHealFromFoodRegen(event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerEat(LivingEntityUseItemEvent.Finish event) {
        if (!this.isEnabled()
                || !event.getItem().isEdible()
                || !(event.getEntity() instanceof Player player)
                || event.getEntity().level.isClientSide)
            return;

        Item item = event.getItem().getItem();
        healOnEat(player, item, item.getFoodProperties(event.getItem(), player));
    }

    private static final FoodProperties CAKE_FOOD_PROPERTIES = new FoodProperties.Builder().nutrition(2).saturationMod(0.1f).build();

    @SubscribeEvent
    public void onCakeEat(CakeEatEvent event) {
        if (!this.isEnabled()
                || ((Level)event.getLevel()).isClientSide)
            return;

        healOnEat(event.getEntity(), null, CAKE_FOOD_PROPERTIES);
    }

    @SuppressWarnings("ConstantConditions")
    public void healOnEat(Player player, @Nullable Item item, FoodProperties foodProperties) {
        if (foodHealHealthMultiplier == 0d)
            return;
        boolean isRawFood = item != null && isRawFood(item);
        if (player.getRandom().nextDouble() < rawFoodPoisonChance && isRawFood) {
            player.addEffect(new MobEffectInstance(MobEffects.POISON, foodProperties.getNutrition() * 20 * 3));
        }

        float heal = getFoodHealing(foodProperties);
        if (buffCakes && item == null)
            heal = Math.max((player.getMaxHealth() - player.getHealth()) * 0.4f, 1f);
        if (isRawFood && rawFoodHealPercentage != 1d)
            heal *= rawFoodHealPercentage;

        if (foodHealInstantly) {
            player.heal(heal);
        }
        else {
            float strength = getFoodHealingStrength(foodProperties) / 20f;
            setFoodRegenLeft(player, heal);
            setFoodRegenStrength(player, strength);
        }
    }

    private static int getPassiveRegenSpeed(Player player) {
        float healthPerc = 1 - (player.getHealth() / player.getMaxHealth());
        int secs;
        secs = (int) ((passiveRegenerationTime.max - passiveRegenerationTime.min) * healthPerc + passiveRegenerationTime.min);
        if (player.level.getDifficulty().equals(Difficulty.HARD))
            secs *= 1.5d;
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
        player.getPersistentData().putInt(PASSIVE_REGEN_TICK, getPassiveRegenTick(player) + FOOD_REGEN_TICK_RATE);
    }

    private static void resetPassiveRegenTick(Player player) {
        player.getPersistentData().putInt(PASSIVE_REGEN_TICK, 0);
    }

    private static float getFoodRegenLeft(Player player) {
        return player.getPersistentData().getFloat(FOOD_REGEN_LEFT);
    }

    private static void setFoodRegenLeft(Player player, float amount) {
        player.getPersistentData().putFloat(FOOD_REGEN_LEFT, amount);
    }

    public static float getFoodHealing(FoodProperties food) {
        return (float) (Math.pow(food.getNutrition(), 1.5f) * foodHealHealthMultiplier.floatValue());
    }

    public static float getFoodHealingStrength(FoodProperties food) {
        //Clamped between 0.1 and 0.5 hp/s
        return Mth.clamp(0.5f * (1.4f - food.getSaturationModifier()), 0.1f, 0.5f);
    }

    private static void consumeAndHealFromFoodRegen(Player player) {
        float regenLeft = getFoodRegenLeft(player);
        float regenStrength = getFoodRegenStrength(player) * FOOD_REGEN_TICK_RATE;
        if (regenLeft < regenStrength)
            regenStrength = regenLeft;
        player.heal(regenStrength);
        regenLeft -= regenStrength;
        if (regenLeft <= 0f){
            regenLeft = 0f;
            setFoodRegenStrength(player, 0f);
        }
        setFoodRegenLeft(player, regenLeft);
    }

    private static float getFoodRegenStrength(Player player) {
        return player.getPersistentData().getFloat(FOOD_REGEN_STRENGTH);
    }

    public static void setFoodRegenStrength(Player player, float amount) {
        player.getPersistentData().putFloat(FOOD_REGEN_STRENGTH, amount);
        if (player instanceof ServerPlayer serverPlayer) {
            Object msg = new MessageFoodRegenSync(amount);
            NetworkHandler.CHANNEL.sendTo(msg, serverPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static boolean isRawFood(Item item) {
        return Utils.isItemInTag(item, RAW_FOOD);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void removeFoodBar(final RenderGuiOverlayEvent.Pre event) {
        if (this.isEnabled()) {
            if (event.getOverlay().equals(VanillaGuiOverlay.FOOD_LEVEL.type())) {
                event.setCanceled(true);
            }
            if (event.getOverlay().equals(VanillaGuiOverlay.ARMOR_LEVEL.type()) && renderArmorAtHunger) {
                event.setCanceled(true);
                Minecraft mc = Minecraft.getInstance();
                ForgeGui gui = (ForgeGui) mc.gui;
                if (!mc.options.hideGui && gui.shouldDrawSurvivalElements()) {
                    renderArmor(event.getPoseStack(), event.getWindow().getGuiScaledWidth(), event.getWindow().getGuiScaledHeight());
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected void renderArmor(PoseStack poseStack, int width, int height) {
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
                GuiComponent.blit(poseStack, left, top, 34, 9, 9, 9, 256, 256);
            else if (i == level)
                ClientUtils.blitVericallyMirrored(poseStack, left, top, 25, 9, 9, 9, 256, 256);
            else
                GuiComponent.blit(poseStack, left, top, 16, 9, 9, 9, 256, 256);
            left -= 8;
        }
        if (level > 0)
            gui.rightHeight += 10;

        RenderSystem.disableBlend();
        mc.getProfiler().pop();
    }

    static ResourceLocation PLAYER_HEALTH_ELEMENT = new ResourceLocation("minecraft", "player_health");

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderGuiOverlayPre(RenderGuiOverlayEvent.Post event) {
        if (!this.isEnabled())
            return;
        if (event.getOverlay() == GuiOverlayManager.findOverlay(PLAYER_HEALTH_ELEMENT)) {
            Minecraft mc = Minecraft.getInstance();
            ForgeGui gui = (ForgeGui) mc.gui;
            if (!mc.options.hideGui && gui.shouldDrawSurvivalElements()) {
                renderFoodRegen(gui, event.getPoseStack(), event.getPartialTick(), event.getWindow().getScreenWidth(), event.getWindow().getScreenHeight());
            }
        }
    }

    private static final Vec2 UV_ARROW = new Vec2(0, 18);

    @OnlyIn(Dist.CLIENT)
    public static void renderFoodRegen(ForgeGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
        int healthIconsOffset = 49;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        assert player != null;

        int right = mc.getWindow().getGuiScaledWidth() / 2 - 94;
        int top = mc.getWindow().getGuiScaledHeight() - healthIconsOffset + 11;
        float saturationModifier = getFoodRegenStrength(player) * 20 * 2;
        if (saturationModifier == 0f)
            return;
        RenderSystem.setShaderTexture(0, SurvivalReimagined.GUI_ICONS);
        Stamina.setColor(1.2f - (saturationModifier / 1.2f), 0.78f, 0.17f, 1f);
        GuiComponent.blit(poseStack, right, top, (int) UV_ARROW.x, (int) UV_ARROW.y, 9, 9);
        Stamina.resetColor();

        // rebind default icons
        RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        boolean isCake = event.getItemStack().is(Items.CAKE);

        if (!this.isEnabled()
                || (!event.getItemStack().getItem().isEdible() && !isCake))
            return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer playerEntity = mc.player;
        if (playerEntity == null)
            return;

        if (!mc.options.reducedDebugInfo().get() && mc.options.advancedItemTooltips) {
            FoodProperties food;
            if (isCake)
                food = CAKE_FOOD_PROPERTIES;
            else
                food = event.getItemStack().getItem().getFoodProperties(event.getItemStack(), event.getEntity());
            //noinspection ConstantConditions
            float heal = getFoodHealing(food);
            //Half heart per second by default
            float strength = getFoodHealingStrength(food);
            if (buffCakes && isCake)
                event.getToolTip().add(Component.translatable(FOOD_STATS_PERCENTAGE_LANG, SurvivalReimagined.ONE_DECIMAL_FORMATTER.format(40f)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
            else
                event.getToolTip().add(Component.translatable(FOOD_STATS_LANG, SurvivalReimagined.ONE_DECIMAL_FORMATTER.format(heal), SurvivalReimagined.ONE_DECIMAL_FORMATTER.format(heal / strength)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        }
    }
}
