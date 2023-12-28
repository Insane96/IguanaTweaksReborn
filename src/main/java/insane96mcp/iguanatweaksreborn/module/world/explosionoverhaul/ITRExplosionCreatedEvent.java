package insane96mcp.iguanatweaksreborn.module.world.explosionoverhaul;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when a SRExplosion is created, allowing for modifications.
 * Not cancellable and doesn't have a result
 */
@Cancelable
public class ITRExplosionCreatedEvent extends Event {
    private final ITRExplosion explosion;

    public ITRExplosionCreatedEvent(ITRExplosion explosion) {
        super();
        this.explosion = explosion;
    }

    public ITRExplosion getExplosion() {
        return this.explosion;
    }
}
