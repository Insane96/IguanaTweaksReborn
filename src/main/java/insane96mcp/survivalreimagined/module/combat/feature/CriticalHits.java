package insane96mcp.survivalreimagined.module.combat.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Critical Hits")
@LoadFeature(module = Modules.Ids.COMBAT)
public class CriticalHits extends Feature {
	@Config(min = 0d)
	@Label(name = "Critical hit damage multiplier", description = "How much damage a critical hit does. Vanilla is 1.5")
	public static Double critDamageMultiplier = 1.30d;

	public CriticalHits(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	//Run before Critical enchantment
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onCriticalHit(CriticalHitEvent event) {
		if (!this.isEnabled())
			return;
		event.setDamageModifier(critDamageMultiplier.floatValue());
	}
}