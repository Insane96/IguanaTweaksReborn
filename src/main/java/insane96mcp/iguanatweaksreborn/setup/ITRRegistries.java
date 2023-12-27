package insane96mcp.iguanatweaksreborn.setup;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

public class ITRRegistries {
	public static final List<DeferredRegister<?>> REGISTRIES = new ArrayList<>();

	public static final DeferredRegister<Attribute> ATTRIBUTES = createRegistry(ForgeRegistries.ATTRIBUTES);
	public static final DeferredRegister<Enchantment> ENCHANTMENTS = createRegistry(ForgeRegistries.ENCHANTMENTS);

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
