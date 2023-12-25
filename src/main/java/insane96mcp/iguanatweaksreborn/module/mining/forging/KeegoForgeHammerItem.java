package insane96mcp.iguanatweaksreborn.module.mining.forging;

import insane96mcp.iguanatweaksreborn.module.mining.keego.Keego;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import org.jetbrains.annotations.Nullable;

public class KeegoForgeHammerItem extends ForgeHammerItem {
    public KeegoForgeHammerItem(Tier tier, int useCooldown, int useDamageTaken, Properties pProperties) {
        super(tier, useCooldown, useDamageTaken, pProperties);
    }

    @Override
    public int getUseCooldown(@Nullable LivingEntity entity, ItemStack stack) {
        int cooldown = super.getUseCooldown(entity, stack);
        if (entity == null || !entity.hasEffect(Keego.ATTACK_MOMENTUM.get()))
            return cooldown;
        int amplifier = entity.getEffect(Keego.ATTACK_MOMENTUM.get()).getAmplifier();
        return cooldown - ((amplifier + 1));
    }

    @Override
    public void onUse(Player player, ItemStack stack) {
        int cooldown = this.getUseCooldown(player, stack);
        int amplifier = 0;
        if (player.hasEffect(Keego.ATTACK_MOMENTUM.get()))
            //noinspection DataFlowIssue
            amplifier = player.getEffect(Keego.ATTACK_MOMENTUM.get()).getAmplifier() + 1;
        double duration = cooldown * 2;
        player.addEffect(new MobEffectInstance(Keego.ATTACK_MOMENTUM.get(), (int) Math.max(duration, 10), Math.min(amplifier, 7), false, false, true));
        super.onUse(player, stack);
    }
}
