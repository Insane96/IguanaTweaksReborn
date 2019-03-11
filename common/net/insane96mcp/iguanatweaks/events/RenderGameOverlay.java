package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.modules.ModuleMisc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
public class RenderGameOverlay {
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void eventRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
		EntityPlayerSP player = Minecraft.getInstance().player;
		ElementType type = event.getType();
		
		
		if (type.equals(ElementType.HEALTH) && player.isPotionActive(ModuleMisc.alteredPoison)) {
			ModuleMisc.renderPoisonedHearts(Minecraft.getInstance().mainWindow.getScaledWidth(), Minecraft.getInstance().mainWindow.getScaledHeight());
			event.setCanceled(true);
		}
		
		/*boolean hideHealthBar = ModuleHud.HideHealthBar(type, player);
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
		}*/
	}
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
		//ModuleMovementRestriction.PrintHudInfos(event);
		//ModuleHud.PrintCreativeText(event);
	}
}
