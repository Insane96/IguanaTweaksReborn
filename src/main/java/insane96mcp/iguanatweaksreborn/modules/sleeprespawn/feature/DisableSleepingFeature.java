package insane96mcp.iguanatweaksreborn.modules.sleeprespawn.feature;

import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.base.ITModule;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DisableSleepingFeature extends ITFeature {

    private final ForgeConfigSpec.ConfigValue<Boolean> disableBedSpawnConfig;

    public boolean disableBedSpawn = false;

    public DisableSleepingFeature(ITModule module) {
        super("Disable Sleeping", "Makes sleeping impossible while begin able to set (or not) the spawn point", module, false);
        
        Config.builder.comment(this.getDescription()).push(this.getName());
        disableBedSpawnConfig = Config.builder
                .comment("If set to true the player spawn point will not change when the player cannot sleep. Has no effect if the player can sleep.")
                .define("Disable Bed Spawn", this.disableBedSpawn);
        Config.builder.pop();
    }

    @SubscribeEvent
    public void disableSleeping(PlayerSleepInBedEvent event) {
        if (!this.isModuleEnabled())
            return;
        if (!this.isEnabled())
            return;

        if (event.getPlayer().world.isRemote)
            return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();

        event.setResult(PlayerEntity.SleepResult.OTHER_PROBLEM);

        if (this.disableBedSpawn) {
            player.sendStatusMessage(new TranslationTextComponent(Strings.Translatable.DECORATIVE_BEDS), true);
        }
        else {
            player.sendStatusMessage(new TranslationTextComponent(Strings.Translatable.ENJOY_THE_NIGHT), false);
            //ServerPlayerEntity#setPlayerSpawn
            player.func_242111_a(player.world.getDimensionKey(), event.getPos(), player.rotationYaw, false, false);
        }
    }
}
