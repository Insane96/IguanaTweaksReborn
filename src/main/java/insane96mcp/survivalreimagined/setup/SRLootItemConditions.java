package insane96mcp.survivalreimagined.setup;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.data.condition.LootItemCurrentSeasonCondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SRLootItemConditions {
    public static final DeferredRegister<LootItemConditionType> LOOT_ITEM_CONDITION_TYPES = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE.location(), SurvivalReimagined.MOD_ID);

    public static final RegistryObject<LootItemConditionType> CURRENT_SEASON = LOOT_ITEM_CONDITION_TYPES.register("current_season", () -> new LootItemConditionType(new LootItemCurrentSeasonCondition.Serializer()));
}
