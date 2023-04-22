package insane96mcp.survivalreimagined.module.combat.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.shieldsplus.setup.SPItems;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.List;
import java.util.function.Supplier;

@Label(name = "Gold Absorption", description = "Players with Gold armor and shield will be granted 1 regenerating heart of absorption.")
@LoadFeature(module = Modules.Ids.COMBAT)
public class GoldenAbsorption extends Feature {

	private static final String GOLDEN_ABSORPTION = SurvivalReimagined.RESOURCE_PREFIX + "golden_absorption";

	@Config(min = 0)
	@Label(name = "Absorption per golden piece")
	public static Double absorptionPerGoldenPiece = 2d;
	@Config(min = 0)
	@Label(name = "Regen Speed", description = "Speed (in ticks) at which Absorption hearts regenerate")
	public static Integer regenSpeed = 200;
	@Config(min = 0)
	@Label(name = "Absorption decay", description = "Speed (in ticks) at which Absorption hearts decay")
	public static Integer absorptionDecay = 20;
	@Config
	@Label(name = "Cap to health", description = "The amount of absorption hearts cannot go over the player's current health.")
	public static Boolean capToHealth = true;

	public GoldenAbsorption(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	public static Supplier<List<Item>> VALID_GOLD_ARMOR_ITEMS = () -> List.of(
			Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS
	);

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (!this.isEnabled()
				|| event.phase == TickEvent.Phase.START
				|| event.side == LogicalSide.CLIENT)
			return;

		Player player = event.player;

		float goldenAbsorption = player.getPersistentData().getInt(GOLDEN_ABSORPTION);
		if (player.tickCount % 20 == 5) {
			goldenAbsorption = updateGoldenAbsorption(player);
		}

		//Take into account absorption effect
		int absorptionAmplifier = 0;
		if (player.hasEffect(MobEffects.ABSORPTION)) {
			absorptionAmplifier = player.getEffect(MobEffects.ABSORPTION).getAmplifier() + 1;
		}

		float actualGoldenAbsorption = player.getAbsorptionAmount() - (absorptionAmplifier * 4);

		if (actualGoldenAbsorption != goldenAbsorption) {
			if (actualGoldenAbsorption > goldenAbsorption && player.tickCount % absorptionDecay == 0) {
				player.setAbsorptionAmount(player.getAbsorptionAmount() - 1);
			} else if (player.tickCount % regenSpeed == 0) {
				float newAbsorption = Math.min(player.getAbsorptionAmount() + 1, player.getPersistentData().getInt(GOLDEN_ABSORPTION) + (absorptionAmplifier * 4));
				if (capToHealth)
					newAbsorption = Math.min(newAbsorption, player.getHealth() + (absorptionAmplifier * 4));
				player.setAbsorptionAmount(newAbsorption);
			}
		}
	}

	private static int updateGoldenAbsorption(Player player) {
		MutableInt absorptionHearts = new MutableInt();
		player.getArmorSlots().forEach(itemStack -> {
			for (Item item : VALID_GOLD_ARMOR_ITEMS.get()) {
				if (itemStack.is(item)) {
					absorptionHearts.add(absorptionPerGoldenPiece);
					break;
				}
			}
		});
		if (player.getOffhandItem().is(SPItems.GOLDEN_SHIELD.get()))
			absorptionHearts.add(absorptionPerGoldenPiece);

		player.getPersistentData().putInt(GOLDEN_ABSORPTION, absorptionHearts.intValue());
		return absorptionHearts.intValue();
	}
}