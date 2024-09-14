package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.module.world.explosionoverhaul.ITRExplosion;
import insane96mcp.iguanatweaksreborn.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ExplodeParticles {
	private final double x;
	private final double y;
	private final double z;
	private final float radius;
	private final boolean hasBrokenBlocks;
	private final Explosion.BlockInteraction blockInteraction;

	public ExplodeParticles(double x, double y, double z, float radius, boolean hasBrokenBlocks, Explosion.BlockInteraction blockInteraction) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.radius = radius;
		this.hasBrokenBlocks = hasBrokenBlocks;
		this.blockInteraction = blockInteraction;
	}

	public static void encode(ExplodeParticles pkt, FriendlyByteBuf buf) {
		buf.writeDouble(pkt.x);
		buf.writeDouble(pkt.y);
		buf.writeDouble(pkt.z);
		buf.writeFloat(pkt.radius);
		buf.writeBoolean(pkt.hasBrokenBlocks);
		buf.writeEnum(pkt.blockInteraction);
	}

	public static ExplodeParticles decode(FriendlyByteBuf buf) {
		return new ExplodeParticles(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat(), buf.readBoolean(), buf.readEnum(Explosion.BlockInteraction.class));
	}

	public static void handle(final ExplodeParticles message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Level level = Minecraft.getInstance().level;
			if (level == null)
				return;
			if (message.hasBrokenBlocks && message.radius >= 2 && message.blockInteraction != Explosion.BlockInteraction.KEEP) {
				int particleCount = (int) (message.radius * 100);
				for (int i = 0; i < particleCount; i++) {
					double r = message.radius * 1;
					double v = r / 2f;
					double x = message.x + level.random.nextFloat() * r - v;
					double y = message.y + level.random.nextFloat() * r - v;
					double z = message.z + level.random.nextFloat() * r - v;
					Vec3 dir = new Vec3(x - message.x, y - message.y, z - message.z).normalize().scale(0.4f);
					level.addParticle(ParticleTypes.POOF, x, y, z, dir.x, dir.y, dir.z);
					r = message.radius * 1.5;
					v = r / 2f;
					x = message.x + level.random.nextFloat() * r - v;
					y = message.y + level.random.nextFloat() * r - v;
					z = message.z + level.random.nextFloat() * r - v;
					 dir = new Vec3(x - message.x, y - message.y, z - message.z).normalize().scale(0.7f);
					level.addParticle(ParticleTypes.SMOKE, x, y, z, dir.x, dir.y, dir.z);
					//level.sendParticles(ParticleTypes.POOF, message.x, message.y, message.z, particleCount, message.radius / 3f, message.radius / 3f, message.radius / 3f, 0.3D);
					//level.sendParticles(ParticleTypes.SMOKE, message.x, message.y, message.z, particleCount, message.radius / 3f, message.radius / 3f, message.radius / 3f, 0.3D);
				}
			}
			else if (message.radius < 2) {
				level.addParticle(ParticleTypes.EXPLOSION, message.x, message.y, message.z, 0.0D, 0.0D, 0.0D);
				//level.sendParticles(ParticleTypes.EXPLOSION, message.x, message.y, message.z, 1, 0.0D, 0.0D, 0.0D, 1f);
			}
			else {
				level.addParticle(ParticleTypes.EXPLOSION_EMITTER, message.x, message.y, message.z, 0.0D, 0.0D, 0.0D);
				//level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, message.x, message.y, message.z, 1, 0.0D, 0.0D, 0.0D, 1f);
			}
		});
		ctx.get().setPacketHandled(true);
	}

	public static void sync(ServerLevel level, ITRExplosion explosion) {
		Object msg = new ExplodeParticles(explosion.getPosition().x, explosion.getPosition().y, explosion.getPosition().z, explosion.radius, !explosion.getToBlow().isEmpty(), explosion.blockInteraction);
		level.players().forEach(player -> NetworkHandler.CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT));
	}
}
