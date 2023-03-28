package insane96mcp.survivalreimagined.setup;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraftforge.registries.DeferredRegister;

public class SRStructurePlacements {
    public static final DeferredRegister<StructurePlacementType<?>> STRUCTURE_PLACEMENTS = DeferredRegister.create(Registries.STRUCTURE_PLACEMENT, SurvivalReimagined.MOD_ID);

    //public static final RegistryObject<StructurePlacementType<?>> RANDOM_SPREAD_WITH_CHANCE = STRUCTURE_PLACEMENTS.register("random_spread_with_chance", () -> SRRandomSpreadWithChance.CODEC);
}
