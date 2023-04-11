package insane96mcp.survivalreimagined.mixin;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.survivalreimagined.module.experience.feature.OtherExperience;
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

		ItemStack left = this.inputSlots.getItem(0);
		this.cost.set(1);
		int mergeCost = 0;
		int baseCost = 0;
		boolean isRenaming = false;
		if (left.isEmpty()) {
			this.resultSlots.setItem(0, ItemStack.EMPTY);
			this.cost.set(0);
		} else {
			ItemStack leftCopy = left.copy();
			ItemStack right = this.inputSlots.getItem(1);
			Map<Enchantment, Integer> leftEnchantments = EnchantmentHelper.getEnchantments(leftCopy);
			baseCost += left.getBaseRepairCost() + (right.isEmpty() ? 0 : right.getBaseRepairCost());
			this.repairItemCountCost = 0;
			boolean isEnchantedBook = false;

			if (!right.isEmpty()) {
				if (!net.minecraftforge.common.ForgeHooks.onAnvilChange((AnvilMenu) (Object) this, left, right, resultSlots, itemName, baseCost, this.player)) return;
				isEnchantedBook = right.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(right).isEmpty();
				if (leftCopy.isDamageableItem() && leftCopy.getItem().isValidRepairItem(left, right)) {
					int repairSteps = Math.min(leftCopy.getDamageValue(), leftCopy.getMaxDamage() / 4);
					if (repairSteps <= 0) {
						this.resultSlots.setItem(0, ItemStack.EMPTY);
						this.cost.set(0);
						return;
					}

					int repairItemCountCost;
					for(repairItemCountCost = 0; repairSteps > 0 && repairItemCountCost < right.getCount(); ++repairItemCountCost) {
						int j3 = leftCopy.getDamageValue() - repairSteps;
						leftCopy.setDamageValue(j3);
						++mergeCost;
						repairSteps = Math.min(leftCopy.getDamageValue(), leftCopy.getMaxDamage() / 4);
					}

					this.repairItemCountCost = repairItemCountCost;
				}
				else {
					if (!isEnchantedBook && (!leftCopy.is(right.getItem()) || !leftCopy.isDamageableItem())) {
						this.resultSlots.setItem(0, ItemStack.EMPTY);
						this.cost.set(0);
						return;
					}

					if (leftCopy.isDamageableItem() && !isEnchantedBook) {
						int l = left.getMaxDamage() - left.getDamageValue();
						int i1 = right.getMaxDamage() - right.getDamageValue();
						int j1 = i1 + leftCopy.getMaxDamage() * 12 / 100;
						int k1 = l + j1;
						int l1 = leftCopy.getMaxDamage() - k1;
						if (l1 < 0) {
							l1 = 0;
						}

						if (l1 < leftCopy.getDamageValue()) {
							leftCopy.setDamageValue(l1);
							mergeCost += 2;
						}
					}

					Map<Enchantment, Integer> rightEnchantment = EnchantmentHelper.getEnchantments(right);
					boolean flag2 = false;
					boolean flag3 = false;

					for(Enchantment enchantment1 : rightEnchantment.keySet()) {
						if (enchantment1 != null) {
							int leftLvl = leftEnchantments.getOrDefault(enchantment1, 0);
							int rightLvl = rightEnchantment.get(enchantment1);
							rightLvl = leftLvl == rightLvl ? rightLvl + 1 : Math.max(rightLvl, leftLvl);
							boolean canEnchant = enchantment1.canEnchant(left);
							if (this.player.getAbilities().instabuild || left.is(Items.ENCHANTED_BOOK)) {
								canEnchant = true;
							}

							for(Enchantment enchantment : leftEnchantments.keySet()) {
								if (enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment)) {
									canEnchant = false;
									++mergeCost;
								}
							}

							if (!canEnchant) {
								flag3 = true;
							} else {
								flag2 = true;
								if (rightLvl > enchantment1.getMaxLevel() && leftLvl == rightLvl /*Added to allow over max level enchantment books to be applied to items*/) {
									rightLvl = enchantment1.getMaxLevel();
								}

								leftEnchantments.put(enchantment1, rightLvl);
								int enchantmentRarityCost = switch (enchantment1.getRarity()) {
									case COMMON -> 1;
									case UNCOMMON -> 2;
									case RARE -> 4;
									case VERY_RARE -> 8;
								};

								if (isEnchantedBook) {
									enchantmentRarityCost = Math.max(1, enchantmentRarityCost / 2);
								}

								mergeCost += enchantmentRarityCost * rightLvl;
								if (left.getCount() > 1) {
									mergeCost = 40;
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
				if (left.hasCustomHoverName()) {
					isRenaming = true;
					leftCopy.resetHoverName();
				}
			}
			else if (!this.itemName.equals(left.getHoverName().getString())) {
				isRenaming = true;
				leftCopy.setHoverName(Component.literal(this.itemName));
			}
			if (isEnchantedBook && !leftCopy.isBookEnchantable(right))
				leftCopy = ItemStack.EMPTY;

			this.cost.set(baseCost + mergeCost);
			if (isRenaming && !OtherExperience.isFreeRenaming())
				this.cost.set(this.cost.get() + COST_RENAME);
			if (mergeCost <= 0 && !isRenaming) {
				leftCopy = ItemStack.EMPTY;
			}

			if (isRenaming && OtherExperience.isFreeRenaming() && mergeCost <= 0) {
				this.cost.set(0);
			}

			//Set Too Expensive cap
			if (this.cost.get() >= OtherExperience.anvilRepairCap && !this.player.getAbilities().instabuild) {
				leftCopy = ItemStack.EMPTY;
			}

			if (!leftCopy.isEmpty()) {
				int toolRepairCost = leftCopy.getBaseRepairCost();
				if (!right.isEmpty() && toolRepairCost < right.getBaseRepairCost()) {
					toolRepairCost = right.getBaseRepairCost();
				}

				if (mergeCost >= 1) {
					toolRepairCost = AnvilMenu.calculateIncreasedRepairCost(toolRepairCost);
				}

				leftCopy.setRepairCost(toolRepairCost);
				EnchantmentHelper.setEnchantments(leftEnchantments, leftCopy);
			}

			this.resultSlots.setItem(0, leftCopy);
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

	@Shadow
	protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
		return null;
	}
}
