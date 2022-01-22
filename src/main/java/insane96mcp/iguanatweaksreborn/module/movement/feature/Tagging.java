package insane96mcp.iguanatweaksreborn.module.movement.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Tagging", description = "Player's slowed down for a brief moment when hit.")
public class Tagging extends Feature {

	public Tagging(Module module) {
		super(Config.builder, module);
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
	}

	@SubscribeEvent
	public void onDamageTaken(LivingDamageEvent event) {
		if (!this.isEnabled())
			return;
		if (!(event.getEntity() instanceof Player playerEntity))
			return;
		if (event.getSource().getEntity() instanceof LivingEntity)
			playerEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int) (event.getAmount() * 5), 1, false, false, true));
	}
}