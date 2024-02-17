package insane96mcp.iguanatweaksreborn.module.farming.plantsgrowth.modifier;

import insane96mcp.iguanatweaksreborn.module.farming.plantsgrowth.PlantGrowthModifier;
import insane96mcp.insanelib.data.IdTagMatcher;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBiomeModifier extends PlantGrowthModifier {
    protected List<IdTagMatcher> biomes = new ArrayList<>();
    protected AbstractBiomeModifier(float multiplier, List<IdTagMatcher> biomes) {
        super(multiplier);
        this.biomes.addAll(biomes);
    }
}
