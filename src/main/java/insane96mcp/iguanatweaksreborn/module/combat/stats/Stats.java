package insane96mcp.iguanatweaksreborn.module.combat.stats;

import com.google.common.collect.Multimap;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.combat.PiercingPickaxes;
import insane96mcp.iguanatweaksreborn.module.misc.DataPacks;
import insane96mcp.iguanatweaksreborn.setup.IntegratedPack;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.MCUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.AttackDamageMobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Label(name = "Misc Stats", description = "Various changes from weapons damage to armor reduction")
@LoadFeature(module = Modules.Ids.COMBAT)
public class Stats extends Feature {
	public static final UUID ATTACK_RANGE_REDUCTION_UUID = UUID.fromString("0dd017a7-274c-4101-85b4-78af20a24c54");
	public static final UUID MOVEMENT_SPEED_REDUCTION_UUID = UUID.fromString("a88ac0d1-e2b3-4cf1-bb0e-9577486c874a");
	@Config(min = -4d, max = 4d)
	@Label(name = "Player attack range modifier", description = "Adds this to players' attack range")
	public static Double playerAttackRangeModifier = -0.5d;
	@Config
	@Label(name = "Player no damage when spamming", description = "In vanilla, if you attack as soon as you just attacked you already deal 20% of the full damage. This changes that to 0%.")
	public static Boolean playerNoDamageWhenSpamming = true;
	@Config
	@Label(name = "Players movement speed reduction", description = "Reduces movement speed for players by this percentage.")
	public static Double playersMovementSpeedReduction = 0.05d;
	@Config
	@Label(name = "Disable Critical Arrows bonus damage", description = "If true, Arrows from Bows and Crossbows will no longer deal more damage when fully charged.")
	public static Boolean disableCritArrowsBonusDamage = true;
	@Config(min = 0, max = 1)
	@Label(name = "Hoes Knockback multiplier")
	public static Double hoesKnockbackMultiplier = 0.4d;

	@Config
	@Label(name = "Fix tooltips", description = "Vanilla tooltips on gear don't sum up multiple modifiers (e.g. a sword would have \"4 Attack Damage\" and \"-2 Attack Damage\" instead of \"2 Attack Damage\". This might break other mods messing with these Tooltips (e.g. Quark's improved tooltips)")
	public static Boolean fixTooltips = true;

	@Config
	@Label(name = "Combat Test Strength", description = "Changes Strength effect from +3 damage per level to +20% damage per level. (Requires a Minecraft restart)")
	public static Boolean combatTestStrength = true;
	@Config
	@Label(name = "Better weakness", description = "Changes Weakness like Strength effect from -3 damage per level to -20% damage per level. (Requires a Minecraft restart)")
	public static Boolean betterWeakness = true;
	@Config
	@Label(name = "Better haste/mining fatigue", description = "Changes Mining fatigue and haste to no longer affects attack speed. (Requires a Minecraft restart)")
	public static Boolean betterHasteMiningFatigue = true;
	@Config(min = 0d, max = 10d)
	@Label(name = "Bow's Arrows Base Damage", description = "Set arrow's base damage if shot from bow.")
	public static Double bowsArrowsBaseDamage = 1.5d;
	@Config
	@Label(name = "1 damage for tools attacking", description = "If enabled, tools will not take 2 damage when used to hurt entities")
	public static Boolean oneDamageForToolAttacking = true;
	@Config
	@Label(name = "Item Stats Data Pack", description = "Enables a data pack that changes all the item stats")
	public static Boolean itemStatsDataPack = true;

