package insane96mcp.iguanatweaksreborn.utils;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

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

	public static double getStateWeight(BlockState state) {
		Material blockMaterial = state.getMaterial();
		if (!materialWeight.containsKey(blockMaterial))
			return 1d;
		return materialWeight.get(blockMaterial);
	}
}
