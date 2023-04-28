package insane96mcp.survivalreimagined.data.generator.client;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.List;

public class SRItemModelsProvider extends ItemModelProvider {
    public SRItemModelsProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    List<String> poorRichOres = List.of("iron", "gold", "copper");

    @Override
    protected void registerModels() {
        for (String poorRichOre : poorRichOres) {
            withExistingParent("poor_%s_ore".formatted(poorRichOre), new ResourceLocation(SurvivalReimagined.MOD_ID, "block/poor_%s_ore".formatted(poorRichOre)));
            withExistingParent("rich_%s_ore".formatted(poorRichOre), new ResourceLocation(SurvivalReimagined.MOD_ID, "block/rich_%s_ore".formatted(poorRichOre)));
            withExistingParent("poor_deepslate_%s_ore".formatted(poorRichOre), new ResourceLocation(SurvivalReimagined.MOD_ID, "block/poor_deepslate_%s_ore".formatted(poorRichOre)));
            withExistingParent("rich_deepslate_%s_ore".formatted(poorRichOre), new ResourceLocation(SurvivalReimagined.MOD_ID, "block/rich_deepslate_%s_ore".formatted(poorRichOre)));
        }
    }
}
