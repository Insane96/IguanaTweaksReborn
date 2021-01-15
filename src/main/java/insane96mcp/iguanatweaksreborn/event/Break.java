package insane96mcp.iguanatweaksreborn.event;

import insane96mcp.iguanatweaksreborn.other.ITExplosion;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.modules.ExperienceModule;
import insane96mcp.iguanatweaksreborn.modules.HungerHealthModule;
import insane96mcp.iguanatweaksreborn.setup.ModSounds;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.world.Explosion;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID)
public class Break {

    @SubscribeEvent
    public static void eventBreak(BlockEvent.BreakEvent event) {
        ExperienceModule.oreXpDrop(event);
        HungerHealthModule.breakExaustion(event);
    }

    @SubscribeEvent
    public static void explosion(ExplosionEvent.Start event) {
        if (event.getWorld().isRemote)
            return;

        event.setCanceled(true);

        ServerWorld world = (ServerWorld) event.getWorld();

        Explosion e = event.getExplosion();
        float size = e.size;
        boolean causesFire = e.causesFire;
        if (e.exploder instanceof CreeperEntity) {
            CreeperEntity creeper = (CreeperEntity) e.exploder;

            if (creeper.hasCustomName() && creeper.getCustomName().getString().equals("John Cena")){
                size *= 2;
                causesFire = true;
            }
        }
        ITExplosion explosion = new ITExplosion(e.world, e.exploder, e.getDamageSource(), e.context, e.getPosition().x, e.getPosition().y, e.getPosition().z, size, causesFire, e.mode);

        explosion.doExplosionA();
        explosion.doExplosionB(false);
        if (explosion.mode == Explosion.Mode.NONE) {
            explosion.clearAffectedBlockPositions();
        }

        for(ServerPlayerEntity serverplayerentity : world.getPlayers()) {
            if (serverplayerentity.getDistanceSq(explosion.getPosition().x, explosion.getPosition().y, explosion.getPosition().z) < 4096.0D) {
                serverplayerentity.connection.sendPacket(new SExplosionPacket(explosion.getPosition().x, explosion.getPosition().y, event.getExplosion().getPosition().z, explosion.size, explosion.getAffectedBlockPositions(), explosion.getPlayerKnockbackMap().get(serverplayerentity)));
            }
        }
    }

    @SubscribeEvent
    public static void explosionStartEvent(ExplosionEvent.Detonate event) {

        Explosion e = event.getExplosion();
        if (e.exploder instanceof CreeperEntity) {
            CreeperEntity creeper = (CreeperEntity) e.exploder;

            if (creeper.hasCustomName() && creeper.getCustomName().getString().equals("John Cena")){
                creeper.playSound(ModSounds.CREEPER_CENA_EXPLODE.get(), 1.0f, 1.0f);
            }
        }
    }
}
