package insane96mcp.survivalreimagined.setup;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.experience.enchantment.Blasting;
import insane96mcp.survivalreimagined.module.experience.enchantment.Expanded;
import insane96mcp.survivalreimagined.module.experience.enchantment.MagicProtection;
import insane96mcp.survivalreimagined.module.experience.enchantment.Magnetic;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SREnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, SurvivalReimagined.MOD_ID);

    public static final RegistryObject<Enchantment> MAGNETIC = ENCHANTMENTS.register("magnetic", Magnetic::new);
    public static final RegistryObject<Enchantment> MAGIC_PROTECTION = ENCHANTMENTS.register("magic_protection", MagicProtection::new);
    public static final RegistryObject<Enchantment> BLASTING = ENCHANTMENTS.register("blasting", Blasting::new);
    public static final RegistryObject<Enchantment> EXPANDED = ENCHANTMENTS.register("expanded", Expanded::new);
}
