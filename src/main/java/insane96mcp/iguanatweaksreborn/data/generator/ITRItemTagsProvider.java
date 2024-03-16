package insane96mcp.iguanatweaksreborn.data.generator;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.FireAspect;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.Knockback;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.Luck;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.damage.BonusDamageEnchantment;
import insane96mcp.iguanatweaksreborn.module.hungerhealth.fooddrinks.FoodDrinks;
import insane96mcp.iguanatweaksreborn.module.items.StackSizes;
import insane96mcp.iguanatweaksreborn.module.items.itemstats.ItemStats;
import insane96mcp.iguanatweaksreborn.module.sleeprespawn.tiredness.Tiredness;
import insane96mcp.iguanatweaksreborn.module.world.spawners.Spawners;
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

public class ITRItemTagsProvider extends ItemTagsProvider {

    public static final TagKey<Item> WOODEN_HAND_EQUIPMENT = ITRItemTagsProvider.create("equipment/hand/wooden");
    public static final TagKey<Item> STONE_HAND_EQUIPMENT = ITRItemTagsProvider.create("equipment/hand/stone");
    public static final TagKey<Item> LEATHER_ARMOR_EQUIPMENT = ITRItemTagsProvider.create("equipment/armor/leather");

    public ITRItemTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> completableFuture, CompletableFuture<TagLookup<Block>> tagLookupCompletableFuture, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, completableFuture, tagLookupCompletableFuture, modId, existingFileHelper);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        //Vanilla

        //ITR
        tag(StackSizes.NO_STACK_SIZE_CHANGES)
                .add(Items.ROTTEN_FLESH, Items.SPIDER_EYE);

        tag(Tiredness.ENERGY_BOOST_ITEM_TAG)
                .add(Items.COOKIE)
                .addOptional(new ResourceLocation("farmersdelight:chocolate_pie_slice")).addOptional(new ResourceLocation("create:bar_of_chocolate")).addOptional(new ResourceLocation("create:chocolate_glazed_berries"));

        tag(ItemStats.NOT_UNBREAKABLE)
                .addTags(WOODEN_HAND_EQUIPMENT, STONE_HAND_EQUIPMENT, LEATHER_ARMOR_EQUIPMENT)
                .addOptionalTag(new ResourceLocation("iguanatweaksexpanded:equipment/hand/flint")).addOptionalTag(new ResourceLocation("iguanatweaksexpanded:equipment/hand/copper"))
                .addOptionalTag(new ResourceLocation("iguanatweaksexpanded:equipment/armor/chained_copper"))
                .addOptional(new ResourceLocation("shieldsplus:wooden_shield")).addOptional(new ResourceLocation("shieldsplus:stone_shield")).addOptional(new ResourceLocation("iguanatweaksexpanded:copper_shield")).addOptional(new ResourceLocation("iguanatweaksexpanded:flint_shield"));
        tag(FoodDrinks.RAW_FOOD)
                .add(Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PORKCHOP, Items.MUTTON, Items.BEEF, Items.CHICKEN, Items.RABBIT, Items.ROTTEN_FLESH, Items.GOLDEN_CARROT);
		tag(Spawners.SPAWNER_REACTIVATOR_TAG)
				.add(Items.ECHO_SHARD);
        tag(BonusDamageEnchantment.ACCEPTS_ENCHANTMENT)
                .addTags(ItemTags.AXES, ItemTags.PICKAXES, ItemTags.SHOVELS, ItemTags.HOES, ItemTags.SWORDS);
        tag(Luck.ACCEPTS_ENCHANTMENT)
                .addTag(BonusDamageEnchantment.ACCEPTS_ENCHANTMENT)
                .add(Items.FISHING_ROD);
        tag(Knockback.ACCEPTS_ENCHANTMENT)
                .addTag(BonusDamageEnchantment.ACCEPTS_ENCHANTMENT);
        tag(FireAspect.ACCEPTS_ENCHANTMENT)
                .addTag(BonusDamageEnchantment.ACCEPTS_ENCHANTMENT);
    }

    public static TagKey<Item> create(String tagName) {
        return TagKey.create(Registries.ITEM, new ResourceLocation(IguanaTweaksReborn.MOD_ID, tagName));
    }
}
