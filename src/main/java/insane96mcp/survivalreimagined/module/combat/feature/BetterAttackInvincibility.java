package insane96mcp.survivalreimagined.module.combat.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.event.PostEntityHurtEvent;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.network.NetworkHandler;
import insane96mcp.survivalreimagined.network.message.SyncInvulnerableTimeMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TieredItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkDirection;

@Label(name = "Better attack invincibility", description = "When attacking, less invincibility frames are applied to entities attacked based off attack speed (only works with a weapon).")
@LoadFeature(module = Modules.Ids.COMBAT)
public class BetterAttackInvincibility extends Feature {
	public BetterAttackInvincibility(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void onAttack(PostEntityHurtEvent event) {
		if (!this.isEnabled()
				|| !(event.getDamageSource().getEntity() instanceof ServerPlayer serverPlayer)
				|| serverPlayer.getAttribute(Attributes.ATTACK_SPEED).getValue() < 2f
				|| !(serverPlayer.getMainHandItem().getItem() instanceof TieredItem))
			return;

		int time = (int) ((1f / serverPlayer.getAttribute(Attributes.ATTACK_SPEED).getValue()) * 20);
		event.getEntity().invulnerableTime = time;
		event.getEntity().hurtTime = time;
		Object msg = new SyncInvulnerableTimeMessage(event.getEntity().getId(), time);
		for (Player player : event.getEntity().level.players()) {
			NetworkHandler.CHANNEL.sendTo(msg, ((ServerPlayer) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
		}
	}
}