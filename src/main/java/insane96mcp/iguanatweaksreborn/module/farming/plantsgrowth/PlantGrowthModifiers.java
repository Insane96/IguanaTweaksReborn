package insane96mcp.iguanatweaksreborn.module.farming.plantsgrowth;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.farming.plantsgrowth.modifier.SunlightModifier;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class PlantGrowthModifiers {
    public static final Map<ResourceLocation, Type> MODIFIERS = new HashMap<>();
    public static final Type SUNLIGHT = registerModifier(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "sunlight"), SunlightModifier.class);

    public static Type registerModifier(ResourceLocation id, Type modifier) {
        MODIFIERS.put(id, modifier);
        return modifier;
    }
}
