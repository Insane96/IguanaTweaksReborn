package insane96mcp.iguanatweaksreborn.modules.combat.feature;

import com.google.common.collect.Multimap;
import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.base.ITModule;
import insane96mcp.iguanatweaksreborn.base.Label;
import insane96mcp.iguanatweaksreborn.common.classutils.IdTagMatcher;
import insane96mcp.iguanatweaksreborn.setup.Config;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Label(name = "No Item No Knockback", description = "Player will deal no knockback if attacking with a non-weapon")
public class NoItemNoKnockbackFeature extends ITFeature {
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> customNoKnockbackItemsConfig;

    public ArrayList<IdTagMatcher> customNoKnockbackItems;

    public NoItemNoKnockbackFeature(ITModule module) {
        super(module);
        Config.builder.comment(this.getDescription()).push(this.getName());
        customNoKnockbackItemsConfig = Config.builder
                .comment("A list of items and tags that should deal no knockback when attacking.")
                .defineList("Custom No Knockback Items", ArrayList::new, o -> o instanceof String);
        Config.builder.pop();
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        customNoKnockbackItems = IdTagMatcher.parseStringList(customNoKnockbackItemsConfig.get());
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
        ItemStack itemStack = player.getHeldItemMainhand();
        boolean isInList = false;
        for (IdTagMatcher idTagMatcher : this.customNoKnockbackItems) {
            if (idTagMatcher.isInTagOrItem(itemStack.getItem(), null)) {
                isInList = true;
                break;
            }
        }
        Multimap<Attribute, AttributeModifier> attributeModifiers = itemStack.getAttributeModifiers(EquipmentSlotType.MAINHAND);
        if (!attributeModifiers.containsKey(Attributes.ATTACK_DAMAGE) || isInList) {
            event.setCanceled(true);
        }
    }
}
