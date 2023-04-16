package insane96mcp.survivalreimagined.setup;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SRSoundEvents {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, SurvivalReimagined.MOD_ID);

	public static final RegistryObject<SoundEvent> INJURED = REGISTRY.register("injured", () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(SurvivalReimagined.MOD_ID, "injured"), 16f));
}
