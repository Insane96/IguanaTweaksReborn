package insane96mcp.iguanatweaksreborn.module.experience.feature;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Other Experience", description = "Change other experience sources")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class OtherExperience extends Feature {
	@Config(min = 0, max = 512)
	@Label(name = "Bottle o' Enchanting Bonus XP", description = "Bottle o' enchanting will drop this more XP. Experience is still affected by 'Global Experience Multiplier'\nCan be set to 0 to make Bottle o' enchanting drop no experience")
	public static Integer xpBottleBonus = 18;
	@Config(min = 0)
	@Label(name = "Anvil Repair Cap", description = "Set the cap for repairing items in the anvil (vanilla is 40)")
	public static Integer anvilRepairCap = 1024;
	@Config
	@Label(name = "Remove rename cost", description = "Removes cost of renaming items in Anvil")
	public static Boolean freeRenaming = true;
	@Config
	@Label(name = "Unmending", description = "Replaces the default Mending enchantment. Mending sets the repair cost of an item to 'Unmending Cap' and will stop it from increasing. No longer repairs items with xp.")
	public static Boolean unmending = true;
	@Config(min = 1)
	@Label(name = "Unmending Cap", description = "Set the cap repair cost set by Unmending")
	public static Integer unmendingCap = 20;

	public OtherExperience(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	public void onXpBottleHit(ThrownExperienceBottle xpBottle) {
		if (!this.isEnabled()
				|| xpBottleBonus == 0)
			return;

		if (xpBottle.level instanceof ServerLevel) {
			ExperienceOrb.award((ServerLevel)xpBottle.level, xpBottle.position(), xpBottleBonus);
		}
	}

	public static boolean isFreeRenaming() {
		return isEnabled(OtherExperience.class) && freeRenaming;
	}

	@SubscribeEvent
	public void onAnvilUse(AnvilRepairEvent event) {
		if (!this.isEnabled()
				|| !unmending)
			return;
		ItemStack output = event.getOutput();
		if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, output) > 0 && output.getBaseRepairCost() > 15) {
			output.setRepairCost(unmendingCap);
		}
	}

	public static boolean isUnmendingEnabled() {
		return isEnabled(OtherExperience.class) && unmending;
	}
}