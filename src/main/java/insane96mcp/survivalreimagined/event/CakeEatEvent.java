package insane96mcp.survivalreimagined.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Fired after a cake has been sucessfully eaten.
 * <p>
 * The event is not cancellable.
 */
public class CakeEatEvent extends PlayerEvent {

    final BlockPos pos;
    final LevelAccessor level;

    public CakeEatEvent(Player player, BlockPos pos, LevelAccessor level) {
        super(player);
        this.pos = pos;
        this.level = level;
    }

    public BlockPos getPos() {
        return pos;
    }

    public LevelAccessor getLevel() {
        return level;
    }
}
