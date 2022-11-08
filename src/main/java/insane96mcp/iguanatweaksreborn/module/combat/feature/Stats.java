package insane96mcp.iguanatweaksreborn.module.combat.feature;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.combat.utils.ItemAttributeModifier;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Stats", description = "Various changes from weapons damage to armor reduction")
@LoadFeature(module = Modules.Ids.COMBAT)
public class Stats extends ITFeature {
	static final List<ItemAttributeModifier> CLASS_ATTRIBUTE_MODIFIER = new ArrayList<>();

	public static final ArrayList<ItemAttributeModifier> itemModifiers = new ArrayList<>(Arrays.asList(
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:iron_helmet", EquipmentSlot.HEAD, Attributes.ARMOR_TOUGHNESS, 1.0d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:iron_chestplate", EquipmentSlot.CHEST, Attributes.ARMOR_TOUGHNESS, 1.0d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:iron_leggings", EquipmentSlot.LEGS, Attributes.ARMOR_TOUGHNESS, 1.0d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:iron_boots", EquipmentSlot.FEET, Attributes.ARMOR_TOUGHNESS, 1.0d, AttributeModifier.Operation.ADDITION),

			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:netherite_helmet", EquipmentSlot.HEAD, Attributes.ARMOR, 1.0d, AttributeModifier.Operation.ADDITION),
			new ItemAttributeModifier(IdTagMatcher.Type.ID, "minecraft:netherite_boots", EquipmentSlot.FEET, Attributes.ARMOR, 1.0d, AttributeModifier.Operation.ADDITION)
	));

	@Config
	@Label(name = "Nerf weapons", description = "If true, Swords, Tridents and Axes get -1 damage and Axes get -1 attack reach.")
	public static Boolean nerfWeapons = true;
	@Config(min = 0d, max = 10d)
	@Label(name = "Power Enchantment Damage Increase", description = "Set arrow's damage increase with the Power enchantment (vanilla is 0.5). Set to 0.5 to disable.")
	public static Double powerEnchantmentDamageIncrease = 0.35d;
	@Config
	@Label(name = "Disable Arrow Crits", description = "If true, Arrows from Bows will no longer randomly crit (basically disables the random bonus damage given when firing a fully charged arrow).")
	public static Boolean disableCritArrows = true;
	@Config
	@Label(name = "Adjust Crossbow Damage", description = "If true, Arrows from Crossbows will no longer deal random damage, but a set amount of damage (about 9 at a medium distance, like Bedrock Edition).")
	public static Boolean adjustCrossbowDamage = true;
	@Config
	@Label(name = "Nerf Protection Enchantment", description = """
						DISABLE: Disables protection enchantment.
						NERF: Sets max protection level to 3 instead of 4
						NONE: no changes to protection are done""")
	public static ProtectionNerf protectionNerf = ProtectionNerf.DISABLE;

	public Stats(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@Override
	public void readConfig(final ModConfigEvent event) {
		super.readConfig(event);
		CLASS_ATTRIBUTE_MODIFIER.clear();
		if (nerfWeapons) {
			CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(SwordItem.class, EquipmentSlot.MAINHAND, Attributes.ATTACK_DAMAGE, -1d, AttributeModifier.Operation.ADDITION));
			CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(AxeItem.class, EquipmentSlot.MAINHAND, Attributes.ATTACK_DAMAGE, -1d, AttributeModifier.Operation.ADDITION));
			CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(AxeItem.class, EquipmentSlot.MAINHAND, ForgeMod.ATTACK_RANGE.get(), -1d, AttributeModifier.Operation.ADDITION));
			CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(TridentItem.class, EquipmentSlot.MAINHAND, Attributes.ATTACK_DAMAGE, -1d, AttributeModifier.Operation.ADDITION));
		}
	}

	static final Type itemAttributeModifierListType = new TypeToken<ArrayList<ItemAttributeModifier>>(){}.getType();
	@Override
	public void loadJsonConfigs() {
		super.loadJsonConfigs();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		File itemModifiersFile = new File(jsonConfigFolder, "item_modifiers.json");
		if (!itemModifiersFile.exists()) {
			try {
				if (!itemModifiersFile.createNewFile()) {
					throw new Exception("File#createNewFile failed");
				}
				String json = gson.toJson(itemModifiers, itemAttributeModifierListType);
				Files.write(itemModifiersFile.toPath(), json.getBytes());
			}
			catch (Exception e) {
				LogHelper.error("Failed to create default Json %s: %s", FilenameUtils.removeExtension(itemModifiersFile.getName()), e.getMessage());
			}
		}

		itemModifiers.clear();
		try {
			FileReader fileReader = new FileReader(itemModifiersFile);
			List<ItemAttributeModifier> itemAttributeModifiers = gson.fromJson(fileReader, itemAttributeModifierListType);
			//itemAttributeModifiers.validate();
			itemModifiers.addAll(itemAttributeModifiers);
		}
		catch (JsonSyntaxException e) {
			LogHelper.error("Parsing error loading Json %s: %s", FilenameUtils.removeExtension(itemModifiersFile.getName()), e.getMessage());
		}
		catch (Exception e) {
			LogHelper.error("Failed loading Json %s: %s", FilenameUtils.removeExtension(itemModifiersFile.getName()), e.getMessage());
		}
	}

	public static void addClassItemAttributeModifier(Class<? extends Item> itemClass, EquipmentSlot slot, Attribute attribute, double amount, AttributeModifier.Operation operation) {
		CLASS_ATTRIBUTE_MODIFIER.add(new ItemAttributeModifier(itemClass, slot, attribute, amount, operation));
	}

	@SubscribeEvent
	public void onArrowSpawn(EntityJoinLevelEvent event) {
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
		if (disableCritArrows)
			arrow.setCritArrow(false);

		if (powerEnchantmentDamageIncrease != 0.5d && arrow.getOwner() instanceof LivingEntity) {
			int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER_ARROWS, (LivingEntity) arrow.getOwner());
			double powerReduction = 0.5d - powerEnchantmentDamageIncrease;
			arrow.setBaseDamage(arrow.getBaseDamage() - (powerLevel * powerReduction + powerReduction));
		}
	}

	private void processCrossbow(AbstractArrow arrow) {
		if (adjustCrossbowDamage) {
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

	private void classAttributeModifiers(ItemAttributeModifierEvent event) {
		for (ItemAttributeModifier itemAttributeModifier : CLASS_ATTRIBUTE_MODIFIER) {
			if (itemAttributeModifier.itemClass.equals(event.getItemStack().getItem().getClass()) && itemAttributeModifier.slot.equals(event.getSlotType())) {
				AttributeModifier modifier = new AttributeModifier(Strings.AttributeModifiers.GENERIC_ITEM_MODIFIER_UUID, Strings.AttributeModifiers.GENERIC_ITEM_MODIFIER, itemAttributeModifier.amount, itemAttributeModifier.operation);
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

			AttributeModifier modifier = new AttributeModifier(Strings.AttributeModifiers.GENERIC_ITEM_MODIFIER_UUID, Strings.AttributeModifiers.GENERIC_ITEM_MODIFIER, itemAttributeModifier.amount, itemAttributeModifier.operation);
			event.addModifier(itemAttributeModifier.attribute, modifier);
		}
	}

	public static boolean disableEnchantment(Enchantment enchantment) {
		return enchantment == Enchantments.ALL_DAMAGE_PROTECTION && protectionNerf == ProtectionNerf.DISABLE;
	}

	public enum ProtectionNerf {
		NONE, NERF, DISABLE
	}
}