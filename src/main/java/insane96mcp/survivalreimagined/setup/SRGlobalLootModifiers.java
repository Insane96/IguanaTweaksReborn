package insane96mcp.survivalreimagined.setup;

import com.mojang.serialization.Codec;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.data.lootmodifier.DropMultiplierModifier;
import insane96mcp.survivalreimagined.data.lootmodifier.ReplaceDropModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SRGlobalLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, SurvivalReimagined.MOD_ID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> REPLACE_DROP_MODIFIER = LOOT_MODIFIERS.register("replace_drop", ReplaceDropModifier.CODEC);
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> DROP_MULTIPLIER_MODIFIER = LOOT_MODIFIERS.register("drop_multiplier", DropMultiplierModifier.CODEC);
}
