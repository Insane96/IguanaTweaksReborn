package insane96mcp.survivalreimagined.module.world.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.world.block.OreRockBlock;
import insane96mcp.survivalreimagined.setup.SRBlocks;
import insane96mcp.survivalreimagined.setup.SRItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Beeg Veins", description = "Add Beeg Veins to the world")
@LoadFeature(module = Modules.Ids.WORLD)
public class BeegVeins extends Feature {

    public static final RegistryObject<OreRockBlock> IRON_ORE_ROCK = SRBlocks.REGISTRY.register("iron_ore_rock", () -> new OreRockBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).strength(0.5F, 2.0F).offsetType(BlockBehaviour.OffsetType.XZ).dynamicShape()));
    public static final RegistryObject<BlockItem> IRON_ORE_ROCK_ITEM = SRItems.REGISTRY.register("iron_ore_rock", () -> new BlockItem(IRON_ORE_ROCK.get(), new Item.Properties()));
    public static final RegistryObject<OreRockBlock> COPPER_ORE_ROCK = SRBlocks.REGISTRY.register("copper_ore_rock", () -> new OreRockBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).strength(0.5F, 2.0F).offsetType(BlockBehaviour.OffsetType.XZ).dynamicShape()));
    public static final RegistryObject<BlockItem> COPPER_ORE_ROCK_ITEM = SRItems.REGISTRY.register("copper_ore_rock", () -> new BlockItem(COPPER_ORE_ROCK.get(), new Item.Properties()));
    public static final RegistryObject<OreRockBlock> GOLD_ORE_ROCK = SRBlocks.REGISTRY.register("gold_ore_rock", () -> new OreRockBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).strength(0.5F, 2.0F).offsetType(BlockBehaviour.OffsetType.XZ).dynamicShape()));
    public static final RegistryObject<BlockItem> GOLD_ORE_ROCK_ITEM = SRItems.REGISTRY.register("gold_ore_rock", () -> new BlockItem(GOLD_ORE_ROCK.get(), new Item.Properties()));

    public BeegVeins(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }
}
