package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.modules.Modules;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SuspiciousStewItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SuspiciousStewItem.class)
public class SuspiciousStewItemMixin extends Item {

	public SuspiciousStewItemMixin(Properties properties) {
		super(properties);
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		if (Modules.hungerHealth.fasterConsuming.fasterSoupConsuming)
			return 20;
		return super.getUseDuration(stack);
	}
}
