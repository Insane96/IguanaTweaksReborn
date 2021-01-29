package insane96mcp.iguanatweaksreborn.common;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;

import java.util.HashMap;

public class Weight {
    //TODO Make this configurable
    public static double getBlockWeight(BlockState state) {
        Material blockMaterial = state.getMaterial();

        if (!materialWeight.containsKey(blockMaterial))
            return 1d;

        return materialWeight.get(blockMaterial);
    }

    public static HashMap<Material, Double> materialWeight = new HashMap<>();

    public static void initMaterialWeight() {
        materialWeight.put(Material.ANVIL, 6d);
        materialWeight.put(Material.BAMBOO, 2d);
        materialWeight.put(Material.BAMBOO_SAPLING, 2d);
        materialWeight.put(Material.BARRIER, 1d);
        materialWeight.put(Material.CACTUS, 2d);
        materialWeight.put(Material.CAKE, 2d);
        materialWeight.put(Material.CARPET, 2d);
        materialWeight.put(Material.CLAY, 3d);
        materialWeight.put(Material.CORAL, 2d);
        materialWeight.put(Material.DRAGON_EGG, 1d);
        materialWeight.put(Material.EARTH, 3d);
        materialWeight.put(Material.GLASS, 2d);
        materialWeight.put(Material.GOURD, 2d);
        materialWeight.put(Material.ICE, 3d);
        materialWeight.put(Material.IRON, 6d);
        materialWeight.put(Material.LEAVES, 2d);
        materialWeight.put(Material.MISCELLANEOUS, 2d);
        materialWeight.put(Material.NETHER_PLANTS, 2d);
        materialWeight.put(Material.NETHER_WOOD, 3d);
        materialWeight.put(Material.OCEAN_PLANT, 2d);
        materialWeight.put(Material.ORGANIC, 3d);
        materialWeight.put(Material.PACKED_ICE, 4d);
        materialWeight.put(Material.PISTON, 2d);
        materialWeight.put(Material.PLANTS, 2d);
        materialWeight.put(Material.REDSTONE_LIGHT, 2d);
        materialWeight.put(Material.ROCK, 4d);
        materialWeight.put(Material.SAND, 2d);
        materialWeight.put(Material.SEA_GRASS, 2d);
        materialWeight.put(Material.SHULKER, 1d);
        materialWeight.put(Material.SNOW, 2d);
        materialWeight.put(Material.SNOW_BLOCK, 3d);
        materialWeight.put(Material.SPONGE, 2d);
        materialWeight.put(Material.TALL_PLANTS, 2d);
        materialWeight.put(Material.TNT, 2d);
        materialWeight.put(Material.WEB, 2d);
        materialWeight.put(Material.WOOD, 3d);
        materialWeight.put(Material.WOOL, 4d);
    }
}
