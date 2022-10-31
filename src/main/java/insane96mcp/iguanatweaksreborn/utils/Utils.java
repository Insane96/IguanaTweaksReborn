package insane96mcp.iguanatweaksreborn.utils;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class Utils {
    public static boolean isItemInTag(Item item, ResourceLocation tag) {
        TagKey<Item> tagKey = TagKey.create(Registry.ITEM_REGISTRY, tag);
        //noinspection ConstantConditions
        return ForgeRegistries.ITEMS.tags().getTag(tagKey).contains(item);
    }

    public static boolean isBlockInTag(Block block, ResourceLocation tag) {
        TagKey<Block> tagKey = TagKey.create(Registry.BLOCK_REGISTRY, tag);
        //noinspection ConstantConditions
        return ForgeRegistries.BLOCKS.tags().getTag(tagKey).contains(block);
    }

    public static boolean isEntityInTag(Entity entity, ResourceLocation tag) {
        TagKey<EntityType<?>> tagKey = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, tag);
        //noinspection ConstantConditions
        return ForgeRegistries.ENTITY_TYPES.tags().getTag(tagKey).contains(entity.getType());
    }
}
