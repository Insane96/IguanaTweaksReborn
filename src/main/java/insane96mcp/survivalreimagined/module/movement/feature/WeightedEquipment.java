package insane96mcp.survivalreimagined.module.movement.feature;

import com.google.common.collect.Multimap;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.insanelib.util.Utils;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.combat.feature.Stats;
import insane96mcp.survivalreimagined.module.movement.utils.ArmorEnchantmentWeight;
import insane96mcp.survivalreimagined.module.movement.utils.ArmorMaterialWeight;
import insane96mcp.survivalreimagined.setup.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
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
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

@Label(name = "Weighted Equipment", description = "Armor and Shield slows down the player. Material Weights and Enchantment Weights are controlled via json in this feature's folder")
@LoadFeature(module = Modules.Ids.MOVEMENT)
public class WeightedEquipment extends SRFeature {
	public static final ArrayList<ArmorMaterialWeight> MATERIAL_WEIGHTS_DEFAULTS = new ArrayList<>(Arrays.asList(
			new ArmorMaterialWeight("leather", 0.025d),
			new ArmorMaterialWeight("chained_copper", 0.05d),
			new ArmorMaterialWeight("chainmail", 0.05d),
			new ArmorMaterialWeight("golden", 0.025d),
			new ArmorMaterialWeight("iron", 0.075d),
			new ArmorMaterialWeight("diamond", 0.10d),
			new ArmorMaterialWeight("netherite", 0.15d)
	));
	public static final ArrayList<ArmorMaterialWeight> materialWeight = new ArrayList<>();

	public static final ArrayList<ArmorEnchantmentWeight> ENCHANTMENTS_LIST_DEFAULT = new ArrayList<>(Arrays.asList(
			new ArmorEnchantmentWeight("minecraft:feather_falling", -0.05d),
			new ArmorEnchantmentWeight("elenaidodge2:lightweight", -0.025d)
	));
	public static final ArrayList<ArmorEnchantmentWeight> enchantmentsList = new ArrayList<>();

	@Config(min = 0, max = 1d)
	@Label(name = "Slowdown per Armor", description = "Percentage slowdown per point of armor the player is wearing.")
	public static Double slownessPerArmor = 0.005d;
	@Config(min = 0, max = 1d)
	@Label(name = "Percentage Increase per Toughness", description = """
						This value times the Armor Toughness worn by the player is a percentage increase of the Slowdown per Armor.
						Total percentage slowdown is '(slowness_per_armor * armor_points) * (1 + (toughness * percentage_per_toughness))'
						E.g. with 'Slowness per Armor' set to 0.02 and this set to 0.04 and the player wearing Diamond Armor the slowdown is '(0.02 * 20) * (1 + (8 * 0.04))' = '0.4 * 1.32'= '0.528' = -52.8% Speed applied to the player.""")
	public static Double percentagePerToughness = 0.025d;
	@Config(min = 0, max = 1d)
	@Label(name = "Shield Slowdown", description = "Shields will slowdown the player by this percentage.")
	public static Double shieldSlowdown = 0d;

	// 11 - 16 - 15 - 13
	private static final HashMap<EquipmentSlot, Double> armorDurabilityRatio = new HashMap<>();

	public WeightedEquipment(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);

		armorDurabilityRatio.put(EquipmentSlot.HEAD, 0.2d);
		armorDurabilityRatio.put(EquipmentSlot.CHEST, 0.290909091d);
		armorDurabilityRatio.put(EquipmentSlot.LEGS, 0.272727273d);
		armorDurabilityRatio.put(EquipmentSlot.FEET, 0.236363636d);
	}

	@Override
	public void loadJsonConfigs() {
		if (!this.isEnabled())
			return;
		super.loadJsonConfigs();
		this.loadAndReadFile("enchantments_weights.json", enchantmentsList, ENCHANTMENTS_LIST_DEFAULT, ArmorEnchantmentWeight.LIST_TYPE);
		this.loadAndReadFile("materials_weights.json", materialWeight, MATERIAL_WEIGHTS_DEFAULTS, ArmorMaterialWeight.LIST_TYPE);
	}

	@Override
	public void readConfig(final ModConfigEvent event) {
		super.readConfig(event);
		Stats.removeClassItemAttributeModifier(ShieldItem.class);
		if (shieldSlowdown > 0d) {
			Stats.addClassItemAttributeModifier(ShieldItem.class, UUID.fromString("ef620642-7e4d-43cb-88e3-feee47a531a0"), EquipmentSlot.MAINHAND, Attributes.MOVEMENT_SPEED, -shieldSlowdown, AttributeModifier.Operation.MULTIPLY_BASE);
			Stats.addClassItemAttributeModifier(ShieldItem.class, UUID.fromString("49c54369-7541-4cb4-8ee2-63863457427d"), EquipmentSlot.OFFHAND, Attributes.MOVEMENT_SPEED, -shieldSlowdown, AttributeModifier.Operation.MULTIPLY_BASE);
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
		for (ArmorMaterialWeight armorMaterialWeight : materialWeight) {
			//noinspection ConstantConditions
			if (!ForgeRegistries.ITEMS.getKey(itemStack.getItem()).getPath().contains(armorMaterialWeight.id))
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
			double armorSlowdown = armor * slownessPerArmor;
			double toughnessSlowdown = armorToughness * percentagePerToughness;
			slowdown = -(armorSlowdown * (1 + toughnessSlowdown));
		}
		double flatEnchantmentSlowdown = 0d, percentageEnchantmentSlowdown = 0d;
		for (ArmorEnchantmentWeight enchantmentWeight : enchantmentsList) {
			int enchantmentLevel = MCUtils.getEnchantmentLevel(enchantmentWeight.location, itemStack);
			if (enchantmentLevel == 0)
				continue;
			flatEnchantmentSlowdown += (enchantmentWeight.flatSlowness + (enchantmentWeight.flatSlownessPerLevel * enchantmentLevel));
			percentageEnchantmentSlowdown += (enchantmentWeight.percentageSlowness + (enchantmentWeight.percentageSlownessPerLevel * enchantmentLevel));
		}
		slowdown -= flatEnchantmentSlowdown;
		slowdown *= 1 + percentageEnchantmentSlowdown;
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
		event.getToolTip().add(Component.translatable(Strings.Translatable.ARMOR_SLOWDOWN, Utils.formatDecimal(slowdown, "#.#")).withStyle(ChatFormatting.RED));
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void debugScreen(CustomizeGuiOverlayEvent.DebugText event) {
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
		if (mc.options.renderDebug && !mc.showOnlyReducedInfo() && modifier != null) {
			event.getLeft().add(String.format("Armor Slowdown: %s%%", Utils.formatDecimal(Math.abs(modifier.getAmount()) * 100f, "#.#")));
		}
	}
}