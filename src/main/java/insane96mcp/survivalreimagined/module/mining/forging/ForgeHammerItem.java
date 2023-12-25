package insane96mcp.survivalreimagined.module.mining.forging;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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

    public ForgeHammerItem(Tier tier, int useCooldown, int useDamageTaken, Properties pProperties) {
        super(tier, pProperties);
        this.useCooldown = useCooldown;
        this.useDamageTaken = useDamageTaken;
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", 10 + getTier().getAttackDamageBonus(), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -(4d - 0.4), AttributeModifier.Operation.ADDITION));
        builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ENTITY_REACH_UUID, "Tool modifier", -0.75d, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
     */
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(equipmentSlot);
    }

    public int getUseCooldown(@Nullable LivingEntity entity, ItemStack stack) {
        int cooldown = this.useCooldown;
        int efficiency = stack.getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY);
        if (efficiency <= 0)
            return cooldown;
        //Each efficiency removed 2 ticks from cooldown
        return cooldown - (efficiency * 2);
    }

    public int getSmashesOnHit(ItemStack stack, RandomSource random) {
        return 1;
    }

    public void onUse(Player player, ItemStack stack) {
        player.getCooldowns().addCooldown(this, this.getUseCooldown(player, stack));
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.BLOCK_EFFICIENCY || enchantment.category == EnchantmentCategory.BREAKABLE || enchantment.category == EnchantmentCategory.VANISHABLE;
    }

    public int getUseDamageTaken() {
        return this.useDamageTaken;
    }

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        pStack.hurtAndBreak(this.useDamageTaken, pAttacker, (p_43296_) -> {
            p_43296_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;
    }

    /**
     * Called when a {@link net.minecraft.world.level.block.Block} is destroyed using this Item. Return {@code true} to
     * criterion the "Use Item" statistic.
     */
    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        if (pState.getDestroySpeed(pLevel, pPos) != 0.0F) {
            pStack.hurtAndBreak(this.useDamageTaken, pEntityLiving, (p_43276_) -> {
                p_43276_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }

        return true;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(CommonComponents.space().append(Component.translatable(FORGE_COOLDOWN_LANG, SurvivalReimagined.ONE_DECIMAL_FORMATTER.format(this.getUseCooldown(null, pStack) / 20f)).withStyle(ChatFormatting.DARK_GREEN)));
        pTooltipComponents.add(CommonComponents.space().append(Component.translatable(FORGE_DURABILITY_LANG, this.useDamageTaken).withStyle(ChatFormatting.DARK_GREEN)));
    }
}
