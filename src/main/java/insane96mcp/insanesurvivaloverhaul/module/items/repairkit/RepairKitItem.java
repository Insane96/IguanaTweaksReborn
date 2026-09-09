package insane96mcp.insanesurvivaloverhaul.module.items.repairkit;

import insane96mcp.insanesurvivaloverhaul.setup.ISORegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RepairKitItem extends Item {
    public RepairKitItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        String material = stack.get(ISORegistries.REPAIR_KIT_MATERIAL.get());
        if (material == null)
            return super.getName(stack);

        Component materialName;
        if (material.startsWith("#")) {
            // Tag material (e.g. "#minecraft:planks"): use the tag's own name rather than one of the items it
            // covers, since a single kit can be valid for repairing with any of them.
            ResourceLocation tagId = ResourceLocation.parse(material.substring(1));
            materialName = Component.literal(titleCase(tagId.getPath()));
        } else {
            Item materialItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(material));
            materialName = Component.translatable(materialItem.getDescriptionId());
        }
        return Component.translatable(this.getDescriptionId(stack) + ".material", materialName);
    }

    /**
     * {@code "planks"} -> {@code "Planks"}, {@code "test_tag"} -> {@code "Test Tag"}.
     */
    private static String titleCase(String path) {
        String[] words = path.replace('/', ' ').replace('_', ' ').trim().split("\\s+");
        return Arrays.stream(words)
                .filter(word -> !word.isEmpty())
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }
}
