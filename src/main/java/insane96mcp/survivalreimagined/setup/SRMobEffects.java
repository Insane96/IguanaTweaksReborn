package insane96mcp.survivalreimagined.setup;

import insane96mcp.insanelib.util.ILMobEffect;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SRMobEffects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, SurvivalReimagined.MOD_ID);

	public static final RegistryObject<MobEffect> INJURED = REGISTRY.register("injured", () -> new ILMobEffect(MobEffectCategory.HARMFUL, 0xbb0a1e, false));
	public static final RegistryObject<MobEffect> WELL_FED = REGISTRY.register("well_fed", () -> new ILMobEffect(MobEffectCategory.BENEFICIAL, 0x632e00, false));
	public static final RegistryObject<MobEffect> VIGOUR = REGISTRY.register("vigour", () -> new ILMobEffect(MobEffectCategory.BENEFICIAL, 0xFCD373, false));
}
