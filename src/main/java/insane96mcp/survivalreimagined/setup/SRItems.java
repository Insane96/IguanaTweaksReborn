package insane96mcp.survivalreimagined.setup;

import insane96mcp.shieldsplus.setup.SPItems;
import insane96mcp.shieldsplus.world.item.SPShieldItem;
import insane96mcp.shieldsplus.world.item.SPShieldMaterial;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SRItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, SurvivalReimagined.MOD_ID);

    public static RegistryObject<SPShieldItem> registerShield(String id, SPShieldMaterial material) {
        return registerShield(id, material, false);
    }

    public static RegistryObject<SPShieldItem> registerShield(String id, SPShieldMaterial material, boolean fireResistant) {
        Item.Properties properties = new Item.Properties().durability(material.durability).rarity(material.rarity);
        RegistryObject<SPShieldItem> shield = SRItems.REGISTRY.register(id, () -> new SPShieldItem(material, properties));
        SPItems.SHIELDS.add(shield);
        return shield;
    }
}
