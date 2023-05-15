package insane96mcp.survivalreimagined.module.combat.feature;

import com.google.common.collect.Multimap;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.combat.data.ItemAttributeModifier;
import insane96mcp.survivalreimagined.network.message.JsonConfigSyncMessage;
import insane96mcp.survivalreimagined.setup.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
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
	static final List<ItemAttributeModifier> CLASS_ATTRIBUTE_MODIFIER = new ArrayList<>();

	public static final ArrayList<ItemAttributeModifier> ITEM_MODIFIERS_DEFAULT = new ArrayList<>(Arrays.asList(
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:netherite_helmet", UUID.fromString("da2b677f-467c-49b1-bdf0-070ee782bc0f"), EquipmentSlot.HEAD, Attributes.ARMOR, 1.0d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:netherite_boots", UUID.fromString("796d3f0c-89e4-47e8-9f95-3a3d5506f70f"), EquipmentSlot.FEET, Attributes.ARMOR, 1.0d, AttributeModifier.Operation.ADDITION),

			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:golden_sword", UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, Attributes.ATTACK_SPEED, .4d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:golden_axe", UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, Attributes.ATTACK_SPEED, .25d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:golden_pickaxe", UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, Attributes.ATTACK_SPEED, .3d, AttributeModifier.Operation.ADDITION),

			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:golden_helmet", UUID.fromString("3f22e9a3-0916-43ab-a93f-ba52e5ae28e5"), EquipmentSlot.HEAD, Attributes.MAX_HEALTH, 2d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:golden_chestplate", UUID.fromString("f700b45a-0c51-40f8-9f59-836c519d64d5"), EquipmentSlot.CHEST, Attributes.MAX_HEALTH, 2d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:golden_leggings", UUID.fromString("4f1caa92-2558-4416-829c-9faf922d7137"), EquipmentSlot.LEGS, Attributes.MAX_HEALTH, 2d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:golden_boots", UUID.fromString("dc49f564-489f-4f70-ab50-ce85cc4bfa85"), EquipmentSlot.FEET, Attributes.MAX_HEALTH, 2d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "shieldsplus:golden_shield", UUID.fromString("51def2a9-8222-4d2d-824e-3d1afc6be89c"), EquipmentSlot.OFFHAND, Attributes.MAX_HEALTH, 2d, AttributeModifier.Operation.ADDITION)
	));
	public static final ArrayList<ItemAttributeModifier> itemModifiers = new ArrayList<>();

	public static final UUID ATTACK_RANGE_REDUCTION_UUID = UUID.fromString("0dd017a7-274c-4101-85b4-78af20a24c54");
	@Config
	@Label(name = "Reduce player attack range", description = "If true, player attack range is reduced by 0.5.")
	public static Boolean reducePlayerAttackRange = true;
	@Config
	@Label(name = "Adjust weapons", description = """
			If true the following changes to items are made:
				* Sword, Shovels and Hoes get +0.5 entity reach distance
				* Tridents get +1 entity reach distance
				* Swords get -2 damage
				* Pickaxes get -1 damage""")
	public static Boolean adjustWeapons = true;
	@Config
	@Label(name = "Disable Crit Arrows bonus damage", description = "If true, Arrows from Bows and Crossbows will no longer deal more damage when fully charged.")
	public static Boolean disableCritArrowsBonusDamage = true;
	@Config
	@Label(name = "Arrows don't trigger invincibility frames", description = "If true, Arrows will no longer trigger the invincibility frames (like Combat Test Snapshots).")
	public static Boolean arrowsNoInvincFrames = true;

	@Config
	@Label(name = "Fix tooltips", description = "Vanilla tooltips on gear don't sum up multiple modifiers (e.g. a sword would have \"4 Attack Damage\" and \"-2 Attack Damage\" instead of \"2 Attack Damage\". This might break other mods messing with Tooltips (e.g. Quark's improved tooltips)")
	public static Boolean fixTooltips = true;

	public Stats(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		JSON_CONFIGS.add(new JsonConfig<>("item_modifiers.json", itemModifiers, ITEM_MODIFIERS_DEFAULT, ItemAttributeModifier.LIST_TYPE, true, JsonConfigSyncMessage.ConfigType.ITEM_ATTRIBUTE_MODIFIERS));
	}

	@Override
	public void readConfig(final ModConfigEvent event) {
		super.readConfig(event);
		CLASS_ATTRIBUTE_MODIFIER.clear();
		if (adjustWeapons) {
			CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(SwordItem.class, UUID.fromString("55c71d5e-fc26-418a-b531-d50c66bfb589"), EquipmentSlot.MAINHAND, ForgeMod.ENTITY_REACH.get(), 0.5d, AttributeModifier.Operation.ADDITION));
			CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(ShovelItem.class, UUID.fromString("55c71d5e-fc26-418a-b531-d50c66bfb589"), EquipmentSlot.MAINHAND, ForgeMod.ENTITY_REACH.get(), 0.5d, AttributeModifier.Operation.ADDITION));
			CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(HoeItem.class, UUID.fromString("55c71d5e-fc26-418a-b531-d50c66bfb589"), EquipmentSlot.MAINHAND, ForgeMod.ENTITY_REACH.get(), 0.5d, AttributeModifier.Operation.ADDITION));
			CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(TridentItem.class, UUID.fromString("55c71d5e-fc26-418a-b531-d50c66bfb589"), EquipmentSlot.MAINHAND, ForgeMod.ENTITY_REACH.get(), 1d, AttributeModifier.Operation.ADDITION));

			CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(SwordItem.class, UUID.fromString("50850a15-845a-4923-972b-f6cd1c16a7d3"), EquipmentSlot.MAINHAND, Attributes.ATTACK_DAMAGE, -2d, AttributeModifier.Operation.ADDITION));
			CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(PickaxeItem.class, UUID.fromString("50850a15-845a-4923-972b-f6cd1c16a7d3"), EquipmentSlot.MAINHAND, Attributes.ATTACK_DAMAGE, -1d, AttributeModifier.Operation.ADDITION));
			CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(TridentItem.class, UUID.fromString("50850a15-845a-4923-972b-f6cd1c16a7d3"), EquipmentSlot.MAINHAND, Attributes.ATTACK_DAMAGE, -1d, AttributeModifier.Operation.ADDITION));

			/*CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(AxeItem.class, UUID.fromString("324a87bd-89ea-4d1b-866a-ce49d360d632"), EquipmentSlot.MAINHAND, Attributes.ATTACK_DAMAGE, -1d, AttributeModifier.Operation.ADDITION));
			CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(AxeItem.class, UUID.fromString("a60ab219-6c3a-453b-a55c-41e9c83f9c0f"), EquipmentSlot.MAINHAND, ForgeMod.ENTITY_REACH.get(), -0.5d, AttributeModifier.Operation.ADDITION));
			CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(TridentItem.class, UUID.fromString("f98cb9bc-3fa7-4fb5-b07a-babe8e35f967"), EquipmentSlot.MAINHAND, Attributes.ATTACK_DAMAGE, -1d, AttributeModifier.Operation.ADDITION));*/
		}
	}

	@SubscribeEvent
	public void onPlayerJoinLevel(EntityJoinLevelEvent event) {
		if (!this.isEnabled()
				|| !reducePlayerAttackRange
				|| !(event.getEntity() instanceof Player player))
			return;

		MCUtils.applyModifier(player, ForgeMod.ENTITY_REACH.get(), ATTACK_RANGE_REDUCTION_UUID, "Entity Reach reduction", -0.5d, AttributeModifier.Operation.ADDITION, false);
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

	public static void addClassItemAttributeModifier(Class<? extends Item> itemClass, UUID uuid, EquipmentSlot slot, Attribute attribute, double amount, AttributeModifier.Operation operation) {
		synchronized (CLASS_ATTRIBUTE_MODIFIER) {
			CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(itemClass, uuid, slot, attribute, amount, operation));
		}
	}

	public static void removeClassItemAttributeModifier(Class<? extends Item> itemClass) {
		synchronized (CLASS_ATTRIBUTE_MODIFIER) {
			CLASS_ATTRIBUTE_MODIFIER.removeIf(iam -> iam.itemClass.equals(itemClass));
		}
	}

	@SubscribeEvent
	public void onAttributeEvent(ItemAttributeModifierEvent event) {
		if (!this.isEnabled())
			return;

		classAttributeModifiers(event);
		itemAttributeModifiers(event);
	}

	private void classAttributeModifiers(ItemAttributeModifierEvent event) {
		for (ItemAttributeModifier itemAttributeModifier : CLASS_ATTRIBUTE_MODIFIER) {
			if (itemAttributeModifier.itemClass.equals(event.getItemStack().getItem().getClass()) && itemAttributeModifier.slot.equals(event.getSlotType())) {
				AttributeModifier modifier = new AttributeModifier(itemAttributeModifier.uuid, Strings.AttributeModifiers.GENERIC_ITEM_MODIFIER, itemAttributeModifier.amount, itemAttributeModifier.operation);
				event.addModifier(itemAttributeModifier.attribute, modifier);
			}
		}
	}

	private void itemAttributeModifiers(ItemAttributeModifierEvent event) {
		for (ItemAttributeModifier itemAttributeModifier : itemModifiers) {
			if (!itemAttributeModifier.matches(event.getItemStack().getItem()))
				continue;
			if (event.getSlotType() != itemAttributeModifier.slot)
				continue;

			AttributeModifier modifier = new AttributeModifier(itemAttributeModifier.uuid, Strings.AttributeModifiers.GENERIC_ITEM_MODIFIER, itemAttributeModifier.amount, itemAttributeModifier.operation);
			event.addModifier(itemAttributeModifier.attribute, modifier);
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
								component = Component.translatable(translationString + operation.toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(amount * 100)) + "%", Component.translatable(attribute.getDescriptionId()));
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

	public static boolean disableArrowInvFrames() {
		return isEnabled(Stats.class) && arrowsNoInvincFrames;
	}
}