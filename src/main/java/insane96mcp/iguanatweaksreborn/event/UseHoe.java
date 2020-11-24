package insane96mcp.iguanatweaksreborn.event;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.modules.FarmingModule;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID)
public class UseHoe {

	@SubscribeEvent
	//TODO Replace with PlayerInteractEvent.RightClickBlock
	public static void useHoe(PlayerInteractEvent.RightClickBlock event) {
		FarmingModule.Agriculture.disabledHoes(event);
		FarmingModule.Agriculture.harderTilling(event);
	}
}
