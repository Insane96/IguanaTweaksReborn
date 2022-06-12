package insane96mcp.iguanatweaksreborn.module.misc.feature;

import insane96mcp.iguanatweaksreborn.module.misc.utils.DeBuff;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.util.MCUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "DeBuffs", description = "Apply potion effects on certain hunger / health / experience level")
public class DeBuffs extends Feature {
	private final ForgeConfigSpec.ConfigValue<List<? extends String>> deBuffsConfig;

	private final List<String> deBuffsDefault = Arrays.asList("HUNGER,..2,minecraft:mining_fatigue,0", "HUNGER,..4,minecraft:slowness,0", "HEALTH,..3,minecraft:slowness,0");

	public ArrayList<DeBuff> deBuffs;

	public DeBuffs(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		deBuffsConfig = Config.builder
				.comment("A list of DeBuffs to apply to the player when has on low hunger / health / experience level. Each string must be 'stat,range,status_effect,amplifier', where stat MUST BE one of the following: HUNGER, HEALTH, EXPERIENCE_LEVEL; range must be a range for the statistic like it's done in commands.\n" +
						"'10' When the player has exactly ten of the specified stat.\n" +
						"'10..12' When the player has between 10 and 12 (inclusive) of the specified stat.\n" +
						"'5..' When the player has five or greater of the specified stat.\n" +
						"'..15' When the player has 15 or less of the specified stat.\n" +
						"effect must be a potion id, e.g. minecraft:weakness\n" +
						"amplifier must be the potion level starting from 0 (0 = level I)\n" +
						"")
				.defineList("DeBuffs", deBuffsDefault, o -> o instanceof String);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		deBuffs = (ArrayList<DeBuff>) DeBuff.parseStringList(deBuffsConfig.get());
	}

	@SubscribeEvent
	public void deBuffsOnLowStats(TickEvent.PlayerTickEvent event) {
		if (!this.isEnabled())
			return;

		if (event.phase != TickEvent.Phase.START)
			return;

		if (deBuffs.isEmpty())
			return;

		if (event.player.level.isClientSide)
			return;

		ServerPlayer player = (ServerPlayer) event.player;

		if (!player.isAlive())
			return;

		if (player.tickCount % 20 != 0)
			return;

		for (DeBuff deBuff : deBuffs) {
			boolean pass = false;
			switch (deBuff.stat) {
				case HEALTH:
					if (player.getHealth() <= deBuff.max && player.getHealth() >= deBuff.min)
						pass = true;
					break;

				case HUNGER:
					if (player.getFoodData().getFoodLevel() <= deBuff.max && player.getFoodData().getFoodLevel() >= deBuff.min)
						pass = true;
					break;

				case EXPERIENCE_LEVEL:
					if (player.experienceLevel <= deBuff.max && player.experienceLevel >= deBuff.min)
						pass = true;
					break;
				default:
					break;
			}
			if (pass) {
				MobEffectInstance effectInstance = MCUtils.createEffectInstance(deBuff.effect, 30, deBuff.amplifier, true, true, false, false);
				player.addEffect(effectInstance);
			}
		}
	}
}