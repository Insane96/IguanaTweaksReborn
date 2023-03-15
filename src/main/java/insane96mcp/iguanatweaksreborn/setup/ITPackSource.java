package insane96mcp.iguanatweaksreborn.setup;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.PackSource;

import java.util.function.UnaryOperator;

public interface ITPackSource {
    UnaryOperator<Component> NO_DECORATION = UnaryOperator.identity();
    PackSource DISABLED = create(NO_DECORATION, false);

    static PackSource create(final UnaryOperator<Component> componentUnaryOperator, final boolean shouldAddAutomatically) {
        return new PackSource() {
            public Component decorate(Component component) {
                return componentUnaryOperator.apply(component);
            }

            public boolean shouldAddAutomatically() {
                return shouldAddAutomatically;
            }
        };
    }

    private static UnaryOperator<Component> decorateWithSource(String translatable) {
        Component component = Component.translatable(translatable);
        return (comp) -> Component.translatable("pack.nameAndSource", comp, component).withStyle(ChatFormatting.GRAY);
    }
}
