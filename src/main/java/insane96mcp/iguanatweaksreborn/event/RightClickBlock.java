package insane96mcp.iguanatweaksreborn.event;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.modules.FarmingModule;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID)
public class RightClickBlock {

	@SubscribeEvent
	//TODO Replace with PlayerInteractEvent.RightClickBlock
	public static void useHoe(BlockEvent.BlockToolInteractEvent event) {
		FarmingModule.Agriculture.disabledHoes(event);
		FarmingModule.Agriculture.harderTilling(event);
	}
}
