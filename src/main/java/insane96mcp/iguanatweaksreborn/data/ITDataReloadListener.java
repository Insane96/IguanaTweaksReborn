package insane96mcp.iguanatweaksreborn.data;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.util.FileUtils;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.FileReader;
import java.util.ArrayList;

public class ITDataReloadListener extends SimplePreparableReloadListener<Void> {
    @Override
    protected @NotNull Void prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        return null;
    }

    @Override
    protected void apply(@NotNull Void v, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        LogHelper.info("Reloading Data");

        Stats.loadJson

        Gson gson = new Gson();

        ArrayList<File> jsonFiles = FileUtils.ListFilesForFolder(groupsFolder);

        for (File file : jsonFiles) {
            //Ignore files that start with underscore '_' or comma '.'
            if (file.getName().startsWith("_") || file.getName().startsWith("."))
                continue;

            try {
                FileReader fileReader = new FileReader(file);
                MPRGroup group = gson.fromJson(fileReader, MPRGroup.class);
                group.name = FilenameUtils.removeExtension(file.getName());
                group.validate();
                MPR_GROUPS.add(group);
            }
            catch (JsonValidationException e) {
                Logger.error("Validation error loading Group %s: %s", FilenameUtils.removeExtension(file.getName()), e.getMessage());
            }
            catch (JsonSyntaxException e) {
                Logger.error("Parsing error loading Group %s: %s", FilenameUtils.removeExtension(file.getName()), e.getMessage());
            }
            catch (Exception e) {
                Logger.error("Failed loading Group %s: %s", FilenameUtils.removeExtension(file.getName()), e.getMessage());
            }
        }

        Logger.info("Loaded %s Groups", MPR_GROUPS.size());
    }
}
