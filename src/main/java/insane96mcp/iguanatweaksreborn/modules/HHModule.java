package insane96mcp.iguanatweaksreborn.modules;

import insane96mcp.iguanatweaksreborn.base.Modules;
import insane96mcp.iguanatweaksreborn.setup.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BlockEvent;

public class HHModule {



	public static void breakExaustion(BlockEvent.BreakEvent event) {
		if (!ModConfig.Modules.hungerHealth)
			return;
		if (ModConfig.HungerHealth.blockBreakExaustionMultiplier == 0d)
			return;
		ServerWorld world = (ServerWorld) event.getWorld();
		BlockState state = world.getBlockState(event.getPos());
		Block block = state.getBlock();
		ResourceLocation dimensionId = world.getDimensionKey().getLocation();
		double hardness = state.getBlockHardness(event.getWorld(), event.getPos());
		double globalHardnessMultiplier = Modules.miningModule.globalHardnessFeature.getBlockGlobalHardness(block, dimensionId);
		if (globalHardnessMultiplier != -1d)
			hardness *= globalHardnessMultiplier;
		double singleHardness = Modules.miningModule.customHardnessFeature.getBlockSingleHardness(block, dimensionId);
		if (singleHardness != -1d)
			hardness = singleHardness;
		event.getPlayer().addExhaustion((float) (hardness * ModConfig.HungerHealth.blockBreakExaustionMultiplier) - 0.005f);
	}

	public static void debuffsOnLowStats(TickEvent.PlayerTickEvent event) {
		if (event.player.world.isRemote())
			return;

		ServerPlayerEntity player = (ServerPlayerEntity) event.player;

		if (player.ticksExisted % 20 != 0)
			return;

		for (ModConfig.HungerHealth.Debuff debuff : ModConfig.HungerHealth.debuffs) {
			boolean pass = false;
			switch (debuff.stat) {
				case HEALTH:
					if (player.getHealth() <= debuff.max && player.getHealth() >= debuff.min)
						pass = true;
					break;

				case HUNGER:
					if (player.getFoodStats().getFoodLevel() <= debuff.max && player.getFoodStats().getFoodLevel() >= debuff.min)
						pass = true;
					break;

				case EXPERIENCE_LEVEL:
					if (player.experienceLevel <= debuff.max && player.experienceLevel >= debuff.min)
						pass = true;
					break;
				default:
					break;
			}
			if (pass) {
				EffectInstance effectInstance = new EffectInstance(debuff.effect, 30, debuff.amplifier, true, true, false);
				player.addPotionEffect(effectInstance);
			}
		}
	}
}
