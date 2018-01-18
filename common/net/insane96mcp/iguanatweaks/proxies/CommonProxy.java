package net.insane96mcp.iguanatweaks.proxies;

import net.insane96mcp.iguanatweaks.events.Break;
import net.insane96mcp.iguanatweaks.events.ClientTick;
import net.insane96mcp.iguanatweaks.events.EntityJoinWorld;
import net.insane96mcp.iguanatweaks.events.FovUpdate;
import net.insane96mcp.iguanatweaks.events.ItemToss;
import net.insane96mcp.iguanatweaks.events.LivingDrops;
import net.insane96mcp.iguanatweaks.events.LivingHurt;
import net.insane96mcp.iguanatweaks.events.LivingUpdate;
import net.insane96mcp.iguanatweaks.events.Mouse;
import net.insane96mcp.iguanatweaks.events.PlayerLogInRespawn;
import net.insane96mcp.iguanatweaks.events.PlayerSleepInBed;
import net.insane96mcp.iguanatweaks.events.RenderGameOverlay;
import net.insane96mcp.iguanatweaks.lib.Config;
import net.insane96mcp.iguanatweaks.lib.Properties;
import net.insane96mcp.iguanatweaks.modules.ModuleHardness;
import net.insane96mcp.iguanatweaks.modules.ModuleHud;
import net.insane96mcp.iguanatweaks.modules.ModuleStackSizes;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {
	public void PreInit(FMLPreInitializationEvent event) {
		Config.config = new Configuration(event.getSuggestedConfigurationFile());
		Config.SyncConfig();
		Properties.Init();
		
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
	}
	
	public void PostInit(FMLPostInitializationEvent event) {
		Config.SaveConfig();
		
		//Hardness
		ModuleHardness.ProcessGlobalHardness();
		ModuleHardness.ProcessSingleHardness();
		
		//StackSizes
		ModuleStackSizes.ProcessBlocks();
		ModuleStackSizes.ProcessItems();
		
		//Hud
		ModuleHud.HideExperienceBar();
	}
}
