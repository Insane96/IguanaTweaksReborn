package insane96mcp.survivalreimagined.event;

import insane96mcp.survivalreimagined.module.misc.level.SRExplosion;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.common.MinecraftForge;

public class SREventFactory {

    public static boolean doPlayerSprintCheck(LocalPlayer player)
    {
        PlayerSprintEvent event = new PlayerSprintEvent(player);
        MinecraftForge.EVENT_BUS.post(event);
        return event.canSprint();
    }

    public static void onSRExplosionCreated(SRExplosion explosion)
    {
        SRExplosionCreatedEvent event = new SRExplosionCreatedEvent(explosion);
        MinecraftForge.EVENT_BUS.post(event);
    }
}
