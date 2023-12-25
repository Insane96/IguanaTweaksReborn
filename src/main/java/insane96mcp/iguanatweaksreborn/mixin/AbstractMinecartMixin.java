package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.movement.minecarts.SRPoweredRail;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeAbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin extends Entity implements IForgeAbstractMinecart {

    public AbstractMinecartMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @ModifyConstant(method = "moveAlongTrack", constant = @Constant(doubleValue = 0.06d))
    private double preventAcceleration(double acceleration, BlockPos pos, BlockState state) {
        BaseRailBlock baserailblock = (BaseRailBlock) state.getBlock();
        float railMaxSpeed = baserailblock.getRailMaxSpeed(state, this.level(), pos, (AbstractMinecart) (Object) this);
        if (this.getDeltaMovement().horizontalDistance() >= railMaxSpeed)
            return 0f;
        else if (baserailblock instanceof SRPoweredRail srPoweredRail)
            return srPoweredRail.getRailAcceleration(state, this.level(), pos, (AbstractMinecart) (Object) this);
        return acceleration;
    }

    @ModifyVariable(method = "getMaxSpeedWithRail", at = @At(value = "STORE"), ordinal = 0, remap = false)
    public float railMaxSpeed(float maxSpeed) {
        BlockPos pos = this.getCurrentRailPosition();
        BlockState state = this.level().getBlockState(pos);
        if (state.is(Blocks.RAIL))
            return 0.7f;
        return maxSpeed;
    }
}
