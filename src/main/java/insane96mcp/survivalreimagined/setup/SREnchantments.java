package insane96mcp.survivalreimagined.setup;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.experience.enchantments.enchantment.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SREnchantments {
    public static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, SurvivalReimagined.MOD_ID);

    public static final RegistryObject<Enchantment> MAGNETIC = REGISTRY.register("magnetic", Magnetic::new);
    public static final RegistryObject<Enchantment> MAGIC_PROTECTION = REGISTRY.register("magic_protection", MagicProtection::new);
    public static final RegistryObject<Enchantment> MELEE_PROTECTION = REGISTRY.register("melee_protection", MeleeProtection::new);
    public static final RegistryObject<Enchantment> BLASTING = REGISTRY.register("blasting", Blasting::new);
    public static final RegistryObject<Enchantment> EXPANDED = REGISTRY.register("expanded", Expanded::new);
    public static final RegistryObject<Enchantment> STEP_UP = REGISTRY.register("step_up", StepUp::new);
    public static final RegistryObject<Enchantment> BANE_OF_SSSSS = REGISTRY.register("bane_of_sssss", BaneOfSSSS::new);
    public static final RegistryObject<Enchantment> WATER_COOLANT = REGISTRY.register("water_coolant", WaterCoolant::new);
    public static final RegistryObject<Enchantment> SMARTNESS = REGISTRY.register("smartness", Smartness::new);
    public static final RegistryObject<Enchantment> MA_JUMP = REGISTRY.register("ma_jump", DoubleJump::new);
    public static final RegistryObject<Enchantment> GRAVITY_DEFYING = REGISTRY.register("gravity_defying", GravityDefying::new);
    public static final RegistryObject<Enchantment> CRITICAL = REGISTRY.register("critical", Critical::new);
    public static final RegistryObject<Enchantment> HEALTHY = REGISTRY.register("healthy", Healthy::new);
}
