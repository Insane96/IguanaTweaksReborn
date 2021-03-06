package insane96mcp.iguanatweaksreborn.modules.movement.feature;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.base.ITModule;
import insane96mcp.iguanatweaksreborn.base.Label;
import insane96mcp.iguanatweaksreborn.setup.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

@Label(name = "Weighted Armor", description = "Armor slows down the player based off the Armor and Toughness given.")
public class WeightedArmorFeature extends ITFeature {

    private final ForgeConfigSpec.ConfigValue<Double> slownessPerArmorConfig;
    private final ForgeConfigSpec.ConfigValue<Double> percentagePerToughnessConfig;

    public double slownessPerArmor = 2d;
    public double percentagePerToughness = 0.04d;

    public WeightedArmorFeature(ITModule module) {
        super(module);
        Config.builder.comment(this.getDescription()).push(this.getName());
        slownessPerArmorConfig = Config.builder
                .comment("Percentage slowdown per point of armor the player is wearing.")
                .defineInRange("Slowdown per Armor", slownessPerArmor, 0.0d, 128d);
        percentagePerToughnessConfig = Config.builder
                .comment("This value times the Armor Toughness worn by the player is a percentage increase of the Slowdown per Armor.\n" +
                        "Total percentage slowdown is '(slowness_per_armor * armor_points) * (1 + (toughness * percentage_per_toughness))'" +
                        "E.g. with 'Slowness per Armor' set to 2 and this set to 0.04 and the player wearing Diamond Armor the slowdown is '(2 * 20) * (1 + (8 * 0.04))' = '(2 * 20) + 32%' = '40 + 32%' ~= -53% Speed applied to the player")
                .defineInRange("Percentage Increase per Toughness", percentagePerToughness, 0.0d, 128.0d);
        Config.builder.pop();
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        this.slownessPerArmor = this.slownessPerArmorConfig.get();
        this.percentagePerToughness = this.percentagePerToughnessConfig.get();
    }

    private final UUID armorSlowdownModifierUUID = UUID.fromString("5a8c2add-015c-4b39-837c-3188a57fa3d6");

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        PlayerEntity playerEntity = event.player;
        //Tick the player only every 1/4 of a second
        //if (playerEntity.ticksExisted % 5 != 0)
        //return;
        ModifiableAttributeInstance movementSpeed = playerEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed == null)
            return;
        AttributeModifier modifier = movementSpeed.getModifier(armorSlowdownModifierUUID);
        if (!this.isEnabled()) {
            if (modifier != null)
                movementSpeed.removeModifier(modifier);
            return;
        }
        double armorSlowdown = playerEntity.getAttribute(Attributes.ARMOR).getValue();
        armorSlowdown *= this.slownessPerArmor;
        double toughnessSlowdown = playerEntity.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue();
        toughnessSlowdown *= this.percentagePerToughness;
        double slowdown = -(armorSlowdown * (1 + toughnessSlowdown)) / 100d;
        if (slowdown == 0d) {
            if (modifier != null)
                movementSpeed.removeModifier(modifier);
            return;
        }
        if (modifier == null || modifier.getAmount() != slowdown) {
            modifier = new AttributeModifier(armorSlowdownModifierUUID, IguanaTweaksReborn.RESOURCE_PREFIX + "armor_slowdown", slowdown, AttributeModifier.Operation.MULTIPLY_BASE);
            movementSpeed.removeModifier(armorSlowdownModifierUUID);
            movementSpeed.applyPersistentModifier(modifier);
        }
        playerEntity.jumpMovementFactor = (float) (0.02f * (1 + slowdown));
    }

    @SubscribeEvent
    public void debugScreen(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity playerEntity = mc.player;
        ModifiableAttributeInstance movementSpeed = playerEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed == null)
            return;
        AttributeModifier modifier = movementSpeed.getModifier(armorSlowdownModifierUUID);
        if (mc.gameSettings.showDebugInfo && modifier != null) {
            event.getLeft().add(String.format("Armor Slowdown: %.2f%%", Math.abs(modifier.getAmount()) * 100f));
        }
    }
}
