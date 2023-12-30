package insane96mcp.iguanatweaksreborn.module.world.timber;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.data.generator.ITRBlockTagsProvider;
import insane96mcp.iguanatweaksreborn.entity.ITRFallingBlockEntity;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.JsonFeature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Timber Trees", description = "Trees fall when cut.")
@LoadFeature(module = Modules.Ids.WORLD)
public class TimberTrees extends JsonFeature {

    public static final TagKey<Block> TIMBER_TRUNKS = ITRBlockTagsProvider.create("timber_trunks");

    /*public static final ArrayList<LogsLeavesPair> LOGS_LEAVES_PAIRS_DEFAULT = new ArrayList<>(List.of(
            new LogsLeavesPair(new IdTagMatcher(IdTagMatcher.Type.ID, "minecraft:oak_log"), new IdTagMatcher(IdTagMatcher.Type.ID, "minecraft:oak_leaves")),
            new LogsLeavesPair(new IdTagMatcher(IdTagMatcher.Type.ID, "minecraft:birch_log"), new IdTagMatcher(IdTagMatcher.Type.ID, "minecraft:birch_leaves")),
            new LogsLeavesPair(new IdTagMatcher(IdTagMatcher.Type.ID, "minecraft:spruce_log"), new IdTagMatcher(IdTagMatcher.Type.ID, "minecraft:spruce_leaves")),
            new LogsLeavesPair(new IdTagMatcher(IdTagMatcher.Type.ID, "minecraft:jungle_log"), new IdTagMatcher(IdTagMatcher.Type.ID, "minecraft:jungle_leaves")),
            new LogsLeavesPair(new IdTagMatcher(IdTagMatcher.Type.ID, "minecraft:dark_oak_log"), new IdTagMatcher(IdTagMatcher.Type.ID, "minecraft:dark_oak_leaves")),
            new LogsLeavesPair(new IdTagMatcher(IdTagMatcher.Type.ID, "minecraft:acacia_log"), new IdTagMatcher(IdTagMatcher.Type.ID, "minecraft:acacia_leaves")),
            new LogsLeavesPair(new IdTagMatcher(IdTagMatcher.Type.ID, "minecraft:mangrove_log"), new IdTagMatcher(IdTagMatcher.Type.ID, "minecraft:mangrove_leaves"))
    ));
    public static final ArrayList<LogsLeavesPair> logsLeavesPairs = new ArrayList<>();*/

    @Config
    @Label(name = "Requires axe")
    public static Boolean requiresAxe = false;

    public TimberTrees(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
        //JSON_CONFIGS.add(new SRFeature.JsonConfig<>("logs_leaves_pairs.json", logsLeavesPairs, LOGS_LEAVES_PAIRS_DEFAULT, LogsLeavesPair.LIST_TYPE));
    }

    @Override
    public String getModConfigFolder() {
        return IguanaTweaksReborn.CONFIG_FOLDER;
    }

