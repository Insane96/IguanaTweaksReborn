package insane96mcp.iguanatweaksreborn.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

/*
	Shamelessly copypasted from AppleSkin's code
 */
public class NetworkHelper {
	public static PlayerEntity getSidedPlayer(NetworkEvent.Context ctx) {
		return ctx.getDirection() == NetworkDirection.PLAY_TO_SERVER ? ctx.getSender() : Minecraft.getInstance().player;
	}
}