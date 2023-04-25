package insane96mcp.survivalreimagined.module.combat.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.event.PostEntityHurtEvent;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.PickaxeItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Piercing Pickaxes", description = "Pickaxes deal bonus piercing damage.")
@LoadFeature(module = Modules.Ids.COMBAT)
public class PiercingPickaxes extends Feature {

	ResourceKey<DamageType> PIERCING_MOB_ATTACK = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(SurvivalReimagined.MOD_ID, "piercing_mob_attack"));
	ResourceKey<DamageType> PIERCING_PLAYER_ATTACK = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(SurvivalReimagined.MOD_ID, "piercing_player_attack"));
	static final String PIERCING_TOOLTIP = SurvivalReimagined.MOD_ID + ".piercing_damage";

	@Config(min = 0d)
	@Label(name = "Pickaxe damage to piercing ratio")
	public static Double piercingRatio = 1d;

	public PiercingPickaxes(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SuppressWarnings("DataFlowIssue")
	@SubscribeEvent
	public void onPostEntityDamaged(PostEntityHurtEvent event) {
		if (!this.isEnabled()
				|| !(event.getDamageSource().getDirectEntity() instanceof LivingEntity attacker)
				|| event.getDamageSource().is(PIERCING_MOB_ATTACK)
				|| event.getDamageSource().is(PIERCING_PLAYER_ATTACK))
			return;

		if (attacker.getMainHandItem().getItem() instanceof PickaxeItem pickaxeItem) {
			DamageSource piercingDamageSource;
			if (attacker instanceof Player)
				piercingDamageSource = attacker.damageSources().source(PIERCING_PLAYER_ATTACK, attacker);
			else
				piercingDamageSource = attacker.damageSources().source(PIERCING_MOB_ATTACK, attacker);

			AttributeModifier attributeModifier = new AttributeModifier("Piercing Knockback resistance", 1d, AttributeModifier.Operation.ADDITION);
			if (event.getEntity().getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null)
				event.getEntity().getAttribute(Attributes.KNOCKBACK_RESISTANCE).addTransientModifier(attributeModifier);
			event.getEntity().hurt(piercingDamageSource, (float) (pickaxeItem.getAttackDamage() * piercingRatio));
			if (event.getEntity().getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null)
				event.getEntity().getAttribute(Attributes.KNOCKBACK_RESISTANCE).removeModifier(attributeModifier);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onTooltip(ItemTooltipEvent event) {
		if (!this.isEnabled()
				|| !(event.getItemStack().getItem() instanceof PickaxeItem pickaxeItem))
			return;

		event.getToolTip().add(Component.literal(" ").append(Component.translatable(PIERCING_TOOLTIP, SurvivalReimagined.ONE_DECIMAL_FORMATTER.format(pickaxeItem.getAttackDamage()))).withStyle(ChatFormatting.DARK_GREEN));
	}

}