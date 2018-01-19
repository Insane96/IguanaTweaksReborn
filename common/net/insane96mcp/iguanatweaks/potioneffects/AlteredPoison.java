package net.insane96mcp.iguanatweaks.potioneffects;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;

public class AlteredPoison extends Potion {

	public AlteredPoison(boolean isBadEffectIn, int liquidColorIn) {
		super(isBadEffectIn, liquidColorIn);
	}

	@Override
	public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
		if (this != MobEffects.POISON)
			return;
		
		entityLivingBaseIn.attackEntityFrom(DamageSource.MAGIC, 1.0f);
		
		if (entityLivingBaseIn instanceof EntityPlayer) {
			((EntityPlayer) entityLivingBaseIn).addExhaustion((float) (0.05 * amplifier));
		}
	}
	
	@Override
	public boolean isReady(int duration, int amplifier) {
		int j = 100 >> amplifier;

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
	public Potion setIconIndex(int p_76399_1_, int p_76399_2_) {
		return super.setIconIndex(p_76399_1_, p_76399_2_);
	}
	
	@Override
	protected Potion setEffectiveness(double effectivenessIn) {
		return super.setEffectiveness(0.25d);
	}
}
