package net.insane96mcp.iguanatweaks;

import org.apache.logging.log4j.Logger;

import net.insane96mcp.iguanatweaks.proxies.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = IguanaTweaks.MOD_ID, name = IguanaTweaks.MOD_NAME, version = IguanaTweaks.VERSION, acceptedMinecraftVersions = IguanaTweaks.MINECRAFT_VERSIONS)
public class IguanaTweaks {
	
	public static final String MOD_ID = "iguanatweaks";
	public static final String MOD_NAME = "IguanaTweaks";
	public static final String VERSION = "1.3.5";
	public static final String RESOURCE_PREFIX = MOD_ID.toLowerCase() + ":";
	public static final String MINECRAFT_VERSIONS = "[1.12,1.12.2]";
	
	public static Logger logger;
	
	@Instance(MOD_ID)
	public static IguanaTweaks instance;
	
	@SidedProxy(clientSide = "net.insane96mcp.iguanatweaks.proxies.ClientProxy", serverSide = "net.insane96mcp.iguanatweaks.proxies.ServerProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void PreInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		
		proxy.PreInit(event);
	}
	
	@EventHandler
	public void Init(FMLInitializationEvent event) {
		proxy.Init(event);
	}
	
	@EventHandler
	public void PostInit(FMLPostInitializationEvent event) {
		proxy.PostInit(event);
	}
}
