package insane96mcp.iguanatweaksreborn.base;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.lang.reflect.Method;

public class ITFeature {
    private final String name;
    private final String description;
    private final ForgeConfigSpec.ConfigValue<Boolean> enabledConfig;
    private final ITModule module;

    private boolean enabled;

    public ITFeature(ITModule module, boolean enabledByDefault) {
        if (!this.getClass().isAnnotationPresent(Label.class))
            LogHelper.Error(String.format("%s is missing the Label Annotation.", this.getClass().getName()));

        this.name = this.getClass().getAnnotation(Label.class).name();
        this.description = this.getClass().getAnnotation(Label.class).name();

        this.module = module;

        enabledConfig = Config.builder.comment(getDescription()).define("Enable " + getName(), enabledByDefault);

        this.registerEvents();
    }

    public ITFeature(ITModule module) {
        this(module, true);
    }

    /**
     * @return true if both the feature and the module are enabled
     */
    public boolean isEnabled() {
        return enabled && this.isModuleEnabled();
    }

    public boolean isModuleEnabled() {
        return this.module.isEnabled();
    }

    /*public ITModule getModule() {
        return module;
    }*/

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void loadConfig() {
        this.enabled = enabledConfig.get();
    }

    public void registerEvents() {
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(SubscribeEvent.class))
                continue;

            MinecraftForge.EVENT_BUS.register(this);
        }
    }

}
