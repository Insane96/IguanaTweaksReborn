package net.insane96mcp.iguanatweaks.potioneffects;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;

public class AlteredPoison extends Potion {

	public static final DamageSource ALTERED_POISON = new DamageSource("altered_poison").setDamageBypassesArmor();
	
	public AlteredPoison(boolean isBadEffectIn, int liquidColorIn) {
		super(isBadEffectIn, liquidColorIn);
	}

	@Override
	public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {		
		entityLivingBaseIn.attackEntityFrom(ALTERED_POISON, 1.0f);
		
		if (entityLivingBaseIn instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)entityLivingBaseIn;
			player.addExhaustion(0.5f * (amplifier + 1) * (entityLivingBaseIn.world.getDifficulty().getId() - 1));
		}
	}
	
	@Override
	public boolean isReady(int duration, int amplifier) {
		int t = 75;
		int j = t >> amplifier;

        if (j > 0)
        {
            return duration % j == 0;
        }
        else
        {
            return true;
        }
	}
	
	@Override
	public Potion setIconIndex(int x, int y) {
		return super.setIconIndex(x, y);
	}
	
	@Override
	protected Potion setEffectiveness(double effectivenessIn) {
		return super.setEffectiveness(0.25d);
	}
}
