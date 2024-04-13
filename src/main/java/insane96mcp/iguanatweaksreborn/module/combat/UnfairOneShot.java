package insane96mcp.iguanatweaksreborn.module.combat;

import insane96mcp.iguanatweaksreborn.data.criterion.ITRTriggers;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.setup.ITRRegistries;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Unfair one-shots", description = "Players be left with half a heart when too much damage that would kill them is dealt (only works for damage taken from mobs)")
@LoadFeature(module = Modules.Ids.COMBAT)
public class UnfairOneShot extends Feature {

	@Config
	@Label(name = "Resistance", description = "If true, a resistance effect is given to players on activation")
	public static Boolean resistance = true;

	public UnfairOneShot(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void onPlayerAttackEvent(LivingDamageEvent event) {
		if (!this.isEnabled()
				|| !(event.getSource().getEntity() instanceof LivingEntity)
				|| !(event.getEntity() instanceof ServerPlayer player))
			return;

		if (player.getHealth() >= 15 && player.getHealth() - event.getAmount() <= 0) {
			event.setAmount(player.getHealth() - 1f);
			player.level().playSound(null, player.blockPosition(), ITRRegistries.UNFAIR_ONE_SHOT.get(), SoundSource.PLAYERS, 2f, 0.7f);
			ITRTriggers.UNFAIR_ONESHOT.trigger(player);
			if (resistance) {
				player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 50, 4));
				player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 3));
				player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 150, 1));
			}
		}
	}
}