package insane96mcp.iguanatweaksreborn.common.classutils;


import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class IdTagMatcher {
    public ResourceLocation id;
    public ResourceLocation tag;
    public ResourceLocation dimension;

    public IdTagMatcher(@Nullable ResourceLocation id, @Nullable ResourceLocation tag, ResourceLocation dimension) {
        if (id == null && tag == null)
            throw new NullPointerException("'id' and 'tag' can't be both null");

        this.id = id;
        this.tag = tag;
        this.dimension = dimension;
    }

    public IdTagMatcher(@Nullable ResourceLocation id, @Nullable ResourceLocation tag) {
        this(id, tag, AnyRL);
    }

    @Nullable
    public static IdTagMatcher parseLine(String line) {
        String[] split = line.split(",");
        if (split.length < 1 || split.length > 2) {
            LogHelper.warn("Invalid line \"%s\". Format must be modid:item_or_block_id,modid:dimension", line);
            return null;
        }
        ResourceLocation dimension = AnyRL;
        if (split.length == 2) {
            dimension = ResourceLocation.tryCreate(split[1]);
            if (dimension == null) {
                LogHelper.warn(String.format("Invalid dimension \"%s\". Ignoring it", split[1]));
                dimension = AnyRL;
            }
        }
        if (split[0].startsWith("#")) {
            String replaced = split[0].replace("#", "");
            ResourceLocation tag = ResourceLocation.tryCreate(replaced);
            if (tag == null) {
                LogHelper.warn("%s tag is not valid", replaced);
                return null;
            }
            return new IdTagMatcher(null, tag, dimension);
        }
        else {
            ResourceLocation id = ResourceLocation.tryCreate(split[0]);
            if (id == null) {
                LogHelper.warn("%s id is not valid", line);
                return null;
            }
            if (ForgeRegistries.BLOCKS.containsKey(id) || ForgeRegistries.ITEMS.containsKey(id)) {
                return new IdTagMatcher(id, null, dimension);
            }
            else {
                LogHelper.warn(String.format("%s id seems to not exist", line));
                return null;
            }
        }
    }

    public static ArrayList<IdTagMatcher> parseStringList(List<? extends String> list) {
        ArrayList<IdTagMatcher> commonTagBlock = new ArrayList<>();
        for (String line : list) {
            IdTagMatcher idTagMatcher = IdTagMatcher.parseLine(line);
            if (idTagMatcher != null)
                commonTagBlock.add(idTagMatcher);
        }
        return commonTagBlock;
    }

    public static final ResourceLocation AnyRL = new ResourceLocation("any");

    public boolean isInTagOrBlock(Block block) {
        return isInTagOrBlock(block, null);
    }

    public boolean isInTagOrBlock(Block block, @Nullable ResourceLocation dimensionId) {
        if (dimensionId == null)
            dimensionId = AnyRL;
        ResourceLocation blockId = block.getRegistryName();
        if (this.tag != null) {
            if (!BlockTags.getCollection().getRegisteredTags().contains(this.tag))
                return false;
            ITag<Block> blockTag = BlockTags.getCollection().get(this.tag);
            if (blockTag == null)
                return false;
            if (!blockTag.contains(block))
                return false;
            if (this.dimension.equals(AnyRL) || this.dimension.equals(dimensionId))
                return true;
        }
        else {
            if (blockId.equals(this.id))
                if (this.dimension.equals(AnyRL) || this.dimension.equals(dimensionId))
                    return true;
        }
        return false;
    }

    public boolean isInTagOrItem(Item item) {
        return isInTagOrItem(item, null);
    }

    public boolean isInTagOrItem(Item item, @Nullable ResourceLocation dimensionId) {
        if (dimensionId == null)
            dimensionId = AnyRL;
        ResourceLocation itemId = item.getRegistryName();
        if (this.tag != null) {
            if (!ItemTags.getCollection().getRegisteredTags().contains(this.tag))
                return false;
            ITag<Item> itemTag = ItemTags.getCollection().get(this.tag);
            if (itemTag == null)
                return false;
            if (!itemTag.contains(item))
                return false;
            if (this.dimension.equals(AnyRL) || this.dimension.equals(dimensionId))
                return true;
        }
        else {
            if (itemId.equals(this.id))
                if (this.dimension.equals(AnyRL) || this.dimension.equals(dimensionId))
                    return true;
        }
        return false;
    }

    public List<Block> getAllBlocks() {
        List<Block> blocks = new ArrayList<>();
        if (this.id != null) {
            Block block = ForgeRegistries.BLOCKS.getValue(this.id);
            if (block != null)
                blocks.add(block);
        }
        else {
            ITag<Block> blockTag = BlockTags.getCollection().get(this.tag);
            if (blockTag != null)
                blocks.addAll(blockTag.getAllElements());
        }
        return blocks;
    }

    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        if (this.id != null) {
            Item item = ForgeRegistries.ITEMS.getValue(this.id);
            if (item != null)
                items.add(item);
        }
        else {
            ITag<Item> itemTag = ItemTags.getCollection().get(this.tag);
            if (itemTag != null)
                items.addAll(itemTag.getAllElements());
        }
        return items;
    }
}