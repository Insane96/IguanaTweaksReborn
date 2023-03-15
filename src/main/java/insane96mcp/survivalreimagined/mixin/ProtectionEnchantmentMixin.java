package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.combat.feature.Stats;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ProtectionEnchantment.class)
public class ProtectionEnchantmentMixin extends Enchantment {

	@Shadow
	@Final
	public ProtectionEnchantment.Type type;

	protected ProtectionEnchantmentMixin(Rarity rarityIn, EnchantmentCategory category, EquipmentSlot[] slots) {
		super(rarityIn, category, slots);
	}

	@Override
	public boolean isTreasureOnly() {
		if (this.type == ProtectionEnchantment.Type.ALL && Stats.protectionNerf == Stats.ProtectionNerf.DISABLE)
			return true;
		return super.isTreasureOnly();
	}

	@Override
	public boolean isTradeable() {
		if (this.type == ProtectionEnchantment.Type.ALL && Stats.protectionNerf == Stats.ProtectionNerf.DISABLE)
			return false;
		return super.isTradeable();
	}

	@Override
	public boolean isDiscoverable() {
		if (this.type == ProtectionEnchantment.Type.ALL && Stats.protectionNerf == Stats.ProtectionNerf.DISABLE)
			return false;
		return super.isDiscoverable();
	}

	@Override
	public int getMaxLevel() {
		if (this.type == ProtectionEnchantment.Type.ALL && Stats.protectionNerf == Stats.ProtectionNerf.NERF)
			return 3;

		return 4;
	}
}