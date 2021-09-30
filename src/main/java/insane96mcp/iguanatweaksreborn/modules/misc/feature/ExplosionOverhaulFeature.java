package insane96mcp.iguanatweaksreborn.modules.misc.feature;

import insane96mcp.iguanatweaksreborn.modules.misc.world.ITExplosion;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.Explosion;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Explosion Overhaul", description = "Various changes to explosions from knockback to shielding.")
public class ExplosionOverhaulFeature extends Feature {

	private final ForgeConfigSpec.ConfigValue<Boolean> disableExplosionRandomnessConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> enablePoofParticlesConfig;
	private final ForgeConfigSpec.ConfigValue<Double> blockingDamageScalingConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> knockbackScalesWithSizeConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> explosionAtHalfEntityConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> enableFlyingBlocksConfig;

	public boolean disableExplosionRandomness = true;
	public boolean enablePoofParticles = false;
	public double blockingDamageScaling = 0.5d;
	public boolean knockbackScalesWithSize = true;
	public boolean explosionAtHalfEntity = true;
	public boolean enableFlyingBlocks = false;

	public ExplosionOverhaulFeature(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		disableExplosionRandomnessConfig = Config.builder
				.comment("Vanilla Explosions use a random number that changes the explosion power. With this enabled the ray strength will be as the explosion size.")
				.define("Disable Explosion Randomness", disableExplosionRandomness);
		enablePoofParticlesConfig = Config.builder
				.comment("Somewhere around 1.15 Mojang (for performance issues) removed the poof particles from Explosions. Disable them if you have a low end PC.")
				.define("Enable Poof Particles", enablePoofParticles);
		blockingDamageScalingConfig = Config.builder
				.comment("How much damage and knockback will the player take when blocking an explosion with a shield. Putting 0 shields will block like Vanilla.")
				.defineInRange("Blocking Damage Scaling", blockingDamageScaling, 0.0d, 1.0d);
        knockbackScalesWithSizeConfig = Config.builder
                .comment("While enabled knockback is greatly increased by explosion size")
                .define("Knockback Scales With Size", knockbackScalesWithSize);
		explosionAtHalfEntityConfig = Config.builder
				.comment("Explosions will start from the middle of the entity instead of feets.")
				.define("Explosions at Half Entity", explosionAtHalfEntity);
		enableFlyingBlocksConfig = Config.builder
				.comment("EXPERIMENTAL! This will make explosion blast blocks away. Blocks that can't land will drop the block as a TNT would have destroyed it.")
				.define("Enable Flying Blocks", enableFlyingBlocks);
        Config.builder.pop();
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        this.disableExplosionRandomness = this.disableExplosionRandomnessConfig.get();
        this.enablePoofParticles = this.enablePoofParticlesConfig.get();
        this.blockingDamageScaling = this.blockingDamageScalingConfig.get();
        this.knockbackScalesWithSize = this.knockbackScalesWithSizeConfig.get();
        this.explosionAtHalfEntity = this.explosionAtHalfEntityConfig.get();
        this.enableFlyingBlocks = this.enableFlyingBlocksConfig.get();
    }

    @SubscribeEvent
    public void explosionPoofParticles(ExplosionEvent.Detonate event) {
        if (!this.isEnabled())
            return;

        if (!this.enablePoofParticles)
            return;

        Explosion e = event.getExplosion();
        if (e.world instanceof ServerWorld && !e.getAffectedBlockPositions().isEmpty()) {
            ServerWorld world = (ServerWorld) e.world;
            int particleCount = (int)(e.size * 200);
            world.spawnParticle(ParticleTypes.POOF, e.getPosition().x, e.getPosition().y, e.getPosition().z, particleCount, e.size / 4f, e.size / 4f, e.size / 4f, 0.33D);
        }
    }

    //Setting low priority so other mods can change explosions params before creating the ITExplosion
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onExplosionStart(ExplosionEvent.Start event) {
        if (!this.isEnabled())
            return;

        event.setCanceled(true);
        Explosion e = event.getExplosion();
        double y = e.getPosition().y;
        if (e.exploder != null && this.explosionAtHalfEntity)
        	y += e.exploder.getHeight() / 2d;
        ITExplosion explosion = new ITExplosion(e.world, e.exploder, e.getDamageSource(), e.context, e.getPosition().x, y, e.getPosition().z, e.size, e.causesFire, e.mode);

        if (!event.getWorld().isRemote) {
            ServerWorld world = (ServerWorld) event.getWorld();
            explosion.gatherAffectedBlocks(!this.disableExplosionRandomness);
            if (this.enableFlyingBlocks)
                explosion.fallingBlocks();
            explosion.destroyBlocks();
            explosion.processEntities(this.blockingDamageScaling, this.knockbackScalesWithSize);
            explosion.destroyLateBlocks();
            explosion.dropItems();
            explosion.processFire();
            if (explosion.mode == Explosion.Mode.NONE) {
                explosion.clearAffectedBlockPositions();
            }
            for (ServerPlayerEntity serverplayerentity : world.getPlayers()) {
                if (serverplayerentity.getDistanceSq(explosion.getPosition().x, explosion.getPosition().y, explosion.getPosition().z) < 4096.0D) {
                    serverplayerentity.connection.sendPacket(new SExplosionPacket(explosion.getPosition().x, explosion.getPosition().y, event.getExplosion().getPosition().z, explosion.size, explosion.getAffectedBlockPositions(), explosion.getPlayerKnockbackMap().get(serverplayerentity)));
                }
            }
        }
        else {
            explosion.gatherAffectedBlocks(!this.disableExplosionRandomness);
            if (this.enableFlyingBlocks)
                explosion.fallingBlocks();
            explosion.destroyBlocks();
            //explosion.processEntities(this.blockingDamageScaling, this.knockbackScalesWithSize);
			explosion.destroyLateBlocks();
            explosion.playSound();
            explosion.spawnParticles();
            explosion.processFire();
            explosion.doExplosionB(true);
        }
    }
}
