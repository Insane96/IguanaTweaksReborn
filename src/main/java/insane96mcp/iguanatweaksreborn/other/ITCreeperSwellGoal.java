package insane96mcp.iguanatweaksreborn.other;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.util.text.StringTextComponent;

import java.util.EnumSet;

public class ITCreeperSwellGoal extends Goal {
    protected final CreeperEntity swellingCreeper;
    protected LivingEntity creeperAttackTarget;

    public ITCreeperSwellGoal(CreeperEntity entitycreeperIn) {
        this.swellingCreeper = entitycreeperIn;
        //this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean shouldExecute() {
        LivingEntity livingentity = this.swellingCreeper.getAttackTarget();
        //if (livingentity != null)
            //livingentity.sendMessage(new StringTextComponent("should execute " + this.swellingCreeper.getDistanceSq(livingentity)), swellingCreeper.getUniqueID());
        return this.swellingCreeper.getCreeperState() > 0 || livingentity != null && this.swellingCreeper.getDistanceSq(livingentity) < 9.0D;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        //this.swellingCreeper.getNavigator().clearPath();
        this.creeperAttackTarget = this.swellingCreeper.getAttackTarget();
        //if (this.creeperAttackTarget != null)
            //this.creeperAttackTarget.sendMessage(new StringTextComponent("startExecuting " + this.swellingCreeper.getDistanceSq(creeperAttackTarget)), swellingCreeper.getUniqueID());
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask() {
        //if (this.creeperAttackTarget != null)
            //this.creeperAttackTarget.sendMessage(new StringTextComponent("resetTask " + this.swellingCreeper.getDistanceSq(creeperAttackTarget)), swellingCreeper.getUniqueID());
        this.creeperAttackTarget = null;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        //if (this.creeperAttackTarget != null)
            //this.creeperAttackTarget.sendMessage(new StringTextComponent("tick " + this.swellingCreeper.getDistanceSq(creeperAttackTarget)), swellingCreeper.getUniqueID());
        if (this.creeperAttackTarget == null) {
            this.swellingCreeper.setCreeperState(-1);
        } else if (this.swellingCreeper.getDistanceSq(this.creeperAttackTarget) > 49.0D) {
            this.swellingCreeper.setCreeperState(-1);
        } /*else if (!this.swellingCreeper.getEntitySenses().canSee(this.creeperAttackTarget)) {
            this.swellingCreeper.setCreeperState(-1);
        }*/ else {
            this.swellingCreeper.setCreeperState(1);
        }
    }
}