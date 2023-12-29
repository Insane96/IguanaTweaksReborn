package insane96mcp.iguanatweaksreborn.module.world.explosionoverhaul;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Explosion Overhaul", description = "Various changes to explosions from knockback to shielding.")
@LoadFeature(module = Modules.Ids.WORLD)
public class ExplosionOverhaul extends Feature {
	public static final TagKey<EntityType<?>> KNOCKBACK_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(IguanaTweaksReborn.MOD_ID, "explosion_knockback_blacklist"));
	public static final TagKey<EntityType<?>> ENTITY_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(IguanaTweaksReborn.MOD_ID, "explosion_entity_blacklist"));

	@Config
	@Label(name = "Disable Explosion Randomness", description = "Vanilla Explosions use a random number that changes the explosion power. With this enabled the ray strength will be as the explosion size.")
	public static Boolean disableExplosionRandomness = true;
	@Config
	@Label(name = "Enable Poof Particles", description = "Somewhere around 1.15 Mojang (for performance issues) removed the poof particles from Explosions. Keep them disabled if you have a low end PC.\n" +
			"These particles aren't shown when explosion power is <= 1")
	public static Boolean enablePoofParticles = true;
	@Config
	@Label(name = "Disable Emitter Particles", description = "Removes the particles spawned by the explosion.")
	public static Boolean disableEmitterParticles = true;
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
	@Label(name = "Explosion Damage calculation multiplier", description = "Number in the explosion damage calculation. Vanilla is 7. Higher = More damage")
	public static Double explosionDamageCalculationMultiplier = 5.5d;
	@Config
	@Label(name = "Limit explosion size", description = "Disabled if set to -1.")
	public static Integer limitExplosionSize = 12;

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
			if (e instanceof ITRExplosion itrExplosion && !itrExplosion.poofParticles)
				return;
			if (e.blockInteraction != Explosion.BlockInteraction.KEEP) {
				int particleCount = (int) (e.radius * 80);
				level.sendParticles(ParticleTypes.POOF, e.getPosition().x(), e.getPosition().y(), e.getPosition().z(), particleCount, e.radius / 4f, e.radius / 4f, e.radius / 4f, 0.25D);
				level.sendParticles(ParticleTypes.SMOKE, e.getPosition().x(), e.getPosition().y(), e.getPosition().z(), particleCount, e.radius / 4f, e.radius / 4f, e.radius / 4f, 0.25D);
			}
			else {
				level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, e.getPosition().x(), e.getPosition().y(), e.getPosition().z(), 1, 0.0D, 0.0D, 0.0D, 1f);
			}

			//Pre 1.15 particle spawn, but it was done for each block broken
			/*double d0 = e.getPosition().x + level.getRandom().nextFloat();
			double d1 = e.getPosition().y + level.getRandom().nextFloat();
			double d2 = e.getPosition().z + level.getRandom().nextFloat();
			double d3 = d0 - e.getPosition().x;
			double d4 = d1 - e.getPosition().y;
			double d5 = d2 - e.getPosition().z;
			double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
			d3 = d3 / d6;
			d4 = d4 / d6;
			d5 = d5 / d6;
			double d7 = 0.5D / (d6 / (double)e.radius + 0.1D);
			d7 = d7 * (double)(level.getRandom().nextFloat() * level.getRandom().nextFloat() + 0.3F);
			d3 = d3 * d7;
			d4 = d4 * d7;
			d5 = d5 * d7;
			level.sendParticles(ParticleTypes.POOF, (d0 + e.getPosition().x) / 2.0D, (d1 + e.getPosition().y) / 2.0D, (d2 + e.getPosition().z) / 2.0D, particleCount, d3, d4, d5, 0.22D);
			level.sendParticles(ParticleTypes.SMOKE, d0, d1, d2, particleCount, d3, d4, d5, 0.22D);*/
		}
	}

	//Setting the lowest priority so other mods can change explosions params before creating the ITExplosion
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void replaceExplosionWithSRExplosion(ExplosionEvent.Start event) {
		if (!this.isEnabled()
				|| !(event.getLevel() instanceof ServerLevel level)
				|| (event.getExplosion().getExploder() != null && event.getExplosion().getExploder().getType().is(ENTITY_BLACKLIST)))
			return;

		event.setCanceled(true);
		Explosion e = event.getExplosion();
		double y = e.getPosition().y;
		if (e.source != null && explosionAtHalfEntity)
			y += e.source.getBbHeight() / 2d;
		ITRExplosion.explode(level, e.source, e.getDamageSource(), e.damageCalculator, e.getPosition().x, y, e.getPosition().z, e.radius, e.fire, e.blockInteraction, true);
	}

	public static boolean shouldTakeReducedKnockback(Entity entity) {
		if (!(entity instanceof LivingEntity))
			return true;

		return entity.getType().is(KNOCKBACK_BLACKLIST);
	}
}