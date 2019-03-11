package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.modules.ModuleMisc;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID, bus = Bus.MOD)
public class RegistryRegister {
	
	@SubscribeEvent
	public static void eventRegisterPotion(RegistryEvent.Register<Potion> event) {
		ModuleMisc.registerPoison(event);
	}
}
