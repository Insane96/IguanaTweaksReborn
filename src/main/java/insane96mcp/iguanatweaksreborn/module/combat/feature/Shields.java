package insane96mcp.iguanatweaksreborn.module.combat.feature;

import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;

@Label(name = "Shields", description = "Various changes to Shields. Disabled if Shields+ is installed.")
public class Shields extends Feature {
	private final ForgeConfigSpec.BooleanValue removeShieldWindupConfig;
	private final ForgeConfigSpec.DoubleValue shieldBlockDamageConfig;
	private final ForgeConfigSpec.BooleanValue combatTestShieldDisablingConfig;

	public boolean removeShieldWindup = true;
	public double shieldBlockDamage = 5d;
	public boolean combatTestShieldDisabling = true;

	public Shields(Module module) {
		super(ITCommonConfig.builder, module);
		this.pushConfig(ITCommonConfig.builder);
		removeShieldWindupConfig = ITCommonConfig.builder
				.comment("In vanilla when you start blocking with a shield, there's a 0.25 seconds window where you are still not blocking. If true this windup time is removed.")
				.define("Remove Shield Windup", this.removeShieldWindup);
		shieldBlockDamageConfig = ITCommonConfig.builder
				.comment("Shields will only block this amount of damage. Setting to 0 will make shield block like vanilla")
				.defineInRange("Shield Damage Blocked", this.shieldBlockDamage, 0d, 128d);
		combatTestShieldDisablingConfig = ITCommonConfig.builder
				.comment("Makes shields always disable for 1.6 seconds like Combat Test snapshots.")
				.define("Combat Test shield disabling", this.combatTestShieldDisabling);
		ITCommonConfig.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.removeShieldWindup = this.removeShieldWindupConfig.get();
		this.shieldBlockDamage = this.shieldBlockDamageConfig.get();
		this.combatTestShieldDisabling = this.combatTestShieldDisablingConfig.get();
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled() && !ModList.get().isLoaded("shieldsplus");
	}

	@SubscribeEvent
	public void onShieldBlock(ShieldBlockEvent event) {
		if (!this.isEnabled() || this.shieldBlockDamage == 0)
			return;

		event.setBlockedDamage((float) this.shieldBlockDamage);
	}

	public boolean shouldRemoveShieldWindup() {
		return this.isEnabled() && this.removeShieldWindup && !ModList.get().isLoaded("shieldsplus");
	}

	public boolean combatTestShieldDisabling() {
		return this.isEnabled() && this.combatTestShieldDisabling && !ModList.get().isLoaded("shieldsplus");
	}
}