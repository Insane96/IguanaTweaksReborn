package insane96mcp.iguanatweaksreborn.module.combat;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.data.generator.ITRDamageTypeTagsProvider;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.items.itemstats.ItemStats;
import insane96mcp.iguanatweaksreborn.setup.ITRRegistries;
import insane96mcp.iguanatweaksreborn.utils.MCUtils;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Piercing damage", description = "Adds a new attribute that deals bonus damage that bypasses armor")
@LoadFeature(module = Modules.Ids.COMBAT)
public class PiercingDamage extends Feature {
	public static ResourceKey<DamageType> PIERCING_MOB_ATTACK = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(IguanaTweaksReborn.MOD_ID, "piercing_mob_attack"));
	public static ResourceKey<DamageType> PIERCING_PLAYER_ATTACK = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(IguanaTweaksReborn.MOD_ID, "piercing_player_attack"));

	public static final RegistryObject<Attribute> PIERCING_DAMAGE = ITRRegistries.ATTRIBUTES.register("piercing_damage", () -> new RangedAttribute("attribute.name.piercing_damage", 0d, 0d, 1024d));

	public static final TagKey<DamageType> PIERCING_DAMAGE_TYPE = ITRDamageTypeTagsProvider.create("piercing_damage_type");
	public static final TagKey<DamageType> DOESNT_TRIGGER_PIERCING = ITRDamageTypeTagsProvider.create("doesnt_trigger_piercing");

	public PiercingDamage(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	public static void addAttribute(EntityAttributeModificationEvent event) {
		for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
			if (event.has(entityType, PIERCING_DAMAGE.get()))
				continue;

			event.add(entityType, PIERCING_DAMAGE.get());
		}
	}

	@SuppressWarnings("DataFlowIssue")
	@SubscribeEvent
	public void onEntityDamaged(LivingDamageEvent event) {
		if (!this.isEnabled()
				|| !(event.getSource().getDirectEntity() instanceof LivingEntity attacker)
				|| event.getEntity().isDeadOrDying()
				|| event.getSource().is(DOESNT_TRIGGER_PIERCING)
				|| attacker.getAttribute(PIERCING_DAMAGE.get()) == null
				|| (isEnabled(ItemStats.class) && ItemStats.unbreakableItems && ItemStats.isBroken(attacker.getMainHandItem())))
			return;

		AttributeInstance piercingInstance = attacker.getAttribute(PIERCING_DAMAGE.get());
		float amount = (float) piercingInstance.getValue();
		if (amount <= 0d)
			return;
		DamageSource piercingDamageSource = attacker.damageSources().source(PIERCING_MOB_ATTACK, attacker);
		if (attacker instanceof Player player) {
			piercingDamageSource = attacker.damageSources().source(PIERCING_PLAYER_ATTACK, attacker);
			float f2 = player.getAttackStrengthScale(0.5F);
			amount *= f2 * f2;
		}

		MCUtils.attackEntityIgnoreInvFrames(piercingDamageSource, amount, event.getEntity(), event.getEntity(), true);
	}
}