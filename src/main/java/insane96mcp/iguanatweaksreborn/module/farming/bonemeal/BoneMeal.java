package insane96mcp.iguanatweaksreborn.module.farming.bonemeal;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.data.criterion.ITRTriggers;
import insane96mcp.iguanatweaksreborn.data.generator.ITRBlockTagsProvider;
import insane96mcp.iguanatweaksreborn.data.generator.ITRItemTagsProvider;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.mining.blockdata.BlockData;
import insane96mcp.iguanatweaksreborn.module.mining.blockdata.BlockDataReloadListener;
import insane96mcp.iguanatweaksreborn.setup.registry.SimpleBlockWithItem;
import insane96mcp.insanelib.base.JsonFeature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.base.config.MinMax;
import insane96mcp.insanelib.util.LogHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

import java.util.Map;
import java.util.Optional;

@Label(name = "Bone meal", description = "Increase uses for bone meal and nerf its use on some plants")
@LoadFeature(module = Modules.Ids.FARMING)
public class BoneMeal extends JsonFeature {

    public static final SimpleBlockWithItem RICH_FARMLAND = SimpleBlockWithItem.register("rich_farmland", () -> new RichFarmlandBlock(BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).randomTicks().strength(0.6F).sound(SoundType.GRAVEL).isViewBlocking((state, blockGetter, pos) -> true).isSuffocating((state, blockGetter, pos) -> true)));

    public static final TagKey<Item> ITEM_BLACKLIST = ITRItemTagsProvider.create("bone_meal_blacklist");
    public static final TagKey<Block> BLOCK_BLACKLIST = ITRBlockTagsProvider.create("bone_meal_blacklist");

    @Config
    @Label(name = "Rich Farmland", description = "Bone meal used on Farmland (or shift right clicked on crops) transforms it into Rich Farmland.")
    public static Boolean richFarmland = true;
    @Config(min = 1)
    @Label(name = "Rich Farmland Extra Ticks", description = "How many extra random ticks does Rich Farmland give to the crop sitting on top?")
    public static Integer richFarmlandExtraTicks = 3;

    @Config(min = 0d, max = 1d)
    @Label(name = "Rich Farmland Chance to Decay", description = "Chance for a Rich farmland to decay back to farmland")
    public static Double richFarmlandChanceToDecay = 0.4d;

    @Config(min = 0, max = 25)
    @Label(name = "Stage growth", description = "How many stages will bone meal make stuff grow?")
    public static MinMax stageGrowth = new MinMax(1, 1);

    @Config
    @Label(name = "Compostable Rotten Flesh")
    public static Boolean compostableRottenFlesh = true;

    @Config
    @Label(name = "Season fail chance", description = "Chance for a bone meal to fail to grow something. Empty this string to disable. Accepts a list of seasons and chances separated by a ;")
    public static String seasonFailChance = "AUTUMN,0.4;WINTER,0.8";

    @Config
    @Label(name = "Bone meal dirt to grass", description = "If true, you can bone meal dirt that's near a grass block to get grass block.")
    public static Boolean boneMealDirtToGrass = true;

    @Config
    @Label(name = "Bone meal canes and cactus")
    public static Boolean boneMealCanesAndCactus = true;

    public BoneMeal(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @Override
    public String getModConfigFolder() {
        return IguanaTweaksReborn.CONFIG_FOLDER;
    }

    @Override
    public void readConfig(ModConfigEvent event) {
        super.readConfig(event);
        if (compostableRottenFlesh)
            ComposterBlock.COMPOSTABLES.put(Items.ROTTEN_FLESH, 0.5f);
        else
            ComposterBlock.COMPOSTABLES.removeFloat(Items.ROTTEN_FLESH);
    }

    @SubscribeEvent
    public void onBoneMeal(BonemealEvent event) {
        if (event.isCanceled()
                || event.getResult() != Event.Result.DEFAULT
                || !this.isEnabled()
                || event.getLevel().isClientSide
                || event.getStack().is(ITEM_BLACKLIST)
                || event.getBlock().is(BLOCK_BLACKLIST))
            return;

        tryMakeRichFarmland(event);
        if (event.getResult() == Event.Result.ALLOW)
            return;
        tryConsumeWithFail(event);
        if (event.getResult() == Event.Result.ALLOW)
            return;
        applyNerfedBoneMeal(event);

        if (event.getResult() != Event.Result.ALLOW) {
            if (boneMealDirtToGrass)
                tryBoneMealDirt(event, event.getLevel(), event.getBlock(), event.getPos());
            if (boneMealCanesAndCactus)
                tryBoneMealCanesAndCactus(event, event.getLevel(), event.getBlock(), event.getPos());
        }
    }

    private void tryMakeRichFarmland(BonemealEvent event) {
        if (!richFarmland)
            return;
        BlockPos farmlandPos = null;
        if (event.getBlock().is(Blocks.FARMLAND))
            farmlandPos = event.getPos();
        else if (event.getLevel().getBlockState(event.getPos().below()).is(Blocks.FARMLAND) && event.getEntity().isCrouching())
            farmlandPos = event.getPos().below();
        if (farmlandPos != null) {
            event.getLevel().setBlockAndUpdate(farmlandPos, RICH_FARMLAND.block().get().defaultBlockState().setValue(FarmBlock.MOISTURE, event.getLevel().getBlockState(farmlandPos).getValue(FarmBlock.MOISTURE)));
            event.getEntity().swing(event.getEntity().getMainHandItem().getItem() == event.getStack().getItem() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, true);
            event.setResult(Event.Result.ALLOW);
            ITRTriggers.MAKE_RICH_FARMLAND.trigger((ServerPlayer) event.getEntity());
        }
    }

    private void tryConsumeWithFail(BonemealEvent event) {
        failFromBlockData(event);
        if (event.getResult() != Event.Result.ALLOW)
            failFromSeason(event);
    }

    private void failFromBlockData(BonemealEvent event) {
        for (BlockData blockData : BlockDataReloadListener.DATA) {
            if (!blockData.matches(event.getBlock())
                    || blockData.boneMealFailChance == null)
                continue;
            if (event.getLevel().random.nextFloat() < blockData.boneMealFailChance) {
                event.setResult(Event.Result.ALLOW);
                return;
            }
        }
    }

    private void failFromSeason(BonemealEvent event) {
        if (seasonFailChance.isEmpty()
                || !ModList.get().isLoaded("sereneseasons")
                || isFullyGrown(event.getBlock()))
            return;

        String[] seasonSplit = seasonFailChance.split(";");
        for (String seasonChance : seasonSplit) {
            String[] chanceSplit = seasonChance.split(",");
            if (chanceSplit.length != 2)
                continue;
            Season season = Season.valueOf(chanceSplit[0]);
            float chance = Float.parseFloat(chanceSplit[1]);
            if (SeasonHelper.getSeasonState(event.getLevel()).getSeason().equals(season) && event.getLevel().random.nextFloat() < chance) {
                event.setResult(Event.Result.ALLOW);
                break;
            }
        }
    }

    private void applyNerfedBoneMeal(BonemealEvent event) {
        BlockState state = event.getBlock();
        if (state.getBlock() instanceof BushBlock bushBlock) {
            Optional<IntegerProperty> oAgeProperty = getAgeProperty(state);
            if (oAgeProperty.isEmpty()) {
                LogHelper.warn("No vanilla age property found for state %s", state);
                return;
            }
            int age = state.getValue(oAgeProperty.get());
            int maxAge = AGE_PROPERTIES.get(oAgeProperty.get());

            age = Mth.clamp(age + stageGrowth.getIntRandBetween(event.getLevel().random), 0, maxAge);
            event.getLevel().setBlockAndUpdate(event.getPos(), state.setValue(oAgeProperty.get(), age));
            if (state.getBlock() instanceof StemBlock && age == maxAge)
                state.randomTick((ServerLevel) event.getLevel(), event.getPos(), event.getLevel().random);
            if (ModList.get().isLoaded("farmersdelight"))
                FarmersDelightIntegration.onBoneMeal(event.getLevel(), event.getPos(), state, oAgeProperty.get(), age);
            if (ModList.get().isLoaded("supplementaries"))
                SupplementariesIntegration.onBoneMeal(event.getLevel(), event.getPos(), state, oAgeProperty.get(), age);
            event.setResult(Event.Result.ALLOW);
        }
    }

    private void tryBoneMealDirt(BonemealEvent event, Level level, BlockState state, BlockPos pos) {
        if (!state.is(Blocks.DIRT)
                || !level.getBlockState(pos.above()).isAir())
            return;

        for (Direction direction : Direction.values()) {
            if (direction == Direction.UP || direction == Direction.DOWN)
                continue;

            if (level.getBlockState(pos.relative(direction)).is(Blocks.GRASS_BLOCK)) {
                level.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState());
                event.getEntity().swing(event.getEntity().getMainHandItem().getItem() == event.getStack().getItem() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, true);
                event.setResult(Event.Result.ALLOW);
                break;
            }
        }
    }

    private void tryBoneMealCanesAndCactus(BonemealEvent event, Level level, BlockState state, BlockPos pos) {
        if (!state.is(Blocks.SUGAR_CANE) && !state.is(Blocks.CACTUS))
            return;
        if (!level.isEmptyBlock(pos.above()))
            return;
        IntegerProperty ageProperty = state.is(Blocks.SUGAR_CANE) ? SugarCaneBlock.AGE : CactusBlock.AGE;
        int height = 1;
        while (level.getBlockState(pos.below(height)).is(Blocks.SUGAR_CANE) || level.getBlockState(pos.below(height)).is(Blocks.CACTUS)) {
            height++;
        }
        if (height >= 3)
            return;

        int age = state.getValue(ageProperty);
        if (age == 15) {
            level.setBlockAndUpdate(pos.above(), state.getBlock().defaultBlockState());
            level.setBlock(pos, state.setValue(ageProperty, 0), 4);
        }
        else {
            level.setBlock(pos, state.setValue(ageProperty, Math.min(age + level.getRandom().nextInt(3) + 1, 15)), 4);
        }
        event.getEntity().swing(event.getEntity().getMainHandItem().getItem() == event.getStack().getItem() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, true);
        event.setResult(Event.Result.ALLOW);
    }

    private static final Map<IntegerProperty, Integer> AGE_PROPERTIES = Map.of(
            BlockStateProperties.AGE_1, 1,
            BlockStateProperties.AGE_2, 2,
            BlockStateProperties.AGE_3, 3,
            BlockStateProperties.AGE_4, 4,
            BlockStateProperties.AGE_5, 5,
            BlockStateProperties.AGE_7, 7,
            BlockStateProperties.AGE_15, 15,
            BlockStateProperties.AGE_25, 25
    );

    public static Optional<IntegerProperty> getAgeProperty(BlockState state) {
        for (var ageProperty : AGE_PROPERTIES.entrySet()) {
            if (state.hasProperty(ageProperty.getKey()))
                return Optional.of(ageProperty.getKey());
        }
        if (ModList.get().isLoaded("farmersdelight"))
        {
            Optional<IntegerProperty> oAgeProperty = FarmersDelightIntegration.getAgeProperty(state);
            if (oAgeProperty.isPresent())
                return oAgeProperty;
        }
        return Optional.empty();
    }

    public static boolean isFullyGrown(BlockState state) {
        for (var ageProperty : AGE_PROPERTIES.entrySet()) {
            if (state.hasProperty(ageProperty.getKey()) && state.getValue(ageProperty.getKey()).equals(ageProperty.getValue()))
                return true;
        }
        return false;
    }
}
