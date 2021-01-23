package insane96mcp.iguanatweaksreborn.base;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.setup.Config;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ITFeature {
    private final String name;
    private final String description;
    private ForgeConfigSpec.ConfigValue<Boolean> enabledConfig;
    private ITModule module;

    public ITFeature(String name, String description, ITModule module, boolean enabledByDefault) {
        this.name = name;
        this.description = description;
        this.module = module;

        enabledConfig = Config.builder.comment(getDescription()).define("Enable " + getName(), enabledByDefault);

        this.registerEvents();
    }

    public ITFeature(String name, String description, ITModule module) {
        this(name, description, module, true);
    }

    //public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public void loadCommonConfig() {
    }

    public boolean isEnabled() {
        return enabledConfig.get();
    }

    public boolean isModuleEnabled() {
        return this.module.isEnabled();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void loadConfig() {

    }

    public void registerEvents() {
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(SubscribeEvent.class))
                return;

            MinecraftForge.EVENT_BUS.register(this);
        }
    }

}
