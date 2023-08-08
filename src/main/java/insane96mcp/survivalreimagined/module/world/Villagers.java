package insane96mcp.survivalreimagined.module.world;

import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Villagers", description = "Change villagers")
@LoadFeature(module = Modules.Ids.WORLD)
public class Villagers extends SRFeature {

    @Config
    @Label(name = "Villagers are despawned and zombie villagers are replaced with normal zombies")
    public static Boolean disableVillagers = true;

    public Villagers(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @SubscribeEvent
    public void onWanderingTradersEvent(EntityJoinLevelEvent event) {
        if (!this.isEnabled()
                || !disableVillagers)
            return;

        if (event.getEntity().getType() == EntityType.VILLAGER) {
            event.getEntity().setPos(new Vec3(0, -64, 0));
            event.getEntity().kill();
        }
        else if (event.getEntity().getType() == EntityType.ZOMBIE_VILLAGER) {
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                CompoundTag tag = new CompoundTag();
                event.getEntity().saveWithoutId(tag);
                Zombie zombie = EntityType.ZOMBIE.create(serverLevel, tag, null, event.getEntity().blockPosition(), MobSpawnType.NATURAL, false, false);
                event.getLevel().addFreshEntity(zombie);
            }
            event.getEntity().setPos(new Vec3(0, -64, 0));
            event.getEntity().kill();
        }
    }
}
