package insane96mcp.survivalreimagined.module.mining.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ForgeHammerItem extends TieredItem implements Vanishable {
    protected static final UUID ENTITY_REACH_UUID = UUID.fromString("cdec6524-49a5-465a-a61c-f53c2e637c48");

    public static final String FORGE_COOLDOWN_LANG = SurvivalReimagined.MOD_ID + ".hammer_cooldown";
    public static final String FORGE_DURABILITY_LANG = SurvivalReimagined.MOD_ID + ".hammer_durability";

    final int useCooldown;
    final int useDamageTaken;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public ForgeHammerItem(float baseAttackDamageMultiplier, float attackSpeed, Tier pTier, int useCooldown, int useDamageTaken, Properties pProperties) {
        super(pTier, pProperties);
        this.useCooldown = useCooldown;
        this.useDamageTaken = useDamageTaken;
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", baseAttackDamageMultiplier * getTier().getAttackDamageBonus(), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -(4d - attackSpeed), AttributeModifier.Operation.ADDITION));
        builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ENTITY_REACH_UUID, "Tool modifier", -0.25d, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
     */
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pEquipmentSlot) {
        return pEquipmentSlot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(pEquipmentSlot);
    }

    public int getUseCooldown(ItemStack stack) {
        int useCooldown = this.useCooldown;
        //Each efficiency level decreases cooldown by 10%
        int efficiency = stack.getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY);
        //Capped to minimum half a second
        return Math.max((int) (useCooldown * (1f - (0.1f * efficiency))), 10);
    }

    public int getSmashesOnHit(ItemStack stack, RandomSource random) {
        int smashes = 1;
        //Each fortune level adds a chance to smash +1 time each right click
        int fortune = stack.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);
        if (fortune <= 0)
            return smashes;
        //E.g. with Fortune 3 you'll get between 1 and 4 smashes
        return Mth.nextInt(random, smashes, fortune + smashes);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.BLOCK_EFFICIENCY || enchantment.category == EnchantmentCategory.BREAKABLE || enchantment == Enchantments.BLOCK_FORTUNE || enchantment.category == EnchantmentCategory.VANISHABLE;
    }

    public int getUseDamageTaken() {
        return this.useDamageTaken;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.literal(" ").append(Component.translatable(FORGE_COOLDOWN_LANG, SurvivalReimagined.ONE_DECIMAL_FORMATTER.format(this.useCooldown / 20f)).withStyle(ChatFormatting.DARK_GREEN)));
        pTooltipComponents.add(Component.literal(" ").append(Component.translatable(FORGE_DURABILITY_LANG, this.useDamageTaken).withStyle(ChatFormatting.DARK_GREEN)));
    }
}
