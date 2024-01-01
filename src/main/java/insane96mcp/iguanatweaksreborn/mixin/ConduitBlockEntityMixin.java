package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.misc.beaconconduit.BeaconConduit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ConduitBlockEntity.class)
public class ConduitBlockEntityMixin {

	@Inject(at = @At("HEAD"), method = "updateDestroyTarget", cancellable = true)
	private static void onUpdateDestroyTarget(Level level, BlockPos blockPos, BlockState state, List<BlockPos> blocks, ConduitBlockEntity conduit, CallbackInfo ci) {
		if (BeaconConduit.conduitUpdateDestroyEnemies(level, blockPos, blocks))
			ci.cancel();
	}
}