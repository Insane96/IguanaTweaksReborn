package insane96mcp.iguanatweaksreborn.module.misc.feature;

import insane96mcp.iguanatweaksreborn.module.misc.level.ITExplosion;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.config.Blacklist;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Label(name = "Explosion Overhaul", description = "Various changes to explosions from knockback to shielding.")
public class ExplosionOverhaul extends Feature {

	private final ForgeConfigSpec.ConfigValue<Boolean> disableExplosionRandomnessConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> enablePoofParticlesConfig;
	private final ForgeConfigSpec.ConfigValue<Double> blockingDamageScalingConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> knockbackScalesWithSizeConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> explosionAtHalfEntityConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> affectJustSpawnedEntitiesConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> enableFlyingBlocksConfig;
	private final Blacklist.Config knockbackBlacklistConfig;
	private final Blacklist.Config entityBlacklistConfig;

	private static final List<String> knockbackBlacklistDefault = Arrays.asList("minecraft:ender_dragon", "minecraft:wither");

	public boolean disableExplosionRandomness = true;
	public boolean enablePoofParticles = false;
	public double blockingDamageScaling = 0.6d;
	public boolean knockbackScalesWithSize = true;
	public boolean explosionAtHalfEntity = true;
	public boolean affectJustSpawnedEntities = false;
	public boolean enableFlyingBlocks = false;
	public Blacklist knockbackBlacklist;
	public Blacklist entityBlacklist;

	public ExplosionOverhaul(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		disableExplosionRandomnessConfig = Config.builder
				.comment("Vanilla Explosions use a random number that changes the explosion power. With this enabled the ray strength will be as the explosion size.")
				.define("Disable Explosion Randomness", disableExplosionRandomness);
		enablePoofParticlesConfig = Config.builder
				.comment("Somewhere around 1.15 Mojang (for performance issues) removed the poof particles from Explosions. Keep them disabled if you have a low end PC.")
				.define("Enable Poof Particles", enablePoofParticles);
		blockingDamageScalingConfig = Config.builder
				.comment("How much damage will the player take when blocking an explosion with a shield. Putting 0 shields will block like Vanilla.")
				.defineInRange("Blocking Damage Scaling", blockingDamageScaling, 0.0d, 1.0d);
		knockbackScalesWithSizeConfig = Config.builder
				.comment("While enabled knockback is greatly increased by explosion size")
				.define("Knockback Scales With Size", knockbackScalesWithSize);
		explosionAtHalfEntityConfig = Config.builder
				.comment("Explosions will start from the middle of the entity instead of feets.")
				.define("Explosions at Half Entity", explosionAtHalfEntity);
		affectJustSpawnedEntitiesConfig = Config.builder
				.comment("Explosions affect even entities spawned by the explosions, like TnTs or chests content. BE AWARE that containers content will most likely get destroyed.")
				.define("Explosion Affect Just Spawed Entities", this.affectJustSpawnedEntities);
		enableFlyingBlocksConfig = Config.builder
				.comment("EXPERIMENTAL! This will make explosion blast blocks away. Blocks that can't land will drop the block as a TNT would have destroyed it.")
				.define("Enable Flying Blocks", enableFlyingBlocks);
		knockbackBlacklistConfig = new Blacklist.Config(Config.builder, "Knockback Blacklist", "A list of mobs (and optionally dimensions) that should take reduced knockback. Non-living entities are blacklisted by default.")
				.setDefaultList(knockbackBlacklistDefault)
				.setIsDefaultWhitelist(false)
				.build();
		entityBlacklistConfig = new Blacklist.Config(Config.builder, "Entity Blacklist", "A list of entities that should not use the mod's explosion.")
				.setDefaultList(Collections.emptyList())
				.setIsDefaultWhitelist(false)
				.build();
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
		this.affectJustSpawnedEntities = this.affectJustSpawnedEntitiesConfig.get();
		this.enableFlyingBlocks = this.enableFlyingBlocksConfig.get();
		this.knockbackBlacklist = this.knockbackBlacklistConfig.get();
		this.entityBlacklist = this.entityBlacklistConfig.get();
	}

	@SubscribeEvent
	public void explosionPoofParticles(ExplosionEvent.Detonate event) {
		if (!this.isEnabled())
			return;

		if (!this.enablePoofParticles)
			return;

		Explosion e = event.getExplosion();
		if (e.level instanceof ServerLevel level && !e.getToBlow().isEmpty() && e.radius > 1) {
			int particleCount = (int)(e.radius * 125);
			level.sendParticles(ParticleTypes.POOF, e.getPosition().x(), e.getPosition().y(), e.getPosition().z(), particleCount, e.radius / 4f, e.radius / 4f, e.radius / 4f, 0.33D);
		}
	}

	//Setting low priority so other mods can change explosions params before creating the ITExplosion
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onExplosionStart(ExplosionEvent.Start event) {
		if (!this.isEnabled())
			return;

		if (event.getExplosion().getExploder() != null && this.isBlacklisted(event.getExplosion().getExploder()))
			return;

		event.setCanceled(true);
		Explosion e = event.getExplosion();
		double y = e.getPosition().y;
		if (e.source != null && this.explosionAtHalfEntity)
			y += e.source.getBbHeight() / 2d;
		ITExplosion explosion = new ITExplosion(e.level, e.source, e.getDamageSource(), e.damageCalculator, e.getPosition().x, y, e.getPosition().z, e.radius, e.fire, e.blockInteraction);

		if (!event.getWorld().isClientSide) {
			ServerLevel world = (ServerLevel) event.getWorld();
			explosion.gatherAffectedBlocks(!this.disableExplosionRandomness);
			if (this.enableFlyingBlocks)
				explosion.fallingBlocks();
			explosion.destroyBlocks();
			explosion.processEntities(this.blockingDamageScaling, this.knockbackScalesWithSize);
			explosion.dropItems();
			explosion.processFire();
			if (explosion.blockInteraction == Explosion.BlockInteraction.NONE) {
				explosion.clearToBlow();
			}
			for (ServerPlayer serverPlayer : world.players()) {
				if (serverPlayer.distanceToSqr(explosion.getPosition().x, explosion.getPosition().y, explosion.getPosition().z) < 4096.0D) {
					serverPlayer.connection.send(new ClientboundExplodePacket(explosion.getPosition().x, explosion.getPosition().y, event.getExplosion().getPosition().z, explosion.radius, explosion.getToBlow(), explosion.getHitPlayers().get(serverPlayer)));
				}
			}
		}
		else {
			explosion.gatherAffectedBlocks(!this.disableExplosionRandomness);
			if (this.enableFlyingBlocks)
				explosion.fallingBlocks();
			explosion.destroyBlocks();
			explosion.playSound();
			explosion.spawnParticles();
			explosion.processFire();
			explosion.finalizeExplosion(true);
		}
	}

	public boolean shouldTakeReducedKnockback(Entity entity) {
		if (!(entity instanceof LivingEntity))
			return true;

		return this.knockbackBlacklist.isBlackWhiteListed(entity.getType());
	}

	public boolean isBlacklisted(Entity entity) {
		return this.entityBlacklist.isBlackWhiteListed(entity.getType());
	}
}