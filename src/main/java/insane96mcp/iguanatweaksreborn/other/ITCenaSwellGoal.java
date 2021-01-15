package insane96mcp.iguanatweaksreborn.other;

import insane96mcp.iguanatweaksreborn.setup.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.CreeperEntity;

public class ITCenaSwellGoal extends ITCreeperSwellGoal {

    public ITCenaSwellGoal(CreeperEntity entitycreeperIn) {
        super(entitycreeperIn);
    }

    public void startExecuting() {
        this.creeperAttackTarget = this.swellingCreeper.getAttackTarget();
        this.swellingCreeper.playSound(ModSounds.CREEPER_CENA_FUSE.get(), 1.0f, 1.0f);
    }
}