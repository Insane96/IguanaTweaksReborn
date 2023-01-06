package insane96mcp.iguanatweaksreborn.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.data.ITDataReloadListener;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ITFeature extends Feature {
    public ITFeature(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
        ITDataReloadListener.INSTANCE.registerJsonConfigFeature(this);
    }

    private File jsonConfigFolder;

    public File getJsonConfigFolder() {
        return jsonConfigFolder;
    }

    public void loadJsonConfigs() {
        jsonConfigFolder = new File(IguanaTweaksReborn.CONFIG_FOLDER + "/" + this.getModule().getName() + "/" + this.getName());
        if (!jsonConfigFolder.exists()) {
            if (!jsonConfigFolder.mkdirs()) {
                LogHelper.warn("Failed to create %s json config folder", this.getName());
            }
        }
    }

    protected <T> void loadAndReadFile(String fileName, List<T> list, final List<T> defaultList, Type listType) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        File file = new File(jsonConfigFolder, fileName);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    throw new Exception("File#createNewFile failed");
                }
                String json = gson.toJson(defaultList, listType);
                Files.write(file.toPath(), json.getBytes());
            }
            catch (Exception e) {
                LogHelper.error("Failed to create default Json %s: %s", FilenameUtils.removeExtension(file.getName()), e.getMessage());
            }
        }

        list.clear();
        try {
            FileReader fileReader = new FileReader(file);
            List<T> listRead = gson.fromJson(fileReader, listType);
            list.addAll(listRead);
        }
        catch (JsonSyntaxException e) {
            LogHelper.error("Parsing error loading Json %s: %s", FilenameUtils.removeExtension(file.getName()), e.getMessage());
        }
        catch (Exception e) {
            LogHelper.error("Failed loading Json %s: %s", FilenameUtils.removeExtension(file.getName()), e.getMessage());
        }
    }

    /**
     * Use this instead of Utils.isItemInTag when reloading data due to tags not existing yet at reload
     */
    public static boolean isItemInTag(Item item, ResourceLocation tag) {
        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tag);
        Collection<Holder<Item>> tags = ITDataReloadListener.reloadContext.getTag(tagKey);
        for (Holder<Item> holder : tags) {
            if (holder.value().equals(item))
                return true;
        }
        return false;
    }

    /**
     * Use this instead of IdTagMatcher#getAllItems when reloading data due to tags not existing yet at reload
     */
    public static List<Item> getAllItems(IdTagMatcher idTagMatcher) {
        if (idTagMatcher.type == IdTagMatcher.Type.ID)
            return idTagMatcher.getAllItems();

        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, idTagMatcher.location);
        Collection<Holder<Item>> tags = ITDataReloadListener.reloadContext.getTag(tagKey);
        ArrayList<Item> list = new ArrayList<>();
        for (Holder<Item> holder : tags) {
            list.add(holder.value());
        }
        return list;
    }

    /**
     * Use this instead of Utils.isBlockInTag when reloading data due to tags not existing yet at reload
     */
    public static boolean isBlockInTag(Block block, ResourceLocation tag) {
        TagKey<Block> tagKey = TagKey.create(Registries.BLOCK, tag);
        Collection<Holder<Block>> tags = ITDataReloadListener.reloadContext.getTag(tagKey);
        for (Holder<Block> holder : tags) {
            if (holder.value().equals(block))
                return true;
        }
        return false;
    }

    /**
     * Use this instead of IdTagMatcher#getAllBlocks when reloading data due to tags not existing yet at reload
     */
    public static List<Block> getAllBlocks(IdTagMatcher idTagMatcher) {
        if (idTagMatcher.type == IdTagMatcher.Type.ID)
            return idTagMatcher.getAllBlocks();

        TagKey<Block> tagKey = TagKey.create(Registries.BLOCK, idTagMatcher.location);
        Collection<Holder<Block>> tags = ITDataReloadListener.reloadContext.getTag(tagKey);
        ArrayList<Block> list = new ArrayList<>();
        for (Holder<Block> holder : tags) {
            list.add(holder.value());
        }
        return list;
    }

    /**
     * Use this instead of Utils.isEntityInTag when reloading data due to tags not existing yet at reload
     */
    public static boolean isEntityInTag(Entity entity, ResourceLocation tag) {
        TagKey<EntityType<?>> tagKey = TagKey.create(Registries.ENTITY_TYPE, tag);
        Collection<Holder<EntityType<?>>> tags = ITDataReloadListener.reloadContext.getTag(tagKey);
        for (Holder<EntityType<?>> holder : tags) {
            if (holder.value().equals(entity.getType()))
                return true;
        }
        return false;
    }
}
