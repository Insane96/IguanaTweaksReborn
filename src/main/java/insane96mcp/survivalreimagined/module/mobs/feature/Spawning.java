package insane96mcp.survivalreimagined.module.mobs.feature;

import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonChangedEvent;
import sereneseasons.api.season.SeasonHelper;

@Label(name = "Spawning", description = "Changes to mob spawn")
@LoadFeature(module = Modules.Ids.MOBS)
public class Spawning extends SRFeature {

    @Config
    @Label(name = "Despawning.Winter", description = "Despawn Distance on winter, note that lower values increase the mobs around the player.")
    public static Integer despawnDistanceWinter = 96;
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
    public static Integer hostileCapWinter = 90;
    @Config
    @Label(name = "Hostile Cap.Spring", description = "Hostile Mobs cap on spring, note that lower values increase the mobs around the player.")
    public static Integer hostileCapSpring = 70;
    @Config
    @Label(name = "Hostile Cap.Summer", description = "Hostile Mobs cap on summer, note that lower values increase the mobs around the player.")
    public static Integer hostileCapSummer = 50;
    @Config
    @Label(name = "Hostile Cap.Autumn", description = "Hostile Mobs cap on autumn, note that lower values increase the mobs around the player.")
    public static Integer hostileCapAutumn = 70;

    @Config
    @Label(name = "No Animals or Sea Creatures Spawn Winter", description = "Animals and Sea Creatures can no longer naturally spawn in Winter.")
    public static Boolean noAnimalOrSeaCreatureSpawnInWinter = true;

    @Config
    @Label(name = "Stupid baby zombies", description = "Disable baby zombies spawning")
    public static Boolean disableBabyZombies = true;

    public Spawning(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @Override
    public void readConfig(ModConfigEvent event) {
        super.readConfig(event);
    }

    @SubscribeEvent
    public void onMobSpawn(SeasonChangedEvent.Standard event) {
        if (!this.isEnabled())
            return;

        update(event.getNewSeason().getSeason());
    }

    @SubscribeEvent
    public void onWorldLoad(LevelEvent.Load event) {
        if (!this.isEnabled()
                || event.getLevel().isClientSide()
                || !(event.getLevel() instanceof Level level)
                || !level.dimension().equals(Level.OVERWORLD))
            return;

        update(SeasonHelper.getSeasonState(level).getSeason());

        if (disableBabyZombies) {
            ForgeConfig.SERVER.zombieBabyChance.set(0d);
        }
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
                MobCategory.CREATURE.max = 10;
                MobCategory.WATER_AMBIENT.max = 20;
            }
            case WINTER -> {
                MobCategory.MONSTER.despawnDistance = despawnDistanceWinter;
                MobCategory.MONSTER.max = hostileCapWinter;
                if (noAnimalOrSeaCreatureSpawnInWinter) {
                    MobCategory.CREATURE.max = 0;
                    MobCategory.WATER_AMBIENT.max = 0;
                }
            }
        }
    }
}
