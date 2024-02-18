package insane96mcp.iguanatweaksreborn.modifier;

import insane96mcp.insanelib.data.IdTagMatcher;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBiomeModifier extends Modifier {
    protected List<IdTagMatcher> biomes = new ArrayList<>();
    protected AbstractBiomeModifier(float multiplier, List<IdTagMatcher> biomes) {
        super(multiplier);
        this.biomes.addAll(biomes);
    }
}
