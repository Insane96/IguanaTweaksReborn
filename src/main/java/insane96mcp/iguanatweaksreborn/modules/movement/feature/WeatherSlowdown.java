package insane96mcp.iguanatweaksreborn.modules.movement.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Weather Slowdown", description = "Different weathers slowdown the player more.")
public class WeatherSlowdown extends Feature {

	private final ForgeConfigSpec.ConfigValue<Double> slowndownInClearConfig;
	private final ForgeConfigSpec.ConfigValue<Double> slowndownInRainConfig;
	private final ForgeConfigSpec.ConfigValue<Double> slowndownInThunderConfig;

	public double slowdownInClear = 0d;
	public double slowdownInRain = 0d;
	public double slowdownInThunder = 0d;

	public WeatherSlowdown(Module module) {
		super(Config.builder, module, false);
		Config.builder.comment(this.getDescription()).push(this.getName());
		slowndownInClearConfig = Config.builder
				.comment("Percentage slowdown when the weather is clear and the player is outside.")
				.defineInRange("Slowdown in Clear", this.slowdownInClear, 0d, 1d);
		slowndownInRainConfig = Config.builder
				.comment("Percentage slowdown when the player's in rain.")
				.defineInRange("Slowdown in Rain", this.slowdownInRain, 0d, 1d);
		slowndownInThunderConfig = Config.builder
				.comment("Percentage slowdown when the player's in a thunderstorm.")
				.defineInRange("Slowdown in Thunder", this.slowdownInThunder, 0d, 1d);
		Config.builder.pop();
	}

    @Override
    public void loadConfig() {
        super.loadConfig();
        this.slowdownInClear = this.slowndownInClearConfig.get();
        this.slowdownInRain = this.slowndownInRainConfig.get();
        this.slowdownInThunder = this.slowndownInThunderConfig.get();
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        PlayerEntity playerEntity = event.player;
        ModifiableAttributeInstance movementSpeed = playerEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed == null)
            return;
		AttributeModifier modifier = movementSpeed.getModifier(Strings.AttributeModifiers.WEATHER_SLOWDOWN_UUID);
		if (!this.isEnabled()) {
			//If the feature has been disabled remove the slowdown from the player
			if (modifier != null)
				movementSpeed.removeModifier(modifier);
			return;
		}

		if (!playerEntity.world.canSeeSky(playerEntity.getPosition()))
			return;

		double slowdown = this.slowdownInClear;
		if (playerEntity.world.isRaining())
			slowdown = this.slowdownInRain;
		if (playerEntity.world.isThundering())
			slowdown = this.slowdownInThunder;

		//If it's 0 then there's no slowdown appliable
		if (slowdown == 0d) {
			if (modifier != null)
				movementSpeed.removeModifier(modifier);
			return;
		}
		if (modifier == null || modifier.getAmount() != slowdown) {
			modifier = new AttributeModifier(Strings.AttributeModifiers.WEATHER_SLOWDOWN_UUID, Strings.AttributeModifiers.WEATHER_SLOWDOWN, slowdown, AttributeModifier.Operation.MULTIPLY_BASE);
			movementSpeed.removeModifier(Strings.AttributeModifiers.WEATHER_SLOWDOWN_UUID);
			movementSpeed.applyNonPersistentModifier(modifier);
		}
	}
}
