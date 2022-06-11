package insane96mcp.iguanatweaksreborn.module.sleeprespawn.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Prevent Sleeping", description = "Makes sleeping impossible while begin able to set (or not) the spawn point")
public class PreventSleeping extends Feature {

	private final ForgeConfigSpec.ConfigValue<Boolean> disableBedSpawnConfig;

	public boolean disableBedSpawn = false;

	public PreventSleeping(Module module) {
		super(Config.builder, module, false);
		Config.builder.comment(this.getDescription()).push(this.getName());
		disableBedSpawnConfig = Config.builder
				.comment("If set to true the player spawn point will not change when the player cannot sleep. Has no effect if the player can sleep.")
				.define("Disable Bed Spawn", this.disableBedSpawn);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.disableBedSpawn = this.disableBedSpawnConfig.get();
	}

	@SubscribeEvent
	public void disableSleeping(PlayerSleepInBedEvent event) {
		if (!this.isEnabled())
			return;

		if (event.getResultStatus() != null)
			return;

		if (event.getPlayer().level.isClientSide)
			return;
		ServerPlayer player = (ServerPlayer) event.getPlayer();

		event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);

		if (this.disableBedSpawn) {
			player.displayClientMessage(new TranslatableComponent(Strings.Translatable.DECORATIVE_BEDS), true);
		}
		else {
			player.displayClientMessage(new TranslatableComponent(Strings.Translatable.ENJOY_THE_NIGHT), false);
			player.setRespawnPosition(player.level.dimension(), event.getPos(), player.getYRot(), false, false);
		}
	}
}