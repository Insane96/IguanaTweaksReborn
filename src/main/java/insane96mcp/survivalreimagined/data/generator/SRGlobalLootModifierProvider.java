package insane96mcp.survivalreimagined.data.generator;

import insane96mcp.survivalreimagined.module.experience.Lapis;
import insane96mcp.survivalreimagined.module.farming.crops.Crops;
import insane96mcp.survivalreimagined.module.items.flintexpansion.FlintExpansion;
import insane96mcp.survivalreimagined.module.items.recallidol.RecallIdol;
import insane96mcp.survivalreimagined.module.mobs.equipment.Equipment;
import insane96mcp.survivalreimagined.module.movement.minecarts.Minecarts;
import insane96mcp.survivalreimagined.module.world.Loot;
import insane96mcp.survivalreimagined.module.world.coalfire.CoalFire;
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
        Lapis.addGlobalLoot(this);
        RecallIdol.addGlobalLoot(this);
    }
}
