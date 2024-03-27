package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.misc.DataPacks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LevelStorageSource.class)
public class LevelStorageSourceMixin {

	@ModifyVariable(method = "getDataConfiguration", at = @At(value = "LOAD"))
	private static CompoundTag onReadLevelTag(CompoundTag tag) {
		return DataPacks.forceReloadWorldDataPacks(tag);
	}
}
