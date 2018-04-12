package net.insane96mcp.iguanatweaks.integration;

import java.lang.reflect.Method;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public class WearableBackpacks {
	private static String name = "wearablebackpacks";
	
	private static Class BackpackHelper;
	private static Class IBackpack;
	private static Object obj;
	private static Method getBackpack;
	
	public static void Init() {
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static boolean IsPresent() {
		return Loader.isModLoaded(name);
	}
}