    @SubscribeEvent
    public void onLogBreak(BlockEvent.BreakEvent event) {
        if (!this.isEnabled()
            || !event.getState().is(TIMBER_TRUNKS)
            || !(event.getState().getBlock() instanceof RotatedPillarBlock)
            || event.getState().getValue(RotatedPillarBlock.AXIS) != Direction.Axis.Y
            || (requiresAxe && !(event.getPlayer().getMainHandItem().getItem() instanceof AxeItem)))
            return;

        BlockPos brokenPos = event.getPos();
        if (event.getLevel().getBlockState(brokenPos.north()).is(event.getState().getBlock())
                || event.getLevel().getBlockState(brokenPos.south()).is(event.getState().getBlock())
                || event.getLevel().getBlockState(brokenPos.east()).is(event.getState().getBlock())
                || event.getLevel().getBlockState(brokenPos.west()).is(event.getState().getBlock()))
            return;
        List<BlockPos> blocks = getTreeBlocks(brokenPos, event.getState(), event.getLevel());
        Direction direction = event.getPlayer().getDirection();
        /*if (event.getLevel().getRandom().nextDouble() < 0.05d)
            direction = direction.getOpposite();*/
        for (BlockPos pos : blocks) {
            if (pos.equals(brokenPos))
                continue;
            Vec3i relative = new BlockPos(pos.getX() - brokenPos.getX(), pos.getY() - brokenPos.getY(), pos.getZ() - brokenPos.getZ());
            int verticalDistance = relative.getY();
            int horizontalDistance;
            if (direction.getAxis() == Direction.Axis.X)
                horizontalDistance = relative.getX();
            else
                horizontalDistance = relative.getZ();
            horizontalDistance *= direction.getAxisDirection().opposite().getStep();
            BlockPos fallingBlockPos = pos.relative(direction, verticalDistance + horizontalDistance).above(horizontalDistance);
            BlockState state = event.getLevel().getBlockState(pos);
            if (state.getBlock() instanceof RotatedPillarBlock) {
                state = rotatePillar(state, direction.getAxis());
            }
            ITRFallingBlockEntity fallingBlock = new ITRFallingBlockEntity((Level) event.getLevel(), fallingBlockPos, state, direction);
            fallingBlock.move(MoverType.SELF, new Vec3(0, 0.1d * horizontalDistance, 0));
            if (state.is(TIMBER_TRUNKS))
                fallingBlock.setHurtsEntities(1f, 1024);
            else
                fallingBlock.setHurtsEntities(0.1f, 1024);
            event.getLevel().addFreshEntity(fallingBlock);
            event.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    private static List<BlockPos> getTreeBlocks(BlockPos pos, BlockState state, LevelAccessor level) {
        List<BlockPos> blocks = new ArrayList<>();
        Block foundLeaves = null;
        int checks = 0;
        int logs = 0;
        int sidewaysLogs = 0;
        List<BlockPos> posToCheck = new ArrayList<>();
        posToCheck.add(pos.above());
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos().set(pos);
        BlockState stateToCheck = level.getBlockState(pos.above());
        if (!stateToCheck.is(state.getBlock())) {
            for (Direction dir : XZ_DIRECTIONS) {
                stateToCheck = level.getBlockState(pos.above().relative(dir));
                if (stateToCheck.is(state.getBlock())) {
                    blocks.add(blockPos.immutable());
                    posToCheck.add(blockPos.immutable());
                    if (stateToCheck.is(state.getBlock()))
                        logs++;
                    break;
                }
            }
        }
        if (!stateToCheck.is(state.getBlock()))
            return blocks;

        blocks.add(blockPos.immutable());
        posToCheck.add(blockPos.immutable());
        if (stateToCheck.is(state.getBlock()))
            logs++;
        //AtomicReference<Block> leaves = new AtomicReference<>();
        //logsLeavesPairs.stream().filter(logsLeavesPair -> logsLeavesPair.log.matchesBlock(state.getBlock())).findFirst().ifPresent(pair -> leaves.set(ForgeRegistries.BLOCKS.getValue(pair.leaves.location)));
        do {
            List<BlockPos> posToCheckTmp = new ArrayList<>(posToCheck);
            posToCheck.clear();
            for (BlockPos p : posToCheckTmp) {
                BlockState currState = level.getBlockState(p);
                Iterable<BlockPos> positionsToLoop = getPositionsToCheck(p, currState);
                for (BlockPos positionToLoop : positionsToLoop) {
                    blockPos.set(positionToLoop);
                    stateToCheck = level.getBlockState(blockPos);
                    if (stateToCheck.isAir())
                        continue;
                    BlockPos posImmutable = blockPos.immutable();
                    boolean isValidLeaves = stateToCheck.is(BlockTags.LEAVES) && !stateToCheck.getValue(LeavesBlock.PERSISTENT) && (foundLeaves == null || stateToCheck.is(foundLeaves));
                    boolean isSameLog = stateToCheck.is(state.getBlock());
                    boolean isInDistance = xzDistance(posImmutable, pos) <= 8;
                    boolean isCurrLeaves = currState.is(BlockTags.LEAVES) && !currState.getValue(LeavesBlock.PERSISTENT);
                    boolean isCorrectLeavesDistance = isValidLeaves && isCurrLeaves && (stateToCheck.getValue(LeavesBlock.DISTANCE) > currState.getValue(LeavesBlock.DISTANCE) || stateToCheck.getValue(LeavesBlock.DISTANCE) == 7 /*|| (stateToCheck.getValue(LeavesBlock.DISTANCE).equals(currState.getValue(LeavesBlock.DISTANCE)) && level.getRandom().nextBoolean())*/);
                    if (isValidLeaves) {
                        foundLeaves = stateToCheck.getBlock();
                    }
                    if (!blocks.contains(posImmutable) && (isSameLog || isValidLeaves) && isInDistance && (!isValidLeaves || !isCurrLeaves || isCorrectLeavesDistance)) {
                        blocks.add(posImmutable);
                        posToCheck.add(posImmutable);
                        if (isSameLog) {
                            if (state.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y)
                                logs++;
                            else
                                sidewaysLogs++;
                        }
                    }
                }
                checks++;
            }
            if (posToCheck.isEmpty() && (foundLeaves == null || logs + sidewaysLogs < 3 || sidewaysLogs > logs)) {
                blocks.clear();
                break;
            }
        } while (checks < 1000 && !posToCheck.isEmpty());
        return blocks;
    }

    public static final List<Direction> XZ_DIRECTIONS = List.of(Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.WEST);
    public static final List<Direction> DIRECTIONS = List.of(Direction.UP, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.WEST);
    public static Iterable<BlockPos> getPositionsToCheck(BlockPos pos, BlockState state) {
        if (state.is(BlockTags.LOGS))
            return BlockPos.betweenClosed(pos.offset(-1, 0, -1), pos.offset(1, 1, 1));
        else
        {
            List<BlockPos> positions = new ArrayList<>();
            for (Direction d : DIRECTIONS) {
                positions.add(pos.relative(d));
            }
            return positions;
        }
    }

    public static int xzDistance(BlockPos pos1, BlockPos pos2) {
        return pos1.atY(pos2.getY()).distManhattan(pos2);
    }

    public static BlockState rotatePillar(BlockState state, Direction.Axis axis) {
        switch (axis) {
            case X -> {
                return switch (state.getValue(RotatedPillarBlock.AXIS)) {
                    case X -> state.setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);
                    case Y -> state.setValue(RotatedPillarBlock.AXIS, Direction.Axis.X);
                    default -> state;
                };
            }
            case Z -> {
                return switch (state.getValue(RotatedPillarBlock.AXIS)) {
                    case Y -> state.setValue(RotatedPillarBlock.AXIS, Direction.Axis.Z);
                    case Z -> state.setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);
                    default -> state;
                };
            }
        }
        return state;
    }
}
