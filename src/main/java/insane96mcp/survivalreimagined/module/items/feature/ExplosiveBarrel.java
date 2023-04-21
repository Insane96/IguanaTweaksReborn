package insane96mcp.survivalreimagined.module.items.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.items.block.ExplosiveBarrelBlock;
import insane96mcp.survivalreimagined.setup.SRBlocks;
import insane96mcp.survivalreimagined.setup.SRItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Explosive Barrel", description = "Add a new explosive block.")
@LoadFeature(module = Modules.Ids.ITEMS)
public class ExplosiveBarrel extends Feature {
	public static final RegistryObject<Block> BLOCK = SRBlocks.REGISTRY.register("explosive_barrel", () -> new ExplosiveBarrelBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)));
	public static final RegistryObject<BlockItem> BLOCK_ITEM = SRItems.REGISTRY.register("explosive_barrel", () -> new BlockItem(BLOCK.get(), new Item.Properties()));

	public ExplosiveBarrel(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}
}