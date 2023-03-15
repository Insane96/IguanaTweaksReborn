package insane96mcp.survivalreimagined.module.combat.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;

@Label(name = "Shields", description = "Various changes to Shields. Disabled if Shields+ is installed.")
@LoadFeature(module = Modules.Ids.COMBAT)
public class Shields extends Feature {

	@Config
	@Label(name = "Remove Shield Windup", description = "In vanilla when you start blocking with a shield, there's a 0.25 seconds window where you are still not blocking. If true this windup time is removed.")
	public static Boolean removeShieldWindup = true;
	@Config(min = 0d, max = 128d)
	@Label(name = "Shield Damage Blocked", description = "Shields will only block this amount of damage. Setting to 0 will make shield block like vanilla.")
	public static Double shieldBlockDamage = 4d;
	@Config
	@Label(name = "Combat Test shield disabling", description = "Makes shields always disable for 1.6 seconds like Combat Test snapshots.")
	public static Boolean combatTestShieldDisabling = true;

	@Label(name = "Min Shield Hurt Damage", description = "The minimum damage dealt to the player for the shield to take damage (durability). Vanilla is 3.")
	public static Double minShieldHurtDamage = 1d;

	public Shields(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled() && !ModList.get().isLoaded("shieldsplus");
	}

	@SubscribeEvent
	public void onShieldBlock(ShieldBlockEvent event) {
		if (!this.isEnabled() || shieldBlockDamage == 0)
			return;

		event.setBlockedDamage(shieldBlockDamage.floatValue());
	}

	public static boolean shouldRemoveShieldWindup() {
		return isEnabled(Shields.class) && removeShieldWindup;
	}

	public static boolean combatTestShieldDisabling() {
		return isEnabled(Shields.class) && combatTestShieldDisabling;
	}
}