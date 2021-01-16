package insane96mcp.iguanatweaksreborn.modules.misc.ai;

import insane96mcp.iguanatweaksreborn.setup.ModSounds;
import net.minecraft.entity.monster.CreeperEntity;

public class ITCenaSwellGoal extends ITCreeperSwellGoal {

    public ITCenaSwellGoal(CreeperEntity entitycreeperIn) {
        super(entitycreeperIn);
    }

    /**
     * Called each tick, if false calls resetTask, if true calls tick and if it's first time executing calls startExecuting
     */
    public void startExecuting() {
        super.startExecuting();
        this.swellingCreeper.playSound(ModSounds.CREEPER_CENA_FUSE.get(), 3.0f, 1.0f);
    }
}