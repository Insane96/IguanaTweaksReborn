package insane96mcp.survivalreimagined.data.generator.client;

import insane96mcp.survivalreimagined.module.mining.feature.Durium;
import insane96mcp.survivalreimagined.module.mining.feature.SoulSteel;
import insane96mcp.survivalreimagined.module.world.feature.CoalFire;
import insane96mcp.survivalreimagined.module.world.feature.CyanFlower;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class SRBlockStatesProvider extends BlockStateProvider {
    public SRBlockStatesProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(Durium.SCRAP_BLOCK.block().get());
        simpleBlock(Durium.BLOCK.block().get());
        simpleBlock(Durium.ORE.block().get());
        simpleBlock(Durium.DEEPSLATE_ORE.block().get());
        simpleBlock(SoulSteel.BLOCK.block().get());
        simpleBlock(CoalFire.SOUL_SAND_HELLISH_COAL_ORE.block().get());
        simpleBlock(CoalFire.SOUL_SOIL_HELLISH_COAL_ORE.block().get());
        simpleBlock(CyanFlower.FLOWER.block().get());
    }
}
