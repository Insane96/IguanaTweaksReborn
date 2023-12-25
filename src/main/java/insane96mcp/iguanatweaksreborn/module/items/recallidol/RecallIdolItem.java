package insane96mcp.iguanatweaksreborn.module.items.recallidol;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class RecallIdolItem extends Item {
	public RecallIdolItem(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public int getUseDuration(ItemStack p_41454_) {
		return 100;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack p_40678_) {
		return UseAnim.BOW;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
		ItemStack itemstack = player.getItemInHand(interactionHand);
		player.startUsingItem(interactionHand);
		return InteractionResultHolder.consume(itemstack);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (!(entity instanceof ServerPlayer player))
			return stack;
		BlockPos respawnPos = player.getRespawnPosition();
		float respawnAngle = player.getRespawnAngle();
		boolean forcedRespawn = player.isRespawnForced();
		ServerLevel serverlevel = player.server.getLevel(player.getRespawnDimension());
		Optional<Vec3> optional;
		if (serverlevel != null && respawnPos != null) {
			optional = Player.findRespawnPositionAndUseSpawnBlock(serverlevel, respawnPos, respawnAngle, forcedRespawn, true);
		} else {
			optional = Optional.empty();
		}
		optional.ifPresent(vec3 -> {
			player.teleportTo(vec3.x, vec3.y, vec3.z);
			stack.shrink(1);
		});
		//TODO totem like animation and sounds when using
		return stack;
	}
}
