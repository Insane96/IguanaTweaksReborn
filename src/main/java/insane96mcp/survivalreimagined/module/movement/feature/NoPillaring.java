package insane96mcp.survivalreimagined.module.movement.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "No Pillaring", description = "Prevents the player from placing blocks below him when in mid air.")
@LoadFeature(module = Modules.Ids.MOVEMENT)
public class NoPillaring extends Feature {

	public NoPillaring(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void playerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		if (!this.isEnabled())
			return;
		Player playerEntity = event.getEntity();
		if (playerEntity.isCreative()
				|| playerEntity.isInWater()
				|| playerEntity.onClimbable())
			return;
		//noinspection ConstantConditions
		BlockPos placedPos = event.getPos().relative(event.getFace());
		Vec3 placedBlock = new Vec3(placedPos.getX() + 0.5d, placedPos.getY() + 0.5d, placedPos.getZ() + 0.5d);
		double distance = placedBlock.distanceTo(playerEntity.position());
		double allowedDistance = 1.36d;
		if (playerEntity.hasEffect(MobEffects.JUMP))
			//noinspection ConstantConditions
			allowedDistance *= 1 + ((playerEntity.getEffect(MobEffects.JUMP).getAmplifier() + 1) * 0.5);
		boolean isSolidBlock = true;
		if (event.getItemStack().getItem() instanceof BlockItem) {
			Block block = ((BlockItem) event.getItemStack().getItem()).getBlock();
			BlockState state = block.getStateForPlacement(new BlockPlaceContext(event.getEntity(), event.getHand(), event.getItemStack(), event.getHitVec()));
			if (state == null)
				state = block.defaultBlockState();
			isSolidBlock = state.blocksMotion();/*state.canOcclude();/*state.entityCanStandOn(event.getLevel(), event.getPos(), event.getEntity());*/
		}
		if (isSolidBlock && playerEntity.getViewXRot(1.0f) > 40f && !playerEntity.onGround() && event.getItemStack().getItem() instanceof BlockItem && distance <= allowedDistance && playerEntity.getY() > placedPos.getY()) {
			event.setCanceled(true);
			event.setResult(Event.Result.DENY);
		}
	}
}