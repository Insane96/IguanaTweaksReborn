package insane96mcp.survivalreimagined.data;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.items.feature.CopperTools;
import insane96mcp.survivalreimagined.module.items.feature.FlintExpansion;
import insane96mcp.survivalreimagined.module.items.feature.Mithril;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class SRItemTagsProvider extends ItemTagsProvider {
    public static final TagKey<Item> DISABLED_HOES = create("disabled_hoes");

    public SRItemTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> completableFuture, CompletableFuture<TagLookup<Block>> tagLookupCompletableFuture, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, completableFuture, tagLookupCompletableFuture, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        //Vanilla
        tag(ItemTags.PICKAXES).add(FlintExpansion.PICKAXE.get(), Mithril.PICKAXE.get(), CopperTools.PICKAXE.get());
        tag(ItemTags.AXES).add(FlintExpansion.AXE.get(), Mithril.AXE.get(), CopperTools.AXE.get());
        tag(ItemTags.SHOVELS).add(FlintExpansion.SHOVEL.get(), Mithril.SHOVEL.get(), CopperTools.SHOVEL.get());
        tag(ItemTags.SWORDS).add(FlintExpansion.SWORD.get(), Mithril.SWORD.get(), CopperTools.SWORD.get());
        tag(ItemTags.HOES).add(FlintExpansion.HOE.get(), Mithril.HOE.get(), CopperTools.HOE.get());

        //Mod's
        tag(DISABLED_HOES)
                .add(Items.WOODEN_HOE);
    }

    private static TagKey<Item> create(String tagName) {
        return TagKey.create(Registries.ITEM, new ResourceLocation(SurvivalReimagined.MOD_ID, tagName));
    }
}
