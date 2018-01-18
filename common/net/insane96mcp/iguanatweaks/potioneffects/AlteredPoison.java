package net.insane96mcp.iguanatweaks.potioneffects;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;

public class AlteredPoison extends Potion {

	public AlteredPoison(boolean isBadEffectIn, int liquidColorIn) {
		super(isBadEffectIn, liquidColorIn);
	}

	@Override
	public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
		entityLivingBaseIn.attackEntityFrom(DamageSource.MAGIC, 1.0f * amplifier);
		
		if (entityLivingBaseIn instanceof EntityPlayer) {
			((EntityPlayer) entityLivingBaseIn).addExhaustion((float) (0.05 * amplifier));
		}
		
		System.out.println("performEffect");
	}
	
	@Override
	public boolean isReady(int duration, int amplifier) {
		int j = 25 >> amplifier;

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
	protected Potion setIconIndex(int p_76399_1_, int p_76399_2_) {
		return super.setIconIndex(6, 0);
	}
	
	@Override
	protected Potion setEffectiveness(double effectivenessIn) {
		return super.setEffectiveness(0.25d);
	}
}
