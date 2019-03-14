package net.insane96mcp.iguanatweaks.modules;

import java.util.List;
import java.util.Map;

import net.insane96mcp.iguanatweaks.capabilities.IPlayerData;
import net.insane96mcp.iguanatweaks.capabilities.PlayerDataProvider;
import net.insane96mcp.iguanatweaks.lib.Properties;
import net.insane96mcp.iguanatweaks.lib.Reflection;
import net.insane96mcp.iguanatweaks.network.HideArmorTimestamp;
import net.insane96mcp.iguanatweaks.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModuleHud {
	@SideOnly(Side.CLIENT)
	public static boolean HideHealthBar(ElementType type, EntityPlayer player) {
		if (!Properties.config.global.hud)
			return false;
		
		if (type != ElementType.HEALTH)
			return false;
		
		if (!Properties.config.hud.hideHealthBar)
			return false;
		
		if (player.isPotionActive(MobEffects.WITHER) || player.isPotionActive(ModuleMisc.alteredPoison))
			return false;
		
		IPlayerData playerData = player.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);
		int totalTime = (int) player.world.getTotalWorldTime();
		
		if (Math.ceil(player.getHealth()) >= Properties.config.hud.hideHealthBarThreshold && player.getAbsorptionAmount() == 0f)
		{
			int delay = totalTime - playerData.getHideHealthBarLastTimestamp();
			if (delay >= Properties.config.hud.hideHealthBarDelay * 20)
				return true;
			else if (delay < 0)
				playerData.setHideHealthBarLastTimestamp(totalTime);
		}
		else
			playerData.setHideHealthBarLastTimestamp(totalTime);
		return false;
	}

	@SideOnly(Side.CLIENT)
	public static boolean HideHungerBar(ElementType type, EntityPlayer player) {
		if (!Properties.config.global.hud)
			return false;
		
		if (type != ElementType.FOOD)
			return false;

		if (!Properties.config.hud.hideHungerBar)
			return false;

		IPlayerData playerData = player.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);
		int totalTime = (int) player.world.getTotalWorldTime();
		
		if (player.getFoodStats().getFoodLevel() >= Properties.config.hud.hideHungerBarThreshold && !player.isPotionActive(Potion.getPotionFromResourceLocation("minecraft:hunger")))
		{
			int delay = totalTime - playerData.getHideHungerBarLastTimestamp();
			if (delay >= Properties.config.hud.hideHungerBarDelay * 20)
				return true;
			else if (delay < 0)
				playerData.setHideHungerBarLastTimestamp(totalTime);
		}
		else 
			playerData.setHideHungerBarLastTimestamp(totalTime);
		
		return false;
	}

	@SideOnly(Side.CLIENT)
	public static boolean HideExperienceBar(ElementType type, EntityPlayerSP player) {
		if (!Properties.config.global.hud)
			return false;
		
		if (type != ElementType.EXPERIENCE)
			return false;
		
		if (!Properties.config.hud.hideExperienceBar)
			return false;

		BlockPos pos1 = player.getPosition().add(-6, -6, -6);
		BlockPos pos2 = player.getPosition().add(6, 6, 6);
		AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pos1, pos2);
		List<EntityXPOrb> orbsInRange = player.world.getEntitiesWithinAABB(EntityXPOrb.class, axisAlignedBB);
		
		IPlayerData playerData = player.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);
		int totalTime = (int) player.world.getTotalWorldTime();
			
		if (orbsInRange.size() == 0 && Minecraft.getMinecraft().currentScreen == null) {
			
			int delay = totalTime - playerData.getHideExperienceLastTimestamp();
			if (delay >= Properties.config.hud.hideExperienceDelay * 20)
				return true;
			if (delay < 0)
				playerData.setHideExperienceLastTimestamp(totalTime);
		}
		else
			playerData.setHideExperienceLastTimestamp(totalTime);
		
		return false;
	}

	@SideOnly(Side.CLIENT)
	public static boolean HideArmorBar(ElementType type, EntityPlayerSP player) {
		if (!Properties.config.global.hud)
			return false;
		
		if (type != ElementType.ARMOR)
			return false;
		
		if (!Properties.config.hud.hideArmorBar)
			return false;
		
		IPlayerData playerData = player.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);
		int totalTime = (int) player.world.getTotalWorldTime();
		int delay = totalTime - playerData.getHideArmorLastTimestamp();

		if (delay >= Properties.config.hud.hideArmorDelay * 20)
			return true;
		if (delay < 0)
			playerData.setHideArmorLastTimestamp(totalTime);
		
		return false;
	}
	
	public static void DamagedPlayer(EntityLivingBase entity) {
		if (!(entity instanceof EntityPlayerMP))
			return;
		
		EntityPlayerMP player = (EntityPlayerMP) entity;
		
		IPlayerData playerData = player.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);
		int totalTime = (int) player.world.getTotalWorldTime();

		PacketHandler.SendToClient(new HideArmorTimestamp(totalTime), player);
		
	}

	@SideOnly(Side.CLIENT)
	public static boolean HideHotbar(ElementType type, EntityPlayer player) {
		if (!Properties.config.global.hud)
			return false;
		
		if (type != ElementType.HOTBAR)
			return false;
		
		if (!Properties.config.hud.hideHotbar)
			return false;
		
		IPlayerData playerData = player.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);
		int totalTime = (int) player.world.getTotalWorldTime();
		
		int delay = totalTime - playerData.getHideHotbarLastTimestamp();
		if (delay >= Properties.config.hud.hideHotbarDelay * 20)
			return true;
		else if (delay < 0)
			playerData.setHideHotbarLastTimestamp(totalTime);
		
		return false;
	}

	@SideOnly(Side.CLIENT)
	public static void HotbarCheckKeyPress(Phase phase) {
		if (!Properties.config.global.hud)
			return;
		if (!Properties.config.hud.hideHotbar)
			return;
		
		if (phase.equals(Phase.END)) {
			Map<String, KeyBinding> binds = null;
			try {
				binds = (Map<String, KeyBinding>) Reflection.Client.KeyBinding_KEYBIND_ARRAY.get(null);
				for (String bind : binds.keySet()) {
					if(binds.get(bind).isKeyDown()){
						if (binds.get(bind).getKeyCode() >= 2 && binds.get(bind).getKeyCode() <= 9) {
							Minecraft mc = Minecraft.getMinecraft();
							if (mc.currentScreen == null)
							{
								EntityPlayerSP player = mc.player;
								IPlayerData playerData = player.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);
								playerData.setHideHotbarLastTimestamp((int) player.world.getTotalWorldTime());
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void HotbarCheckMouse(int dwheel) {
		if (!Properties.config.global.hud)
			return;
		
		if (dwheel != 0) {
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			IPlayerData playerData = player.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);
			playerData.setHideHotbarLastTimestamp((int) player.world.getTotalWorldTime());
		}
	}

	@SideOnly(Side.CLIENT)
	public static void PrintCreativeText(RenderGameOverlayEvent.Text event) {
		if (!Properties.config.hud.showCreativeText || !Properties.config.global.hud)
			return;
		
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.player;
		
		if (Properties.config.hud.showCreativeText && !mc.gameSettings.showDebugInfo && player.capabilities.isCreativeMode)
		{
			event.getLeft().add(I18n.format("gameMode.creative"));
			return;
		}
	}
}
