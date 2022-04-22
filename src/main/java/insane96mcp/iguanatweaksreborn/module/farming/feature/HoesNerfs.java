package insane96mcp.iguanatweaksreborn.module.farming.feature;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.farming.utils.HoeCooldown;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Hoes Nerfs", description = "Slower Hoes and more fragile")
public class HoesNerfs extends Feature {

	private final ForgeConfigSpec.ConfigValue<List<? extends String>> hoesCooldownsConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> disableLowTierHoesConfig;
	private final ForgeConfigSpec.ConfigValue<Integer> hoesDamageOnUseMultiplierConfig;

	private static final List<String> hoesCooldownsDefault = Arrays.asList("minecraft:stone_hoe,20", "minecraft:iron_hoe,15", "minecraft:golden_hoe,4", "minecraft:diamond_hoe,10", "minecraft:netherite_hoe,6", "vulcanite:vulcanite_hoe,15");

	public ArrayList<HoeCooldown> hoesCooldowns;
	public boolean disableLowTierHoes = true;
	public int hoesDamageOnUseMultiplier = 3;

	public HoesNerfs(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		hoesCooldownsConfig = Config.builder
				.comment("A list of hoes and ticks that a hoe will go on cooldown. The format is modid:itemid,ticks. 20 ticks = 1 second. You can even use tags as #modid:tag,ticks.")
				.defineList("Hoes Cooldowns", hoesCooldownsDefault, o -> o instanceof String);
		disableLowTierHoesConfig = Config.builder
				.comment("When true, Wooden and Stone Hoes will not be usable to till dirt and will be heavily damaged when trying to. The list of \"unusable\" hoes can be changed with datapacks by changing the iguanatweaksreborn:disabled_hoes tag")
				.define("Disable Low Tier Hoes", disableLowTierHoes);
		hoesDamageOnUseMultiplierConfig = Config.builder
				.comment("When an hoe is used to till dirt it will lose this durability instead of 1. Set to 1 to disable")
				.defineInRange("Hoes Damage On Use Multiplier", hoesDamageOnUseMultiplier, 1, 1024);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.hoesCooldowns = parseHoesCooldowns(this.hoesCooldownsConfig.get());
		this.disableLowTierHoes = this.disableLowTierHoesConfig.get();
		this.hoesDamageOnUseMultiplier = this.hoesDamageOnUseMultiplierConfig.get();
	}

	public static ArrayList<HoeCooldown> parseHoesCooldowns(List<? extends String> list) {
		ArrayList<HoeCooldown> hoesCooldowns = new ArrayList<>();
		for (String line : list) {
			HoeCooldown hoeCooldown = HoeCooldown.parseLine(line);
			if (hoeCooldown != null)
				hoesCooldowns.add(hoeCooldown);
		}
		return hoesCooldowns;
	}

	@SubscribeEvent
	public void disabledHoes(PlayerInteractEvent.RightClickBlock event) {
		if (!this.isEnabled())
			return;
		if (event.getPlayer().level.isClientSide)
			return;
		if (!this.disableLowTierHoes)
			return;
		if (!isHoeDisabled(event.getItemStack().getItem()))
			return;
		if (!event.getWorld().isEmptyBlock(event.getPos().above()))
			return;
		if (HoeItem.TILLABLES.get(event.getWorld().getBlockState(event.getPos()).getBlock()) == null)
			return;
		ItemStack hoe = event.getItemStack();
		hoe.hurtAndBreak(hoe.getMaxDamage(), event.getPlayer(), (player) -> player.broadcastBreakEvent(event.getPlayer().getUsedItemHand()));
		event.getPlayer().displayClientMessage(new TextComponent("This hoe is too weak to be used"), true);
		event.setCanceled(true);
	}

	@SubscribeEvent
	//TODO Move to BlockInteractionEvent when removed
	public void harderTilling(UseHoeEvent event) {
		if (!this.isEnabled())
			return;
		if (event.getPlayer().level.isClientSide)
			return;
		ItemStack itemStack = event.getContext().getItemInHand();
		if (isHoeDisabled(itemStack.getItem()) && disableLowTierHoes)
			return;
		if (!event.getContext().getLevel().isEmptyBlock(event.getContext().getClickedPos().above()))
			return;
		if (HoeItem.TILLABLES.get(event.getContext().getLevel().getBlockState(event.getContext().getClickedPos()).getBlock()) == null)
			return;
		//TODO Replace with below when implemented
		if (!(itemStack.getItem() instanceof HoeItem))
			return;
		//if (!stack.canPerformAction(ToolActions.TILL))
			//return;
		if (event.getPlayer().getCooldowns().isOnCooldown(itemStack.getItem()))
			return;
		int cooldown = 0;
		for (HoeCooldown hoeCooldown : this.hoesCooldowns) {
			if (hoeCooldown.matchesItem(itemStack.getItem(), null)) {
				cooldown = hoeCooldown.cooldown;
				break;
			}
		}
		if (this.hoesDamageOnUseMultiplier > 1)
			itemStack.hurtAndBreak(this.hoesDamageOnUseMultiplier - 1, event.getPlayer(), (player) -> player.broadcastBreakEvent(event.getPlayer().getUsedItemHand()));
		if (cooldown != 0)
			event.getPlayer().getCooldowns().addCooldown(itemStack.getItem(), cooldown);
	}

	private static final ResourceLocation DISABLED_HOES = new ResourceLocation(IguanaTweaksReborn.MOD_ID, "disabled_hoes");

	private static boolean isHoeDisabled(Item item) {
		TagKey<Item> tagKey = TagKey.create(Registry.ITEM_REGISTRY, DISABLED_HOES);
		ITag<Item> itemTag = ForgeRegistries.ITEMS.tags().getTag(tagKey);
		return itemTag.contains(item);
	}
}