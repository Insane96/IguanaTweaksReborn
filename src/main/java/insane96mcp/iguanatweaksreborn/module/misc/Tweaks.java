package insane96mcp.iguanatweaksreborn.module.misc;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.data.generator.ITRBlockTagsProvider;
import insane96mcp.iguanatweaksreborn.data.generator.ITRItemTagsProvider;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.mining.MiningMisc;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.base.config.MinMax;
import insane96mcp.insanelib.util.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Label(name = "Tweaks", description = "Various stuff that doesn't fit in any other Feature.")
@LoadFeature(module = Modules.Ids.MISC)
public class Tweaks extends Feature {

    public static final TagKey<Block> BREAK_ON_FALL = ITRBlockTagsProvider.create("break_on_fall");
    public static final TagKey<Item> WORLD_IMMUNE = ITRItemTagsProvider.create("world_immune");

    @Config
    @Label(name = "Prevent fire with resistance", description = "If true, entities will no longer be set on fire if have Fire Resistance (like bedrock edition)")
    public static Boolean preventFireWithResistance = true;

    @Config
    @Label(name = "Falling breaking glass", description = "Falling on glass has a chance of breaking it. The higher the fall, the higher the chance. iguanatweaksreborn:fall_on_break block tag can be used to add more blocks that break when falling onto them.")
    public static Boolean fallingBreakingGlass = true;
    @Config(min = 1)
    @Label(name = "Poison damage speed", description = "Poison will damage the player every this ticks at level I. Vanilla is 25.")
    public static Integer poisonDamageSpeed = 60;

    @Config
    @Label(name = "Maximum Sponge Soak Blocks", description = "The maximum amount of blocks a sponge can soak. (Vanilla is 64, disabled if quark is installed)")
    public static Integer maxSpongeSoakBlocks = 256;
    @Config
    @Label(name = "Maximum Sponge Soak Range", description = "The maximum range at which sponges will check for soakable blocks. (Vanilla is 5, disabled if quark is installed)")
    public static Integer maxSpongeSoakRange = 10;
    @Config
    @Label(name = "Sponges dry in the sun and wet in rain", description = "If exposed to the sun sponges may dry and if exposed to rain sponges might get wet. Requires a Minecraft restart if disabled")
    public static Boolean spongesDryInTheSun = true;

    @Config
    @Label(name = "Better hardcore death", description = "When you die in hardcore, your spawn point is set to where you died and a lightning strike is summoned")
    public static Boolean betterHardcoreDeath = true;

    @Config(min = 0, max = 100)
    @Label(name = "Player air ticks consumed", description = "The amount of ticks the player consumes when underwater. In vanilla it's 1 without Respiration enchantment. For non integer numbers the decimal part will count as a chance to have a +1")
    public static Double playerConsumeAirAmount = 1.5d;
    @Config
    @Label(name = "Player air ticks refilled", description = "The amount of air ticks the player regains each tick when out of water. For non integer numbers the decimal part will count as a chance to have a +1. Vanilla is 4. Min is the amount as soon as you exit water, Max is a few seconds out of water.")
    public static MinMax playerRefillAirAmount = new MinMax(1, 2.5);
    @Config
    @Label(name = "Increase drown damage the more drowning")
    public static Boolean increaseDrownDamageTheMoreDrowning = true;
    @Config
    @Label(name = "Totem resistance", description = "If enabled, the Totem of Undying will give Resistance IV for 5 seconds")
    public static Boolean totemResistance = true;
    @Config
    @Label(name = "Turtle Helmet water breathing time", description = "The ticks of Water Breathing given by the Turtle Helmet. Vanilla is 200")
    public static Integer turtleHelmetWaterBreathingTime = 900;

