package insane96mcp.iguanatweaksreborn.module.movement.feature;

import com.google.common.collect.Multimap;
import insane96mcp.iguanatweaksreborn.module.combat.feature.Stats;
import insane96mcp.iguanatweaksreborn.module.combat.utils.ItemAttributeModifier;
import insane96mcp.iguanatweaksreborn.module.movement.utils.ArmorEnchantmentWeight;
import insane96mcp.iguanatweaksreborn.module.movement.utils.ArmorMaterialWeight;
import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.insanelib.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Label(name = "Weighted Equipment", description = "Armor and Shield slows down the player.")
public class WeightedEquipment extends Feature {

	private final ForgeConfigSpec.ConfigValue<Double> slownessPerArmorConfig;
	private final ForgeConfigSpec.ConfigValue<Double> percentagePerToughnessConfig;
	private final ForgeConfigSpec.ConfigValue<List<? extends String>> materialWeightConfig;
	private final ForgeConfigSpec.ConfigValue<List<? extends String>> enchantmentsListConfig;
	private final ForgeConfigSpec.ConfigValue<Double> shieldSlowdownConfig;
	//private final ForgeConfigSpec.ConfigValue<List<? extends String>> customWeightConfig;

	private static final List<String> materialWeightDefault = Arrays.asList("leather,0.04", "chainmail,0.10", "golden,0.07", "iron,0.13", "diamond,0.18", "netherite,0.25");

	private static final List<String> enchantmentsListDefault = Arrays.asList("minecraft:feather_falling,0.10", "elenaidodge2:lightweight,0.15,5");

	public double slownessPerArmor = 0.01d;
	public double percentagePerToughness = 0.025d;
	public ArrayList<ArmorMaterialWeight> materialWeight;
	public ArrayList<ArmorEnchantmentWeight> enchantmentsList;
	public double shieldSlowdown = 0.15d;

	// 11 - 16 - 15 - 13
	private final HashMap<EquipmentSlot, Double> armorDurabilityRatio = new HashMap<>();

