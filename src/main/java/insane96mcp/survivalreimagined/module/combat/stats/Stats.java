package insane96mcp.survivalreimagined.module.combat.stats;

import com.google.common.collect.Multimap;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.combat.stats.data.ItemAttributeModifier;
import insane96mcp.survivalreimagined.network.message.JsonConfigSyncMessage;
import insane96mcp.survivalreimagined.setup.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.effect.AttackDamageMobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.*;
import java.util.stream.Collectors;

@Label(name = "Stats", description = "Various changes from weapons damage to armor reduction. Item modifiers are controlled via json in this feature's folder")
@LoadFeature(module = Modules.Ids.COMBAT)
public class Stats extends SRFeature {
	public static final ArrayList<ItemAttributeModifier> ITEM_MODIFIERS_DEFAULT = new ArrayList<>(Arrays.asList(
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:golden_sword", UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, Attributes.ATTACK_SPEED, .4d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:golden_axe", UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, Attributes.ATTACK_SPEED, .25d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:golden_pickaxe", UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, Attributes.ATTACK_SPEED, .3d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:golden_shovel", UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, Attributes.ATTACK_SPEED, .25d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "survivalreimagined:golden_hammer", UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, Attributes.ATTACK_SPEED, .125d, AttributeModifier.Operation.ADDITION),

			/*new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:golden_helmet", UUID.fromString("3f22e9a3-0916-43ab-a93f-ba52e5ae28e5"), EquipmentSlot.HEAD, Attributes.MOVEMENT_SPEED, 0.06d, AttributeModifier.Operation.MULTIPLY_BASE),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:golden_chestplate", UUID.fromString("f700b45a-0c51-40f8-9f59-836c519d64d5"), EquipmentSlot.CHEST, Attributes.MOVEMENT_SPEED, 0.06d, AttributeModifier.Operation.MULTIPLY_BASE),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:golden_leggings", UUID.fromString("4f1caa92-2558-4416-829c-9faf922d7137"), EquipmentSlot.LEGS, Attributes.MOVEMENT_SPEED, 0.06d, AttributeModifier.Operation.MULTIPLY_BASE),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:golden_boots", UUID.fromString("dc49f564-489f-4f70-ab50-ce85cc4bfa85"), EquipmentSlot.FEET, Attributes.MOVEMENT_SPEED, 0.06d, AttributeModifier.Operation.MULTIPLY_BASE),*/

			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:diamond_helmet", UUID.fromString("ad33dadf-bf7b-4f40-83ed-f93f4721d28e"), EquipmentSlot.HEAD, Attributes.ARMOR, 0, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:diamond_chestplate", UUID.fromString("f690ba04-4d3f-47d0-ad5d-809238b48f45"), EquipmentSlot.CHEST, Attributes.ARMOR, -2, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:diamond_leggings", UUID.fromString("0145a1f9-24c4-458f-b217-8bb1440e99b7"), EquipmentSlot.LEGS, Attributes.ARMOR, -1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:diamond_boots", UUID.fromString("386b4fb6-f3ca-4a54-a455-4b48a179c17a"), EquipmentSlot.FEET, Attributes.ARMOR, 1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:diamond_helmet", UUID.fromString("8b68e416-bf07-4c21-ab8e-d58ac3574d31"), EquipmentSlot.HEAD, Attributes.ARMOR_TOUGHNESS, -1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:diamond_chestplate", UUID.fromString("74d42a8e-d4a3-4c52-ac66-33ab2128e146"), EquipmentSlot.CHEST, Attributes.ARMOR_TOUGHNESS, -1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:diamond_leggings", UUID.fromString("de6a0547-fc18-4e84-b87a-d0333aa06854"), EquipmentSlot.LEGS, Attributes.ARMOR_TOUGHNESS, -1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:diamond_boots", UUID.fromString("30db05c5-7c2f-4fa8-86d8-e8661e42f197"), EquipmentSlot.FEET, Attributes.ARMOR_TOUGHNESS, -1, AttributeModifier.Operation.ADDITION),

