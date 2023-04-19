package insane96mcp.survivalreimagined.module.world.levelgen.feature.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;

public class OreWithRandomPatchConfiguration implements FeatureConfiguration {
    public static final Codec<OreWithRandomPatchConfiguration> CODEC = RecordCodecBuilder.create((p_67849_) -> p_67849_.group(
            OreConfiguration.CODEC.fieldOf("ore_config").forGetter((configuration) -> configuration.oreConfiguration),
            RandomPatchConfiguration.CODEC.fieldOf("random_patch_config").forGetter((configuration) -> configuration.patchConfiguration)
    ).apply(p_67849_, OreWithRandomPatchConfiguration::new));

    public OreConfiguration oreConfiguration;
    public RandomPatchConfiguration patchConfiguration;

    public OreWithRandomPatchConfiguration(OreConfiguration oreConfiguration, RandomPatchConfiguration patchConfiguration) {
        this.oreConfiguration = oreConfiguration;
        this.patchConfiguration = patchConfiguration;
    }

}
