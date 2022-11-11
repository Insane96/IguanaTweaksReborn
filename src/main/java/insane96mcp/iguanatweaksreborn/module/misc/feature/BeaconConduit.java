package insane96mcp.iguanatweaksreborn.module.misc.feature;

import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.misc.utils.IdTagValue;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

@Label(name = "Beacon & Conduit", description = "Beacon Range varying based of blocks of the pyramid and better conduit killing mobs")
@LoadFeature(module = Modules.Ids.MISC)
public class BeaconConduit extends ITFeature {
    public static final ArrayList<IdTagValue> BLOCKS_LIST_DEFAULT = new ArrayList<>(Arrays.asList(
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:iron_block", 1d),
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:emerald_block", 1.2d),
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:gold_block", 1.8d),
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:diamond_block", 2.5d),
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:netherite_block", 4d),
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:cobalt_block", 2.4d),
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:queens_slime_block", 3.0d),
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:hepatizon_block", 2.7d),
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:manyullyn_block", 3.3d)
    ));
    public static final ArrayList<IdTagValue> blocksList = new ArrayList<>();

    @Config
    @Label(name = "Affect Pets", description = "If true, pets will also get the beacon effects")
    public static Boolean affectPets = true;
    @Config(min = 0d, max = 256d)
    @Label(name = "Base Range", description = "Base range of the beacon")
    public static Double baseRange = 10d;
    @Config
    @Label(name = "Better Conduit Protection", description = "Greatly increases the range and damage of the conduit")
    public static Boolean betterConduitProtection = true;

    public BeaconConduit(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @Override
    public void loadJsonConfigs() {
        if (!this.isEnabled())
            return;
        super.loadJsonConfigs();
        this.loadAndReadFile("beacon_blocks_ranges.json", blocksList, BLOCKS_LIST_DEFAULT, IdTagValue.LIST_TYPE);
    }

    public static boolean beaconApplyEffects(Level level, BlockPos blockPos, int layers, MobEffect effectPrimary, MobEffect effectSecondary) {
        if (!isEnabled(BeaconConduit.class)
                || blocksList.isEmpty()
                || level.isClientSide
                || effectPrimary == null)
            return false;

        double blocksRange = getBeaconRange(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), layers);

        double range = blocksRange + baseRange;
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

        if (affectPets) {
            List<TamableAnimal> list2 = level.getEntitiesOfClass(TamableAnimal.class, aabb, TamableAnimal::isTame);

            for (TamableAnimal animal : list2) {
                animal.addEffect(new MobEffectInstance(effectPrimary, j, i, true, true));
            }

            if (layers >= 4 && effectPrimary != effectSecondary && effectSecondary != null) {
                for (TamableAnimal animal : list2) {
                    animal.addEffect(new MobEffectInstance(effectSecondary, j, 0, true, true));
                }
            }
        }

        return true;
    }

    private static double getBeaconRange(Level level, int x, int y, int z, int layers) {
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
            Optional<IdTagValue> optional = blocksList
                    .stream()
                    .filter(idTagValue -> idTagValue.matchesBlock(entry.getKey()))
                    .findFirst();
            if (optional.isPresent())
                range += optional.get().value * entry.getValue() / layers;
        }

        return range;
    }

    static float MIN_DAMAGE = 2f;
    static float MAX_DAMAGE = 6f;

    public static boolean conduitUpdateDestroyEnemies(Level level, BlockPos blockPos, List<BlockPos> blocks) {
        if (!isEnabled(BeaconConduit.class)
                || !betterConduitProtection)
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
                //TODO remake this formula as at maximum range still deals more than MIN_DAMAGE
                damage = (float) (((68d - (distance - 8d)) / 40d) * (MAX_DAMAGE - MIN_DAMAGE) + MIN_DAMAGE);
            entity.hurt(DamageSource.MAGIC, damage);
        }
        return true;
    }

    private static AABB getRange(BlockPos blockPos, List<BlockPos> blocks) {
        double range = blocks.size() / 7d * 8d;
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();
        return (new AABB(x, y, z, x + 1, y + 1, z + 1)).inflate(range);
    }
}