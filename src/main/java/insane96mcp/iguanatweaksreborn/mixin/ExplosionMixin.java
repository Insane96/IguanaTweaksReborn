package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.misc.explosionoverhaul.ExplosionOverhaul;
import insane96mcp.insanelib.base.Feature;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {

    @Shadow @Final private Vec3 position;

    @Redirect(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V", ordinal = 0))
    private void onExplosionEmitterParticle(Level level, ParticleOptions pParticleData, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
		if (!Feature.isEnabled(ExplosionOverhaul.class) || !ExplosionOverhaul.disableEmitterParticles) {
            level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.position.x, this.position.y, this.position.z, 1.0D, 0.0D, 0.0D);
		}
	}
}
