package insane96mcp.survivalreimagined.module.world;

import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Villagers", description = "Change villagers")
@LoadFeature(module = Modules.Ids.WORLD)
public class Villagers extends SRFeature {

    @Config
    @Label(name = "Villagers and zombie villagers no longer spawn")
    public static Boolean disableVillagers = true;

    public Villagers(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @SubscribeEvent
    public void onVillagerTryToSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (!this.isEnabled()
                || !disableVillagers)
            return;

        if (event.getEntity().getType() == EntityType.VILLAGER/* || event.getEntity().getType() == EntityType.ZOMBIE_VILLAGER*/) //Crashes when a zombie villager tries to spawn from a dead villager
            event.setSpawnCancelled(true);
    }
}
