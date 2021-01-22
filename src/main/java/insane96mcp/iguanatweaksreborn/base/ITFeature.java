package insane96mcp.iguanatweaksreborn.base;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static insane96mcp.iguanatweaksreborn.setup.Config.builder;

public class ITFeature {
    private final String name;
    private final String description;
    private ForgeConfigSpec.ConfigValue<Boolean> enabled;

    public ITFeature(String name, String description) {
        this.name = name;
        this.description = description;
        this.loadConfig();
        this.registerEvents();
    }

    //public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isEnabled() {
        return enabled.get();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void loadConfig() {
        enabled = builder.comment(getDescription()).define("Enable " + getName(), true);

        boolean hasConfig = false;
        for (Field field : this.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(ITConfig.class))
                continue;

            if (!hasConfig){
                builder.push(this.getName());
                hasConfig = true;
            }

            ITConfig config = field.getAnnotation(ITConfig.class);
            field.setAccessible(true);

            try {
                if (field.isAnnotationPresent(ConfigInt.class)) {
                    ConfigInt numeric = field.getAnnotation(ConfigInt.class);
                    field.set(null, builder
                            .comment(config.description())
                            .defineInRange(config.name(), (int)field.get(null), numeric.min(), numeric.max()).get()
                    );
                }
                else {
                    field.set(null, builder
                            .comment(config.description())
                            .define(config.name(), field.get(null)).get()
                    );
                }
            } catch (IllegalAccessException e) {
                IguanaTweaksReborn.LOGGER.error(e.getMessage());
            }
        }
    }

    public void registerEvents() {
        enabled = builder.comment(getDescription()).define("Enable " + getName(), true);

        for (Method method : this.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(SubscribeEvent.class))
                return;

            MinecraftForge.EVENT_BUS.register(this);
        }
    }

}
