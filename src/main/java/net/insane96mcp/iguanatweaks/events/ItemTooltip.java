package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.modules.ModuleMovementRestriction;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
public class ItemTooltip {
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void EventItemTooltip(ItemTooltipEvent event) {
		ModuleMovementRestriction.RenderWeightTooltip(event);
	}
}
