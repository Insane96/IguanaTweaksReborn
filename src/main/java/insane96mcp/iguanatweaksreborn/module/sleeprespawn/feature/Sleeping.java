package insane96mcp.iguanatweaksreborn.module.sleeprespawn.feature;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Sleeping", description = "Makes sleeping impossible or possible during day")
public class Sleeping extends Feature {

	private final ForgeConfigSpec.ConfigValue<Boolean> disableSleepingConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> disableBedSpawnConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> allowDaySleepConfig;

	public boolean disableSleeping = false;
	public boolean disableBedSpawn = false;
	public boolean allowDaySleep = true;

	public Sleeping(Module module) {
		super(ITCommonConfig.builder, module);
		this.pushConfig(ITCommonConfig.builder);
		disableSleepingConfig = ITCommonConfig.builder
				.comment("If set to true the player will not be able to sleep.")
				.define("Disable Sleeping", this.disableSleeping);
		disableBedSpawnConfig = ITCommonConfig.builder
				.comment("If set to true the player spawn point will not change when the player cannot sleep. Has no effect if the player can sleep.")
				.define("Disable Bed Spawn", this.disableBedSpawn);
		allowDaySleepConfig = ITCommonConfig.builder
				.comment("If set to true the player will be able to sleep during day time. On wake up it will be night time")
				.define("Allow Sleeping During Day", this.allowDaySleep);
		ITCommonConfig.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.disableSleeping = this.disableSleepingConfig.get();
		this.disableBedSpawn = this.disableBedSpawnConfig.get();
		this.allowDaySleep = this.allowDaySleepConfig.get();
	}

	@SubscribeEvent
	public void disableSleeping(PlayerSleepInBedEvent event) {
		if (!this.isEnabled()
				|| !this.disableSleeping
				|| event.getResultStatus() != null
				|| event.getPlayer().level.isClientSide)
			return;

		ServerPlayer player = (ServerPlayer) event.getPlayer();

		event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);

		if (this.disableBedSpawn) {
			player.displayClientMessage(new TranslatableComponent(Strings.Translatable.DECORATIVE_BEDS), true);
		}
		else {
			player.setRespawnPosition(player.level.dimension(), event.getPos(), player.getYRot(), false, true);
			player.displayClientMessage(new TranslatableComponent(Strings.Translatable.ENJOY_THE_NIGHT), false);
		}
	}

	@SubscribeEvent
	public void notTiredToSleep(PlayerSleepInBedEvent event) {
		if (!this.isEnabled()
				|| !this.allowDaySleep
				|| !Modules.sleepRespawn.tiredness.canSleepDuringDay(event.getPlayer()))
			return;

		ServerPlayer player = (ServerPlayer) event.getPlayer();

		event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
		player.startSleeping(event.getPos());
		((ServerLevel)player.level).updateSleepingPlayerList();
	}

	@SubscribeEvent
	public void sleepDuringDay(SleepingTimeCheckEvent event) {
		if (!this.isEnabled()
				|| !this.allowDaySleep
				|| (Modules.sleepRespawn.tiredness.isEnabled() && !Modules.sleepRespawn.tiredness.canSleepDuringDay(event.getPlayer())))
			return;
		event.setResult(Event.Result.ALLOW);
	}

	@SubscribeEvent
	public void onWakeUp(SleepFinishedTimeEvent event) {
		if (!this.isEnabled()
				|| !this.allowDaySleep
				/*|| !Modules.sleepRespawn.tiredness.canSleepDuringDay(event.get())*/)
			return;
		int toSub = 11458;
		if (event.getWorld().getLevelData().isRaining())
			toSub = 11990;
		event.setTimeAddition(event.getNewTime() - toSub);
	}
}