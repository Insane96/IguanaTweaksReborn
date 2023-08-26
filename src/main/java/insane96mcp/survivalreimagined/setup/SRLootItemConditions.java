package insane96mcp.survivalreimagined.setup;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.data.condition.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SRLootItemConditions {
    public static final DeferredRegister<LootItemConditionType> REGISTRY = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE.location(), SurvivalReimagined.MOD_ID);

    public static final RegistryObject<LootItemConditionType> CURRENT_SEASON = REGISTRY.register("current_season", () -> new LootItemConditionType(new LootItemCurrentSeasonCondition.Serializer()));
    public static final RegistryObject<LootItemConditionType> NON_PLAYER_ARISED_DROP = REGISTRY.register("non_player_arised_drop", () -> new LootItemConditionType(new NonPlayerArisedDropCondition.Serializer()));
    public static final RegistryObject<LootItemConditionType> LIVING_ENTITY = REGISTRY.register("living_entity", () -> new LootItemConditionType(new LivingEntityCondition.Serializer()));
    public static final RegistryObject<LootItemConditionType> KILLER_HAS_ADVANCEMENT = REGISTRY.register("killer_has_advancement", () -> new LootItemConditionType(new KillerHasAdvancementCondition.Serializer()));
    public static final RegistryObject<LootItemConditionType> BLOCK_TAG_MATCH = REGISTRY.register("block_tag_match", () -> new LootItemConditionType(new BlockTagCondition.Serializer()));
}
