package insane96mcp.survivalreimagined.module.world.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.entity.SRFallingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Timber Trees", description = "Trees fall when cut.")
@LoadFeature(module = Modules.Ids.WORLD)
public class TimberTrees extends Feature {

    @Config
    @Label(name = "Requires axe")
    public static Boolean requiresAxe = false;

    static List<Direction> directions = List.of(Direction.UP, Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH);

    public TimberTrees(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @SubscribeEvent
    public void onBushesDamage(BlockEvent.BreakEvent event) {
        if (!this.isEnabled()
            || !event.getState().is(BlockTags.OVERWORLD_NATURAL_LOGS)
            || (requiresAxe && !(event.getPlayer().getMainHandItem().getItem() instanceof AxeItem)))
            return;

        if (event.getLevel().getBlockState(event.getPos().north()).is(event.getState().getBlock())
                || event.getLevel().getBlockState(event.getPos().south()).is(event.getState().getBlock())
                || event.getLevel().getBlockState(event.getPos().east()).is(event.getState().getBlock())
                || event.getLevel().getBlockState(event.getPos().west()).is(event.getState().getBlock()))
            return;
        //Vec3 dir = new Vec3(event.getPlayer().getDirection().getNormal().getX(), event.getPlayer().getDirection().getNormal().getY(), event.getPlayer().getDirection().getNormal().getZ());
        List<BlockPos> blocks = getTreeBlocks(event.getPos(), event.getState(), event.getLevel());
        blocks.forEach(pos -> {
            double distanceFromBrokenBlock = Math.sqrt(pos.distSqr(event.getPos()));
            Vec3i relative = new BlockPos(pos.getX() - event.getPos().getX(), pos.getY() - event.getPos().getY(), pos.getZ() - event.getPos().getZ());
            BlockPos fallingBlockPos = pos.relative(event.getPlayer().getDirection(), (int) distanceFromBrokenBlock).above(relative.getY());
            SRFallingBlockEntity fallingBlock = new SRFallingBlockEntity((Level) event.getLevel(), fallingBlockPos, event.getLevel().getBlockState(pos));
            event.getLevel().addFreshEntity(fallingBlock);
            event.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        });
    }

    private static List<BlockPos> getTreeBlocks(BlockPos pos, BlockState state, LevelAccessor level) {
        List<BlockPos> blocks = new ArrayList<>();
        boolean foundLeaves = false;
        int checks = 0;
        int logs = 0;
        List<BlockPos> posToCheck = new ArrayList<>();
        posToCheck.add(pos.above());
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos().set(pos);
        BlockState stateToCheck = level.getBlockState(pos.above());
        if (stateToCheck.isAir() || stateToCheck.is(BlockTags.LEAVES))
            return blocks;
        if (stateToCheck.is(state.getBlock())) {
            blocks.add(blockPos.immutable());
            posToCheck.add(blockPos.immutable());
            if (stateToCheck.is(state.getBlock()))
                logs++;
        }
        do {
            List<BlockPos> posToCheckTmp = new ArrayList<>(posToCheck);
            posToCheck.clear();
            for (BlockPos p : posToCheckTmp) {
                for (Direction direction : directions) {
                    blockPos.set(p.relative(direction));
                    stateToCheck = level.getBlockState(blockPos);
                    if (stateToCheck.isAir())
                        continue;
                    if (stateToCheck.is(BlockTags.LEAVES))
                        foundLeaves = true;
                    if (!blocks.contains(blockPos.immutable()) && (stateToCheck.is(state.getBlock()) || stateToCheck.is(BlockTags.LEAVES))) {
                        BlockPos posImmutable = blockPos.immutable();
                        blocks.add(posImmutable);
                        if (xzDistance(posImmutable, pos) < 5)
                            posToCheck.add(posImmutable);
                        if (stateToCheck.is(state.getBlock()))
                            logs++;
                    }
                }
                checks++;
            }
            if (posToCheck.isEmpty() && (!foundLeaves || logs < 2)) {
                blocks.clear();
                break;
            }
        } while (checks < 1000 && !posToCheck.isEmpty());
        return blocks;
    }

    public static int xzDistance(BlockPos pos1, BlockPos pos2) {
        return pos1.atY(pos2.getY()).distManhattan(pos2);
    }
}
