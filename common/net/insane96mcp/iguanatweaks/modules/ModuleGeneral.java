package net.insane96mcp.iguanatweaks.modules;

import net.insane96mcp.iguanatweaks.lib.Properties;
import net.insane96mcp.iguanatweaks.potioneffects.AlteredPoison;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistryModifiable;

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

	public static void IncreasedStepHeight(EntityPlayer player){
		if (Properties.General.increasedStepHeight)
			player.stepHeight = 1f;
	}

	public static void LessObiviousSilverfish(){
		Blocks.MONSTER_EGG.setHardness(1.5f).setResistance(10.0F).setHarvestLevel("pickaxe", 0);
	}

	public static void TorchesPerCoal(RegistryEvent.Register<IRecipe> event){
		if (Properties.General.torchesPerCoal == 4)
			return;
		
		/*ResourceLocation torch = new ResourceLocation("minecraft:torch");
        IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) event.getRegistry();
		modRegistry.remove(torch);*/
		//modRegistry.register(value);

		//Re-add the recipe
	}
	
	public static void AlterPoison(RegistryEvent.Register<Potion> event) {
		/*System.out.println("Replacing Poison");
		IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) event.getRegistry();
		ResourceLocation potionName = new ResourceLocation("minecraft:poison");
		modRegistry.remove(potionName);
		AlteredPoison alteredPoison = new AlteredPoison(true, Potion.getPotionFromResourceLocation("minecraft:poison").getLiquidColor());
		alteredPoison.setRegistryName(potionName);
		modRegistry.register(alteredPoison);*/
	}
}
