package insane96mcp.survivalreimagined.module.mining.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Misc", description = "Various mining changes")
@LoadFeature(module = Modules.Ids.MINING)
public class MiningMisc extends Feature {
	@Config
	@Label(name = "Insta-Mine Silverfish", description = "Silverfish blocks will insta-mine like pre-1.17")
	public static Boolean instaMineSilverfish = true;
	@Config
	@Label(name = "Insta-Mine Heads", description = "Heads will insta-break")
	public static Boolean instaMineHeads = true;

	public MiningMisc(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void onBreak(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled())
			return;

		silverfishBreakSpeed(event);
		skullBreakSpeed(event);
	}

	public void silverfishBreakSpeed(PlayerEvent.BreakSpeed event) {
		if (!instaMineSilverfish)
			return;

		if (event.getState().getBlock() instanceof InfestedBlock)
			event.setNewSpeed(Float.MAX_VALUE);
	}

	public void skullBreakSpeed(PlayerEvent.BreakSpeed event) {
		if (!instaMineHeads)
			return;

		if (event.getState().getBlock() instanceof AbstractSkullBlock)
			event.setNewSpeed(Float.MAX_VALUE);
	}
}
