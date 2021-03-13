package insane96mcp.iguanatweaksreborn.modules.movement.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "No Pillaring", description = "Prevents the player from placing blocks below him when in mid air.")
public class NoPillaringFeature extends Feature {

	public NoPillaringFeature(Module module) {
		super(Config.builder, module);
		//Config.builder.comment(this.getDescription()).push(this.getName());
		//Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
	}

	@SubscribeEvent
    public void playerTick(PlayerInteractEvent.RightClickBlock event) {
        if (!this.isEnabled())
            return;
        PlayerEntity playerEntity = event.getPlayer();
        if (playerEntity.isCreative() || playerEntity.isInWater() || playerEntity.isOnLadder())
            return;
        if (playerEntity.getPitch(1.0f) > 40f && !playerEntity.isOnGround() && event.getItemStack().getItem() instanceof BlockItem) {
            event.setCanceled(true);
            event.setResult(Event.Result.DENY);
        }
    }
}
