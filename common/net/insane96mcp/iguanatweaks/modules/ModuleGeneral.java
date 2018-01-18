package net.insane96mcp.iguanatweaks.modules;

import net.insane96mcp.iguanatweaks.lib.Properties;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.FOVUpdateEvent;

public class ModuleGeneral {

	public static void PreventFov(FOVUpdateEvent event) {
		if (!Properties.General.disableFovOnSpeedModified)
			return;
		
		EntityPlayer player = event.getEntity();
		
		float f = 1.0F;

        IAttributeInstance iattributeinstance = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        f = (float)((double)f * ((iattributeinstance.getAttributeValue() / (double)player.capabilities.getWalkSpeed() + 1.0D) / 2.0D));

        if (player.capabilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f))
            f = 1.0F;
        
        event.setNewfov(event.getFov() / f);
	}
}
