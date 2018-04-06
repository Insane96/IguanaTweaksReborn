package net.insane96mcp.iguanatweaks.modules;


import net.insane96mcp.iguanatweaks.lib.Properties;
import net.insane96mcp.iguanatweaks.potioneffects.AlteredPoison;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.registries.IForgeRegistryModifiable;

public class ModuleGeneral {

	public static void PreventFov(FOVUpdateEvent event) {
		if (!Properties.General.disableFovOnSpeedModified)
			return;
		
		EntityPlayer player = event.getEntity();
		
		float f = 1.0F;

        IAttributeInstance iattributeinstance = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        f = (float)((double)f * ((iattributeinstance.getAttributeValue() / (double)player.capabilities.getWalkSpeed() + 1.0D) / 2.0D));

        if (player.isSprinting())
        	f /= 1.23;
        	
        if (player.capabilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f))
            f = 1.0F;
        
        
        event.setNewfov(event.getFov() / f);
	}

	public static void IncreasedStepHeight(EntityPlayer player){
		if (Properties.General.increasedStepHeight)
			player.stepHeight = 1f;
	}

	public static void LessObiviousSilverfish(){
		Blocks.MONSTER_EGG.setHardness(1.4f).setResistance(10.0F).setHarvestLevel("pickaxe", 0);
	}
	
	public static void ExhaustionOnBlockBreak(BreakEvent event) {
		if (!Properties.General.exhaustionOnBlockBreak)
			return;
		
		IBlockState blockState = event.getState();
		Block block = blockState.getBlock();
		float hardness = 0f;
		hardness = block.getBlockHardness(blockState, event.getWorld(), event.getPos());
		
		event.getPlayer().addExhaustion((hardness / 100f) * Properties.General.exhaustionMultiplier);
	}
	
	public static void AlterPoison(RegistryEvent.Register<Potion> event) {
		IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) event.getRegistry();
		ResourceLocation potionName = new ResourceLocation("minecraft:poison");
		AlteredPoison alteredPoison = new AlteredPoison(true, Potion.getPotionFromResourceLocation("minecraft:poison").getLiquidColor());
		alteredPoison.setRegistryName(potionName);
		alteredPoison.setPotionName("effect.poison");
		alteredPoison.setIconIndex(6, 0);
		modRegistry.register(alteredPoison);
	}
}
