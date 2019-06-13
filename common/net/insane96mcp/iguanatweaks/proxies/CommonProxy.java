package net.insane96mcp.iguanatweaks.proxies;

import net.insane96mcp.iguanatweaks.capabilities.IPlayerData;
import net.insane96mcp.iguanatweaks.capabilities.PlayerData;
import net.insane96mcp.iguanatweaks.capabilities.PlayerDataStorage;
import net.insane96mcp.iguanatweaks.integration.Integration;
import net.insane96mcp.iguanatweaks.item.ModItems;
import net.insane96mcp.iguanatweaks.lib.Reflection;
import net.insane96mcp.iguanatweaks.modules.ModuleMisc;
import net.insane96mcp.iguanatweaks.modules.ModuleStackSizes;
import net.insane96mcp.iguanatweaks.network.PacketHandler;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
	public void PreInit(FMLPreInitializationEvent event) {
		Integration.Init();
		Reflection.Init();
		
		PacketHandler.Init();
		
		ModItems.Init();
	}
	
	public void Init(FMLInitializationEvent event) {		
		CapabilityManager.INSTANCE.register(IPlayerData.class, new PlayerDataStorage(), PlayerData.class);
	}
	
	public void PostInit(FMLPostInitializationEvent event) {

		//General
		ModuleMisc.LessObiviousSilverfish();
		
		//StackSizes
		ModuleStackSizes.ProcessBlocks();
		ModuleStackSizes.ProcessItems();
		ModuleStackSizes.ProcessCustom();
	}
}
