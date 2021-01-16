package insane96mcp.iguanatweaksreborn.event;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.modules.ExperienceModule;
import insane96mcp.iguanatweaksreborn.modules.misc.ai.ITCenaSwellGoal;
import insane96mcp.iguanatweaksreborn.modules.misc.ai.ITCreeperSwellGoal;
import net.minecraft.entity.ai.goal.CreeperSwellGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID)
public class EntityJoinWorld {

	@SubscribeEvent
	public static void eventEntityJoinWorld(EntityJoinWorldEvent event) {
		ExperienceModule.globalXpDrop(event);

		if (event.getEntity() instanceof CreeperEntity){
			CreeperEntity creeper = (CreeperEntity) event.getEntity();

			ArrayList<Goal> goalsToRemove = new ArrayList<>();
			creeper.goalSelector.goals.forEach(prioritizedGoal -> {
				if (prioritizedGoal.getGoal() instanceof CreeperSwellGoal)
					goalsToRemove.add(prioritizedGoal.getGoal());
			});

			goalsToRemove.forEach(creeper.goalSelector::removeGoal);

			//creeper.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(creeper, LivingEntity.class, true));

			if (creeper.hasCustomName() && creeper.getCustomName().getString().equals("John Cena")) {
				CompoundNBT compoundNBT = new CompoundNBT();
				compoundNBT.putShort("Fuse", (short)35);
				creeper.readAdditional(compoundNBT);
				creeper.goalSelector.addGoal(2, new ITCenaSwellGoal(creeper));
			}
			else {
				creeper.goalSelector.addGoal(2, new ITCreeperSwellGoal(creeper));
			}
		}
	}
}
