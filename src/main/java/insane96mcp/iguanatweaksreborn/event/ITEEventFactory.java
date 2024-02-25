package insane96mcp.iguanatweaksreborn.event;

import insane96mcp.iguanatweaksreborn.module.world.explosionoverhaul.ITRExplosion;
import insane96mcp.iguanatweaksreborn.module.world.explosionoverhaul.ITRExplosionCreatedEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraftforge.common.MinecraftForge;

public class ITEEventFactory {
    /**
     * Returns true if the event is canceled
     */
    public static boolean onSRExplosionCreated(ITRExplosion explosion)
    {
        ITRExplosionCreatedEvent event = new ITRExplosionCreatedEvent(explosion);
        MinecraftForge.EVENT_BUS.post(event);
        return event.isCanceled();
    }

    /**
     * Apply changes to damage amount after damage absorb but before absorption reduction
     */
    public static float onLivingHurtPreAbsorption(LivingEntity livingEntity, DamageSource source, float amount)
    {
        LivingHurtPreAbsorptionEvent event = new LivingHurtPreAbsorptionEvent(livingEntity, source, amount);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getAmount();
    }

    /**
     * Apply changes to the ticks that will be removed from the hook to lure and hook
     */
    public static int onHookTickToHookLure(FishingHook hook, int tick)
    {
        HookTickToHookLureEvent event = new HookTickToHookLureEvent(hook, tick);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getTick();
    }
}
