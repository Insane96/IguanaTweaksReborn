package net.insane96mcp.iguanatweaks.lib;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Reflection {
	public static Field EntityPlayer_speedInAir;
		
	public static void Init() {
		try {
			EntityPlayer_speedInAir = ReflectionHelper.findField(EntityPlayer.class, "speedInAir", "field_71102_ce");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SideOnly(Side.CLIENT)
	public static class Client {
		public static Field KeyBinding_KEYBIND_ARRAY;
		
		public static void Init() {
			try {
				KeyBinding_KEYBIND_ARRAY = ReflectionHelper.findField(KeyBinding.class, "KEYBIND_ARRAY", "field_74516_a");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void Set(Field field, Object object, Object value) {
		try {
			field.set(object, value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static Object Get(Field field, Object object) {
		try {
			return field.get(object);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object Invoke(Method method, Object object, Object... params) {
		try {
			return method.invoke(object, params);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
}
