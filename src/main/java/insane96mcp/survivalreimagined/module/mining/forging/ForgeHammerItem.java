package insane96mcp.survivalreimagined.module.mining.forging;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ForgeHammerItem extends TieredItem implements Vanishable {
    protected static final UUID ENTITY_REACH_UUID = UUID.fromString("cdec6524-49a5-465a-a61c-f53c2e637c48");

    public static final String FORGE_COOLDOWN_LANG = SurvivalReimagined.MOD_ID + ".hammer_cooldown";
    public static final String FORGE_DURABILITY_LANG = SurvivalReimagined.MOD_ID + ".hammer_durability";

    final int useCooldown;
    final int useDamageTaken;

    public ForgeHammerItem(Tier tier, int useCooldown, int useDamageTaken, Properties pProperties) {
        super(tier, pProperties);
        this.useCooldown = useCooldown;
        this.useDamageTaken = useDamageTaken;
    }

    public int getUseCooldown(ItemStack stack) {
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

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.BLOCK_EFFICIENCY || enchantment.category == EnchantmentCategory.BREAKABLE || enchantment.category == EnchantmentCategory.VANISHABLE;
    }

    public int getUseDamageTaken() {
        return this.useDamageTaken;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(CommonComponents.space().append(Component.translatable(FORGE_COOLDOWN_LANG, SurvivalReimagined.ONE_DECIMAL_FORMATTER.format(this.getUseCooldown(pStack) / 20f)).withStyle(ChatFormatting.DARK_GREEN)));
        pTooltipComponents.add(CommonComponents.space().append(Component.translatable(FORGE_DURABILITY_LANG, this.useDamageTaken).withStyle(ChatFormatting.DARK_GREEN)));
    }
}
