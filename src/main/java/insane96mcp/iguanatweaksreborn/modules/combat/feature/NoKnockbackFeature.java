package insane96mcp.iguanatweaksreborn.modules.combat.feature;

import com.google.common.collect.Multimap;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.common.classutils.IdTagMatcher;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CombatEntry;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Label(name = "No Knockback", description = "Player will deal no knockback if attacking with a non-weapon or spamming")
public class NoKnockbackFeature extends Feature {
	private final ForgeConfigSpec.ConfigValue<List<? extends String>> customNoKnockbackItemsConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> noItemNoKnockbackConfig;
	private final ForgeConfigSpec.ConfigValue<Double> attackCooldownNoKnockbackConfig;

	public ArrayList<IdTagMatcher> customNoKnockbackItems;
	public boolean noItemNoKnockback = true;
	public double attackCooldownNoKnockback = 0.925d;

	public NoKnockbackFeature(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		customNoKnockbackItemsConfig = Config.builder
				.comment("A list of items and tags that should deal no knockback when attacking.")
				.defineList("Custom No Knockback Items", ArrayList::new, o -> o instanceof String);
		attackCooldownNoKnockbackConfig = Config.builder
				.comment("When the attack cooldown is below this percentage the player will deal no knockback. (Between 0 and 1, where 1 is the attack fully charged)")
				.defineInRange("Attack Cooldown No Knockback", this.attackCooldownNoKnockback, 0.0d, 1.0d);
		noItemNoKnockbackConfig = Config.builder
				.comment("If true the player will deal no knockback when not using a tool / weapon")
				.define("No Item No Knockback", this.noItemNoKnockback);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		customNoKnockbackItems = IdTagMatcher.parseStringList(customNoKnockbackItemsConfig.get());
		noItemNoKnockback = noItemNoKnockbackConfig.get();
		attackCooldownNoKnockback = attackCooldownNoKnockbackConfig.get();
	}

	@SubscribeEvent
	public void onAttributeEvent(ItemAttributeModifierEvent event) {
		/*if (event.getSlotType() != EquipmentSlotType.MAINHAND)
			return;

		if (event.getItemStack().getItem() instanceof SwordItem || event.getItemStack().getItem() instanceof AxeItem || event.getItemStack().getItem() instanceof TridentItem) {
			AttributeModifier modifier = new AttributeModifier(UUID.fromString("fd34181b-4664-41cc-a86e-0c7979eee129"), IguanaTweaksReborn.RESOURCE_PREFIX + "weapon_nerf", -1.0f, AttributeModifier.Operation.ADDITION);
			event.addModifier(Attributes.ATTACK_DAMAGE, modifier);
		}*/
	}

	@SubscribeEvent
	public void onPlayerAttackEvent(AttackEntityEvent event) {
		if (!this.isEnabled())
			return;
		PlayerEntity player = event.getPlayer();
		if (player.abilities.isCreativeMode)
			return;
		player.getPersistentData().putInt(IguanaTweaksReborn.RESOURCE_PREFIX + "ticksSinceLastSwing", player.ticksSinceLastSwing);
	}

	@SubscribeEvent
	public void onKnockback(LivingKnockBackEvent event) {
		if (!this.isEnabled())
			return;
		LivingEntity attacker = event.getEntityLiving().getAttackingEntity();
		if (!(attacker instanceof PlayerEntity))
			return;
		PlayerEntity player = (PlayerEntity) attacker;
		if (player.abilities.isCreativeMode)
			return;
		List<CombatEntry> combatEntries = event.getEntityLiving().getCombatTracker().combatEntries;
		if (combatEntries.size() > 0 && !(combatEntries.get(combatEntries.size() - 1).getDamageSrc().getImmediateSource() instanceof PlayerEntity))
			return;
		ItemStack itemStack = player.getHeldItemMainhand();
		boolean isInList = false;
		for (IdTagMatcher idTagMatcher : this.customNoKnockbackItems) {
			if (idTagMatcher.matchesItem(itemStack.getItem(), null)) {
				isInList = true;
				break;
			}
		}
		boolean preventKnockback = false;
		Multimap<Attribute, AttributeModifier> attributeModifiers = itemStack.getAttributeModifiers(EquipmentSlotType.MAINHAND);
		if ((!attributeModifiers.containsKey(Attributes.ATTACK_DAMAGE) && this.noItemNoKnockback) || isInList) {
			preventKnockback = true;
		}
		int ticksSinceLastSwing = player.getPersistentData().getInt(Strings.Tags.TIME_SINCE_LAST_SWING);
		float cooldown = MathHelper.clamp((ticksSinceLastSwing + 0.5f) / player.getCooldownPeriod(), 0.0F, 1.0F);
		if (cooldown <= this.attackCooldownNoKnockback)
			preventKnockback = true;
		if (preventKnockback)
			event.setCanceled(true);
	}
}
