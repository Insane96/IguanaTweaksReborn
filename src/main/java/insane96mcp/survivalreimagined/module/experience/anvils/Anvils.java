package insane96mcp.survivalreimagined.module.experience.anvils;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Label(name = "Anvils", description = "Better repair, free rename and merge.")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class Anvils extends SRFeature {

    public static final String CAN_BE_REPAIRED_WITH_LANG = SurvivalReimagined.MOD_ID + ".can_be_repaired_with";

    @Config(min = 0)
    @Label(name = "Anvil Repair Cap", description = "Set the cap for repairing items in the anvil (vanilla is 40)")
    public static Integer anvilRepairCap = 1024;
    @Config
    @Label(name = "Remove rename cost", description = "Removes cost of renaming items in Anvil")
    public static Boolean freeRenaming = true;
    @Config
    @Label(name = "Merging cost is based off result")
    public static Boolean mergingCostBasedOffResult = true;
    @Config(min = 0, max = 100)
    @Label(name = "Merging Repair bonus", description = "Vanilla is 12%")
    public static Integer mergingRepairBonus = 20;
    @Config
    @Label(name = "No repair cost increase and repair cost based off Enchantments")
    public static Boolean noRepairCostIncreaseAndEnchCost = true;
    @Config(min = 0)
    @Label(name = "Enchantments cost by rarity.Common", description = "Vanilla default: 1")
    public static Integer enchantmentCostCommon = 1;
    @Config(min = 0)
    @Label(name = "Enchantments cost by rarity.Uncommon", description = "Vanilla default: 2")
    public static Integer enchantmentCostUncommon = 2;
    @Config(min = 0)
    @Label(name = "Enchantments cost by rarity.Rare", description = "Vanilla default: 4")
    public static Integer enchantmentCostRare = 3;
    @Config(min = 0)
    @Label(name = "Enchantments cost by rarity.Very Rare", description = "Vanilla default: 8")
    public static Integer enchantmentCostVeryRare = 5;
    @Config(min = 0)
    @Label(name = "Repair cost multiplier", description = "Multiplier for the levels required to repair or merge an item.")
    public static Double repairCostMultiplier = 0.70d;

    @Config
    @Label(name = "Fix anvils with Iron Blocks")
    public static Boolean allowFixingAnvils = true;

    public Anvils(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    public static int getMergingRepairBonus() {
        if (!Feature.isEnabled(Anvils.class))
            return 12;

        return mergingRepairBonus;
    }

    public static Optional<AnvilRecipe> getCustomAnvilRepair(ItemStack left) {
        if (!Feature.isEnabled(Anvils.class))
            return Optional.empty();

        for (AnvilRecipe anvilRecipe : AnvilRecipeReloadListener.RECIPES) {
            if (anvilRecipe.isItemToRepair(left))
                return Optional.of(anvilRecipe);
        }
        return Optional.empty();
    }

    public static Optional<AnvilRecipe.RepairData> getCustomAnvilRepair(ItemStack left, ItemStack right) {
        if (!Feature.isEnabled(Anvils.class))
            return Optional.empty();

        for (AnvilRecipe anvilRecipe : AnvilRecipeReloadListener.RECIPES) {
            if (anvilRecipe.isItemToRepair(left)) {
                Optional<AnvilRecipe.RepairData> repairData = anvilRecipe.getRepairDataFromMaterial(right);
                if (repairData.isPresent())
                    return repairData;
            }
        }
        return Optional.empty();
    }

    @SubscribeEvent
    public void onRightClickAnvil(PlayerInteractEvent.RightClickBlock event) {
        if (!this.isEnabled()
                || !allowFixingAnvils
                || !event.getItemStack().is(Items.IRON_BLOCK))
            return;
        BlockState state = event.getLevel().getBlockState(event.getPos());
        if (!state.is(BlockTags.ANVIL))
            return;

        Direction direction = state.getValue(AnvilBlock.FACING);
        boolean isChipped = state.is(Blocks.CHIPPED_ANVIL);
        boolean isDamaged = state.is(Blocks.DAMAGED_ANVIL);
        if (!isChipped && !isDamaged)
            return;

        event.setResult(Event.Result.DENY);
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (!event.getEntity().getAbilities().instabuild) {
            event.getItemStack().shrink(1);
        }
        event.getLevel().setBlockAndUpdate(event.getPos(), isChipped ? Blocks.ANVIL.defaultBlockState().setValue(AnvilBlock.FACING, direction) : Blocks.CHIPPED_ANVIL.defaultBlockState().setValue(AnvilBlock.FACING, direction));
        event.getLevel().playSound(event.getEntity(), event.getPos(), SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1f, 1.5f);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        if (!this.isEnabled())
            return;

        Minecraft mc = Minecraft.getInstance();
        if (!(mc.screen instanceof AnvilScreen))
            return;

        List<Component> itemsDescriptions = new ArrayList<>();
        Optional<AnvilRecipe> oCustomAnvilRepair = getCustomAnvilRepair(event.getItemStack());
        oCustomAnvilRepair.ifPresent(anvilRecipe -> {
            for (AnvilRecipe.RepairData repairData : anvilRecipe.repairData) {
                if (repairData.repairMaterial().type == IdTagMatcher.Type.TAG) {
                    itemsDescriptions.add(Component.literal("#").append(repairData.repairMaterial().location.toString()));
                }
                else {
                    Optional<Item> oItem = repairData.repairMaterial().getAllItems().stream().findAny();
                    oItem.ifPresent(item -> itemsDescriptions.add(item.getDescription()));
                }
            }
        });

        if (!itemsDescriptions.isEmpty()) {
            String joined = itemsDescriptions.stream().map(Component::getString).collect(Collectors.joining(", "));
            event.getToolTip().add(Component.empty());
            event.getToolTip().add(Component.translatable(CAN_BE_REPAIRED_WITH_LANG, joined).withStyle(ChatFormatting.GREEN));
        }
    }

    public static int getRarityCost(Enchantment enchantment) {
        return switch (enchantment.getRarity()) {
            case COMMON -> enchantmentCostCommon;
            case UNCOMMON -> enchantmentCostUncommon;
            case RARE -> enchantmentCostRare;
            case VERY_RARE -> enchantmentCostVeryRare;
        };
    }
}
