package insane96mcp.iguanatweaksreborn.module.world.timber;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.data.generator.ITRBlockTagsProvider;
import insane96mcp.iguanatweaksreborn.entity.ITRFallingBlockEntity;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.JsonFeature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.LoadFeature;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
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
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

@Label(name = "Timber Trees", description = "Trees fall when cut.")
@LoadFeature(module = Modules.Ids.WORLD)
public class TimberTrees extends JsonFeature {

    public static final TagKey<Block> TIMBER_TRUNKS = ITRBlockTagsProvider.create("timber_trunks");

    public static final ArrayList<TreeInfo> TREE_INFOS_DEFAULT = new ArrayList<>(List.of(
            new TreeInfo.Builder().log(IdTagMatcher.newId("minecraft:oak_log")).leaves(IdTagMatcher.newTag("iguanatweaksreborn:oak_log_leaves")).build(),
            new TreeInfo.Builder().log(IdTagMatcher.newId("minecraft:spruce_log")).leaves(IdTagMatcher.newId("minecraft:spruce_leaves")).build(),
            new TreeInfo.Builder().log(IdTagMatcher.newId("minecraft:birch_log")).leaves(IdTagMatcher.newId("minecraft:birch_leaves")).build(),
            new TreeInfo.Builder().log(IdTagMatcher.newId("minecraft:jungle_log")).leaves(IdTagMatcher.newId("minecraft:jungle_leaves")).build(),
            new TreeInfo.Builder().log(IdTagMatcher.newId("minecraft:dark_oak_log")).leaves(IdTagMatcher.newId("minecraft:dark_oak_leaves")).build(),
            new TreeInfo.Builder().log(IdTagMatcher.newId("minecraft:acacia_log")).leaves(IdTagMatcher.newId("minecraft:acacia_leaves")).build(),
            new TreeInfo.Builder().log(IdTagMatcher.newId("minecraft:cherry_log")).leaves(IdTagMatcher.newId("minecraft:cherry_leaves")).build(),
            new TreeInfo.Builder().log(IdTagMatcher.newId("minecraft:mangrove_log")).leaves(IdTagMatcher.newId("minecraft:mangrove_leaves")).build(),
            new TreeInfo.Builder().log(IdTagMatcher.newId("quark:blossom_log")).leaves(IdTagMatcher.newTag("iguanatweaksreborn:trumpet_leaves")).logsSidewaysRatio(0.33f).maxDistanceFromLogs(12).build(),
            new TreeInfo.Builder().log(IdTagMatcher.newId("quark:azalea_log")).leaves(IdTagMatcher.newTag("iguanatweaksreborn:azalea_leaves")).build(),
            new TreeInfo.Builder().log(IdTagMatcher.newId("autumnity:maple_log")).leaves(IdTagMatcher.newTag("iguanatweaksreborn:maple_leaves")).build()
    ));
    public static final ArrayList<TreeInfo> treeInfos = new ArrayList<>();

    @Config
    @Label(name = "Requires axe")
    public static Boolean requiresAxe = false;

    public TimberTrees(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
        JSON_CONFIGS.add(new JsonConfig<>("tree_info.json", treeInfos, TREE_INFOS_DEFAULT, TreeInfo.LIST_TYPE));
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
                fallingBlock.setHurtsEntities(0.5f, 20);
            else
                fallingBlock.setHurtsEntities(0.1f, 4);
            event.getLevel().addFreshEntity(fallingBlock);
            event.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    private static List<BlockPos> getTreeBlocks(BlockPos pos, BlockState state, LevelAccessor level) {
        List<BlockPos> blocks = new ArrayList<>();
        boolean foundLeaves = false;
        int checks = 0;
        int logs = 0;
        int sidewaysLogs = 0;
        int maxY = pos.getY();
        //Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> posToCheck = new ArrayDeque<>(); //Queue seem to be faster than ArrayList
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
        IdTagMatcher validLeaves = null;
        Optional<TreeInfo> treeInfo = treeInfos.stream().filter(ti -> ti.log.matchesBlock(state.getBlock())).findFirst();
        if (treeInfo.isPresent())
            validLeaves = treeInfo.get().leaves;
        int i = 0;
        while (checks < 1536 && !posToCheck.isEmpty()) {
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
                    //if (visited.contains(positionToLoop))
                        //continue;
                    //visited.add(positionToLoop);
                    BlockPos posImmutable = blockPos.immutable();
                    if (blocks.contains(posImmutable))
                        continue;
                    boolean isLeaves = stateToCheck.is(BlockTags.LEAVES);
                    boolean isValidLeaves = validLeaves != null && validLeaves.matchesBlock(stateToCheck.getBlock()) && !stateToCheck.getValue(LeavesBlock.PERSISTENT);
                    boolean isSameLog = stateToCheck.is(state.getBlock());
                    boolean isInDistance = xzDistance(posImmutable, pos) <= treeInfo.map(ti -> ti.maxDistanceFromLogs).orElse(8);
                    boolean isCurrLeaves = currState.is(BlockTags.LEAVES);
                    boolean isCorrectLeavesDistance = isLeaves && isCurrLeaves && (stateToCheck.getValue(LeavesBlock.DISTANCE) > currState.getValue(LeavesBlock.DISTANCE) || stateToCheck.getValue(LeavesBlock.DISTANCE) == 7);
                    if (isLeaves && validLeaves == null) {
                        validLeaves = IdTagMatcher.newId(ForgeRegistries.BLOCKS.getKey(stateToCheck.getBlock()).toString());
                        isValidLeaves = true;
                    }
                    if ((isSameLog || isValidLeaves) && isInDistance && (!isLeaves || !isCurrLeaves || isCorrectLeavesDistance)) {
                        blocks.add(posImmutable);
                        //level.removeBlock(posImmutable, false);
                        posToCheck.add(posImmutable);
                        if (!FMLLoader.isProduction()) {
                            Display.TextDisplay display = EntityType.TEXT_DISPLAY.create((Level) level);
                            display.setPos(posImmutable.getCenter());
                            display.setText(Component.literal(i++ + ""));
                            level.addFreshEntity(display);
                        }
                        if (isValidLeaves)
                            foundLeaves = true;
                        if (isSameLog) {
                            if (stateToCheck.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y)
                                logs++;
                            else
                                sidewaysLogs++;

                            if (posImmutable.getY() > maxY)
                                maxY = posImmutable.getY();
                        }
                    }
                }
                checks++;
            }
            float logsSidewaysRatio = sidewaysLogs == 0 ? 999 : (float) logs / sidewaysLogs;
            if (posToCheck.isEmpty()
                    && (!foundLeaves
                        || logs + sidewaysLogs < treeInfo.map(ti -> ti.minLogs).orElse(3)
                        || logsSidewaysRatio < treeInfo.map(ti -> ti.logsSidewaysRatio).orElse(0.6f))) {
                blocks.clear();
                break;
            }
        }
        if (maxY < pos.getY() + 3)
            blocks.clear();
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
