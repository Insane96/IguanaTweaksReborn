package net.insane96mcp.iguanatweaks.network;

import net.insane96mcp.iguanatweaks.capabilities.IPlayerData;
import net.insane96mcp.iguanatweaks.capabilities.PlayerDataProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import scala.inline;

public class StunMessageHandler implements IMessageHandler<StunMessage, IMessage> {

	@Override
	public IMessage onMessage(StunMessage message, MessageContext ctx) {
		EntityPlayerSP clientPlayer = Minecraft.getMinecraft().player;
		int duration = message.duration;
		IPlayerData playerData = clientPlayer.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);
    	
    	playerData.setDamageSlownessDuration(duration);
		return null;
	}

}
