package insane96mcp.iguanatweaksreborn.module.misc;

import insane96mcp.iguanatweaksreborn.data.generator.SRBlockTagsProvider;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
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
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Tweaks", description = "Various stuff that doesn't fit in any other Feature.")
@LoadFeature(module = Modules.Ids.MISC)
public class Tweaks extends Feature {

    public static final TagKey<Block> FALL_ON_BREAK = SRBlockTagsProvider.create("fall_on_break");

    @Config
    @Label(name = "Prevent fire with resistance", description = "If true, entities will no longer be set on fire if have Fire Resistance (like bedrock edition)")
    public static Boolean preventFireWithResistance = true;

    @Config
    @Label(name = "Falling breaking glass", description = "Falling on glass has a chance of breaking it. The higher the fall, the higher the chance. iguanatweaksreborn:fall_on_break block tag can be used to add more blocks that break when falling onto them.")
    public static Boolean fallingBreakingGlass = true;
    @Config(min = 1)
    @Label(name = "Poison damage speed", description = "Poison will damage the player every this ticks at level I. Vanilla is 25.")
    public static Integer poisonDamageSpeed = 60;
    //TODO Remove when quark 1.20.x
    @Config
    @Label(name = "Maximum Sponge Soak Blocks", description = "The maximum amount of blocks a sponge can soak. (Vanilla is 64)")
    public static Integer maxSpongeSoakBlocks = 256;
    @Config
    @Label(name = "Maximum Sponge Soak Range", description = "The maximum range at which sponges will check for soakable blocks. (Vanilla is 5)")
    public static Integer maxSpongeSoakRange = 10;

    @Config
    @Label(name = "Better hardcore death", description = "When you die in hardcore, your spawn point is set to where you died and a lightning strike is summoned")
    public static Boolean betterHardcoreDeath = true;

    @Config
    @Label(name = "Player air ticks consumed", description = "The amount of ticks the player consumes when underwater. In vanilla it's 1 without Respiration enchantment.")
    public static Integer playerConsumeAirAmount = 1;
    @Config
    @Label(name = "Player air ticks refilled", description = "The amount of air ticks the player regains each tick when out of water. Vanilla is 4")
    public static Integer playerRefillAirAmount = 1;

    public Tweaks(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
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

    public static int changeSpongeMaxRange(int range) {
        if (!isEnabled(Tweaks.class))
            return range;

        //Vanilla uses < instead of <=
        return maxSpongeSoakRange + 1;
    }

    public static int getPoisonDamageSpeed() {
        return isEnabled(Tweaks.class) ? poisonDamageSpeed : 25;
    }

    @SubscribeEvent
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
        float chance = (event.getDistance() - 3) * 0.05f;
        if (entity.getRandom().nextFloat() >= chance)
            return;
        for (int x2 = mX; x2 < bb.maxX; x2++) {
            for (int z2 = mZ; z2 < bb.maxZ; z2++) {
                pos.set(x2, entity.position().y - 1.0E-5F, z2);
                BlockState state = entity.level().getBlockState(pos);
                if (state.is(FALL_ON_BREAK)) {
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
                || !(event.getEntity() instanceof Player player))
            return;

        int airConsumed = playerConsumeAirAmount;
        int respiration = EnchantmentHelper.getRespiration(player);
        airConsumed = respiration > 0 && player.getRandom().nextInt(respiration + 1) > 0 ? 0 : airConsumed;
        event.setConsumeAirAmount(airConsumed);
        event.setRefillAirAmount(playerRefillAirAmount);
    }
}
