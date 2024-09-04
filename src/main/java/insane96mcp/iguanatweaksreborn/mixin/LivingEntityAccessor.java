package insane96mcp.iguanatweaksreborn.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Invoker("getSoundVolume")
    float soundVolume();
    @Invoker("getVoicePitch")
    float voicePitch();
}
