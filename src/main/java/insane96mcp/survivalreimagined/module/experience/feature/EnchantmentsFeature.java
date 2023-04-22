package insane96mcp.survivalreimagined.module.experience.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.experience.enchantment.*;
import insane96mcp.survivalreimagined.module.misc.feature.DataPacks;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import insane96mcp.survivalreimagined.setup.SREnchantments;
import insane96mcp.survivalreimagined.setup.SRItems;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Enchantments", description = "Change some enchantments and anvil related stuff.")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class EnchantmentsFeature extends Feature {

	public static final RegistryObject<Item> CLEANSED_LAPIS = SRItems.REGISTRY.register("cleansed_lapis", () -> new Item(new Item.Properties()));

	@Config
	@Label(name = "Mending overhaul", description = "Removes the mending enchantment and adds a new item that resets the repair cost of items.")
	public static Boolean mendingOverhaul = true;
	@Config
	@Label(name = "Infinity overhaul", description = "Infinity can go up to level 4. Each level makes an arrow have 1 in level chance to not consume.")
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
	@Label(name = "Cleansed Lapis DataPack", description = "Enables a datapack that adds Cleansed Lapis drop from lapis ore")
	public static Boolean cleansedLapisDataPack = true;

	//TODO Make enchantments deactivable

	public EnchantmentsFeature(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "cleansed_lapis", net.minecraft.network.chat.Component.literal("Survival Reimagined Cleansed Lapis"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && cleansedLapisDataPack));
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
	@OnlyIn(Dist.CLIENT)
	public void onRenderLevel(RenderLevelStageEvent event) {
		if (!this.isEnabled())
			return;

		Expanded.applyDestroyAnimation(event);
	}

	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		if (!this.isEnabled())
			return;

		Vec3 viewVector = event.getPlayer().getViewVector(1f);
		Vec3 endClip = event.getPlayer().getEyePosition().add(viewVector.x * event.getPlayer().getEntityReach(), viewVector.y * event.getPlayer().getEntityReach(), viewVector.z * event.getPlayer().getEntityReach());
		BlockHitResult blockHitResult = event.getLevel().clip(new ClipContext(event.getPlayer().getEyePosition(), endClip, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, event.getPlayer()));
		Expanded.apply(event.getPlayer(), event.getPlayer().getLevel(), event.getPos(), blockHitResult.getDirection(), event.getState());
	}

	@SubscribeEvent
	public void onLivingAttack(LivingHurtEvent event) {
		if (!this.isEnabled()
				|| !(event.getSource().getEntity() instanceof LivingEntity entity)
				|| !(event.getEntity() instanceof Creeper))
			return;

		int lvl = entity.getMainHandItem().getEnchantmentLevel(SREnchantments.BANE_OF_SSSSS.get());
		if (lvl == 0)
			return;

		event.setAmount((float) (event.getAmount() + 2.5d * lvl));
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

	public enum ProtectionNerf {
		NONE, NERF, DISABLE
	}

	public static boolean isMendingOverhaulEnabled() {
		return Feature.isEnabled(EnchantmentsFeature.class) && mendingOverhaul;
	}

	public static boolean isInfinityOverhaulEnabled() {
		return Feature.isEnabled(EnchantmentsFeature.class) && infinityOverhaul;
	}
}