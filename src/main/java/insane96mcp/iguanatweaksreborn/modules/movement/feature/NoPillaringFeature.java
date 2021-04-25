package insane96mcp.iguanatweaksreborn.modules.movement.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "No Pillaring", description = "Prevents the player from placing blocks below him when in mid air.")
public class NoPillaringFeature extends Feature {

	public NoPillaringFeature(Module module) {
		super(Config.builder, module);
		//Config.builder.comment(this.getDescription()).push(this.getName());
		//Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
	}

	@SubscribeEvent
    public void playerTick(PlayerInteractEvent.RightClickBlock event) {
		if (!this.isEnabled())
			return;
		PlayerEntity playerEntity = event.getPlayer();
		if (playerEntity.isCreative() || playerEntity.isInWater() || playerEntity.isOnLadder())
			return;
		BlockPos placedPos = event.getPos().offset(event.getFace());
		Vector3d placedBlock = new Vector3d(placedPos.getX() + 0.5d, placedPos.getY() + 0.5d, placedPos.getZ() + 0.5d);
		double distance = placedBlock.distanceTo(playerEntity.getPositionVec());
		double allowedDistance = 1.36d;
		if (playerEntity.isPotionActive(Effects.JUMP_BOOST))
			allowedDistance *= 1 + ((playerEntity.getActivePotionEffect(Effects.JUMP_BOOST).getAmplifier() + 1) * 0.5);
		boolean isSolidBlock = true;
		if (event.getItemStack().getItem() instanceof BlockItem) {
			Block block = ((BlockItem) event.getItemStack().getItem()).getBlock();
			isSolidBlock = block.getDefaultState().isSolid();
		}
		if (playerEntity.getPitch(1.0f) > 40f && !playerEntity.isOnGround() && event.getItemStack().getItem() instanceof BlockItem && distance <= allowedDistance && playerEntity.getPosY() > placedPos.getY() && isSolidBlock) {
			event.setCanceled(true);
			event.setResult(Event.Result.DENY);
		}
	}
}
