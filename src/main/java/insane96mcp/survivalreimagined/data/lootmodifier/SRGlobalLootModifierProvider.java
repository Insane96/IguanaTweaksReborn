package insane96mcp.survivalreimagined.data.lootmodifier;

import insane96mcp.survivalreimagined.module.farming.feature.Crops;
import insane96mcp.survivalreimagined.module.farming.feature.Livestock;
import insane96mcp.survivalreimagined.module.items.feature.World;
import insane96mcp.survivalreimagined.module.mining.feature.Iron;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class SRGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public SRGlobalLootModifierProvider(PackOutput output, String modid) {
        super(output, modid);
    }

    @Override
    protected void start() {
        Iron.addGlobalLoot(this);
        Crops.addGlobalLoot(this);
        Livestock.addGlobalLoot(this);
        World.addGlobalLoot(this);
    }
}
