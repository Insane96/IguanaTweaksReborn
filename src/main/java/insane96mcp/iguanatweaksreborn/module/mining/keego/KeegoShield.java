package insane96mcp.iguanatweaksreborn.module.mining.keego;

import insane96mcp.iguanatweaksreborn.module.items.solarium.item.SolariumShield;
import insane96mcp.iguanatweaksreborn.setup.SRRegistries;
import insane96mcp.shieldsplus.setup.SPItems;
import insane96mcp.shieldsplus.world.item.SPShieldItem;
import insane96mcp.shieldsplus.world.item.SPShieldMaterial;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class KeegoShield extends SPShieldItem {
    public static final SPShieldMaterial SHIELD_MATERIAL = new SPShieldMaterial("keego", 452, Keego.GEM, 9, Rarity.COMMON);
    public KeegoShield(Properties p_43089_) {
        super(SHIELD_MATERIAL, p_43089_);
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !ItemStack.isSameItem(oldStack, newStack);
    }

    @Override
    public int getCooldown(ItemStack stack, @Nullable LivingEntity entity, Level level) {
        int baseCooldown = super.getCooldown(stack, entity, level);
        if (entity == null
                || !entity.hasEffect(Keego.ATTACK_MOMENTUM.get()))
            return baseCooldown;

        return baseCooldown - (entity.getEffect(Keego.ATTACK_MOMENTUM.get()).getAmplifier() + 1) * 2;
    }

    public static RegistryObject<SPShieldItem> registerShield(String id) {
        Item.Properties properties = new Item.Properties().durability(SHIELD_MATERIAL.durability).rarity(SHIELD_MATERIAL.rarity);
        RegistryObject<SPShieldItem> shield = SRRegistries.ITEMS.register(id, () -> new SolariumShield(properties));
        SPItems.SHIELDS.add(shield);
        return shield;
    }
}
