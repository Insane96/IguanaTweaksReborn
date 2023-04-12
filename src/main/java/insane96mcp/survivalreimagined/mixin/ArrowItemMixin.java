package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArrowItem.class)
public class ArrowItemMixin {
	@Inject(at = @At("RETURN"), method = "isInfinite", cancellable = true, remap = false)
	private void onIsInfinite(ItemStack stack, ItemStack bow, Player player, CallbackInfoReturnable<Boolean> cir) {
		//Change the return value only if the original method had returned true
		if (!cir.getReturnValue())
			return;
		int enchant = EnchantmentHelper.getTagEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.INFINITY_ARROWS, bow);
		RandomSource random = Utils.syncedRandom(player);
		if (random.nextInt(enchant + 1) == 0) cir.setReturnValue(false);
	}
}