package insane96mcp.survivalreimagined.module.combat;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.data.criterion.UnfairOneShotTrigger;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.setup.SRSoundEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Unfair one-shots", description = "Players be left with half a heart when too much damage that would kill them is dealt (only works for damage taken from mobs)")
@LoadFeature(module = Modules.Ids.COMBAT)
public class UnfairOneShot extends Feature {
	public UnfairOneShot(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void onPlayerAttackEvent(LivingDamageEvent event) {
		if (!this.isEnabled()
				|| !(event.getSource().getEntity() instanceof LivingEntity entity)
				|| !(event.getEntity() instanceof ServerPlayer player))
			return;

		if (player.getHealth() >= 15 && player.getHealth() - event.getAmount() <= 0) {
			event.setAmount(player.getHealth() - 1f);
			player.level().playSound(null, player.blockPosition(), SRSoundEvents.INJURED.get(), SoundSource.PLAYERS, 1.5f, 0.8f);
			UnfairOneShotTrigger.TRIGGER.trigger(player);
		}
	}
}