package insane96mcp.survivalreimagined.module.misc.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.level.SRExplosion;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Explosion Overhaul", description = "Various changes to explosions from knockback to shielding.")
@LoadFeature(module = Modules.Ids.MISC)
public class ExplosionOverhaul extends Feature {
	public static final ResourceLocation KNOCKBACK_BLACKLIST = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "explosion_knockback_blacklist");
	public static final ResourceLocation ENTITY_BLACKLIST = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "explosion_entity_blacklist");

	@Config
	@Label(name = "Disable Explosion Randomness", description = "Vanilla Explosions use a random number that changes the explosion power. With this enabled the ray strength will be as the explosion size.")
	public static Boolean disableExplosionRandomness = true;
	@Config
	@Label(name = "Enable Poof Particles", description = "Somewhere around 1.15 Mojang (for performance issues) removed the poof particles from Explosions. Keep them disabled if you have a low end PC.\n" +
			"These particles aren't shown when explosion power is <= 1")
	public static Boolean enablePoofParticles = true;
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
	public static Boolean creeperCollateral = true;

	public ExplosionOverhaul(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void explosionPoofParticles(ExplosionEvent.Detonate event) {
		if (!this.isEnabled()
				|| !enablePoofParticles)
			return;

		Explosion e = event.getExplosion();
		if (e.level instanceof ServerLevel level && !e.getToBlow().isEmpty() && e.blockInteraction != Explosion.BlockInteraction.KEEP && e.radius >= 2) {
			if (e instanceof SRExplosion srExplosion && !srExplosion.poofParticles)
				return;
			int particleCount = (int)(e.radius * 125);
			level.sendParticles(ParticleTypes.POOF, e.getPosition().x(), e.getPosition().y(), e.getPosition().z(), particleCount, e.radius / 4f, e.radius / 4f, e.radius / 4f, 0.33D);
		}
	}

	//Setting the lowest priority so other mods can change explosions params before creating the ITExplosion
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void replaceExplosionWithSRExplosion(ExplosionEvent.Start event) {
		if (!this.isEnabled()
				|| !(event.getLevel() instanceof ServerLevel level)
				|| (event.getExplosion().getExploder() != null && isBlacklisted(event.getExplosion().getExploder())))
			return;

		event.setCanceled(true);
		Explosion e = event.getExplosion();
		double y = e.getPosition().y;
		if (e.source != null && explosionAtHalfEntity)
			y += e.source.getBbHeight() / 2d;
		SRExplosion.explode(level, e.source, e.getDamageSource(), e.damageCalculator, e.getPosition().x, y, e.getPosition().z, e.radius, e.fire, e.blockInteraction, true);
	}

	public static boolean shouldTakeReducedKnockback(Entity entity) {
		if (!(entity instanceof LivingEntity))
			return true;

		return Utils.isEntityInTag(entity, KNOCKBACK_BLACKLIST);
	}

	public static boolean isBlacklisted(Entity entity) {
		return Utils.isEntityInTag(entity, ENTITY_BLACKLIST);
	}
}