	public Stats(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedPack.addPack(new IntegratedPack(PackType.SERVER_DATA, "item_stats", Component.literal("IguanaTweaks Reborn Item Stats"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && itemStatsDataPack));
	}

	@Override
	public void readConfig(ModConfigEvent event) {
		super.readConfig(event);
		if (combatTestStrength) {
			MobEffects.DAMAGE_BOOST.attributeModifiers.remove(Attributes.ATTACK_DAMAGE);
			MobEffects.DAMAGE_BOOST.addAttributeModifier(Attributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.0D, AttributeModifier.Operation.MULTIPLY_BASE);
			((AttackDamageMobEffect)MobEffects.DAMAGE_BOOST).multiplier = 0.2d;
		}
		if (betterWeakness) {
			MobEffects.WEAKNESS.attributeModifiers.remove(Attributes.ATTACK_DAMAGE);
			MobEffects.WEAKNESS.addAttributeModifier(Attributes.ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", 0.0D, AttributeModifier.Operation.MULTIPLY_BASE);
			((AttackDamageMobEffect)MobEffects.WEAKNESS).multiplier = -0.2d;
		}
		if (betterHasteMiningFatigue) {
			MobEffects.DIG_SPEED.attributeModifiers.remove(Attributes.ATTACK_SPEED);
			MobEffects.DIG_SLOWDOWN.attributeModifiers.remove(Attributes.ATTACK_SPEED);
		}
	}

	public static boolean noDamageWhenSpamming() {
		return isEnabled(Stats.class) && playerNoDamageWhenSpamming;
	}

	@SubscribeEvent
	public void onPlayerJoinLevel(EntityJoinLevelEvent event) {
		if (!this.isEnabled()
				|| !(event.getEntity() instanceof Player player))
			return;

		if (playerAttackRangeModifier != 0f)
			MCUtils.applyModifier(player, ForgeMod.ENTITY_REACH.get(), ATTACK_RANGE_REDUCTION_UUID, "Entity Reach reduction", playerAttackRangeModifier, AttributeModifier.Operation.ADDITION, false);
		if (playersMovementSpeedReduction != 0d)
			MCUtils.applyModifier(player, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_REDUCTION_UUID, "Movement Speed reduction", -playersMovementSpeedReduction, AttributeModifier.Operation.MULTIPLY_BASE, false);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onItemTooltipEvent(ItemTooltipEvent event) {
		if (!this.isEnabled()
				|| !fixTooltips
				|| event.getItemStack().getItem() instanceof PotionItem)
			return;

		List<Component> toRemove = new ArrayList<>();
		boolean hasModifiersTooltip = false;

		Component emptyLine = null;
		for (Component mutableComponent : event.getToolTip()) {
			if (emptyLine == null)
				emptyLine = mutableComponent.getSiblings().isEmpty() && mutableComponent.getContents().equals(ComponentContents.EMPTY) ? mutableComponent : null;
			if (mutableComponent.getContents() instanceof TranslatableContents t) {
				if (t.getKey().startsWith("item.modifiers.")) {
					hasModifiersTooltip = true;
					toRemove.add(mutableComponent);
					if (emptyLine != null)
						toRemove.add(emptyLine);
					emptyLine = null;
				}
				else if (t.getKey().startsWith("attribute.modifier."))
					toRemove.add(mutableComponent);
			}

			if (!hasModifiersTooltip)
				continue;

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
				event.getToolTip().add(CommonComponents.EMPTY);
				event.getToolTip().add(Component.translatable("item.modifiers." + equipmentslot.getName()).withStyle(ChatFormatting.GRAY));
				for(Attribute attribute : multimap.keySet()) {
					Map<AttributeModifier.Operation, List<AttributeModifier>> modifiersByOperation = multimap.get(attribute).stream().collect(Collectors.groupingBy(AttributeModifier::getOperation));
					modifiersByOperation.forEach((operation, modifier) -> {
						double amount = modifier.stream().mapToDouble(AttributeModifier::getAmount).sum();

						boolean isEqualTooltip = false;
						if (event.getEntity() != null && operation == AttributeModifier.Operation.ADDITION && equipmentslot == EquipmentSlot.MAINHAND) {
							if (attribute.equals(Attributes.ATTACK_DAMAGE)) {
								amount += event.getEntity().getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
								//amount += EnchantmentHelper.getDamageBonus(event.getItemStack(), MobType.UNDEFINED);
								isEqualTooltip = true;
							}
							if (attribute.equals(PiercingPickaxes.PIERCING_DAMAGE.get())
									|| attribute.equals(Attributes.ATTACK_SPEED)
									|| attribute.equals(Attributes.KNOCKBACK_RESISTANCE)
									|| attribute.equals(ForgeMod.ENTITY_REACH.get())
									|| attribute.equals(ForgeMod.BLOCK_REACH.get())) {
								amount += event.getEntity().getAttributeBaseValue(attribute);
								isEqualTooltip = true;
							}
						}
						if (!isEqualTooltip && amount == 0d)
							return;

						MutableComponent component = null;
						String translationString = "attribute.modifier.plus.";
						if (isEqualTooltip)
							translationString = "attribute.modifier.equals.";
						else if (amount < 0)
							translationString = "attribute.modifier.take.";
						switch (operation) {
							case ADDITION -> {
								if (attribute.equals(Attributes.KNOCKBACK_RESISTANCE))
									component = Component.translatable(translationString + operation.toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(amount * 100) + "%", Component.translatable(attribute.getDescriptionId()));
								else
									component = Component.translatable(translationString + operation.toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(amount)), Component.translatable(attribute.getDescriptionId()));
							}
							case MULTIPLY_BASE -> {
								component = Component.translatable(translationString + operation.toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(amount) * 100), Component.translatable(attribute.getDescriptionId()));
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

	@SubscribeEvent
	public void onKnockback(LivingKnockBackEvent event) {
		if (!this.isEnabled()
				|| hoesKnockbackMultiplier == 1d
				|| !(event.getEntity().getLastHurtByMob() instanceof Player player)
				|| !player.getMainHandItem().is(ItemTags.HOES))
			return;

		event.setStrength(event.getStrength() * hoesKnockbackMultiplier.floatValue());
	}

}