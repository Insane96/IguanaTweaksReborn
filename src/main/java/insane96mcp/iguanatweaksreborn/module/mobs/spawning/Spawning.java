package insane96mcp.iguanatweaksreborn.module.mobs.spawning;

import com.google.common.collect.ImmutableSet;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.setup.ITRRegistries;
import insane96mcp.iguanatweaksreborn.setup.registry.SimpleBlockWithItem;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

@Label(name = "Spawning", description = "Add a brand new Echo Torch and some changes to mob spawn")
@LoadFeature(module = Modules.Ids.MOBS)
public class Spawning extends Feature {

    public static final SimpleBlockWithItem ECHO_TORCH = SimpleBlockWithItem.register("echo_torch", () -> new EchoTorchBlock(BlockBehaviour.Properties.copy(Blocks.SOUL_TORCH).lightLevel(state -> 7), ParticleTypes.SCULK_CHARGE_POP));
    public static final RegistryObject<PoiType> ECHO_TORCH_POI = ITRRegistries.POI_TYPES.register("echo_torch", () -> new PoiType(ImmutableSet.copyOf(ECHO_TORCH.block().get().getStateDefinition().getPossibleStates()), 1, 64));

    @Config
    @Label(name = "No Zombie Villagers", description = "Disables Zombie Villagers")
    public static Boolean noZombieVillagers = true;
    @Config
    @Label(name = "Allow world spawn spawn", description = "Allows mobs to spawn in the world spawn (in vanilla mobs can't spawn in a 24 blocks radius from world spawn)")
    public static Boolean allowWorldSpawnSpawn = true;
    @Config
    @Label(name = "Phantoms in the End", description = "Disables insomnia and makes Phantoms spawn naturally in the End")
    public static Boolean phantomsInTheEnd = true;

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

    public static final List<MobSpawnType> BLOCKED_SPAWN_TYPES = List.of(MobSpawnType.JOCKEY, MobSpawnType.NATURAL);

    @SubscribeEvent
    public void onMobSpawn(MobSpawnEvent.SpawnPlacementCheck event) {
        if (!this.isEnabled()
                || !(event.getLevel() instanceof ServerLevel serverLevel)
                || !BLOCKED_SPAWN_TYPES.contains(event.getSpawnType()))
            return;

        boolean theresTorch = serverLevel.getPoiManager().findAll(poiTypeHolder -> poiTypeHolder.is(ECHO_TORCH_POI.getKey()), blockPos -> true, event.getPos(), 64, PoiManager.Occupancy.ANY)
                .findAny().isPresent();
        if (theresTorch)
            event.setResult(Event.Result.DENY);
    }
}
