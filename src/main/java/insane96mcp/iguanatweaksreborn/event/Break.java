package insane96mcp.iguanatweaksreborn.event;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.modules.HungerHealthModule;
import insane96mcp.iguanatweaksreborn.setup.ModSounds;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.Explosion;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID)
public class Break {

    @SubscribeEvent
    public static void eventBreak(BlockEvent.BreakEvent event) {
        HungerHealthModule.breakExaustion(event);
    }

    @SubscribeEvent
    public static void explosionStartEvent(ExplosionEvent.Detonate event) {

        Explosion e = event.getExplosion();
        if (e.exploder instanceof CreeperEntity) {
            CreeperEntity creeper = (CreeperEntity) e.exploder;

            if (creeper.hasCustomName() && creeper.getCustomName().getString().equals("John Cena")){
                creeper.playSound(ModSounds.CREEPER_CENA_EXPLODE.get(), 3.0f, 1.0f);
            }
        }

        if (e.world instanceof ServerWorld && !e.getAffectedBlockPositions().isEmpty()) {
            ServerWorld world = (ServerWorld) e.world;
            int particleCount = (int)(e.size * 200);
            world.spawnParticle(ParticleTypes.POOF, e.getPosition().x, e.getPosition().y, e.getPosition().z, particleCount, e.size / 4f, e.size / 4f, e.size / 4f, 0.33D);
        }
    }

    @SubscribeEvent
    public static void livingDamageEvent(LivingDamageEvent event) {

        if (event.getSource().isExplosion() && event.getEntityLiving() instanceof CreeperEntity){
            CreeperEntity creeper = (CreeperEntity) event.getEntityLiving();
            creeper.ignite();
        }
    }
}
