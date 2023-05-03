package insane96mcp.survivalreimagined.module.movement.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;

public class NetherInfusedRailBlock extends PoweredRailBlock {
    public NetherInfusedRailBlock(Properties properties) {
        super(properties, true);
    }

    @Override
    public float getRailMaxSpeed(BlockState state, Level level, BlockPos pos, AbstractMinecart cart) {
        if (!(cart instanceof MinecartFurnace)) return cart.isInWater() ? 0.50f : 1.00f;
        else return super.getRailMaxSpeed(state, level, pos, cart);
    }
}
