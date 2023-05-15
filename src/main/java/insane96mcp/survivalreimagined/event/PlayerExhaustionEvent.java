package insane96mcp.survivalreimagined.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Fired after an entity has been damaged in LivingEntity#actuallyHurt.
 */
public class PlayerExhaustionEvent extends PlayerEvent {

    final float originalAmount;
    float amount;
    public PlayerExhaustionEvent(Player player, float amount) {
        super(player);
        this.originalAmount = amount;
        this.amount = amount;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getOriginalAmount() {
        return this.originalAmount;
    }
}
