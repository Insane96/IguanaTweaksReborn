package insane96mcp.survivalreimagined.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * Fired when a falling block successfully lands.
 */

public class FallingBlockLandEvent extends EntityEvent {

    public FallingBlockLandEvent(Entity entity) {
        super(entity);
    }

    public FallingBlockEntity getFallingBlock() {
        return (FallingBlockEntity) this.getEntity();
    }
}
