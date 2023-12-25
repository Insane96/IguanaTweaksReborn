package insane96mcp.iguanatweaksreborn.module.combat.stats;

import com.google.common.collect.Multimap;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.data.generator.SRItemTagsProvider;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.combat.PiercingPickaxes;
import insane96mcp.iguanatweaksreborn.module.combat.stats.data.ItemAttributeModifier;
import insane96mcp.insanelib.base.JsonFeature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.data.IdTagMatcher;
import insane96mcp.insanelib.util.MCUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.AttackDamageMobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ItemAttributeModifierEvent;
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

@Label(name = "Stats", description = "Various changes from weapons damage to armor reduction. Item modifiers are controlled via json in this feature's folder")
@LoadFeature(module = Modules.Ids.COMBAT)
public class Stats extends JsonFeature {

	public static final String GENERIC_ITEM_MODIFIER = IguanaTweaksReborn.RESOURCE_PREFIX + "item_modifier";
	public static TagKey<Item> REMOVE_ORIGINAL_MODIFIERS_TAG = SRItemTagsProvider.create("remove_original_modifiers");

	public static final ArrayList<ItemAttributeModifier> ITEM_MODIFIERS_DEFAULT = new ArrayList<>(List.of(
			// Material Attack Speed changes
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/wooden"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_SPEED, 0.15d, AttributeModifier.Operation.MULTIPLY_BASE),
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/stone"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_SPEED, 0d, AttributeModifier.Operation.MULTIPLY_BASE),
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/flint"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_SPEED, -0.15d, AttributeModifier.Operation.MULTIPLY_BASE),
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/copper"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_SPEED, 0.15d, AttributeModifier.Operation.MULTIPLY_BASE),
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/golden"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_SPEED, 0.20d, AttributeModifier.Operation.MULTIPLY_BASE),
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/iron"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_SPEED, 0d, AttributeModifier.Operation.MULTIPLY_BASE),
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/solarium"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_SPEED, 0.05d, AttributeModifier.Operation.MULTIPLY_BASE),
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/durium"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_SPEED, -0.10d, AttributeModifier.Operation.MULTIPLY_BASE),
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/coated_copper"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_SPEED, -0.20d, AttributeModifier.Operation.MULTIPLY_BASE),
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/keego"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_SPEED, 0.05d, AttributeModifier.Operation.MULTIPLY_BASE),
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/diamond"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_SPEED, -0.05d, AttributeModifier.Operation.MULTIPLY_BASE),
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/soul_steel"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_SPEED, 0.05d, AttributeModifier.Operation.MULTIPLY_BASE),
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/netherite"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_SPEED, 0d, AttributeModifier.Operation.MULTIPLY_BASE),

			// Material Attack damage changes
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/wooden"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_DAMAGE, 0.5d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/stone"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_DAMAGE, 0d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/flint"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_DAMAGE, 0d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/copper"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_DAMAGE, -0.5d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/golden"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_DAMAGE, 1.5d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/iron"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_DAMAGE, -0.5d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/solarium"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_DAMAGE, 0d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/durium"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_DAMAGE, -1d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/coated_copper"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_DAMAGE, 1d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/keego"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_DAMAGE, -1d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/diamond"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_DAMAGE, -1.5d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/soul_steel"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_DAMAGE, -1d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newTag("iguanatweaksreborn:equipment/hand/netherite"), UUID.fromString("294e0db0-1185-4d78-b95e-8823b8bb0041"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_DAMAGE, -1.5d, AttributeModifier.Operation.ADDITION),

			//Reach changes
			new ItemAttributeModifier(IdTagMatcher.newTag("minecraft:swords"), UUID.fromString("de87cf5d-0f15-4b4e-88c5-9b3c971146d0"), EquipmentSlot.MAINHAND, ForgeMod.ENTITY_REACH, 0.5d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newTag("minecraft:hoes"), UUID.fromString("de87cf5d-0f15-4b4e-88c5-9b3c971146d0"), EquipmentSlot.MAINHAND, ForgeMod.ENTITY_REACH, 0.5d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:trident"), UUID.fromString("de87cf5d-0f15-4b4e-88c5-9b3c971146d0"), EquipmentSlot.MAINHAND, ForgeMod.ENTITY_REACH, 1d, AttributeModifier.Operation.ADDITION),

			//Various type specific weapons changes
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:trident"), UUID.fromString("50850a15-845a-4923-972b-f6cd1c16a7d3"), EquipmentSlot.MAINHAND, () -> Attributes.ATTACK_DAMAGE, -1d, AttributeModifier.Operation.ADDITION),

			//Golden max health
			new ItemAttributeModifier(IdTagMatcher.newId("shieldsplus:golden_shield"), UUID.fromString("b12fe849-17b9-4905-b160-6685eb8b854c"), EquipmentSlot.OFFHAND, () -> Attributes.MAX_HEALTH, 2d, AttributeModifier.Operation.ADDITION),

			//Leather
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:leather_helmet"), UUID.fromString("8b68e416-bf07-4c21-ab8e-d58ac3574d31"), EquipmentSlot.HEAD, () -> Attributes.ARMOR_TOUGHNESS, 1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:leather_chestplate"), UUID.fromString("74d42a8e-d4a3-4c52-ac66-33ab2128e146"), EquipmentSlot.CHEST, () -> Attributes.ARMOR_TOUGHNESS, 1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:leather_leggings"), UUID.fromString("de6a0547-fc18-4e84-b87a-d0333aa06854"), EquipmentSlot.LEGS, () -> Attributes.ARMOR_TOUGHNESS, 1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:leather_boots"), UUID.fromString("30db05c5-7c2f-4fa8-86d8-e8661e42f197"), EquipmentSlot.FEET, () -> Attributes.ARMOR_TOUGHNESS, 1, AttributeModifier.Operation.ADDITION),

