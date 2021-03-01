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
		if (!IsPresent())
			return;
		try {
			HCStumping = Class.forName("betterwithmods.module.hardcore.world.stumping.HCStumping");
			obj = HCStumping.newInstance();
			isStump = HCStumping.getMethod("isStump", World.class, BlockPos.class);
			isRoots = HCStumping.getMethod("isRoots", World.class, BlockPos.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static boolean IsPresent() {
		return Loader.isModLoaded(name);
	}
	
	private static boolean IsStump(World world, BlockPos pos) {
		try {
			return (boolean) isStump.invoke(obj, world, pos);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private static boolean IsRoots(World world, BlockPos pos) {
		try {
			return (boolean) isRoots.invoke(obj, world, pos);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean IsStumpOrRoot(IBlockState state, World world, BlockPos pos) {
		if (BetterWithMods.IsPresent()) {
			IBlockState down = world.getBlockState(pos.down());
			IBlockState up = world.getBlockState(pos.up());
			if ((BetterWithMods.IsStump(world, pos) || BetterWithMods.IsRoots(world, pos)))
			{
				return true;
			}
		}
		return false;
	}
}
