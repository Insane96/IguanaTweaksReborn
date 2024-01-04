package insane96mcp.iguanatweaksreborn.module.sleeprespawn.respawn;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.base.config.MinMax;
import insane96mcp.insanelib.util.LogHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;

@Label(name = "Respawn", description = "Changes to respawning")
@LoadFeature(module = Modules.Ids.SLEEP_RESPAWN)
public class Respawn extends Feature {

	public static final String LOOSE_RESPAWN_POINT_SET = IguanaTweaksReborn.MOD_ID + ".loose_bed_respawn_point_set";

	@Config(min = 0)
	@Label(name = "Loose World Spawn Range", description = "The range from world spawn where players will respawn.")
	public static MinMax looseWorldSpawnRange = new MinMax(128d, 192d);

	@Config(min = 0)
	@Label(name = "Loose Bed Spawn Range", description = "The range from beds where players will respawn.")
	public static MinMax looseBedSpawnRange = new MinMax(64d, 128d);

	public Respawn(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	//Run before Survival Reimagined Respawn Obelisk
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
		if (!this.isEnabled()
				|| event.isEndConquered())
			return;

		BlockPos respawn = looseWorldSpawn(event);
		if (respawn == null) {
			respawn = looseBedSpawn(event);
		}

		if (respawn != null)
			event.getEntity().teleportToWithTicket(respawn.getX() + 0.5d, respawn.getY() + 0.5d, respawn.getZ() + 0.5d);

		//tryRespawnObelisk(event);
	}

	@Nullable
	private BlockPos looseWorldSpawn(PlayerEvent.PlayerRespawnEvent event) {
		if (looseWorldSpawnRange.min == 0d
				|| event.getEntity().isSpectator())
			return null;
		ServerPlayer player = (ServerPlayer) event.getEntity();
		BlockPos pos = player.getRespawnPosition();
		if (pos != null)
			return null;

		return getSpawnPositionInRange(player.level().getSharedSpawnPos(), looseWorldSpawnRange, player.level(), player.level().random);
	}

	@Nullable
	private BlockPos looseBedSpawn(PlayerEvent.PlayerRespawnEvent event) {
		if (looseBedSpawnRange.min == 0d
				|| event.getEntity().isSpectator())
			return null;
		ServerPlayer player = (ServerPlayer) event.getEntity();
		BlockPos pos = player.getRespawnPosition();
		if (pos == null
				|| !event.getEntity().level().getBlockState(pos).is(BlockTags.BEDS))
			return null;

		return getSpawnPositionInRange(pos, looseBedSpawnRange, player.level(), player.level().random);
	}

	@Nullable
	private BlockPos getSpawnPositionInRange(BlockPos center, MinMax minMax, Level level, RandomSource random) {
		double minSqr = minMax.min * minMax.min;
		double maxSqr = minMax.max * minMax.max;
		int x, y, z;
		BlockState stateBelow;
		BlockPos.MutableBlockPos respawn = new BlockPos.MutableBlockPos();
		boolean foundValidY = false;
		int triesLeft = 1000;
		do {
			do {
				x = random.nextInt((int) -minMax.max, (int) minMax.max);
				z = random.nextInt((int) -minMax.max, (int) minMax.max);
			} while (x * x + z * z > maxSqr || x * x + z * z < minSqr);
			y = level.getMaxBuildHeight();
			do {
				respawn.set(x + center.getX(), y, z + center.getZ());
				stateBelow = level.getBlockState(respawn.below());
				//Discard if there's lava below
				if (stateBelow.getFluidState().is(FluidTags.LAVA))
					break;
				if (stateBelow.blocksMotion() || !stateBelow.getFluidState().isEmpty()) {
					foundValidY = true;
					break;
				}
				y--;
			} while (y > level.getMinBuildHeight());
			triesLeft--;
		} while (!foundValidY && triesLeft > 0);
		if (triesLeft <= 0) {
			LogHelper.warn("Failed to find a respawn point within %s", center);
			return null;
		}

		return respawn.immutable();
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onSetSpawnLooseMessage(PlayerSetSpawnEvent event) {
		if (!this.isEnabled()
				|| event.isForced()
				|| looseBedSpawnRange.min == 0d
				|| event.getNewSpawn() == null
				|| !event.getEntity().level().getBlockState(event.getNewSpawn()).is(BlockTags.BEDS))
			return;

		ServerPlayer player = (ServerPlayer) event.getEntity();
		if (event.getNewSpawn().equals(player.getRespawnPosition()))
			return;
		player.displayClientMessage(Component.translatable(LOOSE_RESPAWN_POINT_SET), false);
	}
}