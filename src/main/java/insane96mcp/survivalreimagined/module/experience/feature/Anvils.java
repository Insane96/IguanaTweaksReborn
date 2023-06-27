package insane96mcp.survivalreimagined.module.experience.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.experience.data.TwinIdTagMatcher;
import insane96mcp.survivalreimagined.network.message.JsonConfigSyncMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Label(name = "Anvils", description = "Make anvils usable to create blocks. Use the anvil_transformations.json file in the feature's folder to change or add block transformations.")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class Anvils extends SRFeature {

    public static final String PARTIALLY_REPAIRED_LANG = SurvivalReimagined.MOD_ID + ".can_be_partially_repaired";

    @Config(min = 0)
    @Label(name = "Anvil Repair Cap", description = "Set the cap for repairing items in the anvil (vanilla is 40)")
    public static Integer anvilRepairCap = 1024;
    @Config
    @Label(name = "Remove rename cost", description = "Removes cost of renaming items in Anvil")
    public static Boolean freeRenaming = true;
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
    @Config
    @Label(name = "Partially repair Smithed items with base material", description = "E.g. Netherite Tools can be repaired up to 70% of max durability with Diamonds. More items/repair item combination can be added in the anvil_partial_repair_items.json file")
    public static Boolean partiallyRepairUpgradedItemsWithBaseMaterial = true;
    public static final ArrayList<TwinIdTagMatcher> PARTIAL_REPAIR_ITEMS = new ArrayList<>(List.of(
            new TwinIdTagMatcher(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/netherite", IdTagMatcher.Type.ID,"survivalreimagined:soul_steel_ingot"),
            new TwinIdTagMatcher(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/armor/netherite", IdTagMatcher.Type.ID,"survivalreimagined:soul_steel_ingot"),
            new TwinIdTagMatcher(IdTagMatcher.Type.ID, "shieldsplus:netherite_shield", IdTagMatcher.Type.ID,"survivalreimagined:soul_steel_ingot"),
            new TwinIdTagMatcher(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/durium", IdTagMatcher.Type.ID,"minecraft:iron_ingot"),
            new TwinIdTagMatcher(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/armor/durium", IdTagMatcher.Type.ID,"minecraft:iron_ingot"),
            new TwinIdTagMatcher(IdTagMatcher.Type.ID, "survivalreimagined:durium_shield", IdTagMatcher.Type.ID,"minecraft:iron_ingot"),
            new TwinIdTagMatcher(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/hand/solarium", IdTagMatcher.Type.ID,"minecraft:iron_ingot"),
            new TwinIdTagMatcher(IdTagMatcher.Type.TAG, "survivalreimagined:equipment/armor/solarium", IdTagMatcher.Type.ID,"minecraft:iron_ingot"),
            new TwinIdTagMatcher(IdTagMatcher.Type.ID, "survivalreimagined:solarium_shield", IdTagMatcher.Type.ID,"minecraft:iron_ingot")
    ));
    public static final ArrayList<TwinIdTagMatcher> partialRepairItems = new ArrayList<>();

    public Anvils(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
        JSON_CONFIGS.add(new JsonConfig<>("anvil_partial_repair_items.json", partialRepairItems, PARTIAL_REPAIR_ITEMS, TwinIdTagMatcher.LIST_TYPE, true, JsonConfigSyncMessage.ConfigType.PARTIAL_REPAIR_ITEMS));
    }

    public static void handleSyncPacket(String json) {
        loadAndReadJson(json, partialRepairItems, PARTIAL_REPAIR_ITEMS, TwinIdTagMatcher.LIST_TYPE);
    }

    public static boolean isPartialRepairItem(ItemStack left, ItemStack right) {
        if (!Feature.isEnabled(Anvils.class)
                || !partiallyRepairUpgradedItemsWithBaseMaterial)
            return false;

        for (TwinIdTagMatcher anvilRepairItem : partialRepairItems) {
            if (anvilRepairItem.matchesItems(left.getItem(), right.getItem()))
                return true;
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        if (!this.isEnabled()
                || !partiallyRepairUpgradedItemsWithBaseMaterial)
            return;

        Minecraft mc = Minecraft.getInstance();
        if (!(mc.screen instanceof AnvilScreen))
            return;

        for (TwinIdTagMatcher partiallyRepairItem : partialRepairItems) {
            if (partiallyRepairItem.idTagMatcherA.matchesItem(event.getItemStack().getItem())) {
                Optional<Item> oItem = partiallyRepairItem.idTagMatcherB.getAllItems().stream().findAny();
                oItem.ifPresent(item -> {
                    event.getToolTip().add(Component.empty());
                    event.getToolTip().add(Component.translatable(PARTIALLY_REPAIRED_LANG, item.getDescription()).withStyle(ChatFormatting.GREEN));
                });
                if (oItem.isPresent())
                    break;
            }
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
