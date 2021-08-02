package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.modules.Modules;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.inventory.EquipmentSlotType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ProtectionEnchantment.class)
public abstract class ProtectionEnchantmentMixin extends Enchantment {

	@Shadow
	@Final
	public ProtectionEnchantment.Type protectionType;

	protected ProtectionEnchantmentMixin(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType[] slots) {
		super(rarityIn, typeIn, slots);
	}

	@Override
	public boolean isTreasureEnchantment() {
		if (this.protectionType == ProtectionEnchantment.Type.ALL && Modules.combat.stats.armorAdjustments)
			return true;
		return super.isTreasureEnchantment();
	}

	@Override
	public boolean canVillagerTrade() {
		if (this.protectionType == ProtectionEnchantment.Type.ALL && Modules.combat.stats.armorAdjustments)
			return false;
		return super.canVillagerTrade();
	}

	@Override
	public boolean canGenerateInLoot() {
		if (this.protectionType == ProtectionEnchantment.Type.ALL && Modules.combat.stats.armorAdjustments)
			return false;
		return super.canGenerateInLoot();
	}

	@Override
	public int getMaxLevel() {
		if (this.protectionType == ProtectionEnchantment.Type.ALL && Modules.combat.stats.nerfProtectionEnch)
			return 3;
		else
			return 4;
	}
}
