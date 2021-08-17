package insane96mcp.iguanatweaksreborn.modules.combat.feature;

import insane96mcp.iguanatweaksreborn.modules.combat.classutils.ItemAttributeModifier;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TridentItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

@Label(name = "Stats", description = "Various changes from weapons damage to armor reduction")
public class StatsFeature extends Feature {
	private final ForgeConfigSpec.ConfigValue<Boolean> reduceWeaponsDamageConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> nerfPowerConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> disableCritArrowsConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> armorAdjustmentsConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> nerfProtectionEnchConfig;

	public boolean reduceWeaponDamage = true;
	public boolean nerfPower = true;
	public boolean disableCritArrows = true;
	public boolean armorAdjustments = true;
	public boolean nerfProtectionEnch = false;

	public StatsFeature(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		reduceWeaponsDamageConfig = Config.builder
				.comment("If true, Swords, Axes and Tridents get a -1 damage.")
				.define("Reduced Weapon Damage", reduceWeaponDamage);
		nerfPowerConfig = Config.builder
				.comment("If true, Power Enchantment will be nerfed to deal half damage.")
				.define("Nerf Power", this.nerfPower);
		disableCritArrowsConfig = Config.builder
				.comment("If true, Arrows will no longer randomly crit.")
				.define("Disable Arrow Crits", this.disableCritArrows);
		armorAdjustmentsConfig = Config.builder
				.comment("If true, Diamond armor will get -1 toughness, Netherite gets a total of +2 armor points and Protection is disabled.")
				.define("Armor Adjustments", armorAdjustments);
		nerfProtectionEnchConfig = Config.builder
				.comment("If true and 'Armor Adjustments' is active, Protection will be re-enabled but with max level set to 3.")
				.define("Nerf Protection Enchantment", this.nerfProtectionEnch);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.reduceWeaponDamage = this.reduceWeaponsDamageConfig.get();
		this.nerfPower = this.nerfPowerConfig.get();
		this.disableCritArrows = this.disableCritArrowsConfig.get();
		this.armorAdjustments = this.armorAdjustmentsConfig.get();
		this.nerfProtectionEnch = this.nerfProtectionEnchConfig.get();
	}

	@SubscribeEvent
	public void onArrowSpawn(EntityJoinWorldEvent event) {
		if (!this.isEnabled())
			return;
		if (!(event.getEntity() instanceof AbstractArrowEntity))
			return;

		AbstractArrowEntity arrow = (AbstractArrowEntity) event.getEntity();

		if (this.disableCritArrows)
			arrow.setIsCritical(false);

		if (this.nerfPower && arrow.func_234616_v_() instanceof LivingEntity) {
			int powerLevel = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.POWER, (LivingEntity) arrow.func_234616_v_());
			arrow.setDamage(arrow.getDamage() - (powerLevel * 0.25 + 0.25));
		}
	}

	@SubscribeEvent
	public void onAttributeEvent(ItemAttributeModifierEvent event) {
		weaponDamageReduction(event);
		armorAdjustments(event);
	}

	public static final List<ItemAttributeModifier> CLASS_ATTRIBUTE_MODIFIER = Arrays.asList(
		new ItemAttributeModifier((ResourceLocation) null, SwordItem.class, EquipmentSlotType.MAINHAND, Attributes.ATTACK_DAMAGE, -1d, AttributeModifier.Operation.ADDITION),
		new ItemAttributeModifier((ResourceLocation) null, AxeItem.class, EquipmentSlotType.MAINHAND, Attributes.ATTACK_DAMAGE, -1d, AttributeModifier.Operation.ADDITION),
		new ItemAttributeModifier((ResourceLocation) null, TridentItem.class, EquipmentSlotType.MAINHAND, Attributes.ATTACK_DAMAGE, -1d, AttributeModifier.Operation.ADDITION)
	);

	private void weaponDamageReduction(ItemAttributeModifierEvent event) {
		if (!this.isEnabled())
			return;
		if (!this.reduceWeaponDamage)
			return;
		for (ItemAttributeModifier itemAttributeModifier : CLASS_ATTRIBUTE_MODIFIER) {
			if (itemAttributeModifier.itemClass.equals(event.getItemStack().getItem().getClass()) && itemAttributeModifier.slot.equals(event.getSlotType())) {
				AttributeModifier modifier = new AttributeModifier(Strings.AttributeModifiers.GENERIC_ITEM_MODIFIER_UUID, Strings.AttributeModifiers.GENERIC_ITEM_MODIFIER, itemAttributeModifier.amount, itemAttributeModifier.operation);
				event.addModifier(itemAttributeModifier.attribute, modifier);
			}
		}
	}

	public static final List<ItemAttributeModifier> ITEM_ATTRIBUTE_MODIFIER = Arrays.asList(
			new ItemAttributeModifier("minecraft:diamond_helmet", null, EquipmentSlotType.HEAD, Attributes.ARMOR_TOUGHNESS, -1f, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier("minecraft:diamond_chestplate", null, EquipmentSlotType.CHEST, Attributes.ARMOR_TOUGHNESS, -1f, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier("minecraft:diamond_leggings", null, EquipmentSlotType.LEGS, Attributes.ARMOR_TOUGHNESS, -1f, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier("minecraft:diamond_boots", null, EquipmentSlotType.FEET, Attributes.ARMOR_TOUGHNESS, -1f, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier("minecraft:netherite_helmet", null, EquipmentSlotType.HEAD, Attributes.ARMOR, 1f, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier("minecraft:netherite_boots", null, EquipmentSlotType.FEET, Attributes.ARMOR, 1f, AttributeModifier.Operation.ADDITION)
	);

	private void armorAdjustments(ItemAttributeModifierEvent event) {
		if (!this.isEnabled())
			return;
		if (!this.armorAdjustments)
			return;
		for (ItemAttributeModifier itemAttributeModifier : ITEM_ATTRIBUTE_MODIFIER) {
			if (itemAttributeModifier.itemId.equals(event.getItemStack().getItem().getRegistryName()) && itemAttributeModifier.slot.equals(event.getSlotType())) {
				AttributeModifier modifier = new AttributeModifier(Strings.AttributeModifiers.GENERIC_ITEM_MODIFIER_UUID, Strings.AttributeModifiers.GENERIC_ITEM_MODIFIER, itemAttributeModifier.amount, itemAttributeModifier.operation);
				event.addModifier(itemAttributeModifier.attribute, modifier);
			}
		}
	}

	public boolean disableEnchantment(Enchantment enchantment) {
		return enchantment == Enchantments.PROTECTION && this.armorAdjustments && !this.nerfProtectionEnch;
	}
}
