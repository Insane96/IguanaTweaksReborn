package insane96mcp.iguanatweaksreborn.setup;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.effect.ITMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ITMobEffects {
	public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, IguanaTweaksReborn.MOD_ID);

	public static final RegistryObject<MobEffect> INJURED = MOB_EFFECTS.register("injured", () -> new ITMobEffect(MobEffectCategory.HARMFUL, 0xbb0a1e, false));
	public static final RegistryObject<MobEffect> WELL_FED = MOB_EFFECTS.register("well_fed", () -> new ITMobEffect(MobEffectCategory.BENEFICIAL, 0x632e00, false));
	public static final RegistryObject<MobEffect> TIRED = MOB_EFFECTS.register("tired", () -> new ITMobEffect(MobEffectCategory.HARMFUL, 0x818894, false).addAttributeModifier(Attributes.MOVEMENT_SPEED, "697c48dd-6bbd-4082-8501-040bb9812c09", -0.08F, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(Attributes.ATTACK_SPEED, "40c789ef-d30d-4a27-8f46-13fe0edbb259", -0.05F, AttributeModifier.Operation.MULTIPLY_TOTAL));
}
