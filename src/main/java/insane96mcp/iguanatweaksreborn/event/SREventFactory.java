package insane96mcp.iguanatweaksreborn.event;

import insane96mcp.iguanatweaksreborn.module.world.explosionoverhaul.ITRExplosion;
import insane96mcp.iguanatweaksreborn.module.world.explosionoverhaul.ITRExplosionCreatedEvent;
import net.minecraftforge.common.MinecraftForge;

public class SREventFactory {
    /**
     * Returns true if the event is canceled
     */
    public static boolean onSRExplosionCreated(ITRExplosion explosion)
    {
        ITRExplosionCreatedEvent event = new ITRExplosionCreatedEvent(explosion);
        MinecraftForge.EVENT_BUS.post(event);
        return event.isCanceled();
    }
}
