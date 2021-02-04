package insane96mcp.iguanatweaksreborn.event;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID)
public class ThingsEvents {

    /*@SubscribeEvent
    public static void explosionStartEvent(ExplosionEvent.Detonate event) {

        Explosion e = event.getExplosion();
        if (e.exploder instanceof CreeperEntity) {
            CreeperEntity creeper = (CreeperEntity) e.exploder;

            if (creeper.hasCustomName() && creeper.getCustomName().getString().equals("John Cena")){
                creeper.playSound(ModSounds.CREEPER_CENA_EXPLODE.get(), 3.0f, 1.0f);
            }
        }
    }

    @SubscribeEvent
    public static void livingDamageEvent(LivingDamageEvent event) {

        if (event.getSource().isExplosion() && event.getEntityLiving() instanceof CreeperEntity){
            CreeperEntity creeper = (CreeperEntity) event.getEntityLiving();
            CompoundNBT compoundNBT = new CompoundNBT();
            int fuse = RandomHelper.getInt(creeper.world.getRandom(), 10, 20);
            compoundNBT.putShort("Fuse", (short)fuse);
            creeper.readAdditional(compoundNBT);
            creeper.ignite();
        }
    }

    @SubscribeEvent
    public static void onExplosionStart(ExplosionEvent.Start event) {
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
    }
    @SubscribeEvent
    public static void eventEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof CreeperEntity){
            CreeperEntity creeper = (CreeperEntity) event.getEntity();

            ArrayList<Goal> goalsToRemove = new ArrayList<>();
            creeper.goalSelector.goals.forEach(prioritizedGoal -> {
                if (prioritizedGoal.getGoal() instanceof CreeperSwellGoal)
                    goalsToRemove.add(prioritizedGoal.getGoal());
            });

            goalsToRemove.forEach(creeper.goalSelector::removeGoal);

            //creeper.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(creeper, LivingEntity.class, true));

            if (creeper.hasCustomName() && creeper.getCustomName().getString().equals("John Cena")) {
                CompoundNBT compoundNBT = new CompoundNBT();
                compoundNBT.putShort("Fuse", (short)35);
                creeper.readAdditional(compoundNBT);
                creeper.goalSelector.addGoal(2, new ITCenaSwellGoal(creeper));
            }
            else {
                creeper.goalSelector.addGoal(2, new ITCreeperSwellGoal(creeper));
            }
        }
    }*/
}
