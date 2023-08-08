package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.mining.Gold;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DiggerItem.class)
public class DiggerItemMixin {
	@Redirect(method = "isCorrectToolForDrops(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)Z",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/DiggerItem;getTier()Lnet/minecraft/world/item/Tier;"))
	private Tier getTier(DiggerItem item) {
		return Gold.getEffectiveTier(item.getTier());
	}
}
