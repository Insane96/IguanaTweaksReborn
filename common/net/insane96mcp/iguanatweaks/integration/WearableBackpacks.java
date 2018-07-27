package net.insane96mcp.iguanatweaks.integration;

import java.lang.reflect.Method;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.ItemStackHandler;
import scala.reflect.internal.Trees.New;

public class WearableBackpacks {
	private static String name = "wearablebackpacks";

	private static Class BackpackDataItems;
	private static Class BackpackHelper;
	private static Class IBackpackData;
	private static Class IBackpack;
	private static Object obj;
	private static Method getItems;
	private static Method getBackpack;
	private static Method getData;
	
	public static void Init() {
		if (!IsPresent())
			return;
		try {
			BackpackDataItems = Class.forName("net.mcft.copy.backpacks.misc.BackpackDataItems");
			BackpackHelper = Class.forName("net.mcft.copy.backpacks.api.BackpackHelper");
			IBackpackData = Class.forName("net.mcft.copy.backpacks.api.IBackpackData");
			IBackpack = Class.forName("net.mcft.copy.backpacks.api.IBackpack");
			obj = BackpackDataItems.newInstance();
			getItems = BackpackDataItems.getMethod("getItems", World.class, EntityPlayer.class);
			getBackpack = BackpackHelper.getMethod("getBackpack", Entity.class);
			getData = IBackpack.getMethod("getData");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static boolean IsPresent() {
		return Loader.isModLoaded(name);
	}
	
	private static ItemStackHandler GetItems(World world, EntityPlayer player) {
		try {
			return (ItemStackHandler) getItems.invoke(obj, world, player);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return new ItemStackHandler(0);
	}
	
	private static Object GetBackpack() {
		return 1;
	}
	
	public static void getItems(World world, EntityPlayer player) {
		
	}
}
