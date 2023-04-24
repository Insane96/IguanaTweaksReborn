package insane96mcp.survivalreimagined.setup;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.world.entity.PilableFallingLayerEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SREntityTypes {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SurvivalReimagined.MOD_ID);

    public static final RegistryObject<EntityType<PilableFallingLayerEntity>> PILABLE_FALLING_LAYER = REGISTRY.register("pilable_falling_layer", () -> EntityType.Builder.<PilableFallingLayerEntity>of(PilableFallingLayerEntity::new, MobCategory.MISC)
            .sized(0.98f, 0.98f)
            .clientTrackingRange(10)
            .updateInterval(20)
            .build("pilable_falling_layer"));
}
