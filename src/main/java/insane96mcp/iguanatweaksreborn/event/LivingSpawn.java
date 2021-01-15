package insane96mcp.iguanatweaksreborn.event;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.modules.MiscModule;
import insane96mcp.iguanatweaksreborn.other.ITCreeperSwellGoal;
import net.minecraft.entity.ai.goal.CreeperSwellGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID)
public class LivingSpawn {
	@SubscribeEvent
	public static void SpawnSpecial(LivingSpawnEvent.CheckSpawn event) {
		MiscModule.markFromSpawner(event);
	}
}
