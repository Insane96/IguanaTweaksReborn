package insane96mcp.survivalreimagined.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * Fired after an entity has been damaged in LivingEntity#actuallyHurt.
 */
public class PostEntityHurtEvent extends LivingEvent {

    final DamageSource damageSource;
    final float amount;
    public PostEntityHurtEvent(LivingEntity livingEntity, DamageSource damageSource, float amount) {
        super(livingEntity);
        this.damageSource = damageSource;
        this.amount = amount;
    }

    public DamageSource getDamageSource() {
        return damageSource;
    }

    public float getAmount() {
        return amount;
    }
}
