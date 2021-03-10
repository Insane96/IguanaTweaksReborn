package insane96mcp.iguanatweaksreborn.utils;

import insane96mcp.iguanatweaksreborn.common.classutils.IdTagMatcher;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class MCUtils {
	public static final ResourceLocation AnyRL = new ResourceLocation("any");

	public static boolean isInTagOrBlock(IdTagMatcher idTagMatcher, Block block, @Nullable ResourceLocation dimensionId) {
		if (dimensionId == null)
			dimensionId = AnyRL;
		ResourceLocation blockId = block.getRegistryName();
		if (idTagMatcher.tag != null) {
			if (!BlockTags.getCollection().getRegisteredTags().contains(idTagMatcher.tag))
				return false;
			ITag<Block> blockTag = BlockTags.getCollection().get(idTagMatcher.tag);
			if (blockTag == null)
				return false;
			if (!blockTag.contains(block))
				return false;
			if (idTagMatcher.dimension.equals(AnyRL) || idTagMatcher.dimension.equals(dimensionId))
				return true;
		}
		else {
			if (blockId.equals(idTagMatcher.id))
				if (idTagMatcher.dimension.equals(AnyRL) || idTagMatcher.dimension.equals(dimensionId))
					return true;
		}
		return false;
	}

	public static boolean isInTagOrItem(IdTagMatcher idTagMatcher, Item item, @Nullable ResourceLocation dimensionId) {
		if (dimensionId == null)
			dimensionId = AnyRL;
		ResourceLocation itemId = item.getRegistryName();
		if (idTagMatcher.tag != null) {
			if (!BlockTags.getCollection().getRegisteredTags().contains(idTagMatcher.tag))
				return false;
			ITag<Item> itemTag = ItemTags.getCollection().get(idTagMatcher.tag);
			if (itemTag == null)
				return false;
			if (!itemTag.contains(item))
				return false;
			if (idTagMatcher.dimension.equals(AnyRL) || idTagMatcher.dimension.equals(dimensionId))
				return true;
		}
		else {
			if (itemId.equals(idTagMatcher.id))
				if (idTagMatcher.dimension.equals(AnyRL) || idTagMatcher.dimension.equals(dimensionId))
					return true;
		}
		return false;
	}

	public static boolean canBlockDamageSource(DamageSource damageSourceIn, LivingEntity livingEntity) {
		Entity entity = damageSourceIn.getImmediateSource();
		boolean flag = false;
		if (entity instanceof AbstractArrowEntity) {
			AbstractArrowEntity abstractarrowentity = (AbstractArrowEntity)entity;
			if (abstractarrowentity.getPierceLevel() > 0) {
				flag = true;
			}
		}

		if (livingEntity.isActiveItemStackBlocking() && !flag) {
			Vector3d vector3d2 = damageSourceIn.getDamageLocation();
			if (vector3d2 != null) {
				Vector3d vector3d = livingEntity.getLook(1.0F);
				Vector3d vector3d1 = vector3d2.subtractReverse(livingEntity.getPositionVec()).normalize();
				vector3d1 = new Vector3d(vector3d1.x, 0.0D, vector3d1.z);
				if (vector3d1.dotProduct(vector3d) < 0.0D) {
					return true;
				}
			}
		}
		return false;
	}

	public static int getEnchantmentLevel(ResourceLocation enchID, ItemStack stack) {
		if (stack.isEmpty())
			return 0;
		ListNBT listnbt = stack.getEnchantmentTagList();
		for (int i = 0; i < listnbt.size(); ++i) {
			CompoundNBT compoundnbt = listnbt.getCompound(i);
			ResourceLocation itemEnchantment = ResourceLocation.tryCreate(compoundnbt.getString("id"));
			if (itemEnchantment != null && itemEnchantment.equals(enchID)) {
				return MathHelper.clamp(compoundnbt.getInt("lvl"), 0, 255);
			}
		}
		return 0;
	}
}
