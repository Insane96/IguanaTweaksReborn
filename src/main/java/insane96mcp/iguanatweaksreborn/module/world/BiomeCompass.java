package insane96mcp.iguanatweaksreborn.module.world;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.setup.ITRRegistries;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.LoadFeature;
import insane96mcp.insanelib.base.Module;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

@Label(name = "Biome Compass")
@LoadFeature(module = Modules.Ids.WORLD, canBeDisabled = false)
public class BiomeCompass extends Feature {
    public static final RegistryObject<Item> COMPASS = ITRRegistries.ITEMS.register("biome_compass", () -> new BiomeCompassItem(new Item.Properties()));

    public BiomeCompass(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    public static class BiomeCompassItem extends Item {
        public BiomeCompassItem(Properties pProperties) {
            super(pProperties);
        }

        public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
            if (isSelected && entity instanceof Player player) {
                Holder<Biome> biome = level.getBiome(player.blockPosition());
                Optional<Registry<Biome>> registry = level.registryAccess().registry(Registries.BIOME);
                registry.ifPresent(biomes -> {
                    String name = biomes.getKey(biome.value()).toString();
                    name = name.replace(':', '.');
                    player.displayClientMessage(Component.translatable("biome." + name), true);
                });
            }
        }
    }
}