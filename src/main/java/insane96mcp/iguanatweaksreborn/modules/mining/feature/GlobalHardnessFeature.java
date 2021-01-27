package insane96mcp.iguanatweaksreborn.modules.mining.feature;

import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.base.ITModule;
import insane96mcp.iguanatweaksreborn.base.Label;
import insane96mcp.iguanatweaksreborn.base.Modules;
import insane96mcp.iguanatweaksreborn.common.classutils.IdTagMatcher;
import insane96mcp.iguanatweaksreborn.modules.mining.classutils.BlockHardness;
import insane96mcp.iguanatweaksreborn.modules.mining.classutils.DimensionHardnessMultiplier;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.utils.MCUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Global Hardness", description = "Change all the blocks hardness")
public class GlobalHardnessFeature extends ITFeature {

    public ForgeConfigSpec.ConfigValue<Double> hardnessMultiplierConfig;
    public ForgeConfigSpec.ConfigValue<List<? extends String>> dimensionHardnessMultiplierConfig;
    public ForgeConfigSpec.ConfigValue<List<? extends String>> hardnessBlacklistConfig;
    public ForgeConfigSpec.ConfigValue<Boolean> backlistAsWhitelistConfig;

    public double hardnessMultiplier = 3.0d;
    public ArrayList<DimensionHardnessMultiplier> dimensionHardnessMultiplier;
    public ArrayList<IdTagMatcher> hardnessBlacklist;
    public Boolean blacklistAsWhitelist = false;

    public GlobalHardnessFeature(ITModule module) {
        super(module);
        
        Config.builder.comment(this.getDescription()).push(this.getName());
        hardnessMultiplierConfig = Config.builder
                .comment("Multiplier applied to the hardness of blocks. E.g. with this set to 3.0 blocks will take 3x more time to break.")
                .defineInRange("Hardness Multiplier", this.hardnessMultiplier, 0.0d, 128d);
        dimensionHardnessMultiplierConfig = Config.builder
                .comment("A list of dimensions and their relative block hardness multiplier. Each entry has a a dimension and hardness. This overrides the global multiplier.\nE.g. [\"minecraft:overworld,2\", \"minecraft:the_nether,4\"]")
                .defineList("Dimension Hardness Multiplier", new ArrayList<>(), o -> o instanceof String);
        hardnessBlacklistConfig = Config.builder
                .comment("Block ids or tags that will ignore the global or dimensional multipliers. This can be inverted via 'Blacklist as Whitelist'. Each entry has a block or tag and a dimension. E.g. [\"minecraft:stone\", \"minecraft:diamond_block,minecraft:the_nether\"]")
                .defineList("Block Hardnesss Blacklist", new ArrayList<>(), o -> o instanceof String);
        backlistAsWhitelistConfig = Config.builder
                .comment("Block Blacklist will be treated as a whitelist")
                .define("Blacklist as Whitelist", this.blacklistAsWhitelist);
        Config.builder.pop();
    }

    @Override
    public void loadConfig() {
        super.loadConfig();

        hardnessMultiplier = this.hardnessMultiplierConfig.get();
        dimensionHardnessMultiplier = parseDimensionHardnessMultipliers(this.dimensionHardnessMultiplierConfig.get());
        hardnessBlacklist = parseHardnessBlacklist(this.hardnessBlacklistConfig.get());
        blacklistAsWhitelist = this.backlistAsWhitelistConfig.get();
    }

    public static ArrayList<DimensionHardnessMultiplier> parseDimensionHardnessMultipliers(List<? extends String> list) {
        ArrayList<DimensionHardnessMultiplier> dimensionHardnessMultipliers = new ArrayList<>();
        for (String line : list) {
            DimensionHardnessMultiplier dimensionHardnessMultiplier = DimensionHardnessMultiplier.parseLine(line);
            if (dimensionHardnessMultiplier != null)
                dimensionHardnessMultipliers.add(dimensionHardnessMultiplier);
        }

        return dimensionHardnessMultipliers;
    }

    public static ArrayList<IdTagMatcher> parseHardnessBlacklist(List<? extends String> list) {
        ArrayList<IdTagMatcher> commonTagBlock = new ArrayList<>();
        for (String line : list) {
            IdTagMatcher idTagMatcher = IdTagMatcher.parseLine(line);
            if (idTagMatcher != null)
                commonTagBlock.add(idTagMatcher);
        }
        return commonTagBlock;
    }

    @SubscribeEvent
    public void processGlobalHardness(PlayerEvent.BreakSpeed event) {
        if (!this.isEnabled())
            return;

        World world = event.getPlayer().world;
        ResourceLocation dimensionId = world.getDimensionKey().getLocation();
        if (dimensionId == null)
            dimensionId = MCUtils.AnyRL;
        BlockState blockState = world.getBlockState(event.getPos());
        Block block = blockState.getBlock();
        double multiplier = 1d / getBlockGlobalHardness(block, dimensionId);
        if (multiplier == 1d)
            return;
        event.setNewSpeed((float) (event.getNewSpeed() * multiplier));
    }

    /**
     * Returns -1d when no changes must be made, else will return a divider for the block breaking speed (aka multiplier for block hardness)
     */
    public double getBlockGlobalHardness(Block block, ResourceLocation dimensionId) {
        for (BlockHardness blockHardness : Modules.miningModule.customHardnessFeature.customHardness)
            if (MCUtils.isInTagOrBlock(blockHardness, block, dimensionId))
                return 1d;
        boolean isInWhitelist = false;
        for (IdTagMatcher blacklistEntry : this.hardnessBlacklist) {
            if (!this.blacklistAsWhitelist) {
                if (MCUtils.isInTagOrBlock(blacklistEntry, block, dimensionId))
                    return 1d;
            }
            else {
                if (MCUtils.isInTagOrBlock(blacklistEntry, block, dimensionId)) {
                    isInWhitelist = true;
                    break;
                }
            }
        }
        if (!isInWhitelist && this.blacklistAsWhitelist)
            return 1d;
        double multiplier = this.hardnessMultiplier;
        for (DimensionHardnessMultiplier dimensionHardnessMultiplier : this.dimensionHardnessMultiplier) {
            if (dimensionId.equals(dimensionHardnessMultiplier.dimension)) {
                multiplier = dimensionHardnessMultiplier.multiplier;
                break;
            }
        }
        return multiplier;
    }
}
