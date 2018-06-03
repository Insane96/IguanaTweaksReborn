package net.insane96mcp.iguanatweaks.modules;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.lib.Properties;
import net.insane96mcp.iguanatweaks.potioneffects.AlteredPoison;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
	
	public static AlteredPoison alteredPoison;
	
	public static void RegisterPoison(RegistryEvent.Register<Potion> event) {
		IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) event.getRegistry();
		ResourceLocation potionName = new ResourceLocation(IguanaTweaks.MOD_ID, "altered_poison");
		alteredPoison = new AlteredPoison(true, Potion.getPotionFromResourceLocation("minecraft:poison").getLiquidColor());
		alteredPoison.setRegistryName(potionName);
		alteredPoison.setPotionName("effect.poison");
		alteredPoison.setIconIndex(6, 0);
		modRegistry.register(alteredPoison);
	}
	
	public static void ApplyPoison(EntityLivingBase living) {
		if (!Properties.General.alterPoison)
			return;
		
		if (living.ticksExisted % 9 != 0)
			return;
		
		Potion potionPoison = Potion.getPotionFromResourceLocation("minecraft:poison");
		
		if (!living.isPotionActive(potionPoison))
			return;
		
		PotionEffect poison = living.getActivePotionEffect(potionPoison);
		PotionEffect alteredPoison = new PotionEffect(ModuleGeneral.alteredPoison, poison.getDuration() * 2, poison.getAmplifier(), poison.getIsAmbient(), poison.doesShowParticles());
		living.removeActivePotionEffect(potionPoison);
		living.addPotionEffect(alteredPoison);
	}
	
	static long healthUpdateCounter = 0;
	public static int updateCounter = 0;
	static int playerHealth = 0;
	static int lastPlayerHealth = 0;
	static long lastSystemTime;
	
	@SideOnly(Side.CLIENT)
	public static void RenderPoisonedHearts(ScaledResolution scaledResolution) {
		GlStateManager.enableBlend();
		
		GuiIngame gui = Minecraft.getMinecraft().ingameGUI;

		int width = scaledResolution.getScaledWidth();
		int height = scaledResolution.getScaledHeight();
		
	    int left_height = 39;
	    int right_height = 39;

        EntityPlayer player = Minecraft.getMinecraft().player;
        int health = MathHelper.ceil(player.getHealth());
		boolean highlight = healthUpdateCounter > (long)updateCounter && (healthUpdateCounter - (long)updateCounter) / 3L %2L == 1L;

        if (health < playerHealth && player.hurtResistantTime > 0)
        {
            lastSystemTime = Minecraft.getSystemTime();
            healthUpdateCounter = (long)(updateCounter + 20);
        }	
        else if (health > playerHealth && player.hurtResistantTime > 0)
        {
            lastSystemTime = Minecraft.getSystemTime();
            healthUpdateCounter = (long)(updateCounter + 10);
        }

        if (Minecraft.getSystemTime() - lastSystemTime > 1000L)
        {
            playerHealth = health;
            lastPlayerHealth = health;
            lastSystemTime = Minecraft.getSystemTime();
        }

        playerHealth = health;
        int healthLast = lastPlayerHealth;

        IAttributeInstance attrMaxHealth = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        float healthMax = (float)attrMaxHealth.getAttributeValue();
        float absorb = MathHelper.ceil(player.getAbsorptionAmount());

        int healthRows = MathHelper.ceil((healthMax + absorb) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);

        player.world.rand.setSeed((long)(updateCounter * 312871));

        int left = width / 2 - 91;
        int top = height - left_height;
        left_height += (healthRows * rowHeight);
        if (rowHeight != 10) left_height += 10 - rowHeight;

        int regen = -1;
        if (player.isPotionActive(MobEffects.REGENERATION))
        {
            regen = updateCounter % 25;
        }

        final int TOP =  9 * (player.world.getWorldInfo().isHardcoreModeEnabled() ? 5 : 0);
        final int BACKGROUND = (highlight ? 25 : 16);
        int MARGIN = 16;
        if (player.isPotionActive(ModuleGeneral.alteredPoison))
        	MARGIN += 36;
        float absorbRemaining = absorb;

        for (int i = MathHelper.ceil((healthMax + absorb) / 2.0F) - 1; i >= 0; --i)
        {
            int row = MathHelper.ceil((float)(i + 1) / 10.0F) - 1;
            int x = left + i % 10 * 8;
            int y = top - row * rowHeight;

            if (health <= 4) y += player.world.rand.nextInt(2);
            if (i == regen) y -= 2;

            gui.drawTexturedModalRect(x, y, BACKGROUND, TOP, 9, 9);

            if (highlight)
            {
                if (i * 2 + 1 < healthLast)
                	gui.drawTexturedModalRect(x, y, MARGIN + 54, TOP, 9, 9); //6
                else if (i * 2 + 1 == healthLast)
                	gui.drawTexturedModalRect(x, y, MARGIN + 63, TOP, 9, 9); //7
            }

            if (absorbRemaining > 0.0F)
            {
                if (absorbRemaining == absorb && absorb % 2.0F == 1.0F)
                {
                	gui.drawTexturedModalRect(x, y, MARGIN + 153, TOP, 9, 9); //17
                    absorbRemaining -= 1.0F;
                }
                else
                {
                	gui.drawTexturedModalRect(x, y, MARGIN + 144, TOP, 9, 9); //16
                    absorbRemaining -= 2.0F;
                }
            }
            else
            {
                if (i * 2 + 1 < health)
                	gui.drawTexturedModalRect(x, y, MARGIN + 36, TOP, 9, 9); //4
                else if (i * 2 + 1 == health)
                	gui.drawTexturedModalRect(x, y, MARGIN + 45, TOP, 9, 9); //5
            }
        }

        GlStateManager.disableBlend();
	}
}
