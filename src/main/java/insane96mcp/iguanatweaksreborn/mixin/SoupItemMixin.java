package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.modules.Modules;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SoupItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SoupItem.class)
public class SoupItemMixin extends Item {

	public SoupItemMixin(Properties properties) {
		super(properties);
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		if (Modules.hungerHealth.fasterConsuming.fasterSoupConsuming)
			return 20;
		return super.getUseDuration(stack);
	}
}
