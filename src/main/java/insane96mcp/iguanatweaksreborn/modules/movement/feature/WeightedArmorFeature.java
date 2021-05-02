package insane96mcp.iguanatweaksreborn.modules.movement.feature;

import com.google.common.collect.Multimap;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.modules.movement.classutils.ArmorEnchantmentWeight;
import insane96mcp.iguanatweaksreborn.modules.movement.classutils.ArmorMaterialWeight;
import insane96mcp.iguanatweaksreborn.modules.movement.utils.Armor;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.utils.MCUtils;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Label(name = "Weighted Armor", description = "Armor slows down the player based off the Armor and Toughness given.")
public class WeightedArmorFeature extends Feature {

	private final ForgeConfigSpec.ConfigValue<Double> slownessPerArmorConfig;
	private final ForgeConfigSpec.ConfigValue<Double> percentagePerToughnessConfig;
	private final ForgeConfigSpec.ConfigValue<List<? extends String>> materialWeightConfig;
	private final ForgeConfigSpec.ConfigValue<List<? extends String>> enchantmentsListConfig;
	//private final ForgeConfigSpec.ConfigValue<List<? extends String>> customWeightConfig;

	private static final List<String> materialWeightDefault = Arrays.asList("leather,4.0", "chainmail,12.0", "golden,8.0", "iron,16.0", "diamond,30.0", "netherite,40.0");

	private static final List<String> enchantmentsListDefault = Arrays.asList("minecraft:feather_falling,10", "elenaidodge2:lightweight,15,5");

	public double slownessPerArmor = 2d;
	public double percentagePerToughness = 0.04d;
	public ArrayList<ArmorMaterialWeight> materialWeight;
	public ArrayList<ArmorEnchantmentWeight> enchantmentsList;

	public WeightedArmorFeature(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		slownessPerArmorConfig = Config.builder
				.comment("Percentage slowdown per point of armor the player is wearing.")
				.defineInRange("Slowdown per Armor", slownessPerArmor, 0.0d, 128d);
		percentagePerToughnessConfig = Config.builder
				.comment("This value times the Armor Toughness worn by the player is a percentage increase of the Slowdown per Armor.\n" +
						"Total percentage slowdown is '(slowness_per_armor * armor_points) * (1 + (toughness * percentage_per_toughness))'" +
						"E.g. with 'Slowness per Armor' set to 2 and this set to 0.04 and the player wearing Diamond Armor the slowdown is '(2 * 20) * (1 + (8 * 0.04))' = '(2 * 20) + 32%' = '40 + 32%' ~= -53% Speed applied to the player")
				.defineInRange("Percentage Increase per Toughness", percentagePerToughness, 0.0d, 128.0d);
		materialWeightConfig = Config.builder
				.comment("Define here a list of total slowdown percentage (with full armor) per material. Material's names are the names in the armor's ids. E.g. Gold Armor is 'golden' as the ids are like 'golden_chestplate'.\n" +
						"Format is material,total_slowdown")
				.defineList("Material Weight", materialWeightDefault, o -> o instanceof String);
		enchantmentsListConfig = Config.builder
				.comment("Define here a list of Enchantments that will reduce the slowdown on the armor piece having the enchantment.\n" +
						"Format is modid:enchantmentid,reductionPerLevel,flatReduction\n" +
						"Where reduction per level is the percentage slowdown reduction per level, while flatReduction (optional) is a flat percentage slowdown reduction. E.g. 'elenaidodge2:lightweight,15,5' means that you'll get 5% less slowdown on armor plus 15% per level, so at Lightweight II you'll get (5+15*2) = 35% reduction on that piece of armor.")
				.defineList("Enchantments Weight Reduction", enchantmentsListDefault, o -> o instanceof String);
		Config.builder.pop();
	}

    @Override
    public void loadConfig() {
        super.loadConfig();
        this.slownessPerArmor = this.slownessPerArmorConfig.get();
        this.percentagePerToughness = this.percentagePerToughnessConfig.get();
		this.materialWeight = ArmorMaterialWeight.parseStringList(this.materialWeightConfig.get());
		this.enchantmentsList = ArmorEnchantmentWeight.parseEnchantmentStringList(this.enchantmentsListConfig.get());
    }

