package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.modules.ModuleFarming;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.world.BlockEvent.CropGrowEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
public class CropGrow {

	@SubscribeEvent
	public static void EventCropGrowPre(CropGrowEvent.Pre event) {
		ModuleFarming.cropRequireWater(event);
	}

	@SubscribeEvent
	public static void EventCropGrowPre(CropGrowEvent.Post event) {
		ModuleFarming.cropGrowthSpeedMultiplier(event);
		ModuleFarming.reedsGrowthSpeedMultiplier(event);
		ModuleFarming.cactusGrowthSpeedMultiplier(event);
	}
	
	@SubscribeEvent
	public static void EventBonemeal(BonemealEvent event) {
		ModuleFarming.NerfBonemeal(event);
	}
}
