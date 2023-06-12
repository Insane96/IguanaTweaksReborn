package insane96mcp.survivalreimagined.event;

import insane96mcp.survivalreimagined.module.misc.level.SRExplosion;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;

public class SREventFactory {

    public static boolean doPlayerSprintCheck(LocalPlayer player)
    {
        PlayerSprintEvent event = new PlayerSprintEvent(player);
        MinecraftForge.EVENT_BUS.post(event);
        return event.canSprint();
    }

    /**
     * Returns true if the event is canceled
     */
    public static boolean onSRExplosionCreated(SRExplosion explosion)
    {
        SRExplosionCreatedEvent event = new SRExplosionCreatedEvent(explosion);
        MinecraftForge.EVENT_BUS.post(event);
        return event.isCanceled();
    }

    public static void onBlockBurnt(Level level, BlockPos pos, BlockState state)
    {
        BlockBurntEvent event = new BlockBurntEvent(level, pos, state);
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static void onPostHurtEntity(LivingEntity livingEntity, DamageSource damageSource, float amount)
    {
        PostEntityHurtEvent event = new PostEntityHurtEvent(livingEntity, damageSource, amount);
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static float onPlayerExhaustionEvent(Player player, float amount)
    {
        PlayerExhaustionEvent event = new PlayerExhaustionEvent(player, amount);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getAmount();
    }

    public static void onCakeEatEvent(Player player, BlockPos pos, LevelAccessor level)
    {
        CakeEatEvent event = new CakeEatEvent(player, pos, level);
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static void onFallingBlockLand(FallingBlockEntity fallingBlock)
    {
        FallingBlockLandEvent event = new FallingBlockLandEvent(fallingBlock);
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static int getHurtAmount(ItemStack stack, int amount, RandomSource random, @Nullable ServerPlayer player)
    {
        HurtItemStackEvent event = new HurtItemStackEvent(stack, amount, random, player);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getAmount();
    }
}
