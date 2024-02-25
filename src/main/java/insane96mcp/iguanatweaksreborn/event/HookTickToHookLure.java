package insane96mcp.iguanatweaksreborn.event;

import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraftforge.eventbus.api.Event;

public class HookTickToHookLure extends Event {
    private final FishingHook hook;
    private int tick;
    public HookTickToHookLure(FishingHook hook, int tick)
    {
        this.hook = hook;
        this.tick = tick;
    }

    public FishingHook getHookEntity() {
        return this.hook;
    }

    public int getTick() {
        return this.tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }
}
