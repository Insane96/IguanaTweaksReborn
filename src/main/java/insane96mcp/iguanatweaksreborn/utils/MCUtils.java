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
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;

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

	public static EffectInstance parseEffectInstance(String s) {
		String[] split = s.split(",");
		if (split.length != 3) {
			LogHelper.warn("Invalid line \"%s\" for EffectInstance. Format must be modid:potion_id,duration_ticks,amplifier", s);
			return null;
		}
		if (!NumberUtils.isParsable(split[1])) {
			LogHelper.warn(String.format("Invalid duration \"%s\" for EffectInstance", split[1]));
			return null;
		}
		int duration = Integer.parseInt(split[1]);
		if (!NumberUtils.isParsable(split[2])) {
			LogHelper.warn(String.format("Invalid amplifier \"%s\" for EffectInstance", split[1]));
			return null;
		}
		int amplifier = Integer.parseInt(split[2]);
		ResourceLocation potion = ResourceLocation.tryCreate(split[0]);
		if (potion == null) {
			LogHelper.warn("%s potion for EffectInstance is not valid", s);
			return null;
		}
		if (!ForgeRegistries.POTIONS.containsKey(potion)) {
			LogHelper.warn(String.format("%s potion for EffectInstance seems to not exist", s));
			return null;
		}

		return new EffectInstance(ForgeRegistries.POTIONS.getValue(potion), duration, amplifier, true, true);
	}

	public static boolean hurtIgnoreInvuln(LivingEntity hurtEntity, DamageSource source, float amount) {
		int hurtResistantTime = hurtEntity.hurtResistantTime;
		hurtEntity.hurtResistantTime = 0;
		boolean attacked = hurtEntity.attackEntityFrom(source, amount);
		hurtEntity.hurtResistantTime = hurtResistantTime;
		return attacked;
	}
}
