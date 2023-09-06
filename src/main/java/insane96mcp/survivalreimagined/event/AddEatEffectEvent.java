package insane96mcp.survivalreimagined.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Fired before eating effects are applied.
 * <p>
 * Canceling the event will not apply vanilla effects.
 */
@Cancelable
public class AddEatEffectEvent extends LivingEvent {

    ItemStack stack;
    Level level;

    public AddEatEffectEvent(LivingEntity entity, ItemStack stack, Level level) {
        super(entity);
        this.stack = stack;
        this.level = level;
    }

    public ItemStack getStack() {
        return stack;
    }

    public Level getLevel() {
        return level;
    }
}
