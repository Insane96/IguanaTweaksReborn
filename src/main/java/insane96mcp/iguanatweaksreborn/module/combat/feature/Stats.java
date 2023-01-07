package insane96mcp.iguanatweaksreborn.module.combat.feature;

import com.google.common.collect.Lists;
import insane96mcp.iguanatweaksreborn.module.combat.utils.ItemAttributeModifier;
import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Stats", description = "Various changes from weapons damage to armor reduction")
public class Stats extends Feature {
	private final ForgeConfigSpec.BooleanValue reduceWeaponsDamageConfig;
	private final ForgeConfigSpec.DoubleValue powerPowerConfig;
	private final ForgeConfigSpec.BooleanValue disableCritArrowsConfig;
	private final ForgeConfigSpec.BooleanValue adjustCrossbowDamageConfig;
	private final ForgeConfigSpec.ConfigValue<ProtectionNerf> protectionNerfConfig;
	private final ForgeConfigSpec.ConfigValue<List<? extends String>> itemModifiersConfig;

	private static final ArrayList<String> itemModifiersDefault = Lists.newArrayList("minecraft:iron_helmet,HEAD,minecraft:generic.armor_toughness,1.0,ADDITION", "minecraft:iron_chestplate,CHEST,minecraft:generic.armor_toughness,1.0,ADDITION", "minecraft:iron_leggings,LEGS,minecraft:generic.armor_toughness,1.0,ADDITION", "minecraft:iron_boots,FEET,minecraft:generic.armor_toughness,1.0,ADDITION", "minecraft:netherite_helmet,HEAD,minecraft:generic.armor,1,ADDITION", "minecraft:netherite_boots,FEET,minecraft:generic.armor,1,ADDITION");

	public boolean reduceWeaponDamage = true;
	public double powerPower = 0.35d;
	public boolean disableCritArrows = true;
	public boolean adjustCrossbowDamage = true;
	public ProtectionNerf protectionNerf = ProtectionNerf.DISABLE;
	public List<ItemAttributeModifier> itemModifiers;

