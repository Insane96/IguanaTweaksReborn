package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.modules.ModuleHud;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Mouse {
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void EventMouse(MouseEvent event) {
		ModuleHud.HotbarCheckMouse(event.getDwheel());
	}
}
