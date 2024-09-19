package insane96mcp.iguanatweaksreborn.module.client;

import insane96mcp.iguanatweaksreborn.module.ClientModules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.LoadFeature;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Misc", description = "Misc client side changes")
@LoadFeature(module = ClientModules.Ids.CLIENT)
public class Misc extends Feature {

    @Config(min = 0d, max = 1d)
    @Label(name = "World Border Transparency", description = "Multiplies the world border transparency by this value")
    public static Double worldBorderTransparency = 0.4d;

    @Config
    @Label(name = "Shorter world border", description = "If true, the world border height is reduced by 4 times.")
    public static Boolean shorterWorldBorder = true;

    @Config(min = 1d)
    @Label(name = "Cap world border height", description = "Set the max height of the world border.")
    public static Double capWorldBorderHeight = 128d;

    @Config
    @Label(name = "No tilting with some damage types", description = "If true, camera will not tilt when taking magic, wither, on fire, cramming, drowning and thorns damage.")
    public static Boolean noTiltingWithSomeDamageTypes = true;

    @Config
    @Label(name = "Red block outline with wrong tool", description = "If true, the outline around blocks will be red if the tool in hand will make drops not ... drop.")
    public static Boolean redBlockOutlineWithWrongTool = true;

    @Config
    @Label(name = "Thrid person on death", description = "If true, when you die, you switch to third person camera.")
    public static Boolean thirdPersonOnDeath = true;

    public Misc(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    public static float getWorldBorderTransparencyMultiplier() {
        if (isEnabled(Misc.class))
            return worldBorderTransparency.floatValue();
        return 1f;
    }

    public static boolean shouldShortenWorldBorder() {
        return isEnabled(Misc.class) && shorterWorldBorder;
    }

    public static boolean shouldDisableTiltingWithSomeDamageTypes() {
        return isEnabled(Misc.class) && noTiltingWithSomeDamageTypes;
    }

    public static float getRedOutlineAmount(float original) {
        if (!isEnabled(Misc.class) || !redBlockOutlineWithWrongTool)
            return original;
        return 0.42f;
    }

    public static boolean dead = false;
    public static void onDeath() {
        if (!Feature.isEnabled(Misc.class)
                || !thirdPersonOnDeath)
            return;

        CameraType cameratype = Minecraft.getInstance().options.getCameraType();
        if (cameratype != CameraType.FIRST_PERSON)
            return;
        Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
        dead = true;
    }

    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerRespawnEvent event) {

    }
}
