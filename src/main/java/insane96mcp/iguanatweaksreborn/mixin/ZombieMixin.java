package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.misc.feature.Villagers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Zombie.class)
public class ZombieMixin {
	@Inject(at = @At("TAIL"), method = "wasKilled")
	private void wasKilled(ServerLevel level, LivingEntity killedEntity, CallbackInfoReturnable<Boolean> callbackInfo) {
		Villagers.onZombieKillEntity((Zombie) (Object) this, level, killedEntity);
	}
}
