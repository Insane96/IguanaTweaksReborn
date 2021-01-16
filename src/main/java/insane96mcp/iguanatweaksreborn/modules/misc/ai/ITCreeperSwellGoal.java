package insane96mcp.iguanatweaksreborn.modules.misc.ai;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.CreeperEntity;

import java.util.EnumSet;

public class ITCreeperSwellGoal extends Goal {
    protected final CreeperEntity swellingCreeper;
    protected LivingEntity creeperAttackTarget;

    public ITCreeperSwellGoal(CreeperEntity entitycreeperIn) {
        this.swellingCreeper = entitycreeperIn;
        //this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    /**
     * Called each tick, if false calls resetTask, if true calls tick and if it's first time executing calls startExecuting
     */
    public boolean shouldExecute() {
        LivingEntity livingentity = this.swellingCreeper.getAttackTarget();
        return this.swellingCreeper.getCreeperState() > 0 || livingentity != null && this.swellingCreeper.getDistanceSq(livingentity) < 9.0D;
    }

    public void startExecuting() {
        //this.swellingCreeper.getNavigator().clearPath();
        this.creeperAttackTarget = this.swellingCreeper.getAttackTarget();
    }

    public void resetTask() {
        this.creeperAttackTarget = null;
    }

    public void tick() {
        if (this.creeperAttackTarget == null) {
            this.swellingCreeper.setCreeperState(-1);
        }
        //TODO change distance based off creeper explosion size
        else if (this.swellingCreeper.getDistanceSq(this.creeperAttackTarget) > 49.0D) {
            this.swellingCreeper.setCreeperState(-1);
        }
        /*else if (!this.swellingCreeper.getEntitySenses().canSee(this.creeperAttackTarget)) {
            this.swellingCreeper.setCreeperState(-1);
        }*/
        else {
            this.swellingCreeper.setCreeperState(1);
        }
    }
}