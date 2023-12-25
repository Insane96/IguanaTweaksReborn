package insane96mcp.iguanatweaksreborn.event;

import insane96mcp.iguanatweaksreborn.module.misc.explosionoverhaul.SRExplosion;
import net.minecraftforge.common.MinecraftForge;

public class SREventFactory {
    /**
     * Returns true if the event is canceled
     */
    public static boolean onSRExplosionCreated(SRExplosion explosion)
    {
        SRExplosionCreatedEvent event = new SRExplosionCreatedEvent(explosion);
        MinecraftForge.EVENT_BUS.post(event);
        return event.isCanceled();
    }
}
