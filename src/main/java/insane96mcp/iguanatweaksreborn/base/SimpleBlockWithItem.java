package insane96mcp.iguanatweaksreborn.base;


import insane96mcp.iguanatweaksreborn.setup.SRRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public record SimpleBlockWithItem(RegistryObject<Block> block, RegistryObject<BlockItem> item) {
    public static SimpleBlockWithItem register(String id, Supplier<Block> blockSupplier) {
        return register(id, blockSupplier, new Item.Properties());
    }

    public static SimpleBlockWithItem register(String id, Supplier<Block> blockSupplier, Item.Properties itemProperties) {
        RegistryObject<Block> block = SRRegistries.BLOCKS.register(id, blockSupplier);
        RegistryObject<BlockItem> item = SRRegistries.ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        return new SimpleBlockWithItem(block, item);
    }
}
