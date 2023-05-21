package insane96mcp.survivalreimagined.module.mining.item;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ForgeHammerItem extends TieredItem {

    public static final String FORGE_COOLDOWN_LANG = SurvivalReimagined.MOD_ID + ".hammer_cooldown";

    final int useCooldown;

    public ForgeHammerItem(Tier pTier, int useCooldown, Properties pProperties) {
        super(pTier, pProperties);
        this.useCooldown = useCooldown;
        this.maxDamage = (int) (this.maxDamage / 3f);
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
        //E.g. with Fortune 3 you'll get between 1 and 4 smashes
        return Mth.nextInt(random, smashes, fortune + smashes);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.BLOCK_EFFICIENCY || enchantment.category == EnchantmentCategory.BREAKABLE || enchantment == Enchantments.BLOCK_FORTUNE || enchantment.category == EnchantmentCategory.VANISHABLE;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.literal(" ").append(Component.translatable(FORGE_COOLDOWN_LANG, SurvivalReimagined.ONE_DECIMAL_FORMATTER.format(this.useCooldown / 20f)).withStyle(ChatFormatting.DARK_GREEN)));
    }
}