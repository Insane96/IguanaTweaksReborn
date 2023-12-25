package insane96mcp.iguanatweaksreborn.module.experience.anvils;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
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
public class Anvils extends Feature {

    public static final String CAN_BE_REPAIRED_WITH_LANG = IguanaTweaksReborn.MOD_ID + ".can_be_repaired_with";

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
    public static Integer mergingRepairBonus = 15;
    @Config
    @Label(name = "No xp repair cost", description = "Repairing items in an anvil doesn't cost xp")
    public static Boolean noXpRepairCost = true;
    @Config(min = 0)
    @Label(name = "Enchantments cost by rarity.Common", description = "Vanilla: 1")
    public static Integer enchantmentCostCommon = 1;
    @Config(min = 0)
    @Label(name = "Enchantments cost by rarity.Uncommon", description = "Vanilla: 2")
    public static Integer enchantmentCostUncommon = 2;
    @Config(min = 0)
    @Label(name = "Enchantments cost by rarity.Rare", description = "Vanilla: 4")
    public static Integer enchantmentCostRare = 3;
    @Config(min = 0)
    @Label(name = "Enchantments cost by rarity.Very Rare", description = "Vanilla: 8")
    public static Integer enchantmentCostVeryRare = 6;
    @Config(min = 0)
    @Label(name = "Repair cost multiplier", description = "Multiplier for the levels required to repair or merge an item.")
    public static Double repairCostMultiplier = 0.70d;
    @Config(min = 0)
    @Label(name = "Break chance", description = "Chance for an anvil to become chipped/damaged/break. Vanilla is 12%")
    public static Double breakChance = 0.075d;
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

    public static Optional<AnvilRepair> getCustomAnvilRepair(ItemStack left) {
        if (!Feature.isEnabled(Anvils.class))
            return Optional.empty();

        for (AnvilRepair anvilRepair : AnvilRepairReloadListener.REPAIRS) {
            if (anvilRepair.isItemToRepair(left))
                return Optional.of(anvilRepair);
        }
        return Optional.empty();
    }

    public static Optional<AnvilRepair.RepairData> getCustomAnvilRepair(ItemStack left, ItemStack right) {
        if (!Feature.isEnabled(Anvils.class))
            return Optional.empty();

        for (AnvilRepair anvilRepair : AnvilRepairReloadListener.REPAIRS) {
            if (anvilRepair.isItemToRepair(left)) {
                Optional<AnvilRepair.RepairData> repairData = anvilRepair.getRepairDataFromMaterial(right);
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

        if (event.getEntity() instanceof ServerPlayer serverPlayer)
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, event.getPos(), event.getItemStack());
    }

    @SubscribeEvent
    public void onAnvilRepair(AnvilRepairEvent event) {
        if (!this.isEnabled()
                || breakChance == 0.12f)
            return;

        event.setBreakChance(breakChance.floatValue());
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
        Optional<AnvilRepair> oCustomAnvilRepair = getCustomAnvilRepair(event.getItemStack());
        oCustomAnvilRepair.ifPresent(anvilRepair -> {
            for (AnvilRepair.RepairData repairData : anvilRepair.repairData) {
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
