package insane96mcp.iguanatweaksreborn.utils;

import insane96mcp.iguanatweaksreborn.setup.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class MCUtils {
	public static final ResourceLocation AnyRL = new ResourceLocation("any");

	public static boolean isInTagOrBlock(ModConfig.IdTagMatcher idTagMatcher, Block block, @Nullable ResourceLocation dimensionId) {
		if (dimensionId == null)
			dimensionId = AnyRL;
		ResourceLocation blockId = block.getRegistryName();
		if (idTagMatcher.tag != null) {
			if (!BlockTags.getCollection().getRegisteredTags().contains(idTagMatcher.tag))
				return false;
			ITag<Block> blockTag = BlockTags.getCollection().get(idTagMatcher.tag);
			if (blockTag == null)
				return false;
			if (!blockTag.contains(block))
				return false;
			if (idTagMatcher.dimension.equals(AnyRL) || idTagMatcher.dimension.equals(dimensionId))
				return true;
		}
		else {
			if (blockId.equals(idTagMatcher.id))
				if (idTagMatcher.dimension.equals(AnyRL) || idTagMatcher.dimension.equals(dimensionId))
					return true;
		}
		return false;
	}

	public static boolean isInTagOrItem(ModConfig.IdTagMatcher idTagMatcher, Item item, @Nullable ResourceLocation dimensionId) {
		if (dimensionId == null)
			dimensionId = AnyRL;
		ResourceLocation itemId = item.getRegistryName();
		if (idTagMatcher.tag != null) {
			if (!BlockTags.getCollection().getRegisteredTags().contains(idTagMatcher.tag))
				return false;
			ITag<Item> itemTag = ItemTags.getCollection().get(idTagMatcher.tag);
			if (itemTag == null)
				return false;
			if (!itemTag.contains(item))
				return false;
			if (idTagMatcher.dimension.equals(AnyRL) || idTagMatcher.dimension.equals(dimensionId))
				return true;
		}
		else {
			if (itemId.equals(idTagMatcher.id))
				if (idTagMatcher.dimension.equals(AnyRL) || idTagMatcher.dimension.equals(dimensionId))
					return true;
		}
		return false;
	}
}
