package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.modules.ModuleGeneral;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RegistryRegister {
	
	@SubscribeEvent
	public static void EventRegisterIRecipe(RegistryEvent.Register<IRecipe> event) {
		IguanaTweaks.logger.info("RegisteringRecipes");
		ModuleGeneral.TorchesPerCoal(event);
	}
	
	@SubscribeEvent
	public static void EventRegisterPotion(RegistryEvent.Register<Potion> event) {
		IguanaTweaks.logger.info("RegisteringPotions");
		ModuleGeneral.AlterPoison(event);
	}
}
