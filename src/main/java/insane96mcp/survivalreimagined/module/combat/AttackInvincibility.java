package insane96mcp.survivalreimagined.module.combat;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.DataPacks;
import insane96mcp.survivalreimagined.network.message.SyncInvulnerableTimeMessage;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Attack invincibility", description = "Less invincibility frames and none with arrows.")
@LoadFeature(module = Modules.Ids.COMBAT)
public class AttackInvincibility extends Feature {

	@Config
	@Label(name = "Invincibility frames based off attack speed", description = "If true less invincibility frames are applied to mobs only if using an item with attack speed modifier")
	public static Boolean invincibilityFramesAttackSpeed = true;
	@Config
	@Label(name = "Arrows and magic don't trigger invincibility frames", description = "If true, a datapack is enabled tthat makes Arrows and magic damage no longer trigger the invincibility frames.")
	public static Boolean arrowsNoInvincFrames = true;

	public AttackInvincibility(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "attack_invincibility", Component.literal("Survival Reimagined Attack Invincibility"), () -> super.isEnabled() && !DataPacks.disableAllDataPacks && arrowsNoInvincFrames));
	}

	@SubscribeEvent
	public void onAttack(LivingDamageEvent event) {
		if (!this.isEnabled()
				|| !invincibilityFramesAttackSpeed
				|| !(event.getSource().getEntity() instanceof ServerPlayer serverPlayer)
				|| serverPlayer.getAttribute(Attributes.ATTACK_SPEED).getValue() < 2f
				|| !serverPlayer.getMainHandItem().getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(Attributes.ATTACK_SPEED))
			return;

		int time = (int) ((1f / serverPlayer.getAttribute(Attributes.ATTACK_SPEED).getValue()) * 20);
		event.getEntity().invulnerableTime = time;
		event.getEntity().hurtTime = time;
		SyncInvulnerableTimeMessage.sync((ServerLevel) event.getEntity().level(), event.getEntity(), time);
	}
}