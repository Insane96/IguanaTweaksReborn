package insane96mcp.iguanatweaksreborn.setup;

import com.mojang.serialization.Codec;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.data.lootmodifier.DropMultiplierModifier;
import insane96mcp.iguanatweaksreborn.data.lootmodifier.ReplaceDropModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ITGlobalLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, IguanaTweaksReborn.MOD_ID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> REPLACE_DROP_MODIFIER = LOOT_MODIFIERS.register("replace_drop", ReplaceDropModifier.CODEC);
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> DROP_MULTIPLIER_MODIFIER = LOOT_MODIFIERS.register("drop_multiplier", DropMultiplierModifier.CODEC);
}
