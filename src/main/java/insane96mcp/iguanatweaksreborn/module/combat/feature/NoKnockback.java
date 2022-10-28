package insane96mcp.iguanatweaksreborn.module.combat.feature;

import com.google.common.collect.Multimap;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.CombatEntry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Label(name = "No Knockback", description = "Player will deal no knockback if attacking with a non-weapon or spamming")
@LoadFeature(module = Modules.Ids.COMBAT)
public class NoKnockback extends Feature {

	@Config
	@Label(name = "Custom No Knockback Items", description = "A list of items and tags that should deal no knockback when attacking.")
	public static List<IdTagMatcher> customNoKnockbackItems = new ArrayList<>();
	@Config
	@Label(name = "No Item No Knockback", description = "If true the player will deal no knockback when not using a tool / weapon")
	public static Boolean noItemNoKnockback = true;
	@Config(min = 0d, max = 1d)
	@Label(name = "Attack Cooldown No Knockback", description = "When the attack cooldown is below this percentage the player will deal no knockback. (Between 0 and 1, where 1 is the attack fully charged)")
	public static Double attackCooldownNoKnockback = 0.925d;

	public NoKnockback(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void onPlayerAttackEvent(AttackEntityEvent event) {
		if (!this.isEnabled())
			return;
		Player player = event.getEntity();
		if (player.getAbilities().instabuild)
			return;
		player.getPersistentData().putInt(Strings.Tags.TIME_SINCE_LAST_SWING, player.attackStrengthTicker);
	}

	@SubscribeEvent
	public void onKnockback(LivingKnockBackEvent event) {
		if (!this.isEnabled())
			return;
		LivingEntity attacker = event.getEntity().getKillCredit();
		if (!(attacker instanceof Player player))
			return;
		if (player.getAbilities().instabuild)
			return;
		CombatEntry combatEntry = event.getEntity().getCombatTracker().getLastEntry();
		if (combatEntry == null || !(combatEntry.getSource().getDirectEntity() instanceof Player))
			return;
		ItemStack itemStack = player.getMainHandItem();
		boolean isInList = false;
		for (IdTagMatcher idTagMatcher : customNoKnockbackItems) {
			if (idTagMatcher.matchesItem(itemStack.getItem(), null)) {
				isInList = true;
				break;
			}
		}
		boolean preventKnockback = false;
		Multimap<Attribute, AttributeModifier> attributeModifiers = itemStack.getAttributeModifiers(EquipmentSlot.MAINHAND);
		if ((!attributeModifiers.containsKey(Attributes.ATTACK_DAMAGE) && noItemNoKnockback) || isInList) {
			preventKnockback = true;
		}
		int ticksSinceLastSwing = player.getPersistentData().getInt(Strings.Tags.TIME_SINCE_LAST_SWING);
		float cooldown = Mth.clamp((ticksSinceLastSwing + 0.5f) / player.getCurrentItemAttackStrengthDelay(), 0.0F, 1.0F);
		if (cooldown <= attackCooldownNoKnockback)
			preventKnockback = true;
		if (preventKnockback)
			event.setCanceled(true);
	}
}