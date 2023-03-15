package insane96mcp.survivalreimagined.utils;

import com.google.gson.annotations.SerializedName;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import javax.annotation.Nullable;

public enum AttributeModifierOperation {
    @SerializedName("addition")
    ADDITION(AttributeModifier.Operation.ADDITION, "addition"),
    @SerializedName("multiply_base")
    MULTIPLY_BASE(AttributeModifier.Operation.MULTIPLY_BASE, "multiply_base"),
    @SerializedName("multiply_total")
    MULTIPLY_TOTAL(AttributeModifier.Operation.MULTIPLY_TOTAL, "multiply_total");

    final AttributeModifier.Operation operation;
    public AttributeModifier.Operation get() {
        return this.operation;
    }

    final String serializedName;

    AttributeModifierOperation(AttributeModifier.Operation operation, String serializedName) {
        this.operation = operation;
        this.serializedName = serializedName;
    }

    @Nullable
    public static String getNameFromOperation(AttributeModifier.Operation operation) {
        for (AttributeModifierOperation amo : AttributeModifierOperation.values()) {
            if (amo.operation == operation)
                return amo.serializedName;
        }
        return null;
    }
}
