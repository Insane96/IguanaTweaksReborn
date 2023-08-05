package insane96mcp.survivalreimagined.module.experience.feature.enchantingfeature;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class EnsorcellerBlockEntity extends BlockEntity implements Nameable {
    private Component name;

    public EnsorcellerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(EnchantingFeature.ENSORCELLER_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (this.hasCustomName()) {
            pTag.putString("CustomName", Component.Serializer.toJson(this.name));
        }

    }

    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("CustomName", 8)) {
            this.name = Component.Serializer.fromJson(pTag.getString("CustomName"));
        }

    }

    public Component getName() {
        return (this.name != null ? this.name : Component.translatable("container.enchant"));
    }

    public void setCustomName(@Nullable Component pName) {
        this.name = pName;
    }

    @Nullable
    public Component getCustomName() {
        return this.name;
    }
}
