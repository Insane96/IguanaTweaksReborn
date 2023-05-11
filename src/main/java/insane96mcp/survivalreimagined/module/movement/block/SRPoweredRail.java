package insane96mcp.survivalreimagined.module.movement.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SRPoweredRail extends PoweredRailBlock {
    final float baseSpeed;
    public SRPoweredRail(Properties properties, float baseSpeed) {
        super(properties, true);
        this.baseSpeed = baseSpeed;
    }

    @Override
    public float getRailMaxSpeed(BlockState state, Level level, BlockPos pos, AbstractMinecart cart) {
        if (cart instanceof MinecartFurnace) return cart.isInWater() ? this.baseSpeed * 0.375f : this.baseSpeed * 0.5f;
        else return cart.isInWater() ? this.baseSpeed * 0.5f : this.baseSpeed;
    }
}