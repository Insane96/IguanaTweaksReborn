package insane96mcp.survivalreimagined.base;


import insane96mcp.survivalreimagined.setup.SRBlocks;
import insane96mcp.survivalreimagined.setup.SRItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public record BlockWithItem(RegistryObject<Block> block, RegistryObject<BlockItem> item) {
    public static BlockWithItem register(String id, Supplier<Block> blockSupplier) {
        return register(id, blockSupplier, new Item.Properties());
    }

    public static BlockWithItem register(String id, Supplier<Block> blockSupplier, Item.Properties itemProperties) {
        RegistryObject<Block> block = SRBlocks.REGISTRY.register(id, blockSupplier);
        RegistryObject<BlockItem> item = SRItems.REGISTRY.register(id, () -> new BlockItem(block.get(), itemProperties));
        return new BlockWithItem(block, item);
    }
}
