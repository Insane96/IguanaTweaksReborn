package net.insane96mcp.iguanatweaks.proxies;

import net.insane96mcp.iguanatweaks.capabilities.CapabilityHandler;
import net.insane96mcp.iguanatweaks.capabilities.IPlayerData;
import net.insane96mcp.iguanatweaks.capabilities.PlayerData;
import net.insane96mcp.iguanatweaks.capabilities.PlayerDataStorage;
import net.insane96mcp.iguanatweaks.events.Break;
import net.insane96mcp.iguanatweaks.events.ClientTick;
import net.insane96mcp.iguanatweaks.events.EntityJoinWorld;
import net.insane96mcp.iguanatweaks.events.FovUpdate;
import net.insane96mcp.iguanatweaks.events.ItemToss;
import net.insane96mcp.iguanatweaks.events.LivingDrops;
import net.insane96mcp.iguanatweaks.events.LivingHurt;
import net.insane96mcp.iguanatweaks.events.LivingUpdate;
import net.insane96mcp.iguanatweaks.events.Mouse;
import net.insane96mcp.iguanatweaks.events.PlayerBreakSpeed;
import net.insane96mcp.iguanatweaks.events.PlayerLogInRespawn;
import net.insane96mcp.iguanatweaks.events.PlayerSleepInBed;
import net.insane96mcp.iguanatweaks.events.RenderGameOverlay;
import net.insane96mcp.iguanatweaks.integration.Integration;
import net.insane96mcp.iguanatweaks.lib.Config;
import net.insane96mcp.iguanatweaks.lib.Properties;
import net.insane96mcp.iguanatweaks.modules.ModuleGeneral;
import net.insane96mcp.iguanatweaks.modules.ModuleStackSizes;
import net.insane96mcp.iguanatweaks.network.PacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
	public void PreInit(FMLPreInitializationEvent event) {
		Config.config = new Configuration(event.getSuggestedConfigurationFile());
		Config.SyncConfig();
		Properties.Init();
		Integration.Init();
		
		PacketHandler.Init();
	}
	
	public void Init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(PlayerSleepInBed.class);
		MinecraftForge.EVENT_BUS.register(PlayerLogInRespawn.class);
		MinecraftForge.EVENT_BUS.register(LivingUpdate.class);
		MinecraftForge.EVENT_BUS.register(ClientTick.class);
		MinecraftForge.EVENT_BUS.register(Mouse.class);
		MinecraftForge.EVENT_BUS.register(RenderGameOverlay.class);
		MinecraftForge.EVENT_BUS.register(LivingDrops.class);
		MinecraftForge.EVENT_BUS.register(ItemToss.class);
		MinecraftForge.EVENT_BUS.register(EntityJoinWorld.class);
		MinecraftForge.EVENT_BUS.register(FovUpdate.class);
		MinecraftForge.EVENT_BUS.register(LivingHurt.class);
		MinecraftForge.EVENT_BUS.register(Break.class);
		MinecraftForge.EVENT_BUS.register(CapabilityHandler.class);
		MinecraftForge.EVENT_BUS.register(PlayerBreakSpeed.class);
		
		CapabilityManager.INSTANCE.register(IPlayerData.class, new PlayerDataStorage(), PlayerData.class);
	}
	
	public void PostInit(FMLPostInitializationEvent event) {
		Config.SaveConfig();

		//General
		ModuleGeneral.LessObiviousSilverfish();
		
		//StackSizes
		ModuleStackSizes.ProcessBlocks();
		ModuleStackSizes.ProcessItems();
		ModuleStackSizes.ProcessCustom();
	}
}
