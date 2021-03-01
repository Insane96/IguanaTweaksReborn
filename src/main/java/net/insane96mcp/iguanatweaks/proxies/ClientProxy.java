package net.insane96mcp.iguanatweaks.proxies;

import net.insane96mcp.iguanatweaks.lib.Reflection;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy{

	@Override
	public void PreInit(FMLPreInitializationEvent event) {
		super.PreInit(event);
		Reflection.Client.Init();
	}

	@Override
	public void Init(FMLInitializationEvent event) {
		super.Init(event);
	}

	@Override
	public void PostInit(FMLPostInitializationEvent event) {
		super.PostInit(event);
	}
	
}
