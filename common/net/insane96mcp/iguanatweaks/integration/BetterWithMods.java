package net.insane96mcp.iguanatweaks.integration;

import java.lang.reflect.Method;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public class BetterWithMods {
	private static String name = "betterwithmods";
	
	private static Class HCStumping;
	private static Object obj;
	private static Method isStump, isRoots;
	
	public static void Init() {
		try {
			HCStumping = Class.forName("betterwithmods.module.hardcore.world.HCStumping");
			obj = HCStumping.newInstance();
			isStump = HCStumping.getMethod("isStump", IBlockState.class);
			isRoots = HCStumping.getMethod("isRoots", IBlockState.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean IsPresent() {
		return Loader.isModLoaded(name);
	}
	
	public static boolean IsStump(IBlockState state) {
		try {
			return (boolean) isStump.invoke(obj, state);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean IsRoots(IBlockState state) {
		try {
			return (boolean) isRoots.invoke(obj, state);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean IsStumpOrRoot(IBlockState state, World world, BlockPos pos) {
		if (BetterWithMods.IsPresent()) {
			IBlockState down = world.getBlockState(pos.down());
			IBlockState up = world.getBlockState(pos.up());
			if ((BetterWithMods.IsStump(state) && BetterWithMods.IsRoots(down))
				|| (BetterWithMods.IsRoots(state) && BetterWithMods.IsStump(up)))
			{
				return true;
			}
		}
		return false;
	}
}
