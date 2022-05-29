package insane96mcp.iguanatweaksreborn.module.mining.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

//TODO Change to misc and add skulls insta mining
@Label(name = "Insta-mine Silverfish", description = "Makes silverfishes blocks insta-mine like pre-1.17")
public class InstaMineSilverfish extends Feature {

	public InstaMineSilverfish(Module module) {
		super(Config.builder, module);
	}

	@SubscribeEvent
	public void silverfishBreakSpeed(PlayerEvent.BreakSpeed event) {
		if (event.getState().getBlock() instanceof InfestedBlock)
			event.setNewSpeed(Float.MAX_VALUE);
	}
}
