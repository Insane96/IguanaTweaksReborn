package insane96mcp.iguanatweaksreborn.module.combat.feature;

import com.google.common.collect.Lists;
import insane96mcp.iguanatweaksreborn.module.combat.utils.ItemAttributeModifier;
import insane96mcp.iguanatweaksreborn.setup.Config;
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
import net.minecraft.world.item.ShieldItem;
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
	private final ForgeConfigSpec.ConfigValue<Boolean> reduceWeaponsDamageConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> nerfPowerConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> disableCritArrowsConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> adjustCrossbowDamageConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> shieldSlowdownConfig;
	private final ForgeConfigSpec.ConfigValue<ProtectionNerf> protectionNerfConfig;
	private final ForgeConfigSpec.ConfigValue<List<? extends String>> itemModifiersConfig;

	private static final ArrayList<String> itemModifiersDefault = Lists.newArrayList("minecraft:iron_helmet,HEAD,minecraft:generic.armor_toughness,0.5,ADDITION", "minecraft:iron_chestplate,CHEST,minecraft:generic.armor_toughness,0.5,ADDITION", "minecraft:iron_leggings,LEGS,minecraft:generic.armor_toughness,0.5,ADDITION", "minecraft:iron_boots,FEET,minecraft:generic.armor_toughness,0.5,ADDITION", "minecraft:netherite_helmet,HEAD,minecraft:generic.armor,1,ADDITION", "minecraft:netherite_boots,FEET,minecraft:generic.armor,1,ADDITION");

	public boolean reduceWeaponDamage = true;
	public boolean nerfPower = true;
	public boolean disableCritArrows = true;
	public boolean adjustCrossbowDamage = true;
	public boolean shieldSlowdown = true;
	public ProtectionNerf protectionNerf = ProtectionNerf.DISABLE;
	public List<ItemAttributeModifier> itemModifiers;

	public Stats(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		reduceWeaponsDamageConfig = Config.builder
				.comment("If true, Swords, Axes and Tridents get a -1 damage.")
				.define("Reduce Weapon Damage", reduceWeaponDamage);
		nerfPowerConfig = Config.builder
				.comment("If true, Power Enchantment will be nerfed to deal half damage.")
				.define("Nerf Power", this.nerfPower);
		disableCritArrowsConfig = Config.builder
				.comment("If true, Arrows from Bows will no longer randomly crit (basically disables the random bonus damage given when firing a fully charged arrow).")
				.define("Disable Arrow Crits", this.disableCritArrows);
		adjustCrossbowDamageConfig = Config.builder
				.comment("If true, Arrows from Crossbows will no longer deal random damage, but a set amount of damage (about 9 at a medium distance).")
				.define("Adjust Crossbow Damage", this.adjustCrossbowDamage);
		shieldSlowdownConfig = Config.builder
				.comment("If true, Shields will slowdown the player by 25%.")
				.define("Shield Slowdown", shieldSlowdown);
		protectionNerfConfig = Config.builder
				.comment("DISABLE: Disables protection enchantment.\n" +
						"NERF: Sets max protection level to 3 instead of 4\n" +
						"NONE: no changes to protection are done")
				.defineEnum("Nerf Protection Enchantment", this.protectionNerf);
		itemModifiersConfig = Config.builder
				.comment("Define Attribute Modifiers to apply to single items, one string = one item/tag.\n" +
						"The format is modid:itemid,slot,attribute,amount,operation (or tag instead of itemid #modid:tagid,...)\n" +
						"- slot can be: MAIN_HAND,OFF_HAND,HEAD,CHEST,LEGS,FEET\n" +
						"- operation can be: ADDITION, MULTIPLY_BASE, MULTIPLY_TOTAL")
				.defineList("Item Modifiers", itemModifiersDefault, o -> o instanceof String);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.reduceWeaponDamage = this.reduceWeaponsDamageConfig.get();
		this.nerfPower = this.nerfPowerConfig.get();
		this.disableCritArrows = this.disableCritArrowsConfig.get();
		this.adjustCrossbowDamage = this.adjustCrossbowDamageConfig.get();
		this.shieldSlowdown = this.shieldSlowdownConfig.get();
		this.protectionNerf = this.protectionNerfConfig.get();

		CLASS_ATTRIBUTE_MODIFIER.clear();
		if (this.reduceWeaponDamage) {
			CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(SwordItem.class, EquipmentSlot.MAINHAND, Attributes.ATTACK_DAMAGE, -1d, AttributeModifier.Operation.ADDITION));
			CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(AxeItem.class, EquipmentSlot.MAINHAND, Attributes.ATTACK_DAMAGE, -1d, AttributeModifier.Operation.ADDITION));
			CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(TridentItem.class, EquipmentSlot.MAINHAND, Attributes.ATTACK_DAMAGE, -1d, AttributeModifier.Operation.ADDITION));
		}
		if (this.shieldSlowdown) {
			CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(ShieldItem.class, EquipmentSlot.MAINHAND, Attributes.MOVEMENT_SPEED, -0.25d, AttributeModifier.Operation.MULTIPLY_BASE));
			CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(ShieldItem.class, EquipmentSlot.OFFHAND, Attributes.MOVEMENT_SPEED, -0.25d, AttributeModifier.Operation.MULTIPLY_BASE));
		}

		itemModifiers = (List<ItemAttributeModifier>) ItemAttributeModifier.parseStringList(this.itemModifiersConfig.get());
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

		if (this.nerfPower && arrow.getOwner() instanceof LivingEntity) {
			int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER_ARROWS, (LivingEntity) arrow.getOwner());
			arrow.setBaseDamage(arrow.getBaseDamage() - (powerLevel * 0.25 + 0.25));
		}
	}

	private void processCrossbow(AbstractArrow arrow) {
		if (this.adjustCrossbowDamage) {
			arrow.setCritArrow(false);
			arrow.setBaseDamage(2.8d);
		}
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