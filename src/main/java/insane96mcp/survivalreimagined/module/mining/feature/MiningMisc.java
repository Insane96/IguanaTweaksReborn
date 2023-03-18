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
	//TODO Change blocks hardness maybe
	@Config
	@Label(name = "Insta-Mine Heads", description = "Heads will insta-break")
	public static Boolean instaMineHeads = true;

	//TODO Prevent swords from mining 0 hardness blocks
	/*@Config
	@Label(name = "No Sword breaking insta-mine blocks", description = "Prevents swords from breaking blocks like grass, etc.")
	public static Boolean noSwordBreaking = true;*/

	//TODO Add a powerful TNT. Maybe a tnt that deals more "damage" to stone blocks

	public MiningMisc(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void onBreak(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled())
			return;

		/*if (event.getEntity().getMainHandItem().getItem() instanceof SwordItem && event.getState().destroySpeed == 0f)
			event.setNewSpeed(0f);*/

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