	public WeightedEquipment(Module module) {
		super(ITCommonConfig.builder, module);
		ITCommonConfig.builder.comment(this.getDescription()).push(this.getName());
		slownessPerArmorConfig = ITCommonConfig.builder
				.comment("Percentage slowdown per point of armor the player is wearing.")
				.defineInRange("Slowdown per Armor", slownessPerArmor, 0d, 1d);
		percentagePerToughnessConfig = ITCommonConfig.builder
				.comment("""
						This value times the Armor Toughness worn by the player is a percentage increase of the Slowdown per Armor.
						Total percentage slowdown is '(slowness_per_armor * armor_points) * (1 + (toughness * percentage_per_toughness))'
						E.g. with 'Slowness per Armor' set to 0.02 and this set to 0.04 and the player wearing Diamond Armor the slowdown is '(0.02 * 20) * (1 + (8 * 0.04))' = '0.4 * 1.32'= '0.528' = -52.8% Speed applied to the player.""")
				.defineInRange("Percentage Increase per Toughness", percentagePerToughness, 0d, 1d);
		materialWeightConfig = ITCommonConfig.builder
				.comment("Define here a list of total slowdown percentage (with full armor) per material. This has priority over 'Slowdown per Armor' and 'Percentage Increase per Toughness'. Material's names are the names in the armor's ids. E.g. Gold Armor is 'golden' as the ids are like 'golden_chestplate'.\n" +
						"Format is material,total_slowdown")
				.defineList("Material Weight", materialWeightDefault, o -> o instanceof String);
		enchantmentsListConfig = ITCommonConfig.builder
				.comment("""
						Define here a list of Enchantments that will reduce the slowdown on the armor piece having the enchantment.
						Format is modid:enchantmentid,reductionPerLevel,flatReduction
						Where reduction per level is the percentage slowdown reduction per level, while flatReduction (optional) is a flat percentage slowdown reduction. E.g. 'elenaidodge2:lightweight,0.15,0.05' means that you'll get 5% less slowdown on armor plus 15% per level, so at Lightweight II you'll get (5+15*2) = 35% reduction on that piece of armor.
						Note that the percentage reduction is on the percentage slowdown, and not a flat reduction. E.g. With Feather Falling II on a full chainmail armor you get slowed down by 8% instead of 10%.""")
				.defineList("Enchantments Weight Reduction", enchantmentsListDefault, o -> o instanceof String);
		shieldSlowdownConfig = ITCommonConfig.builder
				.comment("Shields will slowdown the player by this percentage.")
				.defineInRange("Shield Slowdown", this.shieldSlowdown, 0d, 1d);
		ITCommonConfig.builder.pop();

		armorDurabilityRatio.put(EquipmentSlot.HEAD, 0.2d);
		armorDurabilityRatio.put(EquipmentSlot.CHEST, 0.290909091d);
		armorDurabilityRatio.put(EquipmentSlot.LEGS, 0.272727273d);
		armorDurabilityRatio.put(EquipmentSlot.FEET, 0.236363636d);
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.slownessPerArmor = this.slownessPerArmorConfig.get();
		this.percentagePerToughness = this.percentagePerToughnessConfig.get();
		this.materialWeight = ArmorMaterialWeight.parseStringList(this.materialWeightConfig.get());
		this.enchantmentsList = ArmorEnchantmentWeight.parseStringList(this.enchantmentsListConfig.get());
		this.shieldSlowdown = this.shieldSlowdownConfig.get();
		if (this.shieldSlowdown > 0d) {
			Stats.CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(ShieldItem.class, EquipmentSlot.MAINHAND, Attributes.MOVEMENT_SPEED, -this.shieldSlowdown, AttributeModifier.Operation.MULTIPLY_BASE));
			Stats.CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(ShieldItem.class, EquipmentSlot.OFFHAND, Attributes.MOVEMENT_SPEED, -this.shieldSlowdown, AttributeModifier.Operation.MULTIPLY_BASE));
		}
	}

	//Can't use ItemAttributeModifierEvent as I need all the modifiers of the item (ItemStack#getAttributeModifiers) and that causes a loop
	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END)
			return;
		Player player = event.player;
		AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
		if (movementSpeed == null)
			return;
		AttributeModifier modifier = movementSpeed.getModifier(Strings.AttributeModifiers.ARMOR_SLOWDOWN_UUID);
		if (!this.isEnabled()) {
			//If the feature has been disabled remove the slowdown from the player
			if (modifier != null)
				movementSpeed.removeModifier(modifier);
			return;
		}
		double slowdown = 0d;
		for (ItemStack stack : player.getArmorSlots()) {
			slowdown += getArmorSlowdown(stack);
		}
		//If it's 0 then there's no slowdown applicable
		if (slowdown == 0d) {
			if (modifier != null)
				movementSpeed.removeModifier(modifier);
			return;
		}
		if (modifier == null || modifier.getAmount() != slowdown) {
			modifier = new AttributeModifier(Strings.AttributeModifiers.ARMOR_SLOWDOWN_UUID, Strings.AttributeModifiers.ARMOR_SLOWDOWN, slowdown, AttributeModifier.Operation.MULTIPLY_BASE);
			movementSpeed.removeModifier(Strings.AttributeModifiers.ARMOR_SLOWDOWN_UUID);
			movementSpeed.addTransientModifier(modifier);
		}
	}

	private double getArmorSlowdown(ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ArmorItem))
			return 0d;
		double slowdown = 0d;
		boolean noMaterialSlowdown = false;
		for (ArmorMaterialWeight armorMaterialWeight : this.materialWeight) {
			if (!itemStack.getItem().getRegistryName().getPath().contains(armorMaterialWeight.id))
				continue;
			ArmorItem armor = (ArmorItem) itemStack.getItem();
			EquipmentSlot slot = armor.getSlot();
			double armorPieceSlowdown = armorMaterialWeight.totalWeight * armorDurabilityRatio.get(slot);
			if (armorMaterialWeight.totalWeight == 0d)
				noMaterialSlowdown = true;
			slowdown = -armorPieceSlowdown;
			break;
		}
		//If no slowdown was found in the material weight
		if (slowdown == 0d && !noMaterialSlowdown) {
			ArmorItem armorItem = (ArmorItem) itemStack.getItem();
			Multimap<Attribute, AttributeModifier> attributeModifiers = itemStack.getAttributeModifiers(armorItem.getSlot());
			double armor = 0d;
			for (AttributeModifier attributeModifier : attributeModifiers.get(Attributes.ARMOR)) {
				if (!attributeModifier.getOperation().equals(AttributeModifier.Operation.ADDITION))
					continue;
				armor += attributeModifier.getAmount();
			}
			double armorToughness = 0d;
			for (AttributeModifier attributeModifier : attributeModifiers.get(Attributes.ARMOR_TOUGHNESS)) {
				if (!attributeModifier.getOperation().equals(AttributeModifier.Operation.ADDITION))
					continue;
				armorToughness += attributeModifier.getAmount();
			}
			double armorSlowdown = armor * this.slownessPerArmor;
			double toughnessSlowdown = armorToughness * this.percentagePerToughness;
			slowdown = -(armorSlowdown * (1 + toughnessSlowdown));
		}
		double enchantmentSlowdownReduction = 0d;
		for (ArmorEnchantmentWeight enchantmentWeight : enchantmentsList) {
			int enchantmentLevel = MCUtils.getEnchantmentLevel(enchantmentWeight.location, itemStack);
			if (enchantmentLevel == 0)
				continue;
			enchantmentSlowdownReduction += (enchantmentWeight.flatSlownessReduction + (enchantmentWeight.slownessReductionPerLevel * enchantmentLevel));
		}
		slowdown *= 1 - enchantmentSlowdownReduction;
		return slowdown;
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent event) {
		if (!this.isEnabled())
			return;
		ItemStack stack = event.getItemStack();
		double slowdown = -getArmorSlowdown(stack) * 100d;
		if (slowdown <= 0d)
			return;
		event.getToolTip().add(new TranslatableComponent(Strings.Translatable.ARMOR_SLOWDOWN, Utils.formatDecimal(slowdown, "#.#")).withStyle(ChatFormatting.RED));
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void debugScreen(RenderGameOverlayEvent.Text event) {
		if (!this.isEnabled())
			return;
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer playerEntity = mc.player;
		if (playerEntity == null)
			return;
		AttributeInstance movementSpeed = playerEntity.getAttribute(Attributes.MOVEMENT_SPEED);
		if (movementSpeed == null)
			return;
		AttributeModifier modifier = movementSpeed.getModifier(Strings.AttributeModifiers.ARMOR_SLOWDOWN_UUID);
		if (mc.options.renderDebug && modifier != null) {
			event.getLeft().add(String.format("Armor Slowdown: %s%%", Utils.formatDecimal(Math.abs(modifier.getAmount()) * 100f, "#.#")));
		}
	}
}