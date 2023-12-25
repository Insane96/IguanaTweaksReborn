package insane96mcp.iguanatweaksreborn.module.mobs;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonChangedEvent;
import sereneseasons.api.season.SeasonHelper;

@Label(name = "Spawning", description = "Changes to mob spawn")
@LoadFeature(module = Modules.Ids.MOBS)
public class Spawning extends Feature {

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

    @Config
    @Label(name = "Phantoms in the End", description = "Disables insomnia and makes Phantoms spawn naturally in the End")
    public static Boolean phantomsInTheEnd = true;

    @Config
    @Label(name = "No Zombie Villagers", description = "Disables Zombie Villagers")
    public static Boolean noZombieVillagers = true;
    @Config
    @Label(name = "Allow world spawn spawn", description = "Allows mobs to spawn in the world spawn (in vanilla mobs can't spawn in a 24 blocks radius from world spawn)")
    public static Boolean allowWorldSpawnSpawn = true;

    public Spawning(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        if (!this.isEnabled()
                || !phantomsInTheEnd)
            return;

        event.getServer().getGameRules().getRule(GameRules.RULE_DOINSOMNIA).set(false, event.getServer());
    }

    @SubscribeEvent
    public void onZombieVillagerSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (!this.isEnabled()
                || !noZombieVillagers
                || event.getEntity().getType() != EntityType.ZOMBIE_VILLAGER
                || event.getEntity().isAddedToWorld())
            return;

        event.setSpawnCancelled(true);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onSeasonChanged(SeasonChangedEvent.Standard event) {
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
