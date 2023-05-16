package insane96mcp.survivalreimagined.network;

import net.minecraft.client.Minecraft;
import net.minecraft.server.TickTask;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;

public class ClientNetworkHandler {
    public static void handleSyncInvulnerableTimeMessage(int entityId, int invulnerableTime) {
        BlockableEventLoop<? super TickTask> executor = LogicalSidedProvider.WORKQUEUE.get(LogicalSide.CLIENT);
        executor.tell(new TickTask(0, () -> {
            Level level = Minecraft.getInstance().level;
            LivingEntity entity = (LivingEntity) level.getEntity(entityId);
            if (entity == null)
                return;
            entity.invulnerableTime = invulnerableTime;
            entity.hurtTime = invulnerableTime;
        }));
    }
}
