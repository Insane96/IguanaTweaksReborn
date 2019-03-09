package net.insane96mcp.iguanatweaks;

import java.nio.file.Paths;

import org.apache.logging.log4j.Logger;

import net.insane96mcp.iguanatweaks.init.ModConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(value = IguanaTweaks.MOD_ID)
public class IguanaTweaks {
	
	public static final String MOD_ID = "iguanatweaksreborn";
	public static final String RESOURCE_PREFIX = MOD_ID + ":";
	
	public static Logger logger;
	
	public IguanaTweaks() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
	}
	
	private void setup(final FMLCommonSetupEvent event) {
       	ModLoadingContext.get().registerConfig(Type.COMMON, ModConfig.SPEC);
       	ModConfig.Init(Paths.get("config", MOD_ID + ".toml"));
	}
}
