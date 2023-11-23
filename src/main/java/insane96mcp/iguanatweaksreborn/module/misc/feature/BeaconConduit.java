package insane96mcp.iguanatweaksreborn.module.misc.feature;

import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.misc.utils.IdTagValue;
import insane96mcp.iguanatweaksreborn.setup.ITMobEffects;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

@Label(name = "Beacon & Conduit", description = "Beacon Range varying based of blocks of the pyramid and better conduit killing mobs. Blocks list and ranges are controlled via json in this feature's folder")
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
    @Label(name = "Beacon.Affect Pets", description = "If true, pets will also get the beacon effects")
    public static Boolean affectPets = true;
    @Config
    @Label(name = "Beacon.Vigour with Regeneration", description = "If true, with the regeneration effect, the Vigour effect is also applied (reduces hunger consumption by 20%).")
    public static Boolean vigourWithRegen = true;
    @Config(min = 0d, max = 256d)
    @Label(name = "Beacon.Base Range", description = "Base range of the beacon")
    public static Double baseRange = 10d;
    @Config
    @Label(name = "Conduit.Better Protection", description = "Greatly increases the range and damage of the conduit")
    public static Boolean betterConduitProtection = true;
    @Config(min = 0d, max = 64d)
    @Label(name = "Conduit.Protection Distance Multiplier", description = "Distance multiplier (formula is blocks_around / 7 * this_multiplier) from the conduit at which it will deal damage to enemies.")
    public static Double conduitProtectionDistanceMultiplier = 8d;
    @Config(min = 0d, max = 96d)
    @Label(name = "Conduit.Protection Max Damage Distance", description = "If a mob is within this radius from the conduit, it will be dealt the maximum amount of damage.")
    public static Double conduitProtectionMaxDamageDistance = 8d;

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
        int amplifier = 0;
        if (layers >= 4 && effectPrimary == effectSecondary) {
            amplifier = 1;
        }

        int j = (9 + layers * 2) * 20;
        AABB aabb = (new AABB(blockPos)).inflate(range).expandTowards(0.0D, level.getHeight(), 0.0D);
        List<Player> list = level.getEntitiesOfClass(Player.class, aabb);

        for (Player player : list) {
            player.addEffect(new MobEffectInstance(effectPrimary, j, amplifier, true, true));
        }

        if (layers >= 4 && effectPrimary != effectSecondary && effectSecondary != null) {
            for(Player player : list) {
                player.addEffect(new MobEffectInstance(effectSecondary, j, 0, true, true));
                if (vigourWithRegen && effectSecondary.equals(MobEffects.REGENERATION))
                    player.addEffect(new MobEffectInstance(ITMobEffects.VIGOUR.get(), j, 0, true, true));
            }
        }

        if (affectPets) {
            List<TamableAnimal> list2 = level.getEntitiesOfClass(TamableAnimal.class, aabb, TamableAnimal::isTame);

            for (TamableAnimal animal : list2) {
                animal.addEffect(new MobEffectInstance(effectPrimary, j, amplifier, true, true));
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

    boolean replacedTooltip = false;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRenderTooltip(RenderTooltipEvent.Pre event) {
        if (!this.isEnabled()
                || !vigourWithRegen
                || replacedTooltip)
            return;

        if (Minecraft.getInstance().screen instanceof BeaconScreen beaconScreen) {
            for (BeaconScreen.BeaconButton beaconButton : beaconScreen.beaconButtons) {
                if (!(beaconButton instanceof BeaconScreen.BeaconPowerButton beaconPowerButton))
                    continue;
                if (beaconPowerButton.effect.equals(MobEffects.REGENERATION)) {
                    if (vigourWithRegen)
                        beaconPowerButton.setTooltip(Tooltip.create(
                                Component.translatable(beaconPowerButton.effect.getDescriptionId())
                                        .append(Component.literal(" & ")
                                        .append(Component.translatable(ITMobEffects.VIGOUR.get().getDescriptionId()))),
                        null));
                    replacedTooltip = true;
                }
            }
        }

    }

    /*
     * CONDUIT
     */

    static float MIN_DAMAGE = 2f;
    static float MAX_DAMAGE = 6f;

    public static boolean conduitUpdateDestroyEnemies(Level level, BlockPos blockPos, List<BlockPos> blocks) {
        if (!isEnabled(BeaconConduit.class)
                || !betterConduitProtection)
            return false;

        List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, getDamageAABB(blockPos, blocks),
                (living) -> living instanceof Enemy && living.isInWaterOrRain());

        for (LivingEntity entity : list) {
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CONDUIT_ATTACK_TARGET, SoundSource.BLOCKS, 1.0F, 1.0F);
            double distance = entity.position().distanceTo(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
            float damage;
            if (distance < conduitProtectionMaxDamageDistance)
                damage = MAX_DAMAGE;
            else
                damage = (float)(1 - (distance - conduitProtectionMaxDamageDistance) / (maxRangeRadius() - conduitProtectionMaxDamageDistance)) * (MAX_DAMAGE - MIN_DAMAGE) + MIN_DAMAGE;
            entity.hurt(entity.damageSources().magic(), damage);
        }
        return true;
    }

    private static AABB getDamageAABB(BlockPos blockPos, List<BlockPos> blocks) {
        double range = blocks.size() / 7d * conduitProtectionDistanceMultiplier;
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();
        return (new AABB(x, y, z, x + 1, y + 1, z + 1)).inflate(range);
    }

    private static double maxRange() {
        return 42 / 7d * conduitProtectionDistanceMultiplier;
    }

    private static double maxRangeRadius() {
        return Math.sqrt(maxRange() * maxRange() + maxRange() * maxRange());
    }
}