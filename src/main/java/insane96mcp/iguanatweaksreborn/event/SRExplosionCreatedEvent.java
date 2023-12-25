package insane96mcp.iguanatweaksreborn.event;

import insane96mcp.iguanatweaksreborn.module.misc.explosionoverhaul.SRExplosion;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when a SRExplosion is created, allowing for modifications.
 * Not cancellable and doesn't have a result
 */
@Cancelable
public class SRExplosionCreatedEvent extends Event {
    private final SRExplosion explosion;

    public SRExplosionCreatedEvent(SRExplosion explosion) {
        super();
        this.explosion = explosion;
    }

    public SRExplosion getExplosion() {
        return this.explosion;
    }
}
