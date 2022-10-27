package insane96mcp.iguanatweaksreborn.module.experience.feature;

import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Other Experience", description = "Change other experience sources")
public class OtherExperience extends Feature {

	private final ForgeConfigSpec.IntValue xpBottleBonusConfig;
	private final ForgeConfigSpec.IntValue anvilRepairCapConfig;
	private final ForgeConfigSpec.BooleanValue freeRenamingConfig;
	private final ForgeConfigSpec.BooleanValue unmendingConfig;
	private final ForgeConfigSpec.IntValue unmendingCapConfig;

	public int xpBottleBonus = 18;
	public int anvilRepairCap = 2048;
	public boolean freeRenaming = true;
	public boolean unmending = true;
	public int unmendingCap = 20;

	public OtherExperience(Module module) {
		super(ITCommonConfig.builder, module, true);
		this.pushConfig(ITCommonConfig.builder);
		xpBottleBonusConfig = ITCommonConfig.builder
				.comment("Bottle o' enchanting will drop this more XP. Experience is still affected by 'Global Experience Multiplier'\nCan be set to 0 to make Bottle o' enchanting drop no experience")
				.defineInRange("Bottle o' Enchanting Bonus XP", this.xpBottleBonus, 0, 1024);
		anvilRepairCapConfig = ITCommonConfig.builder
				.comment("Set the cap for repairing items in the anvil (vanilla is 40)")
				.defineInRange("Anvil Repair Cap", this.anvilRepairCap, 1, Integer.MAX_VALUE);
		freeRenamingConfig = ITCommonConfig.builder
				.comment("Removes cost of renaming items in Anvil")
				.define("Remove rename cost", this.freeRenaming);
		unmendingConfig = ITCommonConfig.builder
				.comment("Replaces the default Mending enchantment. Mending sets the repair cost of an item to 'Unmending Cap' and will stop it from increasing. No longer repairs items with xp.")
				.define("Unmending", this.unmending);
		unmendingCapConfig = ITCommonConfig.builder
				.comment("Set the cap repair cost set by Unmending")
				.defineInRange("Unmending Cap", this.unmendingCap, 1, Integer.MAX_VALUE);
		ITCommonConfig.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.xpBottleBonus = this.xpBottleBonusConfig.get();
		this.anvilRepairCap = this.anvilRepairCapConfig.get();
		this.freeRenaming = this.freeRenamingConfig.get();
		this.unmending = this.unmendingConfig.get();
		this.unmendingCap = this.unmendingCapConfig.get();
	}

	public void onXpBottleHit(ThrownExperienceBottle xpBottle) {
		if (!this.isEnabled())
			return;

		if (this.xpBottleBonus == 0)
			return;

		if (xpBottle.level instanceof ServerLevel) {
			ExperienceOrb.award((ServerLevel)xpBottle.level, xpBottle.position(), this.xpBottleBonus);
		}
	}

	public boolean isFreeRenaming() {
		return this.isEnabled() && this.freeRenaming;
	}

	@SubscribeEvent
	public void onAnvilUse(AnvilRepairEvent event) {
		if (!this.isEnabled()
				|| !this.unmending)
			return;
		ItemStack output = event.getItemResult();
		if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, output) > 0 && output.getBaseRepairCost() > 15) {
			output.setRepairCost(this.unmendingCap);
		}
	}

	/*@SubscribeEvent
	public void onAnvilUpdate(AnvilUpdateEvent event) {
		if (!this.isEnabled()
				|| !this.unmending)
			return;

		ItemStack left = event.getLeft();
		ItemStack right = event.getRight();
		if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, left) > 0
				|| EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, right) > 0) {
			if (!event.getOutput().isEmpty())
				event.getOutput().setRepairCost(15);
		}
	}*/
}