package net.insane96mcp.iguanatweaks.item;

import java.util.ArrayList;

import net.minecraft.item.Item;

public class ModItems {
	public static ItemWeightlessRing WEIGHTLESS_RING;

	public static ArrayList<Item> ITEMS = new ArrayList<Item>();
	
	public static void Init() {
		WEIGHTLESS_RING = new ItemWeightlessRing();
	}
	
	public static void register(Item item) {
		ITEMS.add(item);
	}
}
