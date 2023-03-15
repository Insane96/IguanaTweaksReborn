package insane96mcp.survivalreimagined.utils;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import javax.annotation.Nullable;
import java.util.HashMap;

public class Weights {

	public static HashMap<Material, Double> materialWeight = new HashMap<>();

	public static void initMaterialWeight() {
		materialWeight.put(Material.AMETHYST, 1.333d);
		materialWeight.put(Material.BAMBOO_SAPLING, 1.333d);
		materialWeight.put(Material.BAMBOO, 1.333d);
		materialWeight.put(Material.BARRIER, 1d);
		materialWeight.put(Material.BUILDABLE_GLASS, 1.333d);
		materialWeight.put(Material.CACTUS, 1.333d);
		materialWeight.put(Material.CAKE, 1.333d);
		materialWeight.put(Material.CLAY, 1.6d);
		materialWeight.put(Material.CLOTH_DECORATION, 1.333d);
		materialWeight.put(Material.DECORATION, 1.333d);
		materialWeight.put(Material.DIRT, 1.6d);
		materialWeight.put(Material.EGG, 1.333d);
		materialWeight.put(Material.EXPLOSIVE, 1.333d);
		materialWeight.put(Material.GLASS, 1.6d);
		materialWeight.put(Material.GRASS, 1.6d);
		materialWeight.put(Material.HEAVY_METAL, 4d);
		materialWeight.put(Material.ICE_SOLID, 2d);
		materialWeight.put(Material.ICE, 1.6d);
		materialWeight.put(Material.LEAVES, 1.333d);
		materialWeight.put(Material.METAL, 2.667d);
		materialWeight.put(Material.MOSS, 1.333d);
		materialWeight.put(Material.NETHER_WOOD, 1.6d);
		materialWeight.put(Material.PISTON, 1.333d);
		materialWeight.put(Material.PLANT, 1.333d);
		materialWeight.put(Material.POWDER_SNOW, 1.333d);
		materialWeight.put(Material.REPLACEABLE_FIREPROOF_PLANT, 1.333d);
		materialWeight.put(Material.REPLACEABLE_PLANT, 1.333d);
		materialWeight.put(Material.REPLACEABLE_WATER_PLANT, 1.333d);
		materialWeight.put(Material.SAND, 1.6d);
		materialWeight.put(Material.SCULK, 1.333d);
		materialWeight.put(Material.SHULKER_SHELL, 3d);
		materialWeight.put(Material.SNOW, 1.6d);
		materialWeight.put(Material.SPONGE, 1.333d);
		materialWeight.put(Material.STONE, 2d);
		materialWeight.put(Material.TOP_SNOW, 1.333d);
		materialWeight.put(Material.VEGETABLE, 1.333d);
		materialWeight.put(Material.WATER_PLANT, 1.333d);
		materialWeight.put(Material.WEB, 1.333d);
		materialWeight.put(Material.WOOD, 1.6d);
		materialWeight.put(Material.WOOL, 1.6d);
	}

	public static double getWeightForState(BlockState state) {
		Material blockMaterial = state.getMaterial();
		if (!materialWeight.containsKey(blockMaterial))
			return 1d;
		return materialWeight.get(blockMaterial);
	}

	@Nullable
	public static Material getMaterialFromString(String s) {
		switch (s) {
			case "amethyst" -> { return Material.AMETHYST; }
			case "bamboo_sapling" -> { return Material.BAMBOO_SAPLING; }
			case "bamboo" -> { return Material.BAMBOO; }
			case "barrier" -> { return Material.BARRIER; }
			case "buildable_glass" -> { return Material.BUILDABLE_GLASS; }
			case "cactus" -> { return Material.CACTUS; }
			case "cake" -> { return Material.CAKE; }
			case "clay" -> { return Material.CLAY; }
			case "cloth_decoration" -> { return Material.CLOTH_DECORATION; }
			case "decoration" -> { return Material.DECORATION; }
			case "dirt" -> { return Material.DIRT; }
			case "egg" -> { return Material.EGG; }
			case "explosive" -> { return Material.EXPLOSIVE; }
			case "glass" -> { return Material.GLASS; }
			case "grass" -> { return Material.GRASS; }
			case "heavy_metal" -> { return Material.HEAVY_METAL; }
			case "ice_solid" -> { return Material.ICE_SOLID; }
			case "ice" -> { return Material.ICE; }
			case "leaves" -> { return Material.LEAVES; }
			case "metal" -> { return Material.METAL; }
			case "moss" -> { return Material.MOSS; }
			case "nether_wood" -> { return Material.NETHER_WOOD; }
			case "piston" -> { return Material.PISTON; }
			case "plant" -> { return Material.PLANT; }
			case "powder_snow" -> { return Material.POWDER_SNOW; }
			case "replaceable_fireproof_plant" -> { return Material.REPLACEABLE_FIREPROOF_PLANT; }
			case "replaceable_plant" -> { return Material.REPLACEABLE_PLANT; }
			case "replaceable_water_plant" -> { return Material.REPLACEABLE_WATER_PLANT; }
			case "sand" -> { return Material.SAND; }
			case "sculk" -> { return Material.SCULK; }
			case "shulker_shell" -> { return Material.SHULKER_SHELL; }
			case "snow" -> { return Material.SNOW; }
			case "sponge" -> { return Material.SPONGE; }
			case "stone" -> { return Material.STONE; }
			case "top_snow" -> { return Material.TOP_SNOW; }
			case "vegetable" -> { return Material.VEGETABLE; }
			case "water_plant" -> { return Material.WATER_PLANT; }
			case "web" -> { return Material.WEB; }
			case "wood" -> { return Material.WOOD; }
			case "wool" -> { return Material.WOOL; }
			default -> { return null; }
		}
	}
}
