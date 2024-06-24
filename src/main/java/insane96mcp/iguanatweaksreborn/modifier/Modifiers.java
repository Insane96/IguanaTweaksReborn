package insane96mcp.iguanatweaksreborn.modifier;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Modifiers {
    public static final Map<ResourceLocation, Type> MODIFIERS = new HashMap<>();

    public static void init() {
        registerModifier(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "global"), Modifier.class);
        registerModifier(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "sunlight"), SunlightModifier.class);
        registerModifier(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "night_time"), NightTimeModifier.class);
        registerModifier(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "correct_biome"), CorrectBiomeModifier.class);
        registerModifier(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "wrong_biome"), WrongBiomeModifier.class);
        registerModifier(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "age"), AgeModifier.class);
        if (ModList.get().isLoaded("sereneseasons"))
            registerModifier(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "season"), SeasonModifier.class);
    }

    public static Type registerModifier(ResourceLocation id, Type modifier) {
        MODIFIERS.put(id, modifier);
        return modifier;
    }
}
