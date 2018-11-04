package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.modules.ModuleGeneral;
import net.insane96mcp.iguanatweaks.modules.ModuleHud;
import net.insane96mcp.iguanatweaks.modules.ModuleMovementRestriction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
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
		
		boolean hideHealthBar = ModuleHud.HideHealthBar(type, player);
		boolean hideHungerBar = ModuleHud.HideHungerBar(type, player);
		boolean hideHotbar = ModuleHud.HideHotbar(type, player);
		boolean hideExperienceBar = ModuleHud.HideExperienceBar(type, player);
		boolean hideArmorBar = ModuleHud.HideArmorBar(type, player);
		
		if (hideHealthBar || hideHungerBar || hideHotbar || hideExperienceBar || hideArmorBar)
			event.setCanceled(true);
		
		if (hideHotbar) {
			GuiIngameForge.left_height -= 28;
			GuiIngameForge.right_height -= 28;
		}

		//Not working for some reasons
		if (!hideHotbar && hideExperienceBar) {
			GuiIngameForge.left_height -= 3;
			GuiIngameForge.right_height -= 3;
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
		ModuleMovementRestriction.PrintHudInfos(event);
		ModuleHud.PrintCreativeText(event);
	}
}
