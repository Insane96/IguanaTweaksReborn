package insane96mcp.iguanatweaksreborn.utils;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MCUtils {

    @Nullable
    public static <T extends LivingEntity> T getNearestEntity(List<? extends T> entitiesNearby, List<? extends T> entitiesToExclude, Vec3 pos) {
        double nearestDistance = -1.0D;
        T r = null;

        for(T entity : entitiesNearby) {
            if (entitiesToExclude.contains(entity))
                continue;
            double distance = entity.distanceToSqr(pos);
            if (nearestDistance == -1.0D || distance < nearestDistance) {
                nearestDistance = distance;
                r = entity;
            }
        }

        return r;
    }

    private static final AttributeModifier ANTI_KNOCKBACK_MODIFIER = new AttributeModifier(IguanaTweaksReborn.RESOURCE_PREFIX + "anti_knockback", 1f, AttributeModifier.Operation.ADDITION);

    //Stolen from Tinkers Construct ToolAttackUtil
    public static void attackEntityIgnoreInvFrames(DamageSource source, float damage, Entity target, @Nullable LivingEntity living, boolean noKnockback) {
        Optional<AttributeInstance> knockbackResistance = getKnockbackAttribute(living);
        // store last damage before secondary attack
        float oldLastDamage = living == null ? 0 : living.lastHurt;

        // prevent knockback in secondary attacks, if requested
        if (noKnockback) {
            knockbackResistance.ifPresent(MCUtils::disableKnockback);
        }

        // set hurt resistance time to 0 because we always want to deal damage in traits
        int lastInvulnerableTime = target.invulnerableTime;
        target.invulnerableTime = 0;
        target.hurt(source, damage);
        target.invulnerableTime = lastInvulnerableTime; // reset to the old time so bows work right
        // set total received damage, important for AI and stuff
        if (living != null) {
            living.lastHurt += oldLastDamage;
        }

        // remove no knockback marker
        if (noKnockback) {
            knockbackResistance.ifPresent(MCUtils::enableKnockback);
        }
    }

    /** Gets the knockback attribute instance if the modifier is not already present */
    private static Optional<AttributeInstance> getKnockbackAttribute(@Nullable LivingEntity living) {
        return Optional.ofNullable(living)
                .map(e -> e.getAttribute(Attributes.KNOCKBACK_RESISTANCE))
                .filter(attribute -> !attribute.hasModifier(ANTI_KNOCKBACK_MODIFIER));
    }

    /** Enable the anti-knockback modifier */
    private static void disableKnockback(AttributeInstance instance) {
        instance.addTransientModifier(ANTI_KNOCKBACK_MODIFIER);
    }

    /** Disables the anti knockback modifier */
    private static void enableKnockback(AttributeInstance instance) {
        instance.removeModifier(ANTI_KNOCKBACK_MODIFIER);
    }

    /**
     * Returns the Tag in the player persistent data that is kept on death / dimension change
     */
    public static CompoundTag getOrCreatePersistedData(Player player) {
        CompoundTag tag;
        if (!player.getPersistentData().contains(Player.PERSISTED_NBT_TAG)) {
            tag = new CompoundTag();
            player.getPersistentData().put(Player.PERSISTED_NBT_TAG, tag);
        }
        else {
            tag = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        }
        return tag;
    }

    public static int getDurabilityLeft(ItemStack stack) {
        return stack.getMaxDamage() - stack.getDamageValue();
    }

    public static float getPercentageDurabilityLeft(ItemStack itemStack) {
        Objects.requireNonNull(itemStack, "itemStack can't be null");
        if (!itemStack.isDamageableItem())
            return 0f;

        return ((float) getDurabilityLeft(itemStack)) / itemStack.getMaxDamage();
    }
}
