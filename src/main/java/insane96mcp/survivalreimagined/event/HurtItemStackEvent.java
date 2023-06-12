package insane96mcp.survivalreimagined.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Fired on ItemStack#hurt after unbreaking has been applied to the amount.
 */
public class HurtItemStackEvent extends PlayerEvent {

    final ItemStack stack;
    final int originalAmount;
    int amount;
    RandomSource randomSource;
    public HurtItemStackEvent(ItemStack stack, int amount, RandomSource random, @Nullable ServerPlayer player) {
        super(player);
        this.stack = stack;
        this.originalAmount = amount;
        this.amount = amount;
        this.randomSource = random;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getOriginalAmount() {
        return this.originalAmount;
    }

    @Nullable
    public ServerPlayer getPlayer() {
        return (ServerPlayer) this.getEntity();
    }

    public RandomSource getRandom() {
        return this.randomSource;
    }

    public ItemStack getStack() {
        return this.stack;
    }
}