			/*new ItemAttributeModifier(IdTagMatcher.Type.ID, "survivalreimagined:soul_steel_helmet", UUID.fromString("ad33dadf-bf7b-4f40-83ed-f93f4721d28e"), EquipmentSlot.HEAD, Attributes.ARMOR, 0, AttributeModifier.Operation.ADDITION),*/
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "survivalreimagined:soul_steel_chestplate", UUID.fromString("f690ba04-4d3f-47d0-ad5d-809238b48f45"), EquipmentSlot.CHEST, Attributes.ARMOR, 1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "survivalreimagined:soul_steel_leggings", UUID.fromString("0145a1f9-24c4-458f-b217-8bb1440e99b7"), EquipmentSlot.LEGS, Attributes.ARMOR, 1, AttributeModifier.Operation.ADDITION),
			/*new ItemAttributeModifier(IdTagMatcher.Type.ID, "survivalreimagined:soul_steel_boots", UUID.fromString("386b4fb6-f3ca-4a54-a455-4b48a179c17a"), EquipmentSlot.FEET, Attributes.ARMOR, 0, AttributeModifier.Operation.ADDITION),*/
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "survivalreimagined:soul_steel_helmet", UUID.fromString("8b68e416-bf07-4c21-ab8e-d58ac3574d31"), EquipmentSlot.HEAD, Attributes.ARMOR_TOUGHNESS, 1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "survivalreimagined:soul_steel_chestplate", UUID.fromString("74d42a8e-d4a3-4c52-ac66-33ab2128e146"), EquipmentSlot.CHEST, Attributes.ARMOR_TOUGHNESS, 1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "survivalreimagined:soul_steel_leggings", UUID.fromString("de6a0547-fc18-4e84-b87a-d0333aa06854"), EquipmentSlot.LEGS, Attributes.ARMOR_TOUGHNESS, 1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "survivalreimagined:soul_steel_boots", UUID.fromString("30db05c5-7c2f-4fa8-86d8-e8661e42f197"), EquipmentSlot.FEET, Attributes.ARMOR_TOUGHNESS, 1, AttributeModifier.Operation.ADDITION),

			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:netherite_helmet", UUID.fromString("da2b677f-467c-49b1-bdf0-070ee782bc0f"), EquipmentSlot.HEAD, Attributes.ARMOR, 1.0d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:netherite_boots", UUID.fromString("796d3f0c-89e4-47e8-9f95-3a3d5506f70f"), EquipmentSlot.FEET, Attributes.ARMOR, 1.0d, AttributeModifier.Operation.ADDITION),

			new ItemAttributeModifier(IdTagMatcher.Type.TAG, "minecraft:swords", UUID.fromString("de87cf5d-0f15-4b4e-88c5-9b3c971146d0"), EquipmentSlot.MAINHAND, ForgeMod.ENTITY_REACH, 0.5d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.TAG, "minecraft:hoes", UUID.fromString("de87cf5d-0f15-4b4e-88c5-9b3c971146d0"), EquipmentSlot.MAINHAND, ForgeMod.ENTITY_REACH, 0.5d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:trident", UUID.fromString("de87cf5d-0f15-4b4e-88c5-9b3c971146d0"), EquipmentSlot.MAINHAND, ForgeMod.ENTITY_REACH, 1d, AttributeModifier.Operation.ADDITION),

			new ItemAttributeModifier(IdTagMatcher.Type.TAG, "minecraft:axes", UUID.fromString("50850a15-845a-4923-972b-f6cd1c16a7d3"), EquipmentSlot.MAINHAND, Attributes.ATTACK_DAMAGE, -1d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.TAG, "minecraft:swords", UUID.fromString("50850a15-845a-4923-972b-f6cd1c16a7d3"), EquipmentSlot.MAINHAND, Attributes.ATTACK_DAMAGE, -2d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.TAG, "minecraft:pickaxes", UUID.fromString("50850a15-845a-4923-972b-f6cd1c16a7d3"), EquipmentSlot.MAINHAND, Attributes.ATTACK_DAMAGE, -1d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:trident", UUID.fromString("50850a15-845a-4923-972b-f6cd1c16a7d3"), EquipmentSlot.MAINHAND, Attributes.ATTACK_DAMAGE, -1d, AttributeModifier.Operation.ADDITION)
	));
	public static final ArrayList<ItemAttributeModifier> itemModifiers = new ArrayList<>();

	public static final UUID ATTACK_RANGE_REDUCTION_UUID = UUID.fromString("0dd017a7-274c-4101-85b4-78af20a24c54");
	public static final UUID MOVEMENT_SPEED_REDUCTION_UUID = UUID.fromString("a88ac0d1-e2b3-4cf1-bb0e-9577486c874a");
	@Config
	@Label(name = "Reduce player attack range", description = "If true, player attack range is reduced by 0.5.")
	public static Boolean reducePlayerAttackRange = true;
	@Config
	@Label(name = "Players movement speed reduction", description = "Reduces movement speed for players by this percentage.")
	public static Double playersMovementSpeedReduction = 0.05d;
	@Config
	@Label(name = "Disable Crit Arrows bonus damage", description = "If true, Arrows from Bows and Crossbows will no longer deal more damage when fully charged.")
	public static Boolean disableCritArrowsBonusDamage = true;

	@Config
	@Label(name = "Fix tooltips", description = "Vanilla tooltips on gear don't sum up multiple modifiers (e.g. a sword would have \"4 Attack Damage\" and \"-2 Attack Damage\" instead of \"2 Attack Damage\". This might break other mods messing with these Tooltips (e.g. Quark's improved tooltips)")
	public static Boolean fixTooltips = true;

	@Config
	@Label(name = "Combat Test Strength", description = "Changes Strength effect from +3 damage per level to +20% damage per level. (Requires a Minecraft restart)")
	public static Boolean combatTestStrength = true;

	public Stats(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		JSON_CONFIGS.add(new JsonConfig<>("item_modifiers.json", itemModifiers, ITEM_MODIFIERS_DEFAULT, ItemAttributeModifier.LIST_TYPE, true, JsonConfigSyncMessage.ConfigType.ITEM_ATTRIBUTE_MODIFIERS));
	}

	@Override
	public void readConfig(ModConfigEvent event) {
		super.readConfig(event);
		if (combatTestStrength) {
			MobEffects.DAMAGE_BOOST.attributeModifiers.remove(Attributes.ATTACK_DAMAGE);
			MobEffects.DAMAGE_BOOST.addAttributeModifier(Attributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.0D, AttributeModifier.Operation.MULTIPLY_BASE);
			((AttackDamageMobEffect)MobEffects.DAMAGE_BOOST).multiplier = 0.2d;
		}
	}

	@SubscribeEvent
	public void onPlayerJoinLevel(EntityJoinLevelEvent event) {
		if (!this.isEnabled()
				|| !(event.getEntity() instanceof Player player))
			return;

		if (reducePlayerAttackRange)
			MCUtils.applyModifier(player, ForgeMod.ENTITY_REACH.get(), ATTACK_RANGE_REDUCTION_UUID, "Entity Reach reduction", -0.5d, AttributeModifier.Operation.ADDITION, false);
		if (playersMovementSpeedReduction != 0d)
			MCUtils.applyModifier(player, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_REDUCTION_UUID, "Movement Speed reduction", -playersMovementSpeedReduction, AttributeModifier.Operation.MULTIPLY_BASE, false);
	}

	@Override
	public void loadJsonConfigs() {
		if (!this.isEnabled())
			return;
		super.loadJsonConfigs();
	}

	public static void handleItemAttributeModifiersPacket(String json) {
		loadAndReadJson(json, itemModifiers, ITEM_MODIFIERS_DEFAULT, ItemAttributeModifier.LIST_TYPE);
	}

	@SubscribeEvent
	public void onAttributeEvent(ItemAttributeModifierEvent event) {
		if (!this.isEnabled())
			return;

		for (ItemAttributeModifier itemAttributeModifier : itemModifiers) {
			if (!itemAttributeModifier.matchesItem(event.getItemStack().getItem()))
				continue;
			if (event.getSlotType() != itemAttributeModifier.slot)
				continue;

			AttributeModifier modifier = new AttributeModifier(itemAttributeModifier.uuid, Strings.AttributeModifiers.GENERIC_ITEM_MODIFIER, itemAttributeModifier.amount, itemAttributeModifier.operation);
			event.addModifier(itemAttributeModifier.attribute.get(), modifier);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onItemTooltipEvent(ItemTooltipEvent event) {
		if (!this.isEnabled()
				|| !fixTooltips
				|| event.getItemStack().getItem() instanceof PotionItem)
			return;

		List<Component> toRemove = new ArrayList<>();
		boolean hasModifiersTooltip = false;

		for (Component mutableComponent : event.getToolTip()) {
			if (mutableComponent.getContents() instanceof TranslatableContents t) {
				if (t.getKey().startsWith("item.modifiers."))
					hasModifiersTooltip = true;
				else if (t.getKey().startsWith("attribute.modifier."))
					toRemove.add(mutableComponent);
			}

			if (!hasModifiersTooltip) {
				continue;
			}
			List<Component> siblings = mutableComponent.getSiblings();
			for (Component component : siblings) {
				if (component.getContents() instanceof TranslatableContents translatableContents && translatableContents.getKey().startsWith("attribute.modifier.")) {
					toRemove.add(mutableComponent);
				}
			}
		}

		toRemove.forEach(component -> event.getToolTip().remove(component));

		for(EquipmentSlot equipmentslot : EquipmentSlot.values()) {
			Multimap<Attribute, AttributeModifier> multimap = event.getItemStack().getAttributeModifiers(equipmentslot);
			if (!multimap.isEmpty()) {
				for(Attribute attribute : multimap.keySet()) {
					Map<AttributeModifier.Operation, List<AttributeModifier>> modifiersByOperation = multimap.get(attribute).stream().collect(Collectors.groupingBy(AttributeModifier::getOperation));
					modifiersByOperation.forEach((operation, modifier) -> {
						double amount = modifier.stream().mapToDouble(AttributeModifier::getAmount).sum();
						if (amount == 0d)
							return;

						boolean isEqualTooltip = false;
						if (event.getEntity() != null) {
							if (attribute.equals(Attributes.ATTACK_DAMAGE)) {
								amount += event.getEntity().getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
								amount += EnchantmentHelper.getDamageBonus(event.getItemStack(), MobType.UNDEFINED);
								isEqualTooltip = true;
							}
							else if (attribute.equals(Attributes.ATTACK_SPEED)) {
								amount += event.getEntity().getAttributeBaseValue(Attributes.ATTACK_SPEED);
								isEqualTooltip = true;
							}
							else if (attribute.equals(Attributes.KNOCKBACK_RESISTANCE)) {
								amount += event.getEntity().getAttributeBaseValue(Attributes.KNOCKBACK_RESISTANCE);
								isEqualTooltip = true;
							}
						}

						MutableComponent component = null;
						String translationString = "attribute.modifier.plus.";
						if (isEqualTooltip)
							translationString = "attribute.modifier.equals.";
						else if (amount < 0)
							translationString = "attribute.modifier.take.";
						switch (operation) {
							case ADDITION -> {
								if (attribute.equals(Attributes.KNOCKBACK_RESISTANCE))
									component = Component.translatable(translationString + operation.toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(amount * 10)), Component.translatable(attribute.getDescriptionId()));
								else
									component = Component.translatable(translationString + operation.toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(amount)), Component.translatable(attribute.getDescriptionId()));
							}
							case MULTIPLY_BASE -> {
								component = Component.translatable(translationString + operation.toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(amount * 100)), Component.translatable(attribute.getDescriptionId()));
							}
							case MULTIPLY_TOTAL -> {
								component = Component.literal("x").append(Component.translatable(translationString + operation.toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(amount) + 1), Component.translatable(attribute.getDescriptionId())));
							}
						}
						if (isEqualTooltip)
							component = CommonComponents.space().append(component.withStyle(ChatFormatting.DARK_GREEN));
						else if (amount > 0)
							component.withStyle(ChatFormatting.BLUE);
						else
							component.withStyle(ChatFormatting.RED);
						event.getToolTip().add(component);
					});
				}
			}
		}
	}

}