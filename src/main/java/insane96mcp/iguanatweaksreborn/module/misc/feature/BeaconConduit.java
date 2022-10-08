package insane96mcp.iguanatweaksreborn.module.misc.feature;

import insane96mcp.iguanatweaksreborn.module.misc.utils.IdTagValue;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.*;

@Label(name = "Beacon & Conduit", description = "Beacon Range varying based of blocks of the pyramid and better conduit killing mobs")
public class BeaconConduit extends Feature {
    private final ForgeConfigSpec.ConfigValue<Double> baseRangeConfig;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> blocksListConfig;

    private final ForgeConfigSpec.BooleanValue betterConduitProtectionConfig;

    private static final List<String> blocksListDefault = Arrays.asList("minecraft:iron_block,1","minecraft:emerald_block,1.2","minecraft:gold_block,1.8","minecraft:diamond_block,2.5","minecraft:netherite_block,4.0", "tconstruct:cobalt_block,2.4", "tconstruct:queens_slime_block,3.0", "tconstruct:hepatizon_block,2.7", "tconstruct:manyullyn_block,3.3");

    public double baseRange = 10;
    public ArrayList<IdTagValue> blocksList;

    public boolean betterConduitProtection = true;

    public BeaconConduit(Module module) {
        super(Config.builder, module);
        this.pushConfig(Config.builder);
        baseRangeConfig = Config.builder
                .comment("Base range of the beacon")
                .defineInRange("Base Range", this.baseRange, 0d, 256d);
        blocksListConfig = Config.builder
                .comment("""
                        A list of blocks and the range increase on the beacon. Each entry represent a block or block tag plus the range increase in blocks of the beacon base.
                        Each block in the pyramid will increase the range of the beacon. After the blocks have been summed the final value is divided by the number of layers of the beacon.
                        E.g. a beacon with 1 layer full Iron blocks will give 9 range (+ base range), while with 2 layers (34 range / 2) = 17.
                        """)
                .defineList("Blocks Range", blocksListDefault, o -> o instanceof String);

        betterConduitProtectionConfig = Config.builder
                .comment("Increases the range and damage of the conduit")
                .define("Better Conduit Protection", this.betterConduitProtection);
        Config.builder.pop();
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        this.baseRange = this.baseRangeConfig.get();
        this.blocksList = IdTagValue.parseStringList(this.blocksListConfig.get());

        this.betterConduitProtection = this.betterConduitProtectionConfig.get();
    }

    public boolean beaconApplyEffects(Level level, BlockPos blockPos, int layers, MobEffect effectPrimary, MobEffect effectSecondary) {
        if (!this.isEnabled())
            return false;

        if (blocksList.isEmpty())
            return false;

        if (level.isClientSide || effectPrimary == null)
            return false;

        double blocksRange = this.getBeaconRange(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), layers);

        double range = blocksRange + this.baseRange;
        int i = 0;
        if (layers >= 4 && effectPrimary == effectSecondary) {
            i = 1;
        }

        int j = (9 + layers * 2) * 20;
        AABB aabb = (new AABB(blockPos)).inflate(range).expandTowards(0.0D, level.getHeight(), 0.0D);
        List<Player> list = level.getEntitiesOfClass(Player.class, aabb);

        for (Player player : list) {
            player.addEffect(new MobEffectInstance(effectPrimary, j, i, true, true));
        }

        if (layers >= 4 && effectPrimary != effectSecondary && effectSecondary != null) {
            for(Player player : list) {
                player.addEffect(new MobEffectInstance(effectSecondary, j, 0, true, true));
            }
        }

        return true;
    }

    private double getBeaconRange(Level level, int x, int y, int z, int layers) {
        Map<Block, Integer> blocksCount = new HashMap<>();

        for (int layer = 1; layer <= layers; layer++) {
            int relativeY = y - layer;

            for (int relativeX = x - layer; relativeX <= x + layer; ++relativeX) {
                for (int relativeZ = z - layer; relativeZ <= z + layer; ++relativeZ) {
                    if (level.getBlockState(new BlockPos(relativeX, relativeY, relativeZ)).is(BlockTags.BEACON_BASE_BLOCKS)) {
                        Block block = level.getBlockState(new BlockPos(relativeX, relativeY, relativeZ)).getBlock();
                        blocksCount.merge(block, 1, Integer::sum);
                    }
                }
            }
        }

        double range = 0d;
        for (Map.Entry<Block, Integer> entry : blocksCount.entrySet()) {
            Optional<IdTagValue> optional = this.blocksList
                    .stream()
                    .filter(idTagValue -> idTagValue.idTagMatcher.matchesBlock(entry.getKey()))
                    .findFirst();
            if (optional.isPresent())
                range += optional.get().value * entry.getValue() / layers;
        }

        return range;
    }

    float MIN_DAMAGE = 2f;
    float MAX_DAMAGE = 6f;

    public boolean conduitUpdateDestroyEnemies(Level level, BlockPos blockPos, BlockState state, List<BlockPos> blocks, ConduitBlockEntity conduit) {
        if (!this.isEnabled()
                || !this.betterConduitProtection)
            return false;

        List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, getRange(blockPos, blocks),
                (living) -> living instanceof Enemy && living.isInWaterOrRain());

        for (LivingEntity entity : list) {
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CONDUIT_ATTACK_TARGET, SoundSource.BLOCKS, 1.0F, 1.0F);
            double distance = entity.position().distanceTo(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
            float damage;
            if (distance < 8d)
                damage = MAX_DAMAGE;
            else
                damage = (float) (((40d - (distance - 8d)) / 40d) * (MAX_DAMAGE - MIN_DAMAGE) + MIN_DAMAGE);
            entity.hurt(DamageSource.MAGIC, damage);
        }
        return true;
    }

    private AABB getRange(BlockPos blockPos, List<BlockPos> blocks) {
        double range = blocks.size() / 7d * 8d;
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();
        return (new AABB(x, y, z, x + 1, y + 1, z + 1)).inflate(range);
    }
}