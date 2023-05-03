package insane96mcp.survivalreimagined.data.generator.client;

import insane96mcp.survivalreimagined.module.mining.feature.SoulSteel;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class SRBlockStatesProvider extends BlockStateProvider {
    public SRBlockStatesProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(SoulSteel.BLOCK.block().get());
    }
}
