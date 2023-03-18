package insane96mcp.survivalreimagined.event;

import insane96mcp.survivalreimagined.module.misc.level.SRExplosion;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when a SRExplosion is created, allowing for modifications.
 * Not cancellable and doesn't have a result
 */
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
