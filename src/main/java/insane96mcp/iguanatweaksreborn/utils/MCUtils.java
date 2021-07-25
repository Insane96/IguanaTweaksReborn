package insane96mcp.iguanatweaksreborn.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;

public class MCUtils {

	public static boolean canBlockDamageSource(DamageSource damageSourceIn, LivingEntity livingEntity) {
		Entity entity = damageSourceIn.getImmediateSource();
		boolean flag = false;
		if (entity instanceof AbstractArrowEntity) {
			AbstractArrowEntity abstractarrowentity = (AbstractArrowEntity) entity;
			if (abstractarrowentity.getPierceLevel() > 0) {
				flag = true;
			}
		}

		if (livingEntity.isActiveItemStackBlocking() && !flag) {
			Vector3d vector3d2 = damageSourceIn.getDamageLocation();
			if (vector3d2 != null) {
				Vector3d vector3d = livingEntity.getLook(1.0F);
				Vector3d vector3d1 = vector3d2.subtractReverse(livingEntity.getPositionVec()).normalize();
				vector3d1 = new Vector3d(vector3d1.x, 0.0D, vector3d1.z);
				if (vector3d1.dotProduct(vector3d) < 0.0D) {
					return true;
				}
			}
		}
		return false;
	}

	public static int getEnchantmentLevel(ResourceLocation enchID, ItemStack stack) {
		if (stack.isEmpty())
			return 0;
		ListNBT listnbt = stack.getEnchantmentTagList();
		for (int i = 0; i < listnbt.size(); ++i) {
			CompoundNBT compoundnbt = listnbt.getCompound(i);
			ResourceLocation itemEnchantment = ResourceLocation.tryCreate(compoundnbt.getString("id"));
			if (itemEnchantment != null && itemEnchantment.equals(enchID)) {
				return MathHelper.clamp(compoundnbt.getInt("lvl"), 0, 255);
			}
		}
		return 0;
	}

	public static EffectInstance createEffectInstance(Effect potion, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon, boolean canBeCured) {
		EffectInstance effectInstance = new EffectInstance(potion, duration, amplifier, ambient, showParticles, showIcon);
		if (!canBeCured)
			effectInstance.setCurativeItems(new ArrayList<>());
		return effectInstance;
	}
}
