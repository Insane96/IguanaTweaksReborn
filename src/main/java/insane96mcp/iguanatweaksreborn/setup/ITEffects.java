package insane96mcp.iguanatweaksreborn.setup;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.effect.ITMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ITEffects {
	public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, IguanaTweaksReborn.MOD_ID);

	public static final RegistryObject<MobEffect> INJURED = EFFECTS.register("injured", () -> new ITMobEffect(MobEffectCategory.HARMFUL, 0xbb0a1e, false));
	public static final RegistryObject<MobEffect> WELL_FED = EFFECTS.register("well_fed", () -> new ITMobEffect(MobEffectCategory.BENEFICIAL, 0x632e00, false));
}
