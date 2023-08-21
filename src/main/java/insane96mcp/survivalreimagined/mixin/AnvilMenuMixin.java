package insane96mcp.survivalreimagined.mixin;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.survivalreimagined.module.experience.anvils.AnvilRecipe;
import insane96mcp.survivalreimagined.module.experience.anvils.Anvils;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
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
import java.util.Optional;

@Mixin(value = AnvilMenu.class, priority = 1001)
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
		if (!Feature.isEnabled(Anvils.class))
			return;

		ItemStack left = this.inputSlots.getItem(0);
		this.cost.set(1);
		int mergeCost = 0;
		int baseCost = 0;
		boolean isRenaming = false;

		if (left.isEmpty()) {
			this.resultSlots.setItem(0, ItemStack.EMPTY);
			this.cost.set(0);
			return;
		}

		ItemStack resultStack = left.copy();
		ItemStack right = this.inputSlots.getItem(1);
		Map<Enchantment, Integer> leftEnchantments = EnchantmentHelper.getEnchantments(resultStack);
		//Don't add the repair cost of the items if remove repair cost increase
		if (!Anvils.noRepairCostIncreaseAndEnchCost)
			baseCost += left.getBaseRepairCost() + (right.isEmpty() ? 0 : right.getBaseRepairCost());
		this.repairItemCountCost = 0;
		boolean isEnchantedBook = false;

		if (!right.isEmpty()) {
			if (!net.minecraftforge.common.ForgeHooks.onAnvilChange((AnvilMenu) (Object) this, left, right, resultSlots, itemName, baseCost, this.player)) return;
			isEnchantedBook = right.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(right).isEmpty();
			Optional<AnvilRecipe.RepairData> oRepairData = Anvils.getCustomAnvilRepair(left, right);
			//If it's a damageable item check if trying to repair it
			if (resultStack.isDamageableItem() && (resultStack.getItem().isValidRepairItem(left, right) || oRepairData.isPresent())) {
				int repairItemCountCost;

				//If a custom anvil repair is present, use that
				if (oRepairData.isPresent()) {
					AnvilRecipe.RepairData repairData = oRepairData.get();
					int maxPartialRepairDmg = Mth.ceil(resultStack.getMaxDamage() * (1f - repairData.maxRepair()));
					int repairSteps = Math.min(resultStack.getDamageValue(), Mth.ceil(resultStack.getMaxDamage() / (float) repairData.amountRequired()));
					if (repairSteps <= 0 || resultStack.getDamageValue() <= maxPartialRepairDmg) {
						this.resultSlots.setItem(0, ItemStack.EMPTY);
						this.cost.set(0);
						return;
					}

					for (repairItemCountCost = 0; repairSteps > 0 && repairItemCountCost < right.getCount() && resultStack.getDamageValue() > maxPartialRepairDmg; ++repairItemCountCost) {
						int dmgAfterRepair = resultStack.getDamageValue() - repairSteps;
						resultStack.setDamageValue(Math.max(maxPartialRepairDmg, dmgAfterRepair));
						if (!Anvils.noRepairCostIncreaseAndEnchCost)
							++mergeCost;
						repairSteps = Math.min(resultStack.getDamageValue(), Mth.ceil(resultStack.getMaxDamage() / (float) repairData.amountRequired()));
					}
				}
				//Otherwise, vanilla behaviour
				else {
					int repairSteps = Math.min(resultStack.getDamageValue(), resultStack.getMaxDamage() / 4);
					if (repairSteps <= 0) {
						this.resultSlots.setItem(0, ItemStack.EMPTY);
						this.cost.set(0);
						return;
					}

					for(repairItemCountCost = 0; repairSteps > 0 && repairItemCountCost < right.getCount(); ++repairItemCountCost) {
						int dmgAfterRepair = resultStack.getDamageValue() - repairSteps;
						resultStack.setDamageValue(dmgAfterRepair);
						if (!Anvils.noRepairCostIncreaseAndEnchCost)
							++mergeCost;
						repairSteps = Math.min(resultStack.getDamageValue(), resultStack.getMaxDamage() / 4);
					}
				}

				if (Anvils.noRepairCostIncreaseAndEnchCost) {
					for (Enchantment leftEnchantment : leftEnchantments.keySet()) {
						int lvl = leftEnchantments.get(leftEnchantment);
						mergeCost += lvl * Anvils.getRarityCost(leftEnchantment);
					}
				}

				if (oRepairData.isPresent()) {
					mergeCost = Mth.floor(mergeCost * oRepairData.get().costMultiplier());
				}

				this.repairItemCountCost = repairItemCountCost;
			}
			//Else it's merging items
			else {
				if (!isEnchantedBook && (!resultStack.is(right.getItem()) || !resultStack.isDamageableItem())) {
					this.resultSlots.setItem(0, ItemStack.EMPTY);
					this.cost.set(0);
					return;
				}

				if (resultStack.isDamageableItem() && !isEnchantedBook) {
					int leftDurabilityLeft = left.getMaxDamage() - left.getDamageValue();
					int rightDurabilityLeft = right.getMaxDamage() - right.getDamageValue();
					int rightDurabilityLeftPlusBonus = rightDurabilityLeft + resultStack.getMaxDamage() * Anvils.getMergingRepairBonus() / 100;
					int leftDurabilityLeftPlusRight = leftDurabilityLeft + rightDurabilityLeftPlusBonus;
					int damageValue = resultStack.getMaxDamage() - leftDurabilityLeftPlusRight;
					if (damageValue < 0)
						damageValue = 0;

					if (damageValue < resultStack.getDamageValue()) {
						resultStack.setDamageValue(damageValue);
						if (!Anvils.mergingCostBasedOffResult)
							mergeCost += 2;
					}
				}

				Map<Enchantment, Integer> rightEnchantments = EnchantmentHelper.getEnchantments(right);
				boolean canEnchant = false;
				boolean cannotEnchant = false;

				for (Enchantment rightEnchantment : rightEnchantments.keySet()) {
                    if (rightEnchantment == null)
						continue;

					int leftLvl = leftEnchantments.getOrDefault(rightEnchantment, 0);
					int rightLvl = rightEnchantments.get(rightEnchantment);
					rightLvl = leftLvl == rightLvl ? rightLvl + 1 : Math.max(rightLvl, leftLvl);
					boolean canEnchant2 = rightEnchantment.canEnchant(left);
					if (this.player.getAbilities().instabuild || left.is(Items.ENCHANTED_BOOK))
						canEnchant2 = true;

					for (Enchantment enchantment : leftEnchantments.keySet()) {
						if (enchantment != rightEnchantment && !rightEnchantment.isCompatibleWith(enchantment)) {
							canEnchant2 = false;
							if (!Anvils.mergingCostBasedOffResult)
								++mergeCost;
						}
					}

					if (!canEnchant2) {
						cannotEnchant = true;
					}
					else {
						canEnchant = true;
						if (rightLvl > rightEnchantment.getMaxLevel()
								&& leftLvl == rightLvl /*Added to allow over max level enchantment books to be applied to items*/)
							rightLvl = rightEnchantment.getMaxLevel();

						leftEnchantments.put(rightEnchantment, rightLvl);
						int enchantmentRarityCost = Anvils.getRarityCost(rightEnchantment);

						if (isEnchantedBook)
							enchantmentRarityCost = Math.max(1, enchantmentRarityCost / 2);

						if (!Anvils.mergingCostBasedOffResult)
							mergeCost += enchantmentRarityCost * rightLvl;
					}
                }

				//If "Merging cost is based off result" is enabled, I loop all the enchantments to calculate the cost based off the result
				if (Anvils.mergingCostBasedOffResult) {
					for (Map.Entry<Enchantment, Integer> enchantment : leftEnchantments.entrySet()) {
						int enchantmentRarityCost = Anvils.getRarityCost(enchantment.getKey());

						if (isEnchantedBook)
							enchantmentRarityCost = Math.max(1, enchantmentRarityCost / 2);
						mergeCost += enchantmentRarityCost * enchantment.getValue();
					}
				}

				if (cannotEnchant && !canEnchant) {
					this.resultSlots.setItem(0, ItemStack.EMPTY);
					this.cost.set(0);
					return;
				}
			}
		}

		if (StringUtils.isBlank(this.itemName)) {
			if (left.hasCustomHoverName()) {
				isRenaming = true;
				resultStack.resetHoverName();
			}
		}
		else if (!this.itemName.equals(left.getHoverName().getString())) {
			isRenaming = true;
			resultStack.setHoverName(Component.literal(this.itemName));
		}
		if (isEnchantedBook && !resultStack.isBookEnchantable(right))
			resultStack = ItemStack.EMPTY;

		this.cost.set((int) Math.round((baseCost + mergeCost) * Anvils.repairCostMultiplier));
		if (isRenaming && !Anvils.freeRenaming)
			this.cost.set(this.cost.get() + COST_RENAME);
		/*if (mergeCost <= 0 && !isRenaming)
			resultStack = ItemStack.EMPTY;*/

		if (isRenaming && Anvils.freeRenaming && mergeCost <= 0)
			this.cost.set(0);
		if (!isRenaming && right.isEmpty())
			resultStack = ItemStack.EMPTY;

		//Set Too Expensive cap
		if (this.cost.get() >= Anvils.anvilRepairCap && !this.player.getAbilities().instabuild)
			resultStack = ItemStack.EMPTY;

		if (!resultStack.isEmpty()) {
			if (!Anvils.noRepairCostIncreaseAndEnchCost) {
				int toolRepairCost = resultStack.getBaseRepairCost();
				if (!right.isEmpty() && toolRepairCost < right.getBaseRepairCost())
					toolRepairCost = right.getBaseRepairCost();
				if (mergeCost >= 1)
					toolRepairCost = AnvilMenu.calculateIncreasedRepairCost(toolRepairCost);
				resultStack.setRepairCost(toolRepairCost);
			}
			EnchantmentHelper.setEnchantments(leftEnchantments, resultStack);
		}

		this.resultSlots.setItem(0, resultStack);
		this.broadcastChanges();

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
