package insane96mcp.iguanatweaksreborn.modules.farming.feature;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.modules.farming.classutils.HoeCooldown;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Hoes Nerfs", description = "Slower Hoes and more fragile")
public class HoesNerfsFeature extends Feature {

	private final ForgeConfigSpec.ConfigValue<List<? extends String>> hoesCooldownsConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> disableLowTierHoesConfig;
	private final ForgeConfigSpec.ConfigValue<Integer> hoesDamageOnUseMultiplierConfig;

	private static final List<String> hoesCooldownsDefault = Arrays.asList("minecraft:stone_hoe,20", "minecraft:iron_hoe,15", "minecraft:golden_hoe,4", "minecraft:diamond_hoe,10", "minecraft:netherite_hoe,6", "vulcanite:vulcanite_hoe,15");

	public ArrayList<HoeCooldown> hoesCooldowns;
	public boolean disableLowTierHoes = true;
	public int hoesDamageOnUseMultiplier = 3;

	public HoesNerfsFeature(Module module) {
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
	//TODO Replace with PlayerInteractEvent.RightClickBlock
	public void disabledHoes(BlockEvent.BlockToolInteractEvent event) {
		if (!this.isEnabled())
			return;
		if (event.getPlayer().world.isRemote)
			return;
		if (!this.disableLowTierHoes)
			return;
		if (!isHoeDisabled(event.getHeldItemStack().getItem()))
			return;
		ItemStack hoe = event.getHeldItemStack();
		hoe.damageItem(hoe.getMaxDamage() - 1, event.getPlayer(), (player) -> player.sendBreakAnimation(event.getPlayer().getActiveHand()));
		event.getPlayer().sendStatusMessage(new StringTextComponent("This hoe is too weak to be used"), true);
		event.setCanceled(true);
	}

	@SubscribeEvent
	public void harderTilling(BlockEvent.BlockToolInteractEvent event) {
		if (!this.isEnabled())
			return;
		if (event.getPlayer().world.isRemote)
			return;
		if (isHoeDisabled(event.getHeldItemStack().getItem()) && disableLowTierHoes)
			return;
		if (/*event.getFace() == Direction.DOWN || */!event.getWorld().isAirBlock(event.getPos().up()))
			return;
		BlockState blockstate = HoeItem.HOE_LOOKUP.get(event.getWorld().getBlockState(event.getPos()).getBlock());
		if (blockstate == null || blockstate.getBlock() != Blocks.FARMLAND)
			return;
		ItemStack stack = event.getHeldItemStack();
		if (!stack.getToolTypes().contains(ToolType.HOE))
			return;
		if (event.getPlayer().getCooldownTracker().hasCooldown(stack.getItem()))
			return;
		int cooldown = 0;
		for (HoeCooldown hoeCooldown : this.hoesCooldowns) {
			if (hoeCooldown.matchesItem(stack.getItem(), null)) {
				cooldown = hoeCooldown.cooldown;
				break;
			}
		}
		if (this.hoesDamageOnUseMultiplier > 1)
			stack.damageItem(this.hoesDamageOnUseMultiplier - 1, event.getPlayer(), (player) -> player.sendBreakAnimation(event.getPlayer().getActiveHand()));
		if (cooldown != 0)
			event.getPlayer().getCooldownTracker().setCooldown(stack.getItem(), cooldown);
	}

	private static boolean isHoeDisabled(Item item) {
		return item.getTags().contains(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "disabled_hoes"));
	}
}
