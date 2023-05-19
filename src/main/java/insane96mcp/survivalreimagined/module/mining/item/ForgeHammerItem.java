package insane96mcp.survivalreimagined.module.mining.item;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;

public class ForgeHammerItem extends TieredItem {

    final int useCooldown;

    public ForgeHammerItem(Tier pTier, int useCooldown, Properties pProperties) {
        super(pTier, pProperties);
        this.useCooldown = useCooldown;
    }

    public int getUseCooldown() {
        return useCooldown;
    }
}
