package insane96mcp.iguanatweaksreborn.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class StaminaEvent extends PlayerEvent {
    public StaminaEvent(Player player)
    {
        super(player);
    }

    public static class Consumed extends StaminaEvent {

        private float amount;
        private final float originalAmount;

        public Consumed(Player player, float amount) {
            super(player);
            this.amount = amount;
            this.originalAmount = amount;
        }

        public float getAmount() {
            return this.amount;
        }

        public void setAmount(float amount) {
            this.amount = amount;
        }

        public float getOriginalAmount() {
            return this.originalAmount;
        }
    }

    public static class Regenerated extends StaminaEvent {

        private float amount;
        private final float originalAmount;

        public Regenerated(Player player, float amount) {
            super(player);
            this.amount = amount;
            this.originalAmount = amount;
        }

        public float getAmount() {
            return this.amount;
        }

        public void setAmount(float amount) {
            this.amount = amount;
        }

        public float getOriginalAmount() {
            return this.originalAmount;
        }
    }
}
