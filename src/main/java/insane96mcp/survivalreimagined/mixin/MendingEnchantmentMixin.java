package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.experience.feature.Enchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.MendingEnchantment;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MendingEnchantment.class)
public class MendingEnchantmentMixin extends Enchantment {
	protected MendingEnchantmentMixin(Rarity rarityIn, EnchantmentCategory category, EquipmentSlot[] slots) {
		super(rarityIn, category, slots);
	}

	@Override
	public boolean isTradeable() {
		if (Enchantments.isMendingOverhaulEnabled())
			return false;
		return super.isTradeable();
	}

	@Override
	public boolean isDiscoverable() {
		if (Enchantments.isMendingOverhaulEnabled())
			return false;
		return super.isDiscoverable();
	}
}