package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.modules.ModuleFarming;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.world.BlockEvent.CropGrowEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
public class CropGrow {

	@SubscribeEvent
	public static void EventCropGrowPre(CropGrowEvent.Pre event) {

	}

	@SubscribeEvent
	public static void EventCropGrowPre(CropGrowEvent.Post event) {
		World world = event.getWorld();
		IBlockState state = event.getOriginalState();
		world.setBlockState(event.getPos(), state);
	}
	
	@SubscribeEvent
	public static void EventBonemeal(BonemealEvent event) {
		ModuleFarming.NerfBonemeal(event);
	}
}
