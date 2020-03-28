package insane96mcp.iguanatweaksreborn.event;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.modules.FarmingModule;
import insane96mcp.iguanatweaksreborn.setup.ModConfig;
import net.minecraft.block.*;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID)
public class CropGrow {

	@SubscribeEvent
	public static void cropGrowPre(BlockEvent.CropGrowEvent.Pre event) {
		FarmingModule.Agriculture.cropsRequireWater(event);
	}

	@SubscribeEvent
	public static void cropGrowPost(BlockEvent.CropGrowEvent.Post event) {
		FarmingModule.Agriculture.cropsGrowthSpeedMultiplier(event);
		FarmingModule.Agriculture.plantGrowthMultiplier(event, SugarCaneBlock.class, ModConfig.Farming.Agriculture.sugarCanesGrowthMultiplier);
		FarmingModule.Agriculture.plantGrowthMultiplier(event, CactusBlock.class, ModConfig.Farming.Agriculture.cactusGrowthMultiplier);
		FarmingModule.Agriculture.plantGrowthMultiplier(event, CocoaBlock.class, ModConfig.Farming.Agriculture.cocoaBeansGrowthMultiplier);
		FarmingModule.Agriculture.plantGrowthMultiplier(event, NetherWartBlock.class, ModConfig.Farming.Agriculture.netherwartGrowthMultiplier);
		FarmingModule.Agriculture.plantGrowthMultiplier(event, ChorusPlantBlock.class, ModConfig.Farming.Agriculture.chorusPlantGrowthMultiplier);
		FarmingModule.Agriculture.plantGrowthMultiplier(event, SaplingBlock.class, ModConfig.Farming.Agriculture.saplingGrowthMultiplier);
		FarmingModule.Agriculture.plantGrowthMultiplier(event, StemBlock.class, ModConfig.Farming.Agriculture.stemGrowthMultiplier);
		FarmingModule.Agriculture.plantGrowthMultiplier(event, SweetBerryBushBlock.class, ModConfig.Farming.Agriculture.berryBushGrowthMultiplier);
		FarmingModule.Agriculture.plantGrowthMultiplier(event, KelpTopBlock.class, ModConfig.Farming.Agriculture.kelpGrowthMultiplier);
		FarmingModule.Agriculture.plantGrowthMultiplier(event, BambooBlock.class, ModConfig.Farming.Agriculture.bambooGrowthMultiplier);
	}

	@SubscribeEvent
	public static void eventBonemeal(BonemealEvent event) {
		FarmingModule.Agriculture.nerfBonemeal(event);
	}
}