    public Tweaks(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @Override
    public void readConfig(ModConfigEvent event) {
        super.readConfig(event);
        if (spongesDryInTheSun) {
            Blocks.WET_SPONGE.isRandomlyTicking = true;
            Blocks.SPONGE.isRandomlyTicking = true;
        }
    }

    public static boolean isFireImmune(Entity entity) {
        if (!isEnabled(Tweaks.class)
                || !preventFireWithResistance
                || !(entity instanceof LivingEntity livingEntity))
            return false;

       return livingEntity.hasEffect(MobEffects.FIRE_RESISTANCE);
    }

    public static int changeMaxSpongeSoakBlocks(int soakableBlocks) {
        if (!isEnabled(Tweaks.class))
            return soakableBlocks;

        //Vanilla uses 65 and not 64
        return maxSpongeSoakBlocks + 1;
    }

	public static int changeSpongeMaxRange(int range) {
		if (!isEnabled(Tweaks.class))
			return range;

		//Vanilla uses < instead of <=
		return maxSpongeSoakRange + 1;
	}

    public static void onSpongeTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!Feature.isEnabled(Tweaks.class))
            return;

        if (!level.canSeeSky(pos.above()))
            return;
        if (state.is(Blocks.SPONGE) && level.isRaining())
                level.setBlockAndUpdate(pos, Blocks.WET_SPONGE.defaultBlockState());
        else if (state.is(Blocks.WET_SPONGE) && !level.isRaining() && level.isDay()) {
            int chance = 5;
            for (Direction direction : Direction.values()) {
                if (level.getBlockState(pos.relative(direction)).is(Blocks.WET_SPONGE)) {
                    chance *= 4;
                    break;
                }
            }
            if (random.nextInt(chance) == 0)
                level.setBlockAndUpdate(pos, Blocks.SPONGE.defaultBlockState());
        }
    }

	public static int getPoisonDamageSpeed(int original) {
		return isEnabled(Tweaks.class) ? poisonDamageSpeed : original;
	}

    public static int destroyDelay(ItemStack stack, DiggerItem item, BlockState state) {
        return isEnabled(MiningMisc.class) && MiningMisc.efficiencyBasedDestroyDelay ? Math.max(5 - (int) (item.speed / 2f), 1) : 5;
    }

	@SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerDeath(LivingDeathEvent event) {
        if (!this.isEnabled()
                || !betterHardcoreDeath
                || event.getEntity().level().isClientSide
                || !(event.getEntity() instanceof ServerPlayer player)
                || event.getEntity() instanceof FakePlayer
                || !event.getEntity().level().getLevelData().isHardcore()
                || player.gameMode.getGameModeForPlayer() == GameType.CREATIVE
                || player.gameMode.getGameModeForPlayer() == GameType.SPECTATOR)
            return;

        //player.serverLevel().getGameRules().getRule(GameRules.RULE_DO_IMMEDIATE_RESPAWN).set(true, player.server);
        player.setRespawnPosition(player.level().dimension(), player.blockPosition(), player.getXRot(), true, false);
        LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, player.level());
        lightningBolt.setVisualOnly(true);
        lightningBolt.setPos(player.position());
        player.level().addFreshEntity(lightningBolt);
        player.level().setBlock(player.blockPosition(), Blocks.AIR.defaultBlockState(), 2);
        /*if (player.serverLevel().getGameRules().getRule(GameRules.RULE_DO_IMMEDIATE_RESPAWN).get()) {
            event.setCanceled(true);
            player.setHealth(player.getMaxHealth());
            Component component = player.getCombatTracker().getDeathMessage();
            player.server.getPlayerList().broadcastSystemMessage(component, false);
        }*/
    }

    boolean appliedResistance = false;
    @SubscribeEvent
    public void onTotemUse(LivingUseTotemEvent event) {
        if (!this.isEnabled()
                || !totemResistance)
            return;

        event.getEntity().addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 110, 3));
        appliedResistance = true;
    }

    @SubscribeEvent
    public void onEffectRemoved(MobEffectEvent.Remove event) {
        if (!this.isEnabled()
                || !appliedResistance
                || !event.getEffect().equals(MobEffects.DAMAGE_RESISTANCE))
            return;
        event.setCanceled(true);
        appliedResistance = false;
    }

    //Lowest priority so other mods can change/cancel fall damage
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onFalling(LivingFallEvent event) {
        if (!this.isEnabled()
                || !fallingBreakingGlass
                || event.getEntity().level().isClientSide)
            return;

        LivingEntity entity = event.getEntity();
        AABB bb = entity.getBoundingBox();
        int mX = Mth.floor(bb.minX);
        int mZ = Mth.floor(bb.minZ);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        float distance = event.getDistance() - 3f;
        if (distance <= 0)
            return;
        float chance = (float) (Math.pow(distance, 1.25f) * 0.05f);
        if (entity.getRandom().nextFloat() >= chance)
            return;
        for (int x2 = mX; x2 < bb.maxX; x2++) {
            for (int z2 = mZ; z2 < bb.maxZ; z2++) {
                pos.set(x2, entity.position().y - 1.0E-5F, z2);
                BlockState state = entity.level().getBlockState(pos);
                if (state.is(BREAK_ON_FALL)) {
                    BlockEntity blockEntity = state.hasBlockEntity() ? entity.level().getBlockEntity(pos) : null;
                    LootParams.Builder lootcontext$builder = (new LootParams.Builder((ServerLevel) entity.level())).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity).withOptionalParameter(LootContextParams.THIS_ENTITY, entity);
                    state.getDrops(lootcontext$builder).forEach(stack ->
                        entity.level().addFreshEntity(new ItemEntity(entity.level(), pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, stack))
                    );
                    entity.level().destroyBlock(pos, false);
                }
            }
        }
    }

    @SubscribeEvent
    public void onBreathe(LivingBreatheEvent event) {
        if (!this.isEnabled()
                || event.getEntity().level().isClientSide)
            return;

        boolean wasBreathing = event.getEntity().getPersistentData().getBoolean(IguanaTweaksReborn.RESOURCE_PREFIX + "was_breathing");
        long ticksSinceOutOfWater = event.getEntity().level().getGameTime() - event.getEntity().getPersistentData().getLong(IguanaTweaksReborn.RESOURCE_PREFIX + "tick_since_out_of_water");
        if (!wasBreathing && event.canBreathe()) {
            ticksSinceOutOfWater = 0;
            event.getEntity().getPersistentData().putLong(IguanaTweaksReborn.RESOURCE_PREFIX + "tick_since_out_of_water", event.getEntity().level().getGameTime());
        }
        int airConsumed = MathHelper.getAmountWithDecimalChance(event.getEntity().getRandom(), playerConsumeAirAmount);
        int respiration = EnchantmentHelper.getRespiration(event.getEntity());
        airConsumed = respiration > 0 && event.getEntity().getRandom().nextInt(respiration + 1) > 0 ? 0 : airConsumed;
        event.setConsumeAirAmount(airConsumed);

        int refillAmount = MathHelper.getAmountWithDecimalChance(event.getEntity().getRandom(), playerRefillAirAmount.min);
        if (ticksSinceOutOfWater > 75) {
            refillAmount = MathHelper.getAmountWithDecimalChance(event.getEntity().getRandom(), playerRefillAirAmount.max);
            if (event.canBreathe())
                event.getEntity().getPersistentData().remove(IguanaTweaksReborn.RESOURCE_PREFIX + "times_drowned");
        }
        event.setRefillAirAmount(refillAmount);
        event.getEntity().getPersistentData().putBoolean(IguanaTweaksReborn.RESOURCE_PREFIX + "was_breathing", event.canBreathe());
    }

    @SubscribeEvent
    public void onDrown(LivingDrownEvent event) {
        if (!this.isEnabled()
                || !increaseDrownDamageTheMoreDrowning)
            return;

        if (event.isDrowning()) {
            int timesDrowned = event.getEntity().getPersistentData().getInt(IguanaTweaksReborn.RESOURCE_PREFIX + "times_drowned");
            timesDrowned++;
            event.getEntity().getPersistentData().putInt(IguanaTweaksReborn.RESOURCE_PREFIX + "times_drowned", timesDrowned);

            event.setDamageAmount(event.getDamageAmount() * timesDrowned * 0.5f);
            event.setBubbleCount((int) (event.getBubbleCount() * timesDrowned * 0.5f));
        }
    }
}
