package insane96mcp.iguanatweaksreborn.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import insane96mcp.iguanatweaksreborn.module.Modules;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//Shamelessly stolen to Charm

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin extends ItemCombinerScreen<AnvilMenu> {

	@Final
	@Shadow
	private Player player;

	public AnvilScreenMixin(AnvilMenu p_98901_, Inventory p_98902_, Component p_98903_, ResourceLocation p_98904_) {
		super(p_98901_, p_98902_, p_98903_, p_98904_);
	}

	//Set too expensive client side
	@ModifyConstant(
			method = "renderLabels",
			constant = @Constant(intValue = 40)
	)
	private int tooExpensiveCap(int cap) {
		return Modules.experience.otherExperience.anvilRepairCap;
	}

	//Show no cost text
	@Inject(
			method = "renderLabels",
			at = @At(
					value = "TAIL"
			)
	)
	public void renderLabels(PoseStack poseStack, int p_97891_, int p_97892_, CallbackInfo ci) {
		if (this.menu.getCost() == 0 && this.menu.getSlot(2).hasItem()) {
			int j = 8453920;
			Component component = new TranslatableComponent("container.repair.free");
			if (!this.menu.getSlot(2).mayPickup(this.player)) {
				j = 16736352;
			}

			int k = this.imageWidth - 8 - this.font.width(component) - 2;
			int l = 69;
			fill(poseStack, k - 2, 67, this.imageWidth - 8, 79, 1325400064);
			this.font.drawShadow(poseStack, component, (float)k, 69.0F, j);
		}
	}
}
