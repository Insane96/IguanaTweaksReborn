package insane96mcp.survivalreimagined.module.world.feature;

import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.farming.feature.Crops;
import insane96mcp.survivalreimagined.module.items.feature.Mithril;
import insane96mcp.survivalreimagined.module.world.data.SerializableTrade;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Label(name = "Wandering Trader", description = "Change wandering trader offers")
@LoadFeature(module = Modules.Ids.WORLD)
public class WanderingTrader extends SRFeature {
    public static final Supplier<ArrayList<SerializableTrade>> WANDERING_TRADER_GENERIC_TRADES_DEFAULT = () -> new ArrayList<>(List.of(
            new SerializableTrade(new ItemStack(Items.EMERALD, 2), new ItemStack(Items.WHEAT_SEEDS), 4),
            new SerializableTrade(new ItemStack(Items.EMERALD, 2), new ItemStack(Crops.CARROT_SEEDS.get()), 4),
            new SerializableTrade(new ItemStack(Items.EMERALD, 2), new ItemStack(Crops.POTATO_SEEDS.get()), 4),
            new SerializableTrade(new ItemStack(Items.EMERALD, 2), new ItemStack(Items.BEETROOT_SEEDS), 4),
            new SerializableTrade(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.BROWN_MUSHROOM), 4),
            new SerializableTrade(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.RED_MUSHROOM), 4),
            new SerializableTrade(new ItemStack(Items.EMERALD, 3), new ItemStack(Items.MELON), 2),
            new SerializableTrade(new ItemStack(Items.EMERALD, 3), new ItemStack(Items.PUMPKIN), 2),
            new SerializableTrade(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.VINE), 4),
            new SerializableTrade(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.LILY_PAD, 2), 4),
            new SerializableTrade(new ItemStack(Items.EMERALD, 2), new ItemStack(Items.POINTED_DRIPSTONE, 2), 3),
            new SerializableTrade(new ItemStack(Items.EMERALD, 2), new ItemStack(Items.SEA_PICKLE), 3),
            new SerializableTrade(new ItemStack(Items.EMERALD, 3), new ItemStack(Items.CACTUS), 3),
            new SerializableTrade(new ItemStack(Items.EMERALD, 3), new ItemStack(Items.KELP), 3),
            new SerializableTrade(new ItemStack(Items.EMERALD, 5), new ItemStack(Items.ACACIA_SAPLING), 3),
            new SerializableTrade(new ItemStack(Items.EMERALD, 5), new ItemStack(Items.BIRCH_SAPLING), 3),
            //new SerializableTrade(new ItemStack(Items.EMERALD, 5), new ItemStack(Items.CHERRY_SAPLING), 3),
            new SerializableTrade(new ItemStack(Items.EMERALD, 5), new ItemStack(Items.SPRUCE_SAPLING), 3),
            new SerializableTrade(new ItemStack(Items.EMERALD, 5), new ItemStack(Items.OAK_SAPLING), 3),
            new SerializableTrade(new ItemStack(Items.EMERALD, 5), new ItemStack(Items.DARK_OAK_SAPLING), 3),
            new SerializableTrade(new ItemStack(Items.EMERALD, 5), new ItemStack(Items.JUNGLE_SAPLING), 3),
            new SerializableTrade(new ItemStack(Items.EMERALD, 5), new ItemStack(Items.NAUTILUS_SHELL), 5),
            new SerializableTrade(new ItemStack(Items.ROTTEN_FLESH, 12), new ItemStack(Items.EMERALD), 2),
            new SerializableTrade(new ItemStack(Items.LAPIS_LAZULI, 16), new ItemStack(Items.EMERALD), 2),
            new SerializableTrade(new ItemStack(Items.IRON_INGOT, 8), new ItemStack(Items.EMERALD), 2),
            new SerializableTrade(new ItemStack(Items.GOLD_INGOT, 4), new ItemStack(Items.EMERALD), 3),
            new SerializableTrade(new ItemStack(Mithril.INGOT.get(), 2), new ItemStack(Items.EMERALD), 3),
            new SerializableTrade(new ItemStack(Items.DIAMOND, 2), new ItemStack(Items.EMERALD), 2),
            new SerializableTrade(new ItemStack(Items.LEATHER, 4), new ItemStack(Items.EMERALD), 2),
            new SerializableTrade(new ItemStack(Items.WHEAT_SEEDS, 16), new ItemStack(Items.EMERALD), 2)
    ));

    public static final ArrayList<SerializableTrade> wanderingTraderGenericTrades = new ArrayList<>();

    public static final Supplier<ArrayList<SerializableTrade>> WANDERING_TRADER_RARE_TRADES_DEFAULT = () -> new ArrayList<>(List.of(
            new SerializableTrade(new ItemStack(Items.EMERALD, 6), new ItemStack(Items.BOOK), 1).enchantResult(8, 16, false),
            new SerializableTrade(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.GUNPOWDER, 2), 8),
            new SerializableTrade(new ItemStack(Items.EMERALD, 4), new ItemStack(Items.TROPICAL_FISH_BUCKET), 4),
            new SerializableTrade(new ItemStack(Items.EMERALD, 4), new ItemStack(Items.PUFFERFISH_BUCKET), 4),
            new SerializableTrade(new ItemStack(Items.EMERALD, 3), new ItemStack(Items.EXPERIENCE_BOTTLE), 8)
    ));

    public static final ArrayList<SerializableTrade> wanderingTraderRareTrades = new ArrayList<>();

    public WanderingTrader(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @Override
    public void loadJsonConfigs() {
        if (!this.isEnabled())
            return;
        if (JSON_CONFIGS.isEmpty()) {
            JSON_CONFIGS.add(new JsonConfig<>("generic_trades.json", wanderingTraderGenericTrades, WANDERING_TRADER_GENERIC_TRADES_DEFAULT.get(), SerializableTrade.SERIALIZABLE_TRADE_LIST_TYPE));
            JSON_CONFIGS.add(new JsonConfig<>("rare_trades.json", wanderingTraderRareTrades, WANDERING_TRADER_RARE_TRADES_DEFAULT.get(), SerializableTrade.SERIALIZABLE_TRADE_LIST_TYPE));
        }
        super.loadJsonConfigs();
    }

    @SubscribeEvent
    public void test(WandererTradesEvent event) {
        event.getGenericTrades().clear();
        for (SerializableTrade serializableTrade : wanderingTraderGenericTrades) {
            event.getGenericTrades().add(serializableTrade);
        }
        event.getRareTrades().clear();
        for (SerializableTrade serializableTrade : wanderingTraderRareTrades) {
            event.getRareTrades().add(serializableTrade);
        }
    }
}
