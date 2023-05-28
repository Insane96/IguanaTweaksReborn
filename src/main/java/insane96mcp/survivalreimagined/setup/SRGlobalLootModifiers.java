package insane96mcp.survivalreimagined.setup;

import com.mojang.serialization.Codec;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.data.lootmodifier.DropMultiplierModifier;
import insane96mcp.survivalreimagined.data.lootmodifier.InjectLootTableModifier;
import insane96mcp.survivalreimagined.data.lootmodifier.LootPurgerModifier;
import insane96mcp.survivalreimagined.data.lootmodifier.ReplaceLootModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SRGlobalLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> REGISTRY = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, SurvivalReimagined.MOD_ID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> REPLACE_DROP_MODIFIER = REGISTRY.register("replace_drop", ReplaceLootModifier.CODEC);
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> DROP_MULTIPLIER_MODIFIER = REGISTRY.register("drop_multiplier", DropMultiplierModifier.CODEC);
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> LOOT_PURGER_MODIFIER = REGISTRY.register("loot_purger", LootPurgerModifier.CODEC);
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> INJECT_LOOT_MODIFIER = REGISTRY.register("inject_loot_table", InjectLootTableModifier.CODEC);
}
