package insane96mcp.iguanatweaksreborn.module.mobs.feature;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.MCUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

@Label(name = "Stats Buffs", description = "Increase monsters health, movement speed, etc. I always recommend Mobs Properties Randomness to have more control over stat, equip, etc.")
@LoadFeature(module = Modules.Ids.MOBS)
public class StatsBuffs extends Feature {

    public static final String STAT_BUFFS_APPLIED = IguanaTweaksReborn.RESOURCE_PREFIX + "stat_buffs_applied";
    public static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("f62c1940-39a9-4a4a-8421-2278e259e5f6");
    public static final UUID HEALTH_UUID = UUID.fromString("ad58b57d-1956-416d-804a-5180a01d0bad");
    public static final UUID SWIM_SPEED_UUID = UUID.fromString("ed9f51b5-e297-498e-a909-31644f2a326d");

    //TODO Swim speed +200%
    @Config(min = 0)
    @Label(name = "Movement Speed.Easy", description = "Bonus percentage movement speed given to mobs on easy difficulty.")
    public static Double movementSpeedEasy = 0.1d;
    @Config(min = 0)
    @Label(name = "Movement Speed.Normal", description = "Bonus percentage movement speed given to mobs on normal difficulty.")
    public static Double movementSpeedNormal = 0.2d;
    @Config(min = 0)
    @Label(name = "Movement Speed.Hard", description = "Bonus percentage movement speed given to mobs on hard difficulty.")
    public static Double movementSpeedHard = 0.25d;

    @Config(min = 0)
    @Label(name = "Health.Easy", description = "Bonus percentage health given to mobs on easy difficulty.")
    public static Double healthEasy = 0.2d;
    @Config(min = 0)
    @Label(name = "Health.Normal", description = "Bonus percentage health given to mobs on normal difficulty.")
    public static Double healthNormal = 0.5d;
    @Config(min = 0)
    @Label(name = "Health.Hard", description = "Bonus percentage health given to mobs on hard difficulty.")
    public static Double healthHard = 0.75d;

    @Config(min = 0)
    @Label(name = "Follow Range.Easy", description = "Override for mobs' follow range on easy difficulty. The override is not applied if the mob's follow range is already higher than this. Set to 0 to not override Follow Range.")
    public static Integer followRangeEasy = 24;
    @Config(min = 0)
    @Label(name = "Follow Range.Normal", description = "Override for mobs' follow range on normal difficulty. The override is not applied if the mob's follow range is already higher than this. Set to 0 to not override Follow Range.")
    public static Integer followRangeNormal = 28;
    @Config(min = 0)
    @Label(name = "Follow Range.Hard", description = "Override for mobs' follow range on hard difficulty. The override is not applied if the mob's follow range is already higher than this. Set to 0 to not override Follow Range.")
    public static Integer followRangeHard = 32;
    @Config(min = 0)
    @Label(name = "Swim speed", description = "Bonus percentage health given to mobs on hard difficulty.")
    public static Double swimSpeed = 2d;

    public StatsBuffs(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @SubscribeEvent
    public void onMobSpawn(EntityJoinLevelEvent event) {
        if (!this.isEnabled()
                || !(event.getEntity() instanceof Enemy)
                || !(event.getEntity() instanceof LivingEntity entity))
            return;

        CompoundTag persistentData = entity.getPersistentData();
        if (persistentData.getBoolean(STAT_BUFFS_APPLIED))
            return;

        Difficulty difficulty = entity.level.getDifficulty();

        float movementSpeedBonus = 0f;
        float healthBonus = 0f;
        float followRangeOverride = 0f;
        switch (difficulty) {
            case EASY -> {
                movementSpeedBonus = movementSpeedEasy.floatValue();
                healthBonus = healthEasy.floatValue();
                followRangeOverride = followRangeEasy.floatValue();
            }
            case NORMAL -> {
                movementSpeedBonus = movementSpeedNormal.floatValue();
                healthBonus = healthNormal.floatValue();
                followRangeOverride = followRangeNormal.floatValue();
            }
            case HARD -> {
                movementSpeedBonus = movementSpeedHard.floatValue();
                healthBonus = healthHard.floatValue();
                followRangeOverride = followRangeHard.floatValue();
            }
        }

        if (movementSpeedBonus != 0f) {
            MCUtils.applyModifier(entity, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID, "Survival Reimagined Bonus Mov Speed", movementSpeedBonus, AttributeModifier.Operation.MULTIPLY_BASE, true);
        }
        if (healthBonus != 0f) {
            MCUtils.applyModifier(entity, Attributes.MAX_HEALTH, HEALTH_UUID, "Survival Reimagined Bonus Health", healthBonus, AttributeModifier.Operation.MULTIPLY_BASE, true);
        }
        if (followRangeOverride != 0f) {
            AttributeInstance attribute = entity.getAttribute(Attributes.FOLLOW_RANGE);
            if (attribute != null && attribute.getBaseValue() < followRangeOverride)
                MCUtils.setAttributeValue(entity, Attributes.FOLLOW_RANGE, followRangeOverride);
        }
        if (swimSpeed != 0f) {
            MCUtils.applyModifier(entity, ForgeMod.SWIM_SPEED.get(), HEALTH_UUID, "Survival Reimagined Bonus Swim speed", swimSpeed, AttributeModifier.Operation.MULTIPLY_BASE, true);
        }

        persistentData.putBoolean(STAT_BUFFS_APPLIED, true);
    }
}
