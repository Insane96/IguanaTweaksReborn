package insane96mcp.survivalreimagined.setup;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.mining.solarium.SoliumBoulderFeature;
import insane96mcp.survivalreimagined.module.world.oregeneration.BeegOreVeinFeature;
import insane96mcp.survivalreimagined.module.world.oregeneration.OreWithRandomPatchConfiguration;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SRFeatureType {
    public static final DeferredRegister<Feature<?>> REGISTRY = DeferredRegister.create(Registries.FEATURE, SurvivalReimagined.MOD_ID);

    public static final RegistryObject<BeegOreVeinFeature> ORE_WITH_SURFACE_FEATURE = REGISTRY.register("ore_with_surface_feature", () -> new BeegOreVeinFeature(OreWithRandomPatchConfiguration.CODEC));
    public static final RegistryObject<SoliumBoulderFeature> SOLIUM_BOUDLER_FEATURE = REGISTRY.register("solium_boulder_feature", () -> new SoliumBoulderFeature(BlockStateConfiguration.CODEC));
}
