package insane96mcp.iguanatweaksreborn.event;

import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when the game checks if the player can sprint.
 * Cancel to prevent player sprinting
 */
@Cancelable
public class PlayerSprintEvent extends Event {
    private final LocalPlayer player;

    public PlayerSprintEvent(LocalPlayer player) {
        super();
        this.player = player;
    }

    public LocalPlayer getPlayer() {
        return player;
    }

    public boolean canSprint() {
        return !this.isCanceled();
    }
}
