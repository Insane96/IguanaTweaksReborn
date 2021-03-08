package insane96mcp.iguanatweaksreborn.modules.misc.feature;

import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.base.ITModule;
import insane96mcp.iguanatweaksreborn.base.Label;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Label(name = "Weight", description = "Not really a feature, more like \"I don't know where to put this config since it's used by multiple modules\"")
public class WeightFeature extends ITFeature {

    private final ForgeConfigSpec.ConfigValue<List<? extends String>> materialsWeightConfig;

    private final List<String> materialsWeightDefault = Arrays.asList("ANVIL,6", "BAMBOO,2", "BAMBOO_SAPLING,2", "BARRIER,1", "CACTUS,2", "CAKE,2", "CARPET,2", "CLAY,3", "CORAL,2", "DRAGON_EGG,1", "EARTH,2", "GLASS,2", "GOURD,2", "ICE,3", "IRON,6", "LEAVES,2", "MISCELLANEOUS,2", "NETHER_PLANTS,2", "NETHER_WOOD,3", "OCEAN_PLANT,2", "ORGANIC,3", "PACKED_ICE,4", "PISTON,2", "PLANTS,2", "REDSTONE_LIGHT,2", "ROCK,4", "SAND,2", "SEA_GRASS,2", "SHULKER,1", "SNOW,2", "SNOW_BLOCK,3", "SPONGE,2", "TALL_PLANTS,2", "TNT,2", "WEB,2", "WOOD,3", "WOOL,4");

    public static HashMap<Material, Double> materialWeight = new HashMap<>();

    public WeightFeature(ITModule module) {
        super(module);
        Config.builder.comment(this.getDescription()).push(this.getName());
        materialsWeightConfig = Config.builder
                .comment("A list of materials and weights used by the Movement and Stack Reduction Modules. Names MUST be all upper case.")
                .defineList("Materials Weights", materialsWeightDefault, o -> o instanceof String);
        Config.builder.pop();
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        parseMaterialWeight(this.materialsWeightConfig.get());
    }

    public static void parseMaterialWeight(List<? extends String> list) {
        for (String line : list) {
            String[] split = line.split(",");
            if (split.length != 2) {
                LogHelper.Warn("Invalid line \"%s\" for Material Weight", line);
                continue;
            }
            Material material = null;
            try {
                material = (Material) Material.class.getField(split[0]).get(null);
            } catch (Exception e) {
                LogHelper.Warn("Invalid material \"%s\" for Material Weight", line);
                continue;
            }
            if (material == null)
                continue;
            if (!NumberUtils.isParsable(split[1])) {
                LogHelper.Warn("Invalid weight \"%s\" for Material Weight", line);
                continue;
            }
            double weight = Double.parseDouble(split[1]);
            materialWeight.put(material, weight);
        }
    }

    public static double getStateWeight(BlockState state) {
        Material blockMaterial = state.getMaterial();
        if (!materialWeight.containsKey(blockMaterial))
            return 1d;
        return materialWeight.get(blockMaterial);
    }
}
