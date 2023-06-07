package insane96mcp.survivalreimagined.data.generator;

import insane96mcp.survivalreimagined.module.experience.feature.EnchantmentsFeature;
import insane96mcp.survivalreimagined.module.farming.feature.Crops;
import insane96mcp.survivalreimagined.module.items.feature.FlintExpansion;
import insane96mcp.survivalreimagined.module.items.feature.RecallIdol;
import insane96mcp.survivalreimagined.module.mobs.feature.Equipment;
import insane96mcp.survivalreimagined.module.movement.feature.Minecarts;
import insane96mcp.survivalreimagined.module.world.feature.CoalFire;
import insane96mcp.survivalreimagined.module.world.feature.Loot;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class SRGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public SRGlobalLootModifierProvider(PackOutput output, String modid) {
        super(output, modid);
    }


    @Override
    protected void start() {
        Crops.addGlobalLoot(this);
        Equipment.addGlobalLoot(this);
        Loot.addGlobalLoot(this);
        FlintExpansion.addGlobalLoot(this);
        Minecarts.addGlobalLoot(this);
        CoalFire.addGlobalLoot(this);
        EnchantmentsFeature.addGlobalLoot(this);
        RecallIdol.addGlobalLoot(this);
    }
}
