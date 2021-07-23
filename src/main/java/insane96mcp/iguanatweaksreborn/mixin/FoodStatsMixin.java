package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.modules.Modules;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.FoodStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(FoodStats.class)
public class FoodStatsMixin {
	/**
	 * @author Insane96MCP
	 */
	@Overwrite
	public void tick(PlayerEntity player) {
		FoodStats $this = (FoodStats) (Object) this;
		Modules.hungerHealth.healthRegen.tickFoodStats($this, player);
	}
}
