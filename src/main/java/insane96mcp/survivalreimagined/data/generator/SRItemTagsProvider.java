package insane96mcp.survivalreimagined.data.generator;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.combat.feature.Knockback;
import insane96mcp.survivalreimagined.module.farming.feature.BoneMeal;
import insane96mcp.survivalreimagined.module.farming.feature.Hoes;
import insane96mcp.survivalreimagined.module.hungerhealth.feature.FoodDrinks;
import insane96mcp.survivalreimagined.module.hungerhealth.feature.NoHunger;
import insane96mcp.survivalreimagined.module.items.feature.CopperTools;
import insane96mcp.survivalreimagined.module.items.feature.FlintExpansion;
import insane96mcp.survivalreimagined.module.items.feature.ItemStats;
import insane96mcp.survivalreimagined.module.items.feature.StackSizes;
import insane96mcp.survivalreimagined.module.mining.feature.Mithril;
import insane96mcp.survivalreimagined.module.mining.feature.SoulSteel;
import insane96mcp.survivalreimagined.module.sleeprespawn.feature.Tiredness;
import insane96mcp.survivalreimagined.module.world.feature.Spawners;
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
    public SRItemTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> completableFuture, CompletableFuture<TagLookup<Block>> tagLookupCompletableFuture, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, completableFuture, tagLookupCompletableFuture, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        //Vanilla
        tag(ItemTags.PICKAXES).add(FlintExpansion.PICKAXE.get(), Mithril.PICKAXE.get(), CopperTools.PICKAXE.get(), SoulSteel.PICKAXE.get());
        tag(ItemTags.AXES).add(FlintExpansion.AXE.get(), Mithril.AXE.get(), CopperTools.AXE.get(), SoulSteel.AXE.get());
        tag(ItemTags.SHOVELS).add(FlintExpansion.SHOVEL.get(), Mithril.SHOVEL.get(), CopperTools.SHOVEL.get(), SoulSteel.SHOVEL.get());
        tag(ItemTags.SWORDS).add(FlintExpansion.SWORD.get(), Mithril.SWORD.get(), CopperTools.SWORD.get(), SoulSteel.SWORD.get());
        tag(ItemTags.HOES).add(FlintExpansion.HOE.get(), Mithril.HOE.get(), CopperTools.HOE.get(), SoulSteel.HOE.get());

        //Mod's
        tag(Hoes.DISABLED_HOES)
                .add(Items.WOODEN_HOE);

        tag(Tiredness.ENERGY_BOOST)
                .add(Items.COOKIE)
                .addOptional(new ResourceLocation("farmersdelight:chocolate_pie_slice")).addOptional(new ResourceLocation("create:bar_of_chocolate")).addOptional(new ResourceLocation("create:chocolate_glazed_berries"));

        tag(ItemStats.NO_DAMAGE);
        tag(ItemStats.NO_EFFICIENCY);
        tag(Knockback.REDUCED_KNOCKBACK);

        tag(Spawners.SPAWNER_REACTIVATOR)
                .add(Items.ECHO_SHARD);

        tag(NoHunger.RAW_FOOD)
                .add(Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PORKCHOP, Items.MUTTON, Items.BEEF, Items.CHICKEN, Items.RABBIT, Items.ROTTEN_FLESH, Items.GOLDEN_CARROT);

        tag(StackSizes.NO_STACK_SIZE_CHANGES)
                .add(Items.ROTTEN_FLESH);

        tag(FoodDrinks.FOOD_BLACKLIST);
        tag(BoneMeal.ITEM_BLACKLIST);
    }

    public static TagKey<Item> create(String tagName) {
        return TagKey.create(Registries.ITEM, new ResourceLocation(SurvivalReimagined.MOD_ID, tagName));
    }
}
