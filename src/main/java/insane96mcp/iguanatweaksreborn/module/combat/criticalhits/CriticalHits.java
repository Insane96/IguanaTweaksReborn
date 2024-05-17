package insane96mcp.iguanatweaksreborn.module.combat.criticalhits;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.setup.ITRRegistries;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Critical Hits", description = "Rework critical hits to be a chance to happen instead of damage on jump")
@LoadFeature(module = Modules.Ids.COMBAT)
public class CriticalHits extends Feature {
	public static final RegistryObject<Enchantment> CRITICAL = ITRRegistries.ENCHANTMENTS.register("critical", CriticalEnchantment::new);

	@Config(min = -1d, max = 1d)
	@Label(name = "Chance", description = "If set >= 0 critical hits are no longer dealt, have a random chance to happen.")
	public static Double chance = 0.05d;
	@Config(min = -1d, max = 1d)
	@Label(name = "Enchantment Chance", description = "Chance increase per level of Critical enchantment.")
	public static Double enchantmentChance = 0.1d;
	@Config(min = -1d, max = 1d)
	@Label(name = "Enchantment Bonus", description = "Critical multiplier increase per level of Critical enchantment. This only applies if chance based crit is enabled, otherwise critical doubles the crit multiplier.")
	public static Double enchantmentBonus = 0.25d;
	@Config(min = 0d)
	@Label(name = "Damage multiplier", description = "How much damage a critical hit does. Vanilla is 1.5")
	public static Double critDamageMultiplier = 1.5d;

	public CriticalHits(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void onCriticalHit(CriticalHitEvent event) {
		if (!this.isEnabled())
			return;
		event.setDamageModifier(critDamageMultiplier.floatValue());
		int lvl = EnchantmentHelper.getEnchantmentLevel(CRITICAL.get(), event.getEntity());
		if (chance >= 0) {
			event.setResult(Event.Result.DENY);
			float c = chance.floatValue();
			if (lvl > 0) {
				c += enchantmentChance.floatValue() * lvl;
				event.setDamageModifier(event.getDamageModifier() + (lvl * enchantmentBonus.floatValue()));
			}
			if (event.getEntity().getRandom().nextFloat() < c)
				event.setResult(Event.Result.ALLOW);
		}
		else if (lvl > 0)
			event.setDamageModifier(CriticalEnchantment.getCritAmount(lvl, event.getDamageModifier()));
	}
}