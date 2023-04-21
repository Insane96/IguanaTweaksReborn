package insane96mcp.survivalreimagined.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.data.SRDataReloadListener;
import insane96mcp.survivalreimagined.network.message.JsonConfigSyncMessage;
import insane96mcp.survivalreimagined.utils.LogHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

public class SRFeature extends Feature {
    public final List<JsonConfig<?>> JSON_CONFIGS = new ArrayList<>();

    public SRFeature(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
        SRDataReloadListener.INSTANCE.registerJsonConfigFeature(this);
    }

    private File jsonConfigFolder;

    public File getJsonConfigFolder() {
        return jsonConfigFolder;
    }

    public void loadJsonConfigs() {
        jsonConfigFolder = new File(SurvivalReimagined.CONFIG_FOLDER + "/" + this.getModule().getName() + "/" + this.getName());
        if (!jsonConfigFolder.exists()) {
            if (!jsonConfigFolder.mkdirs()) {
                LogHelper.warn("Failed to create %s json config folder", this.getName());
            }
        }
        for (JsonConfig<?> jsonConfig : JSON_CONFIGS) {
            jsonConfig.loadAndReadFile(jsonConfigFolder);
        }
    }

    protected static <T> void loadAndReadJson(String json, List<T> list, final List<T> defaultList, Type listType) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<T> listRead;
        try {
            listRead = gson.fromJson(json, listType);
        }
        catch (Exception e) {
            listRead = new ArrayList<>(defaultList);
        }
        list.addAll(listRead);
    }

    /**
     * Use this instead of Utils.isItemInTag when reloading data due to tags not existing yet at reload
     */
    public static boolean isItemInTag(Item item, ResourceLocation tag) {
        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tag);
        Collection<Holder<Item>> tags = SRDataReloadListener.reloadContext.getTag(tagKey);
        for (Holder<Item> holder : tags) {
            if (holder.value().equals(item))
                return true;
        }
        return false;
    }

    /**
     * Use this instead of IdTagMatcher#getAllItems when reloading data due to tags not existing yet at reload
     */
    public static List<Item> getAllItems(IdTagMatcher idTagMatcher, boolean isClientSide) {
        if (idTagMatcher.type == IdTagMatcher.Type.ID || isClientSide)
            return idTagMatcher.getAllItems();

        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, idTagMatcher.location);
        Collection<Holder<Item>> tags = SRDataReloadListener.reloadContext.getTag(tagKey);
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
        Collection<Holder<Block>> tags = SRDataReloadListener.reloadContext.getTag(tagKey);
        for (Holder<Block> holder : tags) {
            if (holder.value().equals(block))
                return true;
        }
        return false;
    }

    /**
     * Use this instead of IdTagMatcher#getAllBlocks when reloading data due to tags not existing yet at reload
     */
    public static List<Block> getAllBlocks(IdTagMatcher idTagMatcher, boolean isClientSide) {
        if (idTagMatcher.type == IdTagMatcher.Type.ID || isClientSide)
            return idTagMatcher.getAllBlocks();

        TagKey<Block> tagKey = TagKey.create(Registries.BLOCK, idTagMatcher.location);
        Collection<Holder<Block>> tags = SRDataReloadListener.reloadContext.getTag(tagKey);
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
        Collection<Holder<EntityType<?>>> tags = SRDataReloadListener.reloadContext.getTag(tagKey);
        for (Holder<EntityType<?>> holder : tags) {
            if (holder.value().equals(entity.getType()))
                return true;
        }
        return false;
    }

    public static class JsonConfig<T> {
        String fileName;
        List<T> list;
        List<T> defaultList;
        Type listType;
        @Nullable
        BiConsumer<List<T>, Boolean> onLoad;
        boolean syncToClient;
        @Nullable
        JsonConfigSyncMessage.ConfigType configType;

        public JsonConfig(String fileName, List<T> list, List<T> defaultList, Type listType, BiConsumer<List<T>, Boolean> onLoad, boolean syncToClient, JsonConfigSyncMessage.ConfigType configType) {
            this.fileName = fileName;
            this.list = list;
            this.defaultList = defaultList;
            this.listType = listType;
            this.onLoad = onLoad;
            this.syncToClient = syncToClient;
            this.configType = configType;
        }

        public JsonConfig(String fileName, List<T> list, List<T> defaultList, Type listType, boolean syncToClient, JsonConfigSyncMessage.ConfigType configType) {
            this(fileName, list, defaultList, listType, null, syncToClient, configType);
        }

        public JsonConfig(String fileName, List<T> list, List<T> defaultList, Type listType, BiConsumer<List<T>, Boolean> onLoad) {
            this(fileName, list, defaultList, listType, onLoad, false, null);
        }

        public JsonConfig(String fileName, List<T> list, List<T> defaultList, Type listType) {
            this(fileName, list, defaultList, listType, false, null);
        }

        protected void loadAndReadFile(File folder) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            File file = new File(folder, this.fileName);
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

            if (this.onLoad != null)
                this.onLoad.accept(list, false);
        }

        public void syncToClient(OnDatapackSyncEvent event) {
            if (!this.syncToClient)
                return;

            Gson gson = new GsonBuilder().create();

            if (event.getPlayer() == null) {
                event.getPlayerList().getPlayers().forEach(player -> {
                    JsonConfigSyncMessage.sync(this.configType, gson.toJson(this.list, this.listType), player);
                });
            }
            else {
                JsonConfigSyncMessage.sync(this.configType, gson.toJson(this.list, this.listType), event.getPlayer());
            }
        }
    }

    @SubscribeEvent
    public void onDataPackSync(OnDatapackSyncEvent event) {
        for (JsonConfig<?> jsonConfig : JSON_CONFIGS) {
            jsonConfig.syncToClient(event);
        }
    }
}
