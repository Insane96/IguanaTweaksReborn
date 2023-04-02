package insane96mcp.survivalreimagined.setup;

import insane96mcp.insanelib.util.ILMobEffect;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SRMobEffects {
	public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, SurvivalReimagined.MOD_ID);

	public static final RegistryObject<MobEffect> INJURED = MOB_EFFECTS.register("injured", () -> new ILMobEffect(MobEffectCategory.HARMFUL, 0xbb0a1e, false));
	public static final RegistryObject<MobEffect> WELL_FED = MOB_EFFECTS.register("well_fed", () -> new ILMobEffect(MobEffectCategory.BENEFICIAL, 0x632e00, false));
	public static final RegistryObject<MobEffect> TIRED = MOB_EFFECTS.register("tired", () -> new ILMobEffect(MobEffectCategory.HARMFUL, 0x818894, false).addAttributeModifier(Attributes.MOVEMENT_SPEED, "697c48dd-6bbd-4082-8501-040bb9812c09", -0.06F, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(Attributes.ATTACK_SPEED, "40c789ef-d30d-4a27-8f46-13fe0edbb259", -0.05F, AttributeModifier.Operation.MULTIPLY_TOTAL));
	public static final RegistryObject<MobEffect> ENERGY_BOOST = MOB_EFFECTS.register("energy_boost", () -> new ILMobEffect(MobEffectCategory.BENEFICIAL, 0x857965, true));
	public static final RegistryObject<MobEffect> VIGOUR = MOB_EFFECTS.register("vigour", () -> new ILMobEffect(MobEffectCategory.BENEFICIAL, 0xFCD373, false));
}
