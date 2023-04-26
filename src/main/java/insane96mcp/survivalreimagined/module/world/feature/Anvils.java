package insane96mcp.survivalreimagined.module.world.feature;

import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.insanelib.util.LogHelper;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.event.FallingBlockLandEvent;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.world.data.AnvilTransformation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Anvils", description = "Make anvils usable to create blocks. Use the anvil_transformations.json file in the feature's folder to change or add block transformations.")
@LoadFeature(module = Modules.Ids.WORLD)
public class Anvils extends SRFeature {

    public static final ArrayList<AnvilTransformation> ANVIL_TRANSFORMATIONS_DEFAULT = new ArrayList<>(List.of(
            new AnvilTransformation(IdTagMatcher.Type.ID, "minecraft:stone", "minecraft:cobblestone"),
        new AnvilTransformation(IdTagMatcher.Type.ID, "minecraft:cobblestone", "minecraft:gravel"),
            new AnvilTransformation(IdTagMatcher.Type.ID, "minecraft:gravel", "minecraft:sand"),
            new AnvilTransformation(IdTagMatcher.Type.ID, "minecraft:sandstone", "minecraft:sand")
        //new AnvilTranformation(IdTagMatcher.Type.ID, "minecraft:sand", "minecraft:dust")
    ));
    public static final ArrayList<AnvilTransformation> anvilTransformations = new ArrayList<>();

    public Anvils(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
        JSON_CONFIGS.add(new JsonConfig<>("anvil_transformations.json", anvilTransformations, ANVIL_TRANSFORMATIONS_DEFAULT, AnvilTransformation.LIST_TYPE));
    }

    @SubscribeEvent
    public void onAnvilLand(FallingBlockLandEvent event) {
        if (!this.isEnabled()
            || !event.getFallingBlock().blockState.is(BlockTags.ANVIL)
            || event.getFallingBlock().time < 7)
            return;

        for (AnvilTransformation anvilTransformation : anvilTransformations) {
            if (anvilTransformation.matchesBlock(event.getFallingBlock().getBlockStateOn().getBlock())) {
                Block block = ForgeRegistries.BLOCKS.getValue(anvilTransformation.to);
                if (block == null) {
                    LogHelper.warn("[Anvil Transformation] %s is not a valid block".formatted(anvilTransformation.to));
                }
                event.getFallingBlock().level.setBlock(event.getFallingBlock().getOnPos(), block.defaultBlockState(), 3);
                break;
            }
        }
    }
}
