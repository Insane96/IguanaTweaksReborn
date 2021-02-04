package insane96mcp.iguanatweaksreborn.utils;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

public class Reflection {
    static MethodHandles.Lookup lookup = MethodHandles.lookup();

    static MethodType mt = MethodType.methodType(void.class, float.class);
    static Method damageShieldMethod;
    public static MethodHandle damageShieldMH;
    public static void PlayerEntity_damageShield(PlayerEntity playerEntity, float damage) {
        try {
            damageShieldMH.invoke(playerEntity, damage);
        }
        catch (Throwable throwable) {
            IguanaTweaksReborn.LOGGER.error(throwable.toString());
        }
    }

    public static void init() {
        try {
            damageShieldMethod = ObfuscationReflectionHelper.findMethod(PlayerEntity.class, "func_184590_k", float.class);
            damageShieldMH = lookup.unreflect(damageShieldMethod);
        } catch (IllegalAccessException e) {
            IguanaTweaksReborn.LOGGER.error(e.toString());
        }
    }
}
