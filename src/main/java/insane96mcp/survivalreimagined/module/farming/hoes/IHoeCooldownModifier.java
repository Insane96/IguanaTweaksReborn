package insane96mcp.survivalreimagined.module.farming.hoes;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface IHoeCooldownModifier {
    int getCooldownOnUse(int baseCooldown, Player player, Level level);
}
