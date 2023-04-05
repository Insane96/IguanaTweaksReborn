package insane96mcp.survivalreimagined.module.combat.feature;

import com.google.common.collect.Multimap;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.setup.Strings;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.resources.ResourceLocation;
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

@Label(name = "Knockback", description = "Player will deal reduced knockback if attacking with a non-weapon or spamming.")
@LoadFeature(module = Modules.Ids.COMBAT)
public class Knockback extends Feature {

	public static final ResourceLocation NO_KNOCKBACK_TAG = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "no_knockback");

	@Config
	@Label(name = "No Weapon No Knockback", description = "If true the player will deal no knockback when not using an item that doesn't have the attack damage attribute.")
	public static Boolean noItemNoKnockback = true;

	@Config(min = 0d, max = 1d)
	@Label(name = "Knockback reduction", description = "Percentage knockback dealt when conditions are met.")
	public static Double knockbackReduction = 0.6d;

	public Knockback(Module module, boolean enabledByDefault, boolean canBeDisabled) {
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
		LivingEntity attacker = event.getEntity().lastHurtByPlayer;
		if (attacker == null)
			attacker = event.getEntity().getKillCredit();
		if (!(attacker instanceof Player player))
			return;
		if (player.getAbilities().instabuild)
			return;
		CombatEntry combatEntry = event.getEntity().getCombatTracker().getLastEntry();
		if (combatEntry == null || !(combatEntry.getSource().getDirectEntity() instanceof Player))
			return;
		ItemStack itemStack = player.getMainHandItem();

		boolean isInTag = Utils.isItemInTag(itemStack.getItem(), NO_KNOCKBACK_TAG);

		boolean preventKnockback = false;
		Multimap<Attribute, AttributeModifier> attributeModifiers = itemStack.getAttributeModifiers(EquipmentSlot.MAINHAND);
		if ((!attributeModifiers.containsKey(Attributes.ATTACK_DAMAGE) && noItemNoKnockback) || isInTag) {
			preventKnockback = true;
		}
		int ticksSinceLastSwing = player.getPersistentData().getInt(Strings.Tags.TIME_SINCE_LAST_SWING);
		float cooldown = Mth.clamp((ticksSinceLastSwing + 0.5f) / player.getCurrentItemAttackStrengthDelay(), 0.0F, 1.0F);
		if (cooldown <= 0.9f)
			preventKnockback = true;
		if (preventKnockback) {
			if (knockbackReduction == 0d)
				event.setCanceled(true);
			else
				event.setStrength(event.getStrength() * knockbackReduction.floatValue());
		}
	}
}