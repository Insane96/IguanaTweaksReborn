package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.item.ModItems;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
public class LootTableLoad {

	@SubscribeEvent
	public static void EventLootTableLoad(LootTableLoadEvent event) {
		/*if (event.getName().equals(LootTableList.CHESTS_NETHER_BRIDGE)
			|| event.getName().equals(LootTableList.CHESTS_END_CITY_TREASURE)) {
			event.getTable().getPool("main").addEntry(new LootEntryItem(ModItems.WEIGHTLESS_RING, 1, 1, new LootFunction[0], new LootCondition[0], "iguanatweaks:weightless_ring"));
		}*/
	}
}