			//Chained Copper
			new ItemAttributeModifier(IdTagMatcher.newId("iguanatweaksreborn:chained_copper_helmet"), UUID.fromString("8b68e416-bf07-4c21-ab8e-d58ac3574d31"), EquipmentSlot.HEAD, () -> Attributes.ARMOR_TOUGHNESS, 1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("iguanatweaksreborn:chained_copper_chestplate"), UUID.fromString("74d42a8e-d4a3-4c52-ac66-33ab2128e146"), EquipmentSlot.CHEST, () -> Attributes.ARMOR_TOUGHNESS, 1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("iguanatweaksreborn:chained_copper_leggings"), UUID.fromString("de6a0547-fc18-4e84-b87a-d0333aa06854"), EquipmentSlot.LEGS, () -> Attributes.ARMOR_TOUGHNESS, 1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("iguanatweaksreborn:chained_copper_boots"), UUID.fromString("30db05c5-7c2f-4fa8-86d8-e8661e42f197"), EquipmentSlot.FEET, () -> Attributes.ARMOR_TOUGHNESS, 1, AttributeModifier.Operation.ADDITION),

			//Chainmail
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:chainmail_helmet"), UUID.fromString("3f22e9a3-0916-43ab-a93f-ba52e5ae28e5"), EquipmentSlot.HEAD, () -> Attributes.ARMOR, -1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:chainmail_helmet"), UUID.fromString("8b68e416-bf07-4c21-ab8e-d58ac3574d31"), EquipmentSlot.HEAD, () -> Attributes.ARMOR_TOUGHNESS, 1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:chainmail_chestplate"), UUID.fromString("74d42a8e-d4a3-4c52-ac66-33ab2128e146"), EquipmentSlot.CHEST, () -> Attributes.ARMOR_TOUGHNESS, 1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:chainmail_leggings"), UUID.fromString("de6a0547-fc18-4e84-b87a-d0333aa06854"), EquipmentSlot.LEGS, () -> Attributes.ARMOR_TOUGHNESS, 1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:chainmail_boots"), UUID.fromString("30db05c5-7c2f-4fa8-86d8-e8661e42f197"), EquipmentSlot.FEET, () -> Attributes.ARMOR_TOUGHNESS, 1, AttributeModifier.Operation.ADDITION),

			//Golden
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:golden_helmet"), UUID.fromString("3f22e9a3-0916-43ab-a93f-ba52e5ae28e5"), EquipmentSlot.HEAD, () -> Attributes.ARMOR, -1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:golden_helmet"), UUID.fromString("3f22e9a3-0916-43ab-a93f-ba52e5ae28e5"), EquipmentSlot.HEAD, () -> Attributes.ARMOR_TOUGHNESS, 1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:golden_chestplate"), UUID.fromString("f700b45a-0c51-40f8-9f59-836c519d64d5"), EquipmentSlot.CHEST, () -> Attributes.ARMOR_TOUGHNESS, 1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:golden_leggings"), UUID.fromString("4f1caa92-2558-4416-829c-9faf922d7137"), EquipmentSlot.LEGS, () -> Attributes.ARMOR_TOUGHNESS, 1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:golden_boots"), UUID.fromString("dc49f564-489f-4f70-ab50-ce85cc4bfa85"), EquipmentSlot.FEET, () -> Attributes.ARMOR_TOUGHNESS, 1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:golden_helmet"), UUID.fromString("3f22e9a3-0916-43ab-a93f-ba52e5ae28e5"), EquipmentSlot.HEAD, () -> Attributes.MAX_HEALTH, 2d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:golden_chestplate"), UUID.fromString("f700b45a-0c51-40f8-9f59-836c519d64d5"), EquipmentSlot.CHEST, () -> Attributes.MAX_HEALTH, 2d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:golden_leggings"), UUID.fromString("4f1caa92-2558-4416-829c-9faf922d7137"), EquipmentSlot.LEGS, () -> Attributes.MAX_HEALTH, 2d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:golden_boots"), UUID.fromString("dc49f564-489f-4f70-ab50-ce85cc4bfa85"), EquipmentSlot.FEET, () -> Attributes.MAX_HEALTH, 2d, AttributeModifier.Operation.ADDITION),

			//Iron
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:iron_chestplate"), UUID.fromString("74d42a8e-d4a3-4c52-ac66-33ab2128e146"), EquipmentSlot.CHEST, () -> Attributes.ARMOR, -1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:iron_leggings"), UUID.fromString("de6a0547-fc18-4e84-b87a-d0333aa06854"), EquipmentSlot.LEGS, () -> Attributes.ARMOR, -1, AttributeModifier.Operation.ADDITION),

			//Solarium
			new ItemAttributeModifier(IdTagMatcher.newId("iguanatweaksreborn:solarium_helmet"), UUID.fromString("74d42a8e-d4a3-4c52-ac66-33ab2128e146"), EquipmentSlot.CHEST, () -> Attributes.ARMOR, -1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("iguanatweaksreborn:solarium_boots"), UUID.fromString("de6a0547-fc18-4e84-b87a-d0333aa06854"), EquipmentSlot.LEGS, () -> Attributes.ARMOR, -1, AttributeModifier.Operation.ADDITION),

			//Durium
			new ItemAttributeModifier(IdTagMatcher.newId("iguanatweaksreborn:durium_helmet"), UUID.fromString("ad33dadf-bf7b-4f40-83ed-f93f4721d28e"), EquipmentSlot.HEAD, () -> Attributes.ARMOR, -1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("iguanatweaksreborn:durium_chestplate"), UUID.fromString("f690ba04-4d3f-47d0-ad5d-809238b48f45"), EquipmentSlot.CHEST, () -> Attributes.ARMOR, -1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("iguanatweaksreborn:durium_leggings"), UUID.fromString("0145a1f9-24c4-458f-b217-8bb1440e99b7"), EquipmentSlot.LEGS, () -> Attributes.ARMOR, -1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("iguanatweaksreborn:durium_boots"), UUID.fromString("386b4fb6-f3ca-4a54-a455-4b48a179c17a"), EquipmentSlot.FEET, () -> Attributes.ARMOR, -1, AttributeModifier.Operation.ADDITION),

			//Keego
			new ItemAttributeModifier(IdTagMatcher.newId("iguanatweaksreborn:keego_helmet"), UUID.fromString("ad33dadf-bf7b-4f40-83ed-f93f4721d28e"), EquipmentSlot.HEAD, () -> Attributes.ARMOR, -2, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("iguanatweaksreborn:keego_chestplate"), UUID.fromString("f690ba04-4d3f-47d0-ad5d-809238b48f45"), EquipmentSlot.CHEST, () -> Attributes.ARMOR, -1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("iguanatweaksreborn:keego_leggings"), UUID.fromString("0145a1f9-24c4-458f-b217-8bb1440e99b7"), EquipmentSlot.LEGS, () -> Attributes.ARMOR, -1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("iguanatweaksreborn:keego_boots"), UUID.fromString("386b4fb6-f3ca-4a54-a455-4b48a179c17a"), EquipmentSlot.FEET, () -> Attributes.ARMOR, -1, AttributeModifier.Operation.ADDITION),

			//Diamond
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:diamond_helmet"), UUID.fromString("ad33dadf-bf7b-4f40-83ed-f93f4721d28e"), EquipmentSlot.HEAD, () -> Attributes.ARMOR, -1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:diamond_chestplate"), UUID.fromString("f690ba04-4d3f-47d0-ad5d-809238b48f45"), EquipmentSlot.CHEST, () -> Attributes.ARMOR, -2, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:diamond_leggings"), UUID.fromString("0145a1f9-24c4-458f-b217-8bb1440e99b7"), EquipmentSlot.LEGS, () -> Attributes.ARMOR, -1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:diamond_boots"), UUID.fromString("386b4fb6-f3ca-4a54-a455-4b48a179c17a"), EquipmentSlot.FEET, () -> Attributes.ARMOR, -2, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:diamond_helmet"), UUID.fromString("ad33dadf-bf7b-4f40-83ed-f93f4721d28e"), EquipmentSlot.HEAD, () -> Attributes.ARMOR_TOUGHNESS, -1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:diamond_chestplate"), UUID.fromString("f690ba04-4d3f-47d0-ad5d-809238b48f45"), EquipmentSlot.CHEST, () -> Attributes.ARMOR_TOUGHNESS, -1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:diamond_leggings"), UUID.fromString("0145a1f9-24c4-458f-b217-8bb1440e99b7"), EquipmentSlot.LEGS, () -> Attributes.ARMOR_TOUGHNESS, -1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:diamond_boots"), UUID.fromString("386b4fb6-f3ca-4a54-a455-4b48a179c17a"), EquipmentSlot.FEET, () -> Attributes.ARMOR_TOUGHNESS, -1, AttributeModifier.Operation.ADDITION),

			//Soul Steel
			new ItemAttributeModifier(IdTagMatcher.newId("iguanatweaksreborn:soul_steel_boots"), UUID.fromString("386b4fb6-f3ca-4a54-a455-4b48a179c17a"), EquipmentSlot.FEET, () -> Attributes.ARMOR, -1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("iguanatweaksreborn:soul_steel_helmet"), UUID.fromString("ad33dadf-bf7b-4f40-83ed-f93f4721d28e"), EquipmentSlot.HEAD, () -> Attributes.KNOCKBACK_RESISTANCE, 0.025, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("iguanatweaksreborn:soul_steel_chestplate"), UUID.fromString("f690ba04-4d3f-47d0-ad5d-809238b48f45"), EquipmentSlot.CHEST, () -> Attributes.KNOCKBACK_RESISTANCE, 0.025, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("iguanatweaksreborn:soul_steel_leggings"), UUID.fromString("0145a1f9-24c4-458f-b217-8bb1440e99b7"), EquipmentSlot.LEGS, () -> Attributes.KNOCKBACK_RESISTANCE, 0.025, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("iguanatweaksreborn:soul_steel_boots"), UUID.fromString("386b4fb6-f3ca-4a54-a455-4b48a179c17a"), EquipmentSlot.FEET, () -> Attributes.KNOCKBACK_RESISTANCE, 0.025, AttributeModifier.Operation.ADDITION),

			//Netherite
			//new ItemAttributeModifier(IdTagMatcher.newId("minecraft:netherite_helmet"), UUID.fromString("ad33dadf-bf7b-4f40-83ed-f93f4721d28e"), EquipmentSlot.HEAD, () -> Attributes.ARMOR, -1, AttributeModifier.Operation.ADDITION),
			//new ItemAttributeModifier(IdTagMatcher.newId("minecraft:netherite_chestplate"), UUID.fromString("f690ba04-4d3f-47d0-ad5d-809238b48f45"), EquipmentSlot.CHEST, () -> Attributes.ARMOR, -2, AttributeModifier.Operation.ADDITION),
			//new ItemAttributeModifier(IdTagMatcher.newId("minecraft:netherite_leggings"), UUID.fromString("0145a1f9-24c4-458f-b217-8bb1440e99b7"), EquipmentSlot.LEGS, () -> Attributes.ARMOR, -1, AttributeModifier.Operation.ADDITION),
			//new ItemAttributeModifier(IdTagMatcher.newId("minecraft:netherite_boots"), UUID.fromString("386b4fb6-f3ca-4a54-a455-4b48a179c17a"), EquipmentSlot.FEET, () -> Attributes.ARMOR, -2, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:netherite_helmet"), UUID.fromString("ad33dadf-bf7b-4f40-83ed-f93f4721d28e"), EquipmentSlot.HEAD, () -> Attributes.ARMOR_TOUGHNESS, -1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:netherite_chestplate"), UUID.fromString("f690ba04-4d3f-47d0-ad5d-809238b48f45"), EquipmentSlot.CHEST, () -> Attributes.ARMOR_TOUGHNESS, -1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:netherite_leggings"), UUID.fromString("0145a1f9-24c4-458f-b217-8bb1440e99b7"), EquipmentSlot.LEGS, () -> Attributes.ARMOR_TOUGHNESS, -1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:netherite_boots"), UUID.fromString("386b4fb6-f3ca-4a54-a455-4b48a179c17a"), EquipmentSlot.FEET, () -> Attributes.ARMOR_TOUGHNESS, -1, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:netherite_helmet"), UUID.fromString("ad33dadf-bf7b-4f40-83ed-f93f4721d28e"), EquipmentSlot.HEAD, () -> Attributes.KNOCKBACK_RESISTANCE, -0.05, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:netherite_chestplate"), UUID.fromString("f690ba04-4d3f-47d0-ad5d-809238b48f45"), EquipmentSlot.CHEST, () -> Attributes.KNOCKBACK_RESISTANCE, -0.05, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:netherite_leggings"), UUID.fromString("0145a1f9-24c4-458f-b217-8bb1440e99b7"), EquipmentSlot.LEGS, () -> Attributes.KNOCKBACK_RESISTANCE, -0.05, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.newId("minecraft:netherite_boots"), UUID.fromString("386b4fb6-f3ca-4a54-a455-4b48a179c17a"), EquipmentSlot.FEET, () -> Attributes.KNOCKBACK_RESISTANCE, -0.05, AttributeModifier.Operation.ADDITION)
	));
	public static final ArrayList<ItemAttributeModifier> itemModifiers = new ArrayList<>();

	public static final UUID ATTACK_RANGE_REDUCTION_UUID = UUID.fromString("0dd017a7-274c-4101-85b4-78af20a24c54");
	public static final UUID MOVEMENT_SPEED_REDUCTION_UUID = UUID.fromString("a88ac0d1-e2b3-4cf1-bb0e-9577486c874a");
	@Config(min = -4d, max = 4d)
	@Label(name = "Player attack range modifier", description = "Adds this to players' attack range")
	public static Double playerAttackRangeModifier = -0.5d;
	@Config
	@Label(name = "Players movement speed reduction", description = "Reduces movement speed for players by this percentage.")
	public static Double playersMovementSpeedReduction = 0.05d;
	@Config
	@Label(name = "Disable Crit Arrows bonus damage", description = "If true, Arrows from Bows and Crossbows will no longer deal more damage when fully charged.")
	public static Boolean disableCritArrowsBonusDamage = true;
	@Config(min = 0, max = 1)
	@Label(name = "Hoes Knockback multiplier")
	public static Double hoesKnockbackMultiplier = 0.35d;

	@Config
	@Label(name = "Fix tooltips", description = "Vanilla tooltips on gear don't sum up multiple modifiers (e.g. a sword would have \"4 Attack Damage\" and \"-2 Attack Damage\" instead of \"2 Attack Damage\". This might break other mods messing with these Tooltips (e.g. Quark's improved tooltips)")
	public static Boolean fixTooltips = true;

	@Config
	@Label(name = "Combat Test Strength", description = "Changes Strength effect from +3 damage per level to +20% damage per level. (Requires a Minecraft restart)")
	public static Boolean combatTestStrength = true;
	@Config
	@Label(name = "Better weakness", description = "Changes Weakness like Strength effect from -3 damage per level to -20% damage per level. (Requires a Minecraft restart)")
	public static Boolean betterWeakness = true;
	@Config(min = 0d, max = 10d)
	@Label(name = "Bow's Arrows Base Damage", description = "Set arrow's base damage if shot from bow.")
	public static Double bowsArrowsBaseDamage = 1.5d;

	public Stats(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		addSyncType(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "item_modifiers"), new SyncType(json -> loadAndReadJson(json, itemModifiers, ITEM_MODIFIERS_DEFAULT, ItemAttributeModifier.LIST_TYPE)));
		JSON_CONFIGS.add(new JsonConfig<>("item_modifiers.json", itemModifiers, ITEM_MODIFIERS_DEFAULT, ItemAttributeModifier.LIST_TYPE, true, new ResourceLocation(IguanaTweaksReborn.MOD_ID, "item_modifiers")));
	}

	@Override
	public String getModConfigFolder() {
		return IguanaTweaksReborn.CONFIG_FOLDER;
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

	//Run before Absorption armor
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onAttributeEvent(ItemAttributeModifierEvent event) {
		if (!this.isEnabled())
			return;

		//Try to remove original modifiers first
		if (event.getItemStack().is(REMOVE_ORIGINAL_MODIFIERS_TAG)) {
			Multimap<Attribute, AttributeModifier> modifiers = event.getOriginalModifiers();
			modifiers.forEach(event::removeModifier);
		}

		for (ItemAttributeModifier itemAttributeModifier : itemModifiers) {
			if (!itemAttributeModifier.item.matchesItem(event.getItemStack().getItem()))
				continue;
			if (event.getSlotType() != itemAttributeModifier.slot)
				continue;

			AttributeModifier modifier = new AttributeModifier(itemAttributeModifier.uuid, GENERIC_ITEM_MODIFIER, itemAttributeModifier.amount, itemAttributeModifier.operation);
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
						if (event.getEntity() != null && operation == AttributeModifier.Operation.ADDITION && equipmentslot == EquipmentSlot.MAINHAND) {
							if (attribute.equals(Attributes.ATTACK_DAMAGE)) {
								amount += event.getEntity().getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
								amount += EnchantmentHelper.getDamageBonus(event.getItemStack(), MobType.UNDEFINED);
								isEqualTooltip = true;
							}
							if (attribute.equals(PiercingPickaxes.PIERCING_DAMAGE.get())
									|| attribute.equals(Attributes.ATTACK_SPEED)
									|| attribute.equals(Attributes.KNOCKBACK_RESISTANCE)) {
								amount += event.getEntity().getAttributeBaseValue(attribute);
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