package insane96mcp.iguanatweaksreborn.data.generator.client;

import insane96mcp.iguanatweaksreborn.module.farming.crops.Crops;
import insane96mcp.iguanatweaksreborn.module.mobs.spawning.Spawning;
import insane96mcp.iguanatweaksreborn.module.world.CyanFlower;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ITRBlockStatesProvider extends BlockStateProvider {
    public ITRBlockStatesProvider(PackOutput output, String modId, ExistingFileHelper existingFileHelper) {
        super(output, modId, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(CyanFlower.FLOWER.block().get());
        simpleBlock(Crops.SOLANUM_NEOROSSII.block().get());
        simpleBlock(Spawning.ECHO_TORCH.block().get());
    }
}
