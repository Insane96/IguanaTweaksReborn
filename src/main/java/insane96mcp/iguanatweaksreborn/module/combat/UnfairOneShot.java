package insane96mcp.iguanatweaksreborn.module.combat;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import insane96mcp.iguanatweaksreborn.data.criterion.ITRTriggers;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.network.message.UnfairOneShotActivation;
import insane96mcp.iguanatweaksreborn.setup.ITRRegistries;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.LoadFeature;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Unfair one-shots", description = "Players be left with half a heart when too much damage that would kill them is dealt (only works for damage taken from mobs)")
@LoadFeature(module = Modules.Ids.COMBAT)
public class UnfairOneShot extends Feature {
	public static final RegistryObject<Item> HALF_HEART_TEXTURE = ITRRegistries.ITEMS.register("half_heart_texture", () -> new Item(new Item.Properties()));

	@Config
	@Label(name = "Effects", description = "A list of effects to give when Unfair One Shot triggers, separated by semi-colons")
	public static String effectsConfig = "minecraft:resistance,50,4;minecraft:resistance,100,3;minecraft:resistance,150,1";
	private static final List<MobEffectInstance> effects = new ArrayList<>();
	@Config
	@Label(name = "Animation", description = "If true, an animation is played on activation")
	public static Boolean animation = true;

	public UnfairOneShot(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@Override
	public void readConfig(ModConfigEvent event) {
		super.readConfig(event);

		effects.clear();
		String[] effectsArray = effectsConfig.split(";");
		for (String effect : effectsArray) {
			if (!effect.isEmpty()) {
				String[] effectArray = effect.split(",");
				MobEffect mobEffect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(effectArray[0]));
				if (mobEffect == null)
					continue;
				int duration = Integer.parseInt(effectArray[1]);
				int amplifier = Integer.parseInt(effectArray[2]);
				effects.add(new MobEffectInstance(mobEffect, duration, amplifier));
			}
		}
	}

	@SubscribeEvent
	public void onPlayerAttackEvent(LivingDamageEvent event) {
		if (!this.isEnabled()
				|| !(event.getSource().getEntity() instanceof LivingEntity)
				|| !(event.getEntity() instanceof ServerPlayer player))
			return;

		if (player.getHealth() >= 15 && player.getHealth() - event.getAmount() <= 0) {
			event.setAmount(player.getHealth() - 1f);
			player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 2f, 0.5f);
			ITRTriggers.UNFAIR_ONESHOT.trigger(player);
			for (MobEffectInstance effect : effects) {
				player.addEffect(new MobEffectInstance(effect));
			}

			if (animation)
				UnfairOneShotActivation.send(player);
		}
	}

	public static int activationTicks = 0;
	public static float activationOffX = 0f;
	public static float activationOffY = 0f;
	//private static ItemStack renderItemStack = new ItemStack(Items.PLAYER_HEAD, 1);
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
		event.registerBelowAll("unfair_oneshot_animation", (gui, guiGraphics, partialTicks, screenWidth, screenHeight) -> {
            if (!Feature.isEnabled(UnfairOneShot.class)
					|| activationTicks == 0)
                return;

			int tick = 30 - activationTicks;
			float f = ((float)tick + partialTicks) / 30f;
			float f1 = f * f;
			float f2 = f * f1;
			float f3 = 10.25F * f2 * f1 - 24.95F * f1 * f1 + 25.5F * f2 - 13.8F * f1 + 4.0F * f;
			float f4 = f3 * (float)Math.PI;
			float f5 = 0;/*activationOffX * (float)(screenWidth / 4)*/;
			float f6 = screenHeight /*activationOffY * (float)(screenHeight / 4)*/;
			RenderSystem.enableDepthTest();
			RenderSystem.disableCull();
			PoseStack posestack = new PoseStack();
			posestack.pushPose();
			posestack.translate((float)(screenWidth / 2) + f5 * Mth.abs(Mth.sin(f4 * 2f)), /*(float)(screenHeight / 2) + f6 * Mth.abs(Mth.sin(f4 * 2.0F))*/(f6 * f), -50.0F);
			float f7 = 120.0F * Mth.sin(f4);
			posestack.scale(f7, -f7, f7);
			posestack.mulPose(Axis.YP.rotationDegrees(180.0F * Mth.abs(Mth.sin(f4))));
			//posestack.mulPose(Axis.XP.rotationDegrees(3.0F * Mth.cos(f * 4.0F)));
			//posestack.mulPose(Axis.ZP.rotationDegrees(3.0F * Mth.cos(f * 4.0F)));
			//guiGraphics.blit(new ResourceLocation("textures/gui/icons.png"), 0, 0, 0, 0, screenWidth, screenHeight);
			MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
			Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(HALF_HEART_TEXTURE.get()), ItemDisplayContext.FIXED, 15728880, OverlayTexture.NO_OVERLAY, posestack, multibuffersource$buffersource, Minecraft.getInstance().level, 0);
			posestack.popPose();
			multibuffersource$buffersource.endBatch();
			RenderSystem.enableCull();
			RenderSystem.disableDepthTest();
        });
	}

	@SubscribeEvent
	public void onRenderTick(TickEvent.LevelTickEvent event) {
		if (activationTicks > 0 && event.phase == TickEvent.Phase.END && event.level.isClientSide && event.level.dimension() == Level.OVERWORLD)
			activationTicks--;
	}
}