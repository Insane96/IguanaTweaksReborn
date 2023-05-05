package insane96mcp.survivalreimagined.module.misc.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Nerfs", description = "Various Nerfs")
@LoadFeature(module = Modules.Ids.MISC)
public class Nerfs extends Feature {
	@Config
	@Label(name = "Iron from Golems only when killed by Player", description = "If true, Iron golems will only drop Iron when killed by the player.")
	public static Boolean ironRequiresPlayer = true;
	@Config
	@Label(name = "No Coordinates", description = "If true, renderDebugInfo is enabled by default. Requires a world restart")
	public static Boolean noCoordinates = true;
	@Config
	@Label(name = "Reduced Random Tick Speed", description = "If true, randomTickSpeed is set to 1 from 3")
	public static Boolean reducedRandomTickSpeed = true;

	public Nerfs(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void onLivingDrop(LivingDropsEvent event) {
		if (!this.isEnabled())
			return;

		if (ironRequiresPlayer && event.getEntity() instanceof IronGolem && !(event.getSource().getDirectEntity() instanceof Player))
			event.getDrops().removeIf(itemEntity -> itemEntity.getItem().is(Items.IRON_INGOT));
	}

	@SubscribeEvent
	public void onServerStarted(ServerStartedEvent event) {
		if (!this.isEnabled())
			return;

		if (noCoordinates)
			event.getServer().getGameRules().getRule(GameRules.RULE_REDUCEDDEBUGINFO).set(true, event.getServer());
		if (reducedRandomTickSpeed)
			event.getServer().getGameRules().getRule(GameRules.RULE_RANDOMTICKING).set(1, event.getServer());
	}
}