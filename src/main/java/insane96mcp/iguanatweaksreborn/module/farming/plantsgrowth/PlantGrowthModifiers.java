package insane96mcp.iguanatweaksreborn.module.farming.plantsgrowth;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.farming.plantsgrowth.modifier.CorrectBiomeModifier;
import insane96mcp.iguanatweaksreborn.module.farming.plantsgrowth.modifier.NightTimeModifier;
import insane96mcp.iguanatweaksreborn.module.farming.plantsgrowth.modifier.SunlightModifier;
import insane96mcp.iguanatweaksreborn.module.farming.plantsgrowth.modifier.WrongBiomeModifier;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class PlantGrowthModifiers {
    public static final Map<ResourceLocation, Type> MODIFIERS = new HashMap<>();

    public static void init() {
        registerModifier(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "sunlight"), SunlightModifier.class);
        registerModifier(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "night_time"), NightTimeModifier.class);
        registerModifier(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "correct_biome"), CorrectBiomeModifier.class);
        registerModifier(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "wrong_biome"), WrongBiomeModifier.class);
    }

    public static Type registerModifier(ResourceLocation id, Type modifier) {
        MODIFIERS.put(id, modifier);
        return modifier;
    }
}
