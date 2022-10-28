package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.experience.feature.OtherExperience;
import insane96mcp.insanelib.base.Feature;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
//Shamelessly stolen from Charm

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin extends ItemCombinerMenu {

	@Shadow
	@Final
	private static int COST_RENAME;

	@Shadow
	@Final
	private DataSlot cost;

	@Shadow
	public int repairItemCountCost;

	@Shadow
	private String itemName;

	public AnvilMenuMixin(@Nullable MenuType<?> p_39773_, int p_39774_, Inventory p_39775_, ContainerLevelAccess p_39776_) {
		super(p_39773_, p_39774_, p_39775_, p_39776_);
	}

	@Inject(
			method = "mayPickup",
			at = @At("HEAD"),
			cancellable = true
	)
	private void mayPickUp(Player player, boolean unused, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue((player.getAbilities().instabuild || player.experienceLevel >= this.cost.get()));
	}

	@Inject(
			at = @At("HEAD"),
			method = "createResult",
			cancellable = true
	)
	public void createResult(CallbackInfo ci) {
		if (!Feature.isEnabled(OtherExperience.class))
			return;

		ItemStack itemstack0 = this.inputSlots.getItem(0);
		this.cost.set(1);
		int cost = 0;
		int baseCost = 0;
		boolean isRenaming = false;
		if (itemstack0.isEmpty()) {
			this.resultSlots.setItem(0, ItemStack.EMPTY);
			this.cost.set(0);
		} else {
			ItemStack itemstack0copy = itemstack0.copy();
			ItemStack itemStack1 = this.inputSlots.getItem(1);
			Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack0copy);
			baseCost += itemstack0.getBaseRepairCost() + (itemStack1.isEmpty() ? 0 : itemStack1.getBaseRepairCost());
			this.repairItemCountCost = 0;
			boolean isEnchantedBook = false;

			if (!itemStack1.isEmpty()) {
				if (!net.minecraftforge.common.ForgeHooks.onAnvilChange((AnvilMenu) (Object) this, itemstack0, itemStack1, resultSlots, itemName, baseCost, this.player)) return;
				isEnchantedBook = itemStack1.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(itemStack1).isEmpty();
				if (itemstack0copy.isDamageableItem() && itemstack0copy.getItem().isValidRepairItem(itemstack0, itemStack1)) {
					int l2 = Math.min(itemstack0copy.getDamageValue(), itemstack0copy.getMaxDamage() / 4);
					if (l2 <= 0) {
						this.resultSlots.setItem(0, ItemStack.EMPTY);
						this.cost.set(0);
						return;
					}

					int i3;
					for(i3 = 0; l2 > 0 && i3 < itemStack1.getCount(); ++i3) {
						int j3 = itemstack0copy.getDamageValue() - l2;
						itemstack0copy.setDamageValue(j3);
						++cost;
						l2 = Math.min(itemstack0copy.getDamageValue(), itemstack0copy.getMaxDamage() / 4);
					}

					this.repairItemCountCost = i3;
				} else {
					if (!isEnchantedBook && (!itemstack0copy.is(itemStack1.getItem()) || !itemstack0copy.isDamageableItem())) {
						this.resultSlots.setItem(0, ItemStack.EMPTY);
						this.cost.set(0);
						return;
					}

					if (itemstack0copy.isDamageableItem() && !isEnchantedBook) {
						int l = itemstack0.getMaxDamage() - itemstack0.getDamageValue();
						int i1 = itemStack1.getMaxDamage() - itemStack1.getDamageValue();
						int j1 = i1 + itemstack0copy.getMaxDamage() * 12 / 100;
						int k1 = l + j1;
						int l1 = itemstack0copy.getMaxDamage() - k1;
						if (l1 < 0) {
							l1 = 0;
						}

						if (l1 < itemstack0copy.getDamageValue()) {
							itemstack0copy.setDamageValue(l1);
							cost += 2;
						}
					}

					Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemStack1);
					boolean flag2 = false;
					boolean flag3 = false;

					for(Enchantment enchantment1 : map1.keySet()) {
						if (enchantment1 != null) {
							int i2 = map.getOrDefault(enchantment1, 0);
							int j2 = map1.get(enchantment1);
							j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
							boolean flag1 = enchantment1.canEnchant(itemstack0);
							if (this.player.getAbilities().instabuild || itemstack0.is(Items.ENCHANTED_BOOK)) {
								flag1 = true;
							}

							for(Enchantment enchantment : map.keySet()) {
								if (enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment)) {
									flag1 = false;
									++cost;
								}
							}

							if (!flag1) {
								flag3 = true;
							} else {
								flag2 = true;
								if (j2 > enchantment1.getMaxLevel()) {
									j2 = enchantment1.getMaxLevel();
								}

								map.put(enchantment1, j2);
								int k3 = switch (enchantment1.getRarity()) {
									case COMMON -> 1;
									case UNCOMMON -> 2;
									case RARE -> 4;
									case VERY_RARE -> 8;
								};

								if (isEnchantedBook) {
									k3 = Math.max(1, k3 / 2);
								}

								cost += k3 * j2;
								if (itemstack0.getCount() > 1) {
									cost = 40;
								}
							}
						}
					}

					if (flag3 && !flag2) {
						this.resultSlots.setItem(0, ItemStack.EMPTY);
						this.cost.set(0);
						return;
					}
				}
			}

			if (StringUtils.isBlank(this.itemName)) {
				if (itemstack0.hasCustomHoverName()) {
					isRenaming = true;
					itemstack0copy.resetHoverName();
				}
			} else if (!this.itemName.equals(itemstack0.getHoverName().getString())) {
				isRenaming = true;
				itemstack0copy.setHoverName(Component.literal(this.itemName));
			}
			if (isEnchantedBook && !itemstack0copy.isBookEnchantable(itemStack1)) itemstack0copy = ItemStack.EMPTY;

			this.cost.set(baseCost + cost);
			if (isRenaming && !OtherExperience.isFreeRenaming())
				this.cost.set(this.cost.get() + COST_RENAME);
			if (cost <= 0 && !isRenaming) {
				itemstack0copy = ItemStack.EMPTY;
			}

			if (isRenaming && OtherExperience.isFreeRenaming() && cost <= 0) {
				this.cost.set(0);
			}

			//Set Too Expensive cap
			if (this.cost.get() >= OtherExperience.anvilRepairCap && !this.player.getAbilities().instabuild) {
				itemstack0copy = ItemStack.EMPTY;
			}

			if (!itemstack0copy.isEmpty()) {
				int k2 = itemstack0copy.getBaseRepairCost();
				if (!itemStack1.isEmpty() && k2 < itemStack1.getBaseRepairCost()) {
					k2 = itemStack1.getBaseRepairCost();
				}

				if (cost > 1) {
					k2 = AnvilMenu.calculateIncreasedRepairCost(k2);
				}

				itemstack0copy.setRepairCost(k2);
				EnchantmentHelper.setEnchantments(map, itemstack0copy);
			}

			this.resultSlots.setItem(0, itemstack0copy);
			this.broadcastChanges();
		}
		ci.cancel();
	}

	@Shadow
	protected boolean mayPickup(@NotNull Player p_39798_, boolean p_39799_) {
		return false;
	}

	@Shadow
	protected void onTake(@NotNull Player p_150601_, @NotNull ItemStack p_150602_) {
	}

	@Shadow
	protected boolean isValidBlock(@NotNull BlockState p_39788_) {
		return false;
	}

	@Shadow
	public void createResult() {
	}
}
