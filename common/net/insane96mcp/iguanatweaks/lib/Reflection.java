package net.insane96mcp.iguanatweaks.lib;

import java.lang.reflect.Field;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
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
}