    private final UUID armorSlowdownModifierUUID = UUID.fromString("5a8c2add-015c-4b39-837c-3188a57fa3d6");

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        PlayerEntity playerEntity = event.player;
        ModifiableAttributeInstance movementSpeed = playerEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed == null)
            return;
        AttributeModifier modifier = movementSpeed.getModifier(armorSlowdownModifierUUID);
        if (!this.isEnabled()) {
            //If the feature has been disabled remove the slowdown from the player
			if (modifier != null)
				movementSpeed.removeModifier(modifier);
			return;
		}
		double slowdown = 0d;
		for (ItemStack stack : playerEntity.getArmorInventoryList()) {
			slowdown += getArmorSlowdown(stack);
		}
		//If it's still 0 then there's no slowdown appliable
		if (slowdown == 0d) {
			if (modifier != null)
				movementSpeed.removeModifier(modifier);
			return;
		}
		if (modifier == null || modifier.getAmount() != slowdown) {
			modifier = new AttributeModifier(armorSlowdownModifierUUID, IguanaTweaksReborn.RESOURCE_PREFIX + "armor_slowdown", slowdown, AttributeModifier.Operation.MULTIPLY_BASE);
			movementSpeed.removeModifier(armorSlowdownModifierUUID);
			movementSpeed.applyNonPersistentModifier(modifier);
		}
		playerEntity.jumpMovementFactor = (float) (0.02f * (movementSpeed.getValue() / movementSpeed.getBaseValue()));
	}

	private double getArmorSlowdown(ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ArmorItem))
			return 0d;
		double slowdown = 0d;
		for (ArmorMaterialWeight armorMaterialWeight : this.materialWeight) {
			if (!itemStack.getItem().getRegistryName().getPath().contains(armorMaterialWeight.id))
				continue;
			ArmorItem armor = (ArmorItem) itemStack.getItem();
			double maxArmor = Armor.getTotalDamageReduction(armor.getArmorMaterial());
			double pieceArmor = armor.getArmorMaterial().getDamageReductionAmount(armor.getEquipmentSlot());
			double ratio = pieceArmor / maxArmor;
			double armorPieceSlowdown = armorMaterialWeight.totalWeight * ratio;
			double enchantmentSlowdownReduction = 0d;
			for (ArmorEnchantmentWeight enchantmentWeight : enchantmentsList) {
				int enchantmentLevel = MCUtils.getEnchantmentLevel(enchantmentWeight.id, itemStack);
				if (enchantmentLevel == 0)
					continue;
				enchantmentSlowdownReduction += (enchantmentWeight.flatSlownessReduction + (enchantmentWeight.slownessReductionPerLevel * enchantmentLevel));
			}
			enchantmentSlowdownReduction /= 100d;
			armorPieceSlowdown *= 1 - enchantmentSlowdownReduction;
			slowdown = -(armorPieceSlowdown) / 100;
			break;
		}
		if (slowdown != 0d)
			return slowdown;
		ArmorItem armorItem = (ArmorItem) itemStack.getItem();
		Multimap<Attribute, AttributeModifier> attributeModifiers = itemStack.getAttributeModifiers(armorItem.getEquipmentSlot());
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
		slowdown = -(armorSlowdown * (1 + toughnessSlowdown)) / 100d;
		return slowdown;
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent event) {
		if (!this.isEnabled())
			return;
		ItemStack stack = event.getItemStack();
		double slowdown = -getArmorSlowdown(stack) * 100d;
		event.getToolTip().add((new StringTextComponent(String.format("Slowdown: %s%%", Utils.formatDecimal(slowdown, "#.#")))).mergeStyle(TextFormatting.RED));
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void debugScreen(RenderGameOverlayEvent.Text event) {
		if (!this.isEnabled())
			return;
		Minecraft mc = Minecraft.getInstance();
		ClientPlayerEntity playerEntity = mc.player;
		if (playerEntity == null)
			return;
		ModifiableAttributeInstance movementSpeed = playerEntity.getAttribute(Attributes.MOVEMENT_SPEED);
		if (movementSpeed == null)
			return;
		AttributeModifier modifier = movementSpeed.getModifier(armorSlowdownModifierUUID);
		if (mc.gameSettings.showDebugInfo && modifier != null) {
			event.getLeft().add(String.format("Armor Slowdown: %s%%", Utils.formatDecimal(Math.abs(modifier.getAmount()) * 100f, "#.#")));
        }
    }
}
