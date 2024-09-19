package insane96mcp.iguanatweaksreborn.module.mobs.spawning;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.LoadFeature;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonChangedEvent;
import sereneseasons.config.ServerConfig;

@Label(name = "Season Spawning", description = "Changes to mob spawn with Serene Seasons installed")
@LoadFeature(module = Modules.Ids.MOBS, requiresMods = {"sereneseasons"})
public class SeasonSpawning extends Feature {

    @Config
    @Label(name = "Despawning.Winter", description = "Despawn Distance on winter, note that lower values increase the mobs around the player.")
    public static Integer despawnDistanceWinter = 112;
    @Config
    @Label(name = "Despawning.Spring", description = "Despawn Distance on spring, note that lower values increase the mobs around the player.")
    public static Integer despawnDistanceSpring = 128;
    @Config
    @Label(name = "Despawning.Summer", description = "Despawn Distance on summer, note that lower values increase the mobs around the player.")
    public static Integer despawnDistanceSummer = 128;
    @Config
    @Label(name = "Despawning.Autumn", description = "Despawn Distance on autumn, note that lower values increase the mobs around the player.")
    public static Integer despawnDistanceAutumn = 128;

    @Config
    @Label(name = "Hostile Cap.Winter", description = "Hostile Mobs cap on winter, note that lower values increase the mobs around the player.")
    public static Integer hostileCapWinter = 85;
    @Config
    @Label(name = "Hostile Cap.Spring", description = "Hostile Mobs cap on spring, note that lower values increase the mobs around the player.")
    public static Integer hostileCapSpring = 70;
    @Config
    @Label(name = "Hostile Cap.Summer", description = "Hostile Mobs cap on summer, note that lower values increase the mobs around the player.")
    public static Integer hostileCapSummer = 55;
    @Config
    @Label(name = "Hostile Cap.Autumn", description = "Hostile Mobs cap on autumn, note that lower values increase the mobs around the player.")
    public static Integer hostileCapAutumn = 70;

    @Config
    @Label(name = "Animals and sea creature in cold seasons", description = "Animals and Sea Creatures can no longer naturally spawn in Winter and will spawn less in autumn.")
    public static Boolean animalsAndSeaCreatureInColdSeasons = false;

    public SeasonSpawning(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && ModList.get().isLoaded("sereneseasons");
    }

    @SubscribeEvent
    public void onSeasonChanged(SeasonChangedEvent.Standard event) {
        if (!this.isEnabled()
                || !ServerConfig.isDimensionWhitelisted(event.getLevel().dimension()))
            return;

        update(event.getNewSeason().getSeason());
    }

    private void update(Season season) {
        switch (season) {
            case SPRING -> {
                MobCategory.MONSTER.despawnDistance = despawnDistanceSpring;
                MobCategory.MONSTER.max = hostileCapSpring;
                MobCategory.CREATURE.max = 10;
                MobCategory.WATER_AMBIENT.max = 20;
            }
            case SUMMER -> {
                MobCategory.MONSTER.despawnDistance = despawnDistanceSummer;
                MobCategory.MONSTER.max = hostileCapSummer;
                MobCategory.CREATURE.max = 10;
                MobCategory.WATER_AMBIENT.max = 20;
            }
            case AUTUMN -> {
                MobCategory.MONSTER.despawnDistance = despawnDistanceAutumn;
                MobCategory.MONSTER.max = hostileCapAutumn;
                if (!animalsAndSeaCreatureInColdSeasons) {
                    MobCategory.CREATURE.max = 5;
                    MobCategory.WATER_AMBIENT.max = 10;
                }
            }
            case WINTER -> {
                MobCategory.MONSTER.despawnDistance = despawnDistanceWinter;
                MobCategory.MONSTER.max = hostileCapWinter;
                if (!animalsAndSeaCreatureInColdSeasons) {
                    MobCategory.CREATURE.max = 0;
                    MobCategory.WATER_AMBIENT.max = 0;
                }
            }
        }
    }
}