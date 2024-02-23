package insane96mcp.iguanatweaksreborn.module.combat;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.items.itemstats.ItemStats;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ShovelItem;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

@Label(name = "Bonking Shovels", description = "Shovels deal bonus knockback.")
@LoadFeature(module = Modules.Ids.COMBAT)
public class BonkingShovels extends Feature {

	public static final UUID SHOVEL_KNOCKBACK_MODIFIER_UUID = UUID.fromString("80d9e6b1-c385-49d4-aac0-cb018f4f1b16");

	@Config(min = 0d)
	@Label(name = "Shovels damage to knockback ratio")
	public static Double knockbackRatio = 0.4d;

	public BonkingShovels(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void addAttributeToPickaxes(ItemAttributeModifierEvent event) {
		if (!this.isEnabled()
				|| event.getSlotType() != EquipmentSlot.MAINHAND
				|| !(event.getItemStack().getItem() instanceof ShovelItem shovelItem)
				|| (isEnabled(ItemStats.class) && ItemStats.unbreakableItems && ItemStats.isBroken(event.getItemStack())))
			return;

		event.addModifier(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(SHOVEL_KNOCKBACK_MODIFIER_UUID, "Shovels Knockback modifier", shovelItem.getAttackDamage() * knockbackRatio, AttributeModifier.Operation.ADDITION));
	}
}