package insane96mcp.survivalreimagined.setup;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SRParticles {
    public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, SurvivalReimagined.MOD_ID);

    public static final RegistryObject<SimpleParticleType> ELECTROCUTION_SPARKS = REGISTRY.register("electrocution_sparks", () -> new SimpleParticleType(true));
}
