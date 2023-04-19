package insane96mcp.survivalreimagined.setup;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.world.levelgen.RandomBlockTagMatchTest;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SRRuleTestType {
    public static final DeferredRegister<RuleTestType<?>> REGISTRY = DeferredRegister.create(Registries.RULE_TEST, SurvivalReimagined.MOD_ID);

    public static final RegistryObject<RuleTestType<RandomBlockTagMatchTest>> RANDOM_BLOCK_TAG_MATCH = REGISTRY.register("random_block_tag_match", RandomBlockTagMatchTest.Type::new);

}
