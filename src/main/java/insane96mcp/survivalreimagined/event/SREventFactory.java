package insane96mcp.survivalreimagined.event;

import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.common.MinecraftForge;

public class SREventFactory {

    public static boolean doPlayerSprintCheck(LocalPlayer player)
    {
        PlayerSprintEvent event = new PlayerSprintEvent(player);
        MinecraftForge.EVENT_BUS.post(event);
        return event.canSprint();
    }
}
