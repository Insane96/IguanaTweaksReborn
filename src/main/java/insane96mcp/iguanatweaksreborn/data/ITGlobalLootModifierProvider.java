package insane96mcp.iguanatweaksreborn.data;

import insane96mcp.iguanatweaksreborn.module.farming.feature.Crops;
import insane96mcp.iguanatweaksreborn.module.farming.feature.Livestock;
import insane96mcp.iguanatweaksreborn.module.mining.feature.Iron;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class ITGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public ITGlobalLootModifierProvider(PackOutput output, String modid) {
        super(output, modid);
    }

    @Override
    protected void start() {
        Iron.addGlobalLoot(this);
        Crops.addGlobalLoot(this);
        Livestock.addGlobalLoot(this);
    }
}
