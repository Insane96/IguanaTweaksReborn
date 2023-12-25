package insane96mcp.iguanatweaksreborn.module.sleeprespawn.respawn;

import insane96mcp.insanelib.base.JsonFeature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.base.config.MinMax;
import insane96mcp.insanelib.data.IdTagValue;
import insane96mcp.insanelib.util.LogHelper;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.base.SimpleBlockWithItem;
import insane96mcp.iguanatweaksreborn.module.Modules;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Label(name = "Respawn", description = "Changes to respawning")
@LoadFeature(module = Modules.Ids.SLEEP_RESPAWN)
public class Respawn extends JsonFeature {

	public static final String LOOSE_RESPAWN_POINT_SET = IguanaTweaksReborn.MOD_ID + ".loose_bed_respawn_point_set";
	public static final String FAIL_RESPAWN_OBELISK_LANG = IguanaTweaksReborn.MOD_ID + ".fail_respawn_obelisk";

	@Config(min = 0)
	@Label(name = "Loose World Spawn Range", description = "The range from world spawn where players will respawn.")
	public static MinMax looseWorldSpawnRange = new MinMax(192d, 256d);

	@Config(min = 0)
	@Label(name = "Loose Bed Spawn Range", description = "The range from beds where players will respawn.")
	public static MinMax looseBedSpawnRange = new MinMax(128d, 192d);

	public static final SimpleBlockWithItem RESPAWN_OBELISK = SimpleBlockWithItem.register("respawn_obelisk", () -> new RespawnObeliskBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(50.0F, 1200.0F).lightLevel(RespawnObeliskBlock::lightLevel)));

	public static final ArrayList<IdTagValue> RESPAWN_OBELISK_CATALYSTS_DEFAULT = new ArrayList<>(List.of(
			IdTagValue.newId("minecraft:iron_block", 0.75d),
			IdTagValue.newId("minecraft:gold_block", 0.3d),
			IdTagValue.newId("iguanatweaksreborn:durium_block", 0.075d),
			IdTagValue.newId("minecraft:diamond_block", 0.05d),
			IdTagValue.newId("iguanatweaksreborn:keego_block", 0.05d),
			IdTagValue.newId("minecraft:emerald_block", 0.35d),
			IdTagValue.newId("minecraft:netherite_block", 0d)
	));

	public static final ArrayList<IdTagValue> respawnObeliskCatalysts = new ArrayList<>();

	public Respawn(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		JSON_CONFIGS.add(new JsonConfig<>("respawn_obelisk_catalysts.json", respawnObeliskCatalysts, RESPAWN_OBELISK_CATALYSTS_DEFAULT, IdTagValue.LIST_TYPE));
	}

	@Override
	public String getModConfigFolder() {
		return IguanaTweaksReborn.CONFIG_FOLDER;
	}

	@Override
	public void loadJsonConfigs() {
		if (!this.isEnabled())
			return;
		super.loadJsonConfigs();
	}

	@SubscribeEvent
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

		tryRespawnObelisk(event);
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

	private void tryRespawnObelisk(PlayerEvent.PlayerRespawnEvent event) {
		ServerPlayer player = (ServerPlayer) event.getEntity();
		BlockPos pos = player.getRespawnPosition();
		if (pos == null
				|| !player.level().getBlockState(pos).is(RESPAWN_OBELISK.block().get()))
			return;

		if (!player.level().getBlockState(pos).getValue(RespawnObeliskBlock.ENABLED)) {
			player.sendSystemMessage(Component.translatable(FAIL_RESPAWN_OBELISK_LANG));
			RespawnObeliskBlock.trySetOldSpawn(player);
			return;
		}
		RespawnObeliskBlock.onObeliskRespawn(player, player.level(), pos);
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

	@SubscribeEvent
	public void onSetSpawnPreventObeliskOverwrite(PlayerSetSpawnEvent event) {
		if (!this.isEnabled()
				|| event.isForced()
				|| !(event.getEntity() instanceof ServerPlayer player))
			return;

		if (player.getRespawnPosition() != null && player.level().getBlockState(player.getRespawnPosition()).is(RESPAWN_OBELISK.block().get()) && player.level().getBlockState(player.getRespawnPosition()).getValue(RespawnObeliskBlock.ENABLED)
			&& event.getNewSpawn() != null && !player.level().getBlockState(event.getNewSpawn()).is(RESPAWN_OBELISK.block().get())) {
			event.setCanceled(true);
		}
	}
}