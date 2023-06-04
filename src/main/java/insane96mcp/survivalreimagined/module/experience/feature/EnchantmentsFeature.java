package insane96mcp.survivalreimagined.module.experience.feature;

import com.mojang.blaze3d.platform.InputConstants;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.data.lootmodifier.InjectLootTableModifier;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.experience.enchantment.*;
import insane96mcp.survivalreimagined.network.message.JumpMidAirMessage;
import insane96mcp.survivalreimagined.setup.SREnchantments;
import insane96mcp.survivalreimagined.setup.SRItems;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Enchantments", description = "Change some enchantments and anvil related stuff.")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class EnchantmentsFeature extends Feature {

	public static final RegistryObject<Item> CLEANSED_LAPIS = SRItems.REGISTRY.register("cleansed_lapis", () -> new Item(new Item.Properties()));
	public static final ResourceLocation WATER_COOLANT_AFFECTED = new ResourceLocation(SurvivalReimagined.MOD_ID, "water_coolant_affected");

	@Config
	@Label(name = "Mending overhaul", description = "Removes the mending enchantment and adds a new item that resets the repair cost of items.")
	public static Boolean mendingOverhaul = true;
	@Config
	@Label(name = "Infinity overhaul", description = "Infinity can go up to level 4. Each level makes an arrow have 1 in level+1 chance to not consume.")
	public static Boolean infinityOverhaul = true;
	@Config
	@Label(name = "Replace Bane of Arthropods", description = "Replace Bane of Arthropods with Bane of SSSSS, similar to Bane of Arthropods deals more damage to Spiders but also creepers.")
	public static Boolean replaceBaneOfArthropods = true;

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
	@Config
	@Label(name = "Buff Feather Falling", description = "Increases the damage protection from Feather Falling. From 12% per level to 16% per level")
	public static Boolean buffFeatherFalling = true;
	@Config
	@Label(name = "Buff Thorns", description = "Thorns no longer deals damage to items.")
	public static Boolean buffThorns = true;

	//TODO Make enchantments deactivable
	public EnchantmentsFeature(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	public static boolean disableEnchantment(Enchantment enchantment) {
		return (enchantment == net.minecraft.world.item.enchantment.Enchantments.ALL_DAMAGE_PROTECTION && protectionNerf == ProtectionNerf.DISABLE)
				|| (enchantment == Enchantments.BANE_OF_ARTHROPODS && replaceBaneOfArthropods);
	}

	@SubscribeEvent
	public void onAnvilUse(AnvilUpdateEvent event) {
		if (!this.isEnabled()
				|| !mendingOverhaul)
			return;

		ItemStack left = event.getLeft();
		if (left.getBaseRepairCost() <= 0)
			return;

		ItemStack right = event.getRight();
		if (!right.is(CLEANSED_LAPIS.get()))
			return;

		ItemStack result = left.copy();
		result.setRepairCost(0);
		event.setCost(0);
		event.setMaterialCost(1);
		event.setOutput(result);
	}

	@SubscribeEvent
	public void onAttributeModifiers(ItemAttributeModifierEvent event) {
		if (!this.isEnabled())
			return;

		StepUp.applyAttributeModifier(event);
		GravityDefying.applyAttributeModifier(event);
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
	}

	@SubscribeEvent
	public void onExperienceDropped(LivingExperienceDropEvent event) {
		if (!this.isEnabled()
				|| event.getAttackingPlayer() == null)
			return;
		int lvl = EnchantmentHelper.getEnchantmentLevel(SREnchantments.SMARTNESS.get(), event.getAttackingPlayer());
		if (lvl > 0)
			event.setDroppedExperience(Smartness.getIncreasedExperience(lvl, event.getDroppedExperience()));
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onRenderLevel(RenderLevelStageEvent event) {
		if (!this.isEnabled())
			return;

		Expanded.applyDestroyAnimation(event);
	}

	//Priority high: run before Timber Trees
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		if (!this.isEnabled())
			return;

		HitResult pick = event.getPlayer().pick(event.getPlayer().getEntityReach() + 0.5d, 1f, false);
		if (pick instanceof BlockHitResult blockHitResult)
			Expanded.apply(event.getPlayer(), event.getPlayer().getLevel(), event.getPos(), blockHitResult.getDirection(), event.getState());
	}

	@SubscribeEvent
	public void onLivingAttack(LivingHurtEvent event) {
		if (!this.isEnabled()
				|| !(event.getSource().getEntity() instanceof LivingEntity attacker))
			return;

		baneOfSssssOnAttack(attacker, event.getEntity(), event);
		waterCoolantOnAttack(attacker, event.getEntity(), event);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onJump(InputEvent.Key event) {
		if (!this.isEnabled()
				|| Minecraft.getInstance().player == null
				|| event.getAction() != InputConstants.PRESS
				|| event.getKey() != Minecraft.getInstance().options.keyJump.getKey().getValue())
			return;

		LocalPlayer player = Minecraft.getInstance().player;
		if (DoubleJump.extraJump(player)) {
			JumpMidAirMessage.jumpMidAir(player);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onLivingFall(LivingFallEvent event) {
		if (!this.isEnabled())
			return;

		if (event.getEntity() instanceof LocalPlayer player)
			player.getPersistentData().putInt("double_jumps", 0);
		GravityDefying.applyFallDamageReduction(event);
	}

	public void baneOfSssssOnAttack(LivingEntity attacker, LivingEntity entity, LivingHurtEvent event) {
		if (!(entity instanceof Creeper))
			return;
		int lvl = attacker.getMainHandItem().getEnchantmentLevel(SREnchantments.BANE_OF_SSSSS.get());
		if (lvl == 0)
			return;

		event.setAmount((event.getAmount() + 2.5f * lvl));
	}

	public void waterCoolantOnAttack(LivingEntity attacker, LivingEntity entity, LivingHurtEvent event) {
		if (!Utils.isEntityInTag(entity, WATER_COOLANT_AFFECTED))
			return;

		int lvl = attacker.getMainHandItem().getEnchantmentLevel(SREnchantments.WATER_COOLANT.get());
		if (lvl == 0)
			return;

		float bonusDamage = 2.5f * lvl;
		event.setAmount((event.getAmount() + bonusDamage));
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
				|| EnchantmentHelper.getEnchantmentLevel(Enchantments.FALL_PROTECTION, entity) <= 0)
			return;

		event.setCanceled(true);
	}

	private void processBow(AbstractArrow arrow) {
		if (powerEnchantmentDamage != 0.5d && arrow.getOwner() instanceof LivingEntity) {
			int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER_ARROWS, (LivingEntity) arrow.getOwner());
			if (powerLevel == 0)
				return;
			double powerReduction = 0.5d - powerEnchantmentDamage;
			arrow.setBaseDamage(arrow.getBaseDamage() - (powerLevel * powerReduction + powerReduction));
		}
	}

	public enum ProtectionNerf {
		NONE, NERF, DISABLE
	}

	public static boolean isMendingOverhaulEnabled() {
		return Feature.isEnabled(EnchantmentsFeature.class) && mendingOverhaul;
	}

	public static boolean isInfinityOverhaulEnabled() {
		return Feature.isEnabled(EnchantmentsFeature.class) && infinityOverhaul;
	}

	public static boolean isFeatherFallingBuffed() {
		return Feature.isEnabled(EnchantmentsFeature.class) && buffFeatherFalling;
	}

	private static final String path = "experience/enchantments";

	public static void addGlobalLoot(GlobalLootModifierProvider provider) {
		provider.add(path + "blocks/lapis_ore", new InjectLootTableModifier(new ResourceLocation("minecraft:blocks/lapis_ore"), new ResourceLocation("survivalreimagined:blocks/cleansed_lapis")));
		provider.add(path + "blocks/deepslate_lapis_ore", new InjectLootTableModifier(new ResourceLocation("minecraft:blocks/deepslate_lapis_ore"), new ResourceLocation("survivalreimagined:blocks/cleansed_lapis")));
	}
}