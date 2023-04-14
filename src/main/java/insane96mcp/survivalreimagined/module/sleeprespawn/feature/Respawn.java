package insane96mcp.survivalreimagined.module.sleeprespawn.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.sleeprespawn.block.RespawnObeliskBlock;
import insane96mcp.survivalreimagined.setup.SRBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;

@Label(name = "Respawn", description = "Changes to respawning")
@LoadFeature(module = Modules.Ids.SLEEP_RESPAWN)
public class Respawn extends Feature {

	public static final String LOOSE_RESPAWN_POINT_SET = SurvivalReimagined.MOD_ID + ".loose_bed_respawn_point_set";

	@Config(min = 0)
	@Label(name = "Loose World Spawn Range", description = "The range from world spawn where players will respawn.")
	public static Double looseWorldSpawnRange = 256d;

	@Config(min = 0)
	@Label(name = "Loose Bed Spawn Range", description = "The range from beds where players will respawn.")
	public static Double looseBedSpawnRange = 256d;

	public static final RegistryObject<RespawnObeliskBlock> RESPAWN_OBELISK = SRBlocks.BLOCKS.register("respawn_obelisk", () -> new RespawnObeliskBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(50.0F, 1200.0F).lightLevel(state -> RespawnObeliskBlock.lightLevel(state))));

	public Respawn(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
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

	private void tryRespawnObelisk(PlayerEvent.PlayerRespawnEvent event) {
		ServerPlayer player = (ServerPlayer) event.getEntity();
		BlockPos pos = player.getRespawnPosition();
		if (pos == null
				|| !event.getEntity().getLevel().getBlockState(pos).is(RESPAWN_OBELISK.get()))
			return;

		Level level = event.getEntity().getLevel();
		if (!level.getBlockState(pos).getValue(RespawnObeliskBlock.ENABLED) || !level.getBlockState(pos.north(4)).is(Blocks.IRON_BLOCK) || !level.getBlockState(pos.south(4)).is(Blocks.IRON_BLOCK) || !level.getBlockState(pos.east(4)).is(Blocks.IRON_BLOCK) || !level.getBlockState(pos.west(4)).is(Blocks.IRON_BLOCK))
			return;

		level.destroyBlock(pos.north(4), false);
		level.destroyBlock(pos.south(4), false);
		level.destroyBlock(pos.east(4), false);
		level.destroyBlock(pos.west(4), false);
	}

	@Nullable
	private BlockPos looseWorldSpawn(PlayerEvent.PlayerRespawnEvent event) {
		if (looseWorldSpawnRange == 0d)
			return null;
		ServerPlayer player = (ServerPlayer) event.getEntity();
		BlockPos pos = player.getRespawnPosition();
		if (pos != null)
			return null;

		BlockPos respawn = BlockPos.randomInCube(player.getLevel().getRandom(), 1, player.getLevel().getSharedSpawnPos(), looseWorldSpawnRange.intValue()).iterator().next();
		int y = player.getLevel().getMaxBuildHeight();
		while (y > player.getLevel().getMinBuildHeight()) {
			respawn = new BlockPos(respawn.getX(), y - 1, respawn.getZ());
			BlockState state = player.getLevel().getBlockState(respawn);
			if (state.getMaterial().blocksMotion() || !state.getFluidState().isEmpty()) {
				respawn = new BlockPos(respawn.getX(), y, respawn.getZ());
				break;
			}
			y--;
		}
		return respawn;
	}

	@Nullable
	private BlockPos looseBedSpawn(PlayerEvent.PlayerRespawnEvent event) {
		if (looseBedSpawnRange == 0d)
			return null;
		ServerPlayer player = (ServerPlayer) event.getEntity();
		BlockPos pos = player.getRespawnPosition();
		if (pos == null
				|| !event.getEntity().getLevel().getBlockState(pos).is(BlockTags.BEDS))
			return null;

		BlockPos respawn = BlockPos.randomInCube(player.getLevel().getRandom(), 1, pos, looseWorldSpawnRange.intValue()).iterator().next();
		int y = player.getLevel().getMaxBuildHeight();
		while (y > player.getLevel().getMinBuildHeight()) {
			respawn = new BlockPos(respawn.getX(), y - 1, respawn.getZ());
			BlockState state = player.getLevel().getBlockState(respawn);
			if (state.getMaterial().blocksMotion() || !state.getFluidState().isEmpty()) {
				respawn = new BlockPos(respawn.getX(), y, respawn.getZ());
				break;
			}
			y--;
		}
		return respawn;
	}

	@SubscribeEvent
	public void onSetSpawn(PlayerSetSpawnEvent event) {
		if (!this.isEnabled()
				|| event.isForced()
				|| looseBedSpawnRange == 0d
				|| event.getNewSpawn() == null
				|| !event.getEntity().getLevel().getBlockState(event.getNewSpawn()).is(BlockTags.BEDS))
			return;

		ServerPlayer player = (ServerPlayer) event.getEntity();
		player.displayClientMessage(Component.translatable(LOOSE_RESPAWN_POINT_SET), false);
	}
}