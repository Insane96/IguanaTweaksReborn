package insane96mcp.iguanatweaksreborn.data.generator;

import insane96mcp.iguanatweaksreborn.module.experience.Lapis;
import insane96mcp.iguanatweaksreborn.module.farming.crops.Crops;
import insane96mcp.iguanatweaksreborn.module.items.recallidol.RecallIdol;
import insane96mcp.iguanatweaksreborn.module.mining.SoulSteel;
import insane96mcp.iguanatweaksreborn.module.mobs.equipment.Equipment;
import insane96mcp.iguanatweaksreborn.module.movement.minecarts.Minecarts;
import insane96mcp.iguanatweaksreborn.module.world.Loot;
import insane96mcp.iguanatweaksreborn.module.world.coalfire.CoalFire;
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
        Minecarts.addGlobalLoot(this);
        CoalFire.addGlobalLoot(this);
        Lapis.addGlobalLoot(this);
        RecallIdol.addGlobalLoot(this);
        SoulSteel.addGlobalLoot(this);
    }
}
