package insane96mcp.survivalreimagined.module.experience.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.experience.enchantment.Blasting;
import insane96mcp.survivalreimagined.module.experience.enchantment.Expanded;
import insane96mcp.survivalreimagined.module.experience.enchantment.MagicProtection;
import insane96mcp.survivalreimagined.module.experience.enchantment.Magnetic;
import insane96mcp.survivalreimagined.setup.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;

@Label(name = "Enchantments", description = "Change some enchantments and anvil related stuff.")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class Enchantments extends Feature {
	@Config
	@Label(name = "Unmending", description = """
			Makes mending reset the repair cost of an item to 0 when applied to it. No longer repairs items with XP.
			If an item has already mending, the enchantment will be removed and repair cost reset when used in an anvil.
			Applying mending still requires the base repair cost of the item (you can't add Mending if the operation is 'Too Expensive'""")
	public static Boolean unmending = true;
	@Config
	@Label(name = "Repaired Tooltip", description = "If true (and Unmending is enabled), items will have a 'Item has been repaired' tooltip.")
	public static Boolean repairedTooltip = false;

	@Config
	@Label(name = "Efficiency changed formula", description = "Change the efficiency formula from tool_efficiency+(lvl*lvl+1) to (tool_efficiency + 75% * level)")
	public static Boolean changeEfficiencyFormula = true;
	@Config(min = 0d, max = 10d)
	@Label(name = "Power Enchantment Damage", description = "Set arrow's damage increase with the Power enchantment (vanilla is 0.5). Set to 0.5 to disable.")
	public static Double powerEnchantmentDamage = 0.4d;
	@Config
	@Label(name = "Nerf Protection Enchantment", description = """
						DISABLE: Disables protection enchantment.
						NERF: Sets max protection level to 3 instead of 4
						NONE: no changes to protection are done""")
	public static ProtectionNerf protectionNerf = ProtectionNerf.DISABLE;

	@Config
	@Label(name = "Prevent farmland trampling with Feather Falling")
	public static Boolean preventFarmlandTramplingWithFeatherFalling = true;

	//TODO Make enchantments deactivable

	public Enchantments(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	public static boolean disableEnchantment(Enchantment enchantment) {
		return enchantment == net.minecraft.world.item.enchantment.Enchantments.ALL_DAMAGE_PROTECTION && protectionNerf == ProtectionNerf.DISABLE;
	}

	@SubscribeEvent
	public void onAnvilUse(AnvilUpdateEvent event) {
		if (!this.isEnabled()
				|| !unmending)
			return;

		ItemStack left = event.getLeft();
		ItemStack right = event.getRight();
		ItemStack out = event.getOutput();

		if (out.isEmpty() && (left.isEmpty() || right.isEmpty()))
			return;

		boolean isMended = false;

		Map<Enchantment, Integer> enchLeft = EnchantmentHelper.getEnchantments(left);
		Map<Enchantment, Integer> enchRight = EnchantmentHelper.getEnchantments(right);

		if (enchLeft.containsKey(net.minecraft.world.item.enchantment.Enchantments.MENDING) || enchRight.containsKey(net.minecraft.world.item.enchantment.Enchantments.MENDING)) {
			if (left.getItem() == right.getItem())
				isMended = true;

			if (right.getItem() == Items.ENCHANTED_BOOK)
				isMended = true;
		}

		if (isMended) {
			if (out.isEmpty())
				out = left.copy();

			if (!out.hasTag())
				out.setTag(new CompoundTag());

			Map<Enchantment, Integer> enchOutput = EnchantmentHelper.getEnchantments(out);
			enchOutput.putAll(enchRight);
			enchOutput.remove(net.minecraft.world.item.enchantment.Enchantments.MENDING);

			EnchantmentHelper.setEnchantments(enchOutput, out);

			out.setRepairCost(0);
			if (out.isDamageableItem())
				out.setDamageValue(0);

			event.setOutput(out);
			if (event.getCost() == 0)
				event.setCost(1);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void unmending(PlayerXpEvent.PickupXp event) {
		if (!this.isEnabled()
				|| !unmending
				|| event.isCanceled())
			return;

		Player player = event.getEntity();
		ExperienceOrb orb = event.getOrb();

		player.takeXpDelay = 2;
		player.take(orb, 1);
		if(orb.value > 0)
			player.giveExperiencePoints(orb.value);

		--orb.count;
		if (orb.count == 0)
			orb.discard();
		event.setCanceled(true);
	}

	@SubscribeEvent
	public void onEntityTick(LivingEvent.LivingTickEvent event) {
		if (!this.isEnabled())
			return;

		Magnetic.tryPullItems(event.getEntity());
	}

	@SubscribeEvent
	public void onEffectAdded(MobEffectEvent.Added event) {
		if (!this.isEnabled())
			return;

		MagicProtection.reduceBadEffectsDuration(event.getEntity(), event.getEffectInstance());
	}

	@SubscribeEvent
	public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled())
			return;

		event.setNewSpeed(event.getNewSpeed() + Blasting.getMiningSpeedBoost(event.getEntity(), event.getState()));

		if (event.getEntity().level.isClientSide && event.getPosition().isPresent()) {
			ClientLevel clientLevel = (ClientLevel) event.getEntity().level;
			Vec3 viewVector = event.getEntity().getViewVector(1f);
			Vec3 endClip = event.getEntity().getEyePosition().add(viewVector.x * event.getEntity().getReachDistance(), viewVector.y * event.getEntity().getReachDistance(), viewVector.z * event.getEntity().getReachDistance());
			BlockHitResult blockHitResult = clientLevel.clip(new ClipContext(event.getEntity().getEyePosition(), endClip, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, event.getEntity()));
			Expanded.applyDestroyAnimation(event.getEntity(), clientLevel, event.getPosition().get(), blockHitResult.getDirection(), event.getState());
		}
	}

	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		if (!this.isEnabled())
			return;

		Vec3 viewVector = event.getPlayer().getViewVector(1f);
		Vec3 endClip = event.getPlayer().getEyePosition().add(viewVector.x * event.getPlayer().getReachDistance(), viewVector.y * event.getPlayer().getReachDistance(), viewVector.z * event.getPlayer().getReachDistance());
		BlockHitResult blockHitResult = event.getLevel().clip(new ClipContext(event.getPlayer().getEyePosition(), endClip, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, event.getPlayer()));
		Expanded.apply(event.getPlayer(), event.getPlayer().getLevel(), event.getPos(), blockHitResult.getDirection(), event.getState());
	}

	@SubscribeEvent
	public void onArrowSpawn(EntityJoinLevelEvent event) {
		if (!this.isEnabled()
				|| !(event.getEntity() instanceof AbstractArrow arrow))
			return;
		if (!arrow.shotFromCrossbow())
			processBow(arrow);
	}

	@SubscribeEvent
	public void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
		if (!this.isEnabled()
				|| !preventFarmlandTramplingWithFeatherFalling
				|| !(event.getEntity() instanceof LivingEntity entity)
				|| EnchantmentHelper.getEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.FALL_PROTECTION, entity) <= 0)
			return;

		event.setCanceled(true);
	}

	private void processBow(AbstractArrow arrow) {
		if (powerEnchantmentDamage != 0.5d && arrow.getOwner() instanceof LivingEntity) {
			int powerLevel = EnchantmentHelper.getEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.POWER_ARROWS, (LivingEntity) arrow.getOwner());
			if (powerLevel == 0)
				return;
			double powerReduction = 0.5d - powerEnchantmentDamage;
			arrow.setBaseDamage(arrow.getBaseDamage() - (powerLevel * powerReduction + powerReduction));
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onTooltip(ItemTooltipEvent event) {
		if (!this.isEnabled()
				|| !unmending
				|| !repairedTooltip)
			return;

		int repairCost = event.getItemStack().getBaseRepairCost();
		if(repairCost > 0)
			event.getToolTip().add(Component.translatable(Strings.Translatable.ITEM_REPAIRED).withStyle(ChatFormatting.YELLOW));
	}

	public enum ProtectionNerf {
		NONE, NERF, DISABLE
	}
}