package insane96mcp.iguanatweaksreborn.mixin.plugin;

import com.google.common.collect.ImmutableMap;
import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class ModLoadedPlugin implements IMixinConfigPlugin {
    private static final Map<String, Supplier<Boolean>> CONDITIONS = ImmutableMap.of(
            "insane96mcp.iguanatweaksreborn.mixin.SpongeBlockMixin", () -> LoadingModList.get().getModFileById("quark") == null,
            "insane96mcp.iguanatweaksreborn.mixin.SpringFeatureMixin", () -> LoadingModList.get().getModFileById("quark") == null,
            "insane96mcp.iguanatweaksreborn.mixin.PistonMovingBlockEntityMixin", () -> LoadingModList.get().getModFileById("quark") == null,
            "insane96mcp.iguanatweaksreborn.mixin.PistonBaseBlockMixin", () -> LoadingModList.get().getModFileById("quark") == null,
            "insane96mcp.iguanatweaksreborn.mixin.FallingBlockEntityMixin", () -> LoadingModList.get().getModFileById("quark") == null,
            "insane96mcp.iguanatweaksreborn.mixin.BoatMixin", () -> LoadingModList.get().getModFileById("quark") == null
    );
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return CONDITIONS.getOrDefault(mixinClassName, () -> true).get();
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
