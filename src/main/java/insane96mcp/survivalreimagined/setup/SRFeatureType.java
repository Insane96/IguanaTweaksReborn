package insane96mcp.survivalreimagined.setup;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.world.levelgen.feature.configuration.OreWithRandomPatchConfiguration;
import insane96mcp.survivalreimagined.module.world.levelgen.feature.configuration.OreWithSurfaceFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SRFeatureType {
    public static final DeferredRegister<Feature<?>> REGISTRY = DeferredRegister.create(Registries.FEATURE, SurvivalReimagined.MOD_ID);

    public static final RegistryObject<OreWithSurfaceFeature> ORE_WITH_SURFACE_FEATURE = REGISTRY.register("ore_with_surface_feature", () -> new OreWithSurfaceFeature(OreWithRandomPatchConfiguration.CODEC));
}
