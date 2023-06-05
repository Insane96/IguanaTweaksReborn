package insane96mcp.survivalreimagined.module.world.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.SimpleBlockWithItem;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.feature.DataPacks;
import insane96mcp.survivalreimagined.module.world.block.GroundRockBlock;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.List;

@Label(name = "Ore Generation", description = "Changes ore generation of many ores. Also adds poor and rich Iron, Gold and Copper")
@LoadFeature(module = Modules.Ids.WORLD)
public class OreGeneration extends Feature {

    public static final SimpleBlockWithItem IRON_ORE_ROCK = SimpleBlockWithItem.register("iron_ore_rock", () -> new GroundRockBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).strength(0.5F, 2.0F).offsetType(BlockBehaviour.OffsetType.XZ).dynamicShape()));
    public static final SimpleBlockWithItem GOLD_ORE_ROCK = SimpleBlockWithItem.register("gold_ore_rock", () -> new GroundRockBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).strength(0.5F, 2.0F).offsetType(BlockBehaviour.OffsetType.XZ).dynamicShape()));
    public static final SimpleBlockWithItem COPPER_ORE_ROCK = SimpleBlockWithItem.register("copper_ore_rock", () -> new GroundRockBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).strength(0.5F, 2.0F).offsetType(BlockBehaviour.OffsetType.XZ).dynamicShape()));

    public static final PoorRichOre POOR_RICH_IRON_ORE = PoorRichOre.register("iron", Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE);
    public static final PoorRichOre POOR_RICH_COPPER_ORE = PoorRichOre.register("copper", Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE);
    public static final PoorRichOre POOR_RICH_GOLD_ORE = PoorRichOre.register("gold", Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE);

    @Config
    @Label(name = "Poor and Rich Iron Generation", description = "Enables a Data Pack that changes Iron generation, including Poor and Rich Ores.")
    public static Boolean ironGenerationDataPack = true;
    @Config
    @Label(name = "Poor and Rich Gold Generation", description = "Enables a Data Pack that changes Gold generation, including Poor and Rich Ores.")
    public static Boolean goldGenerationDataPack = true;
    @Config
    @Label(name = "Poor and Rich Copper Generation", description = "Enables a Data Pack that changes Copper generation, including Poor and Rich Ores.")
    public static Boolean copperGenerationDataPack = true;

    public OreGeneration(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "iron_generation", Component.literal("Survival Reimagined Iron Generation"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && ironGenerationDataPack));
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "gold_generation", Component.literal("Survival Reimagined Gold Generation"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && goldGenerationDataPack));
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "copper_generation", Component.literal("Survival Reimagined Copper Generation"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && copperGenerationDataPack));
    }

    public record PoorRichOre(SimpleBlockWithItem poorOre, SimpleBlockWithItem poorDeepslateOre, SimpleBlockWithItem richOre, SimpleBlockWithItem richDeepslateOre) {
        public static PoorRichOre register(String oreName, Block vanillaOre, Block vanillaDeepslateOre) {
            SimpleBlockWithItem poorOre = SimpleBlockWithItem.register("poor_%s_ore".formatted(oreName), () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(vanillaOre).strength(2f, 4.5f)));
            SimpleBlockWithItem richOre = SimpleBlockWithItem.register("rich_%s_ore".formatted(oreName), () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(vanillaOre).strength(4f, 2f)));
            SimpleBlockWithItem poorDeepslateOre = SimpleBlockWithItem.register("poor_deepslate_%s_ore".formatted(oreName), () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(vanillaDeepslateOre).strength(3f, 4.5f)));
            SimpleBlockWithItem richDeepslateOre = SimpleBlockWithItem.register("rich_deepslate_%s_ore".formatted(oreName), () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(vanillaDeepslateOre).strength(6f, 2f)));
            return new PoorRichOre(poorOre, poorDeepslateOre, richOre, richDeepslateOre);
        }

        public List<Item> getAllItems() {
            return List.of(this.poorOre.item().get(), this.poorDeepslateOre.item().get(), this.richOre.item().get(), this.richDeepslateOre.item().get());
        }
    }
}
