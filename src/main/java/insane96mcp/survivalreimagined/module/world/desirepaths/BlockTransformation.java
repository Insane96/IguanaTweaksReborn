package insane96mcp.survivalreimagined.module.world.desirepaths;

import insane96mcp.insanelib.data.IdTagMatcher;
import insane96mcp.insanelib.util.LogHelper;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockTransformation {
    IdTagMatcher blockToTransform;
    public ResourceLocation transformTo;

    public BlockTransformation(IdTagMatcher blockToTransform) {
        this.blockToTransform = blockToTransform;
    }

    public static ArrayList<BlockTransformation> parseStringList(List<? extends String> list) {
        ArrayList<BlockTransformation> blockTransformations = new ArrayList<>();
        for (String line : list) {
            BlockTransformation blockTransformation = BlockTransformation.parseLine(line);
            if (blockTransformation != null)
                blockTransformations.add(blockTransformation);
        }
        return blockTransformations;
    }

    /**
     * Returns null if it can't parse the line
     */
    @Nullable
    public static BlockTransformation parseLine(String line) {
        String[] split = line.split(",");
        if (split.length != 2) {
            LogHelper.warn("Invalid line \"%s\". Format must be modid:block_or_tag,modid:block", line);
            return null;
        }
        BlockTransformation blockTransformation;
        if (split[0].startsWith("#")) {
            ResourceLocation tag = ResourceLocation.tryParse(split[0].substring(1));
            if (tag == null) {
                LogHelper.warn("Tag is not valid. '%s'", line);
                return null;
            }
            blockTransformation = new BlockTransformation(IdTagMatcher.newTag(split[0].substring(1)));
        }
        else {
            ResourceLocation id = ResourceLocation.tryParse(split[0]);
            if (id == null) {
                LogHelper.warn("Id is not valid. '%s'", line);
                return null;
            }
            blockTransformation = new BlockTransformation(IdTagMatcher.newId(split[0]));
        }

        ResourceLocation id = ResourceLocation.tryParse(split[1]);
        if (id == null) {
            LogHelper.warn("id for transform to is not valid. '%s'", line);
            return null;
        }
        blockTransformation.transformTo = id;

        return blockTransformation;
    }
}
