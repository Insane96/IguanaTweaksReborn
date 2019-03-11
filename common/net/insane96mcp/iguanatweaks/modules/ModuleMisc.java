package net.insane96mcp.iguanatweaks.modules;

import java.util.Collection;

import com.google.common.collect.Multimap;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.init.ModConfig;
import net.insane96mcp.iguanatweaks.potioneffects.AlteredPoison;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class ModuleMisc {

	public static void preventFov(FOVUpdateEvent event) {
		if (!ModConfig.Misc.preventFoVChangesOnSpeedModified.get())
			return;
		
		EntityPlayer player = event.getEntity();
		
		float f = 1.0F;

        IAttributeInstance iattributeinstance = player.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        f *= ((iattributeinstance.getValue() / (double)player.abilities.getWalkSpeed() + 1.0D) / 2.0D);

        if (player.isSprinting())
        	f /= 1.23;

        if (player.abilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f))
            f = 1.0F;
       
        event.setNewfov(event.getNewfov() / f);
	}
	
	public static void exhaustionOnBlockBreak(BreakEvent event) {
		if (!ModConfig.Misc.exhaustionOnBlockBreak.get())
			return;
		
		IBlockState blockState = event.getState();
		float hardness = 0f;
		hardness = blockState.getBlockHardness(event.getWorld(), event.getPos());
		
		float exhaustion = (float) ((hardness / 100f) * ModConfig.Misc.exhaustionMultiplier.get());
		exhaustion -= 0.005;
		event.getPlayer().addExhaustion(exhaustion);
	}
	
	public static AlteredPoison alteredPoison;
	
	public static void registerPoison(RegistryEvent.Register<Potion> event) {
		Potion vanillaPoison = ForgeRegistries.POTIONS.getValue(new ResourceLocation("minecraft:poison"));
		
		ResourceLocation potionName = new ResourceLocation(IguanaTweaks.MOD_ID, "altered_poison");
		alteredPoison = new AlteredPoison(true, vanillaPoison.getLiquidColor());
		alteredPoison.setRegistryName(potionName);
		alteredPoison.setIconIndex(6, 0);
		event.getRegistry().register(alteredPoison);
	}
	
	public static void applyPoison(EntityLivingBase living) {
		if (!ModConfig.Misc.alterPoison.get())
			return;
		
		if (living.ticksExisted % 9 != 0)
			return;
		
		Potion potionPoison = ForgeRegistries.POTIONS.getValue(new ResourceLocation ("minecraft:poison"));
		
		if (!living.isPotionActive(potionPoison))
			return;
		
		PotionEffect poison = living.getActivePotionEffect(potionPoison);
		PotionEffect alteredPoison = new PotionEffect(ModuleMisc.alteredPoison, poison.getDuration() * (living.world.getDifficulty().getId()), poison.getAmplifier(), poison.isAmbient(), poison.doesShowParticles());
		living.removeActivePotionEffect(potionPoison);
		living.addPotionEffect(alteredPoison);
	}
	
	static long healthUpdateCounter = 0;
	public static int updateCounter = 0;
	static int playerHealth = 0;
	static int lastPlayerHealth = 0;
	static long lastSystemTime;
	
	@OnlyIn(Dist.CLIENT)
	//TODO check if something changed since 1.12
	public static void renderPoisonedHearts(int width, int height) {
		GlStateManager.enableBlend();
		
		GuiIngame gui = Minecraft.getInstance().ingameGUI;

        EntityPlayer player = Minecraft.getInstance().player;
        int health = MathHelper.ceil(player.getHealth());
		boolean highlight = healthUpdateCounter > (long)updateCounter && (healthUpdateCounter - (long)updateCounter) / 3L %2L == 1L;

        if (health < playerHealth && player.hurtResistantTime > 0)
        {
            lastSystemTime = Util.milliTime();
            healthUpdateCounter = (long)(updateCounter + 20);
        }	
        else if (health > playerHealth && player.hurtResistantTime > 0)
        {
            lastSystemTime = Util.milliTime();
            healthUpdateCounter = (long)(updateCounter + 10);
        }

        if (Util.milliTime() - lastSystemTime > 1000L)
        {
            playerHealth = health;
            lastPlayerHealth = health;
            lastSystemTime = Util.milliTime();
        }

        playerHealth = health;
        int healthLast = lastPlayerHealth;

        IAttributeInstance attrMaxHealth = player.getAttribute(SharedMonsterAttributes.MAX_HEALTH);
        float healthMax = (float)attrMaxHealth.getValue();
        float absorb = MathHelper.ceil(player.getAbsorptionAmount());

        int healthRows = MathHelper.ceil((healthMax + absorb) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);

        player.world.rand.setSeed((long)(updateCounter * 312871));

        int left = width / 2 - 91;
        int top = height - GuiIngameForge.left_height;
        GuiIngameForge.left_height += (healthRows * rowHeight);
        if (rowHeight != 10) GuiIngameForge.left_height += 10 - rowHeight;

        int regen = -1;
        if (player.isPotionActive(MobEffects.REGENERATION))
        {
            regen = updateCounter % 25;
        }
        final int TOP =  9 * (player.world.getWorldInfo().isHardcore() ? 5 : 0);
        final int BACKGROUND = (highlight ? 25 : 16);
        int MARGIN = 16;
        if (player.isPotionActive(ModuleMisc.alteredPoison))
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

	public static void noItemNoKnockback(LivingAttackEvent event) {
		if (!ModConfig.Misc.noItemNoKnockback.get())
			return;
		
		if (event.getSource().getImmediateSource() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP)event.getSource().getTrueSource();

			ItemStack mainHand = player.getHeldItemMainhand();
			Multimap<String, AttributeModifier> map = mainHand.getItem().getAttributeModifiers(EntityEquipmentSlot.MAINHAND, mainHand);
			Collection<AttributeModifier> modifiers = map.get("generic.attackDamage");
			float fullDamage = 1f;
			for (AttributeModifier attributeModifier : modifiers) {
				if (attributeModifier.getOperation() == 0)
					fullDamage += attributeModifier.getAmount();
				else if (attributeModifier.getOperation() == 1)
					fullDamage *= attributeModifier.getAmount() + 1f;
			}
			
			if (fullDamage <= 1.0f) {
				event.setCanceled(true);
				event.getEntityLiving().attackEntityFrom(DamageSource.GENERIC, 0.2f);
				ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, event.getEntityLiving(), player, "attackingPlayer");
				ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, event.getEntityLiving(), 100, "recentlyHit");
				event.getEntityLiving().setRevengeTarget(player);
				event.getEntityLiving().world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, SoundCategory.PLAYERS, 1.0f, 2.0f);
				mainHand.damageItem(1, player);
			}
			else if (event.getAmount() <= fullDamage * .75f) {
				event.setCanceled(true);
				event.getEntityLiving().attackEntityFrom(DamageSource.GENERIC, event.getAmount());
				ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, event.getEntityLiving(), player, "attackingPlayer");
				ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, event.getEntityLiving(), 100, "recentlyHit");
				event.getEntityLiving().setRevengeTarget(player);
				event.getEntityLiving().world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, SoundCategory.PLAYERS, 1.0f, 2.0f);
				mainHand.damageItem(1, player);
			}
		}
	}
}
