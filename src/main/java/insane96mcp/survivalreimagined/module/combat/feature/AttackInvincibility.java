package insane96mcp.survivalreimagined.module.combat.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.event.PostEntityHurtEvent;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.network.message.SyncInvulnerableTimeMessage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Attack invincibility", description = "Less invincibility frames and none with arrows.")
@LoadFeature(module = Modules.Ids.COMBAT)
public class AttackInvincibility extends Feature {

	@Config
	@Label(name = "Invincibility frames based off attack speed", description = "If true less invincibility frames are applied to mobs only if using an item with attack speed modifier")
	public static Boolean invincibilityFramesAttackSpeed = true;
	@Config
	@Label(name = "Arrows don't trigger invincibility frames", description = "If true, Arrows will no longer trigger the invincibility frames (like Combat Test Snapshots).")
	public static Boolean arrowsNoInvincFrames = true;

	public AttackInvincibility(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	public static boolean disableArrowInvFrames() {
		return isEnabled(Stats.class) && arrowsNoInvincFrames;
	}

	@SubscribeEvent
	public void onAttack(PostEntityHurtEvent event) {
		if (!this.isEnabled()
				|| !invincibilityFramesAttackSpeed
				|| !(event.getDamageSource().getEntity() instanceof ServerPlayer serverPlayer)
				|| serverPlayer.getAttribute(Attributes.ATTACK_SPEED).getValue() < 2f
				/*|| (invincibilityFramesAttackSpeed && serverPlayer.getMainHandItem().getAttributeModifiers(EquipmentSlot.MAINHAND).isEmpty())*/)
			return;

		int time = (int) ((1f / serverPlayer.getAttribute(Attributes.ATTACK_SPEED).getValue()) * 20);
		event.getEntity().invulnerableTime = time;
		event.getEntity().hurtTime = time;
		SyncInvulnerableTimeMessage.sync((ServerLevel) event.getEntity().level, event.getEntity(), time);
	}
}