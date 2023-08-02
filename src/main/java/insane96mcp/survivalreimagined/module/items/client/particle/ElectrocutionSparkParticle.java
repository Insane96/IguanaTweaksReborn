package insane96mcp.survivalreimagined.module.items.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class ElectrocutionSparkParticle extends TextureSheetParticle {

    private static final int LIFETIME = 10;

    private final SpriteSet sprites;

    protected ElectrocutionSparkParticle(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet spriteSet) {
        super(pLevel, pX, pY, pZ, 0, 0, 0);
        this.lifetime = LIFETIME;
        this.friction = 0.96F;
        this.sprites = spriteSet;
        this.quadSize *= 0.75F;
        this.hasPhysics = false;
        this.setColor(1.0F, 0.9F, 1.0F);
        this.setParticleSpeed(0d, 0d, 0d);
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void fade() {
        this.alpha = 1f - (this.age / (float)LIFETIME);
    }

    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
        this.fade();
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new ElectrocutionSparkParticle(pLevel, pX, pY, pZ, this.sprite);
        }
    }
}
