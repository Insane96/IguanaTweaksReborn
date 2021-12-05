package insane96mcp.iguanatweaksreborn.modules.movement.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Tagging", description = "Player's slowed down for a brief moment when hit.")
public class TaggingFeature extends Feature {

	public TaggingFeature(Module module) {
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
		if (!(event.getEntity() instanceof PlayerEntity))
			return;
		PlayerEntity playerEntity = (PlayerEntity) event.getEntity();
		if (event.getSource().getTrueSource() instanceof LivingEntity)
			playerEntity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, (int) (event.getAmount() * 3), 1, false, false, true));
	}
}
