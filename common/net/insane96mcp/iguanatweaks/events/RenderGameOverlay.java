package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.modules.ModuleGeneral;
import net.insane96mcp.iguanatweaks.modules.ModuleHud;
import net.insane96mcp.iguanatweaks.modules.ModuleMovementRestriction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RenderGameOverlay {
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void EventRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		ElementType type = event.getType();
		
		
		if (type.equals(ElementType.HEALTH) && player.isPotionActive(ModuleGeneral.alteredPoison)) {
			ModuleGeneral.RenderPoisonedHearts(event.getResolution());
			event.setCanceled(true);
		}
		
		if (ModuleHud.HideHealthBar(type, player)
			|| ModuleHud.HideHungerBar(type, player)
			|| ModuleHud.HideHotbar(type, player)) 
			event.setCanceled(true);
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
		ModuleMovementRestriction.PrintHudInfos(event);
	}
}
