package insane96mcp.iguanatweaksreborn.setup;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class ITRRegistries {
	public static final List<DeferredRegister<?>> REGISTRIES = new ArrayList<>();

	public static final DeferredRegister<Attribute> ATTRIBUTES = createRegistry(ForgeRegistries.ATTRIBUTES);
	public static final DeferredRegister<Enchantment> ENCHANTMENTS = createRegistry(ForgeRegistries.ENCHANTMENTS);
	public static final DeferredRegister<MobEffect> MOB_EFFECTS = createRegistry(ForgeRegistries.MOB_EFFECTS);
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = createRegistry(ForgeRegistries.SOUND_EVENTS);
	public static final RegistryObject<SoundEvent> UNFAIR_ONE_SHOT = SOUND_EVENTS.register("unfair_one_shot", () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "unfair_one_shot"), 16f));

	static <R> DeferredRegister<R> createRegistry(ResourceKey<? extends Registry<R>> key) {
		DeferredRegister<R> register = DeferredRegister.create(key, IguanaTweaksReborn.MOD_ID);
		REGISTRIES.add(register);
		return register;
	}

	static <R> DeferredRegister<R> createRegistry(IForgeRegistry<R> reg) {
		DeferredRegister<R> register = DeferredRegister.create(reg, IguanaTweaksReborn.MOD_ID);
		REGISTRIES.add(register);
		return register;
	}

	static <R> DeferredRegister<R> createRegistry(ResourceLocation registryName) {
		DeferredRegister<R> register = DeferredRegister.create(registryName, IguanaTweaksReborn.MOD_ID);
		REGISTRIES.add(register);
		return register;
	}
}