	public Stats(Module module) {
		super(ITCommonConfig.builder, module);
		this.pushConfig(ITCommonConfig.builder);
		reduceWeaponsDamageConfig = ITCommonConfig.builder
				.comment("If true, Swords and Tridents get -1 damage and Axes get -1.5 damage.")
				.define("Reduce Weapon Damage", reduceWeaponDamage);
		powerPowerConfig = ITCommonConfig.builder
				.comment("Set the power of the Power enchantment (vanilla is 0.5).")
				.defineInRange("Power Power", this.powerPower, 0, 10);
		disableCritArrowsConfig = ITCommonConfig.builder
				.comment("If true, Arrows from Bows and Crossbows will no longer randomly crit (basically disables the random bonus damage given when firing a fully charged arrow).")
				.define("Disable Arrow Crits", this.disableCritArrows);
		adjustCrossbowDamageConfig = ITCommonConfig.builder
				.comment("If true, Arrows from Crossbows will no longer deal random damage, but a set amount of damage (about 9 at a medium distance).")
				.define("Adjust Crossbow Damage", this.adjustCrossbowDamage);
		protectionNerfConfig = ITCommonConfig.builder
				.comment("""
						DISABLE: Disables protection enchantment.
						NERF: Sets max protection level to 3 instead of 4
						NONE: no changes to protection are done""")
				.defineEnum("Nerf Protection Enchantment", this.protectionNerf);
		itemModifiersConfig = ITCommonConfig.builder
				.comment("""
						Define Attribute Modifiers to apply to single items, one string = one item/tag.
						The format is modid:itemid,slot,attribute,amount,operation (or tag instead of itemid #modid:tagid,...)
						- slot can be: MAIN_HAND,OFF_HAND,HEAD,CHEST,LEGS,FEET
						- operation can be: ADDITION, MULTIPLY_BASE, MULTIPLY_TOTAL""")
				.defineList("Item Modifiers", itemModifiersDefault, o -> o instanceof String);
		ITCommonConfig.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.reduceWeaponDamage = this.reduceWeaponsDamageConfig.get();
		this.powerPower = this.powerPowerConfig.get();
		this.disableCritArrows = this.disableCritArrowsConfig.get();
		this.adjustCrossbowDamage = this.adjustCrossbowDamageConfig.get();
		this.protectionNerf = this.protectionNerfConfig.get();

		CLASS_ATTRIBUTE_MODIFIER.clear();
		if (this.reduceWeaponDamage) {
			CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(SwordItem.class, EquipmentSlot.MAINHAND, Attributes.ATTACK_DAMAGE, -1d, AttributeModifier.Operation.ADDITION));
			CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(AxeItem.class, EquipmentSlot.MAINHAND, Attributes.ATTACK_DAMAGE, -1.5d, AttributeModifier.Operation.ADDITION));
			CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(TridentItem.class, EquipmentSlot.MAINHAND, Attributes.ATTACK_DAMAGE, -1d, AttributeModifier.Operation.ADDITION));
		}

		itemModifiers = ItemAttributeModifier.parseStringList(this.itemModifiersConfig.get());
	}

	@SubscribeEvent
	public void onArrowSpawn(EntityJoinWorldEvent event) {
		if (!this.isEnabled())
			return;
		if (!(event.getEntity() instanceof AbstractArrow arrow))
			return;
		if (!arrow.shotFromCrossbow())
			processBow(arrow);
		else
			processCrossbow(arrow);
	}

	private void processBow(AbstractArrow arrow) {
		if (this.disableCritArrows)
			arrow.setCritArrow(false);

		if (this.powerPower != 0.5d && arrow.getOwner() instanceof LivingEntity) {
			int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER_ARROWS, (LivingEntity) arrow.getOwner());
			double powerReduction = 0.5d - this.powerPower;
			arrow.setBaseDamage(arrow.getBaseDamage() - (powerLevel * powerReduction + powerReduction));
		}
	}

	private void processCrossbow(AbstractArrow arrow) {
		if (this.disableCritArrows)
			arrow.setCritArrow(false);

		if (this.adjustCrossbowDamage)
			arrow.setBaseDamage(2.8d);
	}

	@SubscribeEvent
	public void onAttributeEvent(ItemAttributeModifierEvent event) {
		if (!this.isEnabled())
			return;

		classAttributeModifiers(event);
		itemAttributeModifiers(event);
	}

	public static final List<ItemAttributeModifier> CLASS_ATTRIBUTE_MODIFIER = new ArrayList<>();

	private void classAttributeModifiers(ItemAttributeModifierEvent event) {
		for (ItemAttributeModifier itemAttributeModifier : CLASS_ATTRIBUTE_MODIFIER) {
			if (itemAttributeModifier.itemClass.equals(event.getItemStack().getItem().getClass()) && itemAttributeModifier.slot.equals(event.getSlotType())) {
				AttributeModifier modifier = new AttributeModifier(Strings.AttributeModifiers.GENERIC_ITEM_MODIFIER_UUID, Strings.AttributeModifiers.GENERIC_ITEM_MODIFIER, itemAttributeModifier.amount, itemAttributeModifier.operation);
				event.addModifier(itemAttributeModifier.attribute, modifier);
			}
		}
	}

	private void itemAttributeModifiers(ItemAttributeModifierEvent event) {
		for (ItemAttributeModifier itemAttributeModifier : this.itemModifiers) {
			if (!itemAttributeModifier.matchesItem(event.getItemStack().getItem()))
				continue;
			if (event.getSlotType() != itemAttributeModifier.slot)
				continue;

			AttributeModifier modifier = new AttributeModifier(Strings.AttributeModifiers.GENERIC_ITEM_MODIFIER_UUID, Strings.AttributeModifiers.GENERIC_ITEM_MODIFIER, itemAttributeModifier.amount, itemAttributeModifier.operation);
			event.addModifier(itemAttributeModifier.attribute, modifier);
		}
	}

	public boolean disableEnchantment(Enchantment enchantment) {
		return enchantment == Enchantments.ALL_DAMAGE_PROTECTION && this.protectionNerf == ProtectionNerf.DISABLE;
	}

	public enum ProtectionNerf {
		NONE, NERF, DISABLE
	}
}