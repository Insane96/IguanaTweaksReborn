package insane96mcp.iguanatweaksreborn.data.generator.client;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.farming.crops.Crops;
import insane96mcp.iguanatweaksreborn.module.world.BerryBushes;
import insane96mcp.iguanatweaksreborn.module.world.CyanFlower;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class ITRItemModelsProvider extends ItemModelProvider {
    public ITRItemModelsProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(Crops.CARROT_SEEDS.get());
        basicItem(Crops.ROOTED_POTATO.get());

        basicItem(BerryBushes.SWEET_BERRY_SEEDS.get());

        basicItemWithTexture(CyanFlower.FLOWER.item().get(), new ResourceLocation(IguanaTweaksReborn.MOD_ID, "block/cyan_flower"));
        basicItemWithTexture(Crops.SOLANUM_NEOROSSII.item().get(), new ResourceLocation(IguanaTweaksReborn.MOD_ID, "block/solanum_neorossii"));
    }
    public ItemModelBuilder basicItemWithTexture(Item item, ResourceLocation texture)
    {
        return basicItemWithTexture(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)), texture);
    }

    public ItemModelBuilder basicItemWithTexture(ResourceLocation item, ResourceLocation texture)
    {
        return getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", texture);
    }
}
