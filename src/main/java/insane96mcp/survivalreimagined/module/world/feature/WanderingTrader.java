package insane96mcp.survivalreimagined.module.world.feature;

import com.google.gson.Gson;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.utils.LogHelper;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Wandering Trader", description = "Change wandering trader offers")
@LoadFeature(module = Modules.Ids.WORLD)
public class WanderingTrader extends SRFeature {
    public WanderingTrader(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @SubscribeEvent
    public void test(WandererTradesEvent event) {
        Gson gson = new Gson();
        for (VillagerTrades.ItemListing itemListing : event.getGenericTrades()) {
            LogHelper.info("%s", gson.toJson(itemListing));
        }
        for (VillagerTrades.ItemListing itemListing : event.getRareTrades()) {
            LogHelper.info("%s", gson.toJson(itemListing));
        }
    }
}
