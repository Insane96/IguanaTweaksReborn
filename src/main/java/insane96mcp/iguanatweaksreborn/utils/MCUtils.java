package insane96mcp.iguanatweaksreborn.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;

public class MCUtils {
	public static boolean canBlockDamageSource(DamageSource damageSourceIn, LivingEntity livingEntity) {
		Entity entity = damageSourceIn.getDirectEntity();
		boolean flag = false;
		if (entity instanceof AbstractArrow abstractArrow) {
			if (abstractArrow.getPierceLevel() > 0) {
				flag = true;
			}
		}

		if (livingEntity.isBlocking() && !flag) {
			Vec3 sourcePosition = damageSourceIn.getSourcePosition();
			if (sourcePosition != null) {
				Vec3 livingEntityViewVector = livingEntity.getViewVector(1.0F);
				Vec3 vec3 = sourcePosition.vectorTo(livingEntity.position()).normalize();
				vec3 = new Vec3(vec3.x, 0.0D, vec3.z);
				if (vec3.dot(livingEntityViewVector) < 0.0D) {
					return true;
				}
			}
		}
		return false;
	}

	public static int getEnchantmentLevel(ResourceLocation enchID, ItemStack stack) {
		if (stack.isEmpty())
			return 0;
		ListTag listnbt = stack.getEnchantmentTags();
		for (int i = 0; i < listnbt.size(); ++i) {
			CompoundTag compoundTag = listnbt.getCompound(i);
			ResourceLocation itemEnchantment = ResourceLocation.tryParse(compoundTag.getString("id"));
			if (itemEnchantment != null && itemEnchantment.equals(enchID)) {
				return Mth.clamp(compoundTag.getInt("lvl"), 0, 255);
			}
		}
		return 0;
	}

	public static MobEffectInstance createEffectInstance(MobEffect potion, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon, boolean canBeCured) {
		MobEffectInstance effectInstance = new MobEffectInstance(potion, duration, amplifier, ambient, showParticles, showIcon);
		if (!canBeCured)
			effectInstance.setCurativeItems(new ArrayList<>());
		return effectInstance;
	}

	public static MobEffectInstance parseEffectInstance(String s) {
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
		ResourceLocation potion = ResourceLocation.tryParse(split[0]);
		if (potion == null) {
			LogHelper.warn("%s potion for EffectInstance is not valid", s);
			return null;
		}
		if (!ForgeRegistries.POTIONS.containsKey(potion)) {
			LogHelper.warn(String.format("%s potion for EffectInstance seems to not exist", s));
			return null;
		}

		return new MobEffectInstance(ForgeRegistries.MOB_EFFECTS.getValue(potion), duration, amplifier, true, true);
	}
}
