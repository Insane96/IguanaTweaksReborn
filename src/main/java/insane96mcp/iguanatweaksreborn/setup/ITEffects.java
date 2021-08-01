package insane96mcp.iguanatweaksreborn.setup;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.potion.ITEffect;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ITEffects {

	public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, IguanaTweaksReborn.MOD_ID);

	public static final RegistryObject<Effect> INJURED = EFFECTS.register("injured", () -> new ITEffect(EffectType.HARMFUL, 0xbb0a1e, false));
	public static final RegistryObject<Effect> WELL_FED = EFFECTS.register("well_fed", () -> new ITEffect(EffectType.BENEFICIAL, 0x632e00, false));
}
