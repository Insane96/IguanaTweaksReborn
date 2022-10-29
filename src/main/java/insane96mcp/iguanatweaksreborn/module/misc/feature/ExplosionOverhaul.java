package insane96mcp.iguanatweaksreborn.module.misc.feature;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.misc.level.ITExplosion;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Blacklist;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collections;
import java.util.List;

@Label(name = "Explosion Overhaul", description = "Various changes to explosions from knockback to shielding.")
@LoadFeature(module = Modules.Ids.MISC)
public class ExplosionOverhaul extends Feature {
	private static final List<String> knockbackBlacklistDefault = List.of("minecraft:ender_dragon", "minecraft:wither");

	@Config
	@Label(name = "Disable Explosion Randomness", description = "Vanilla Explosions use a random number that changes the explosion power. With this enabled the ray strength will be as the explosion size.")
	public static Boolean disableExplosionRandomness = true;
	@Config
	@Label(name = "Enable Poof Particles", description = "Somewhere around 1.15 Mojang (for performance issues) removed the poof particles from Explosions. Keep them disabled if you have a low end PC.\n" +
			"These particles aren't shown when explosion power is <= 1")
	public static Boolean enablePoofParticles = false;
	@Config(min = 0d, max = 1d)
	@Label(name = "Blocking Damage Scaling", description = "How much damage will the player take when blocking an explosion with a shield. Putting 0 shields will block all the damage like Vanilla, while putting 1 shields will block no damage.")
	public static Double blockingDamageScaling = 1d;
	@Config
	@Label(name = "Knockback Scales With Size", description = "While enabled knockback is greatly increased by explosion size")
	public static Boolean knockbackScalesWithSize = true;
	@Config
	@Label(name = "Explosions at Half Entity", description = "Explosions will start from the middle of the entity instead of feets.")
	public static Boolean explosionAtHalfEntity = true;
	@Config
	@Label(name = "Explosion Affect Just Spawned Entities", description = "Explosions affect even entities spawned by the explosions, like TnTs or chests content. BE AWARE that containers content will get destroyed.")
	public static Boolean affectJustSpawnedEntities = false;
	@Config
	@Label(name = "Enable Flying Blocks", description = "EXPERIMENTAL! This will make explosion blast blocks away. Blocks that can't land will drop the block as a TNT would have destroyed it.")
	public static Boolean enableFlyingBlocks = false;
	@Config
	@Label(name = "Creeper collateral", description = "If true, creepers explosions will drop no blocks.")
	public static Boolean creeperCollateral = false;
	@Config
	@Label(name = "Knockback Blacklist", description = "A list of mobs (and optionally dimensions) that should take reduced knockback. Non-living entities are blacklisted by default.")
	public static Blacklist knockbackBlacklist = new Blacklist(List.of(
			new IdTagMatcher(IdTagMatcher.Type.ID, "minecraft:ender_dragon"),
			new IdTagMatcher(IdTagMatcher.Type.ID, "minecraft:wither")
	), false);
	@Config
	@Label(name = "Entity Blacklist", description = "A list of entities that should not use the mod's explosion.")
	public static Blacklist entityBlacklist = new Blacklist(Collections.emptyList(), false);

	public ExplosionOverhaul(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void explosionPoofParticles(ExplosionEvent.Detonate event) {
		if (!this.isEnabled()
				|| !enablePoofParticles)
			return;

		Explosion e = event.getExplosion();
		if (e.level instanceof ServerLevel level && !e.getToBlow().isEmpty() && e.radius >= 2) {
			int particleCount = (int)(e.radius * 125);
			level.sendParticles(ParticleTypes.POOF, e.getPosition().x(), e.getPosition().y(), e.getPosition().z(), particleCount, e.radius / 4f, e.radius / 4f, e.radius / 4f, 0.33D);
		}
	}

	//Setting the lowest priority so other mods can change explosions params before creating the ITExplosion
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onExplosionStart(ExplosionEvent.Start event) {
		if (!this.isEnabled()
				|| (event.getExplosion().getExploder() != null && isBlacklisted(event.getExplosion().getExploder())))
			return;

		event.setCanceled(true);
		Explosion e = event.getExplosion();
		double y = e.getPosition().y;
		if (e.source != null && explosionAtHalfEntity)
			y += e.source.getBbHeight() / 2d;
		ITExplosion explosion = new ITExplosion(e.level, e.source, e.getDamageSource(), e.damageCalculator, e.getPosition().x, y, e.getPosition().z, e.radius, e.fire, e.blockInteraction, creeperCollateral);

		if (!event.getLevel().isClientSide) {
			ServerLevel world = (ServerLevel) event.getLevel();
			explosion.gatherAffectedBlocks(!disableExplosionRandomness);
			if (enableFlyingBlocks)
				explosion.fallingBlocks();
			explosion.destroyBlocks();
			explosion.processEntities(blockingDamageScaling, knockbackScalesWithSize);
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
			explosion.gatherAffectedBlocks(!disableExplosionRandomness);
			if (enableFlyingBlocks)
				explosion.fallingBlocks();
			explosion.destroyBlocks();
			explosion.playSound();
			explosion.spawnParticles();
			explosion.processFire();
			explosion.finalizeExplosion(true);
		}
	}

	public static boolean shouldTakeReducedKnockback(Entity entity) {
		if (!(entity instanceof LivingEntity))
			return true;

		return knockbackBlacklist.isEntityBlackOrNotWhitelist(entity.getType());
	}

	public static boolean isBlacklisted(Entity entity) {
		return entityBlacklist.isEntityBlackOrNotWhitelist(entity.getType());
	}
}