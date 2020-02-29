package insane96mcp.iguanatweaksreborn.event;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.modules.FarmingModule;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID)
public class CropGrow {

	@SubscribeEvent
	public static void EventCropGrowPre(BlockEvent.CropGrowEvent.Pre event) {
		FarmingModule.cropsRequireWater(event);
	}

	@SubscribeEvent
	public static void EventCropGrowPre(BlockEvent.CropGrowEvent.Post event) {
		FarmingModule.cropsGrowthSpeedMultiplier(event);
		FarmingModule.sugarCaneGrowthSpeedMultiplier(event);
		FarmingModule.cactusGrowthSpeedMultiplier(event);
	}

	@SubscribeEvent
	public static void eventBonemeal(BonemealEvent event) {
		FarmingModule.nerfBonemeal(event);
	}
}