package net.insane96mcp.iguanatweaks.lib;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config {

	public static Configuration config;
	
	public static int LoadIntProperty(String category, String key, String description, int defaultValue) {
		Property property = Config.config.get(category, key, defaultValue);
		property.setComment(description + " (default: " + defaultValue + ")");
		
		return property.getInt();
	}
	
	public static int[] LoadIntListProperty(String category, String key, String description, int[] defaultValue) {
		Property property = Config.config.get(category, key, defaultValue);
		description += " (default: [";
		for (int i = 0; i < defaultValue.length; i++) {
			description += defaultValue[i];
			if (i != defaultValue.length - 1)
				description += ", ";
		}
		description += "])";
		
		property.setComment(description);
		
		return property.getIntList();
	}
	
	public static double LoadDoubleProperty(String category, String key, String description, double defaultValue) {
		Property property = Config.config.get(category, key, defaultValue);
		property.setComment(description + " (default: " + defaultValue + ")");
		
		return property.getDouble();
	}
	
	public static float LoadFloatProperty(String category, String key, String description, float defaultValue) {
		return (float)LoadDoubleProperty(category, key, description, defaultValue);
	}
	
	public static String LoadStringProperty(String category, String key, String description, String defaultValue) {
		Property property = Config.config.get(category, key, defaultValue);
		property.setComment(description + " (default: " + defaultValue + ")");
		
		return property.getString();
	}
	
	public static String[] LoadStringArrayProperty(String category, String key, String description, String[] defaultValue) {
		Property property = Config.config.get(category, key, defaultValue);
		description += " (default: [";
		for (int i = 0; i < defaultValue.length; i++) {
			description += defaultValue[i];
			if (i != defaultValue.length - 1)
				description += ", ";
		}
		description += "])";
		
		property.setComment(description);
		
		return property.getStringList();
	}
	
	public static List<String> LoadStringListProperty(String category, String key, String description, ArrayList<String> defaultValue) {
		return Arrays.asList(LoadStringArrayProperty(category, key, description, defaultValue.toArray(new String[defaultValue.size()])));
	}
	
	public static boolean LoadBoolProperty(String category, String key, String description, boolean defaultValue) {
		Property property = Config.config.get(category, key, defaultValue);
		property.setComment(description + " (default: " + defaultValue + ")");
		
		return property.getBoolean();
	}
	
	public static void SetCategoryComment(String category, String comment) {
		Config.config.setCategoryComment(category, comment);
	}
	
	/**
	 * Must be called in Mod PreInit before everything and after
	 * Config.config = new Configuration(event.getSuggestedConfigurationFile());
	 */
	public static void SyncConfig() {
		try {
			Config.config.load();
		}
		catch (Exception e) {
			
		}
	}
	/**
	 * It's recommended to be called in Mod PostInit after everything
	 */
	public static void SaveConfig() {
		if(Config.config.hasChanged()) Config.config.save();
	}
}
