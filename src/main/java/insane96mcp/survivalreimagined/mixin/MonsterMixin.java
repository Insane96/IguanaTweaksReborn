package insane96mcp.survivalreimagined.mixin;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.survivalreimagined.module.misc.feature.Spawners;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Monster.class)
public abstract class MonsterMixin {

    @Inject(at = @At("HEAD"), method = "checkMonsterSpawnRules", cancellable = true)
    private static void onMonsterCheckSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos pos, RandomSource random, CallbackInfoReturnable<Boolean> cir) {
        if(mobSpawnType.equals(MobSpawnType.SPAWNER) && Feature.isEnabled(Spawners.class) && Spawners.ignoreLight) {
            cir.setReturnValue(Monster.checkAnyLightMonsterSpawnRules(entityType, levelAccessor, mobSpawnType, pos, random));
        }
    }
}
