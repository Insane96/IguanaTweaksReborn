package insane96mcp.iguanatweaksreborn.setup;

import com.mojang.serialization.Codec;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ITGlobalLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, IguanaTweaksReborn.MOD_ID);
}
