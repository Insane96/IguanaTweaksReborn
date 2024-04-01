package insane96mcp.iguanatweaksreborn.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import insane96mcp.iguanatweaksreborn.module.mobs.equipment.Equipment;
import insane96mcp.insanelib.base.Feature;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {
	protected MobMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
	}

	//Fixes mobs not dropping bonus experience if the drop chance of armor/held items is set higher than 1f
	@ModifyConstant(method = "getExperienceReward", constant = @Constant(floatValue = 1f))
	public float onDropChanceCheck(float dropChance) {
		return 100f;
	}

	@ModifyArg(method = "dropCustomDeathLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;setDamageValue(I)V"))
	public int damageValue(int damageValue, @Local ItemStack stack) {
		if (!Feature.isEnabled(Equipment.class))
			return Math.min(damageValue, stack.getMaxDamage());
		//The max durability doesn't work properly
		return stack.getMaxDamage() - this.random.nextInt((int) (stack.getMaxDamage() * Equipment.maxDurability));
	}
}
