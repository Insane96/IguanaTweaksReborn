package insane96mcp.survivalreimagined.module.mining.keego;

import insane96mcp.survivalreimagined.module.farming.hoes.IHoeCooldownModifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class KeegoHoeItem extends HoeItem implements IHoeCooldownModifier {
    public KeegoHoeItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        InteractionResult interactionResult = super.useOn(context);
        if (interactionResult.consumesAction() && context.getPlayer() != null) {
            int amplifier = 0;
            if (context.getPlayer().hasEffect(Keego.ATTACK_MOMENTUM.get()))
                //noinspection DataFlowIssue
                amplifier = context.getPlayer().getEffect(Keego.ATTACK_MOMENTUM.get()).getAmplifier() + 1;

            context.getPlayer().addEffect(new MobEffectInstance(Keego.ATTACK_MOMENTUM.get(), 40, Math.min(amplifier, 7), false, false, true));
        }
        return interactionResult;
    }

    @Override
    public int getCooldownOnUse(int baseCooldown, Player player, Level level) {
        if (!player.hasEffect(Keego.ATTACK_MOMENTUM.get()))
            return baseCooldown;

        return baseCooldown - (player.getEffect(Keego.ATTACK_MOMENTUM.get()).getAmplifier() + 1);
    }
}
