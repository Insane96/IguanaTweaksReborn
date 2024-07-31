package insane96mcp.iguanatweaksreborn.module.items.itemstats;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.iguanatweaksreborn.data.AttributeModifierOperation;
import insane96mcp.iguanatweaksreborn.module.combat.RegeneratingAbsorption;
import insane96mcp.iguanatweaksreborn.utils.ITRGsonHelper;
import insane96mcp.insanelib.base.JsonFeature;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

//TODO 1.21: rename to Item Data
@JsonAdapter(ItemStatistics.Serializer.class)
public final class ItemStatistics {
	private final IdTagMatcher item;
	@Nullable
	private final Integer maxStackSize;
	private final Durability durability;
	@Nullable
	private final Double efficiency;
	@Nullable
	private final Integer enchantability;
	@Nullable
	private final Double baseAttackDamage;
	@Nullable
	private final Double baseAttackSpeed;
	@Nullable
	private final Double baseArmor;
	@Nullable
	private final Double baseArmorToughness;
	@Nullable
	private final Double baseKnockbackResistance;
	@Nullable
	private final Double baseRegeneratingAbsorption;
	@Nullable
	private final Double baseRegenAbsorptionSpeed;
	@Nullable
	private final Double movementSpeedPenalty;
	@Nullable
	private final List<SerializableAttributeModifier> modifiers;
    public ItemStatistics(@NotNull IdTagMatcher item, @Nullable Integer maxStackSize, @Nullable Integer durability, @Nullable Float durabilityMultiplier, @Nullable Double efficiency, @Nullable Integer enchantability, @Nullable Double baseAttackDamage, @Nullable Double baseAttackSpeed, @Nullable Double baseArmor, @Nullable Double baseArmorToughness, @Nullable Double baseKnockbackResistance, @Nullable Double baseRegeneratingAbsorption, @Nullable Double baseRegenAbsorptionSpeed, @Nullable Double movementSpeedPenalty, @Nullable List<SerializableAttributeModifier> modifiers) {
        this.item = item;
        this.maxStackSize = maxStackSize;
        this.durability = new Durability(durability, null, durabilityMultiplier);
        this.efficiency = efficiency;
        this.enchantability = enchantability;
        this.baseAttackDamage = baseAttackDamage;
        this.baseAttackSpeed = baseAttackSpeed;
        this.baseArmor = baseArmor;
        this.baseArmorToughness = baseArmorToughness;
        this.baseKnockbackResistance = baseKnockbackResistance;
        this.baseRegeneratingAbsorption = baseRegeneratingAbsorption;
        this.baseRegenAbsorptionSpeed = baseRegenAbsorptionSpeed;
        this.movementSpeedPenalty = movementSpeedPenalty;
        this.modifiers = modifiers;
    }

    public void applyStats(boolean isClientSide) {
        List<Item> items = JsonFeature.getAllItems(this.item, isClientSide);
        for (Item item : items) {
            Durability durability = new Durability(null, null, null);
            if (!ItemStatsReloadListener.Durability.containsKey(item))
                ItemStatsReloadListener.Durability.put(item, durability);
            else
                durability = ItemStatsReloadListener.Durability.get(item);
            if (this.durability.durability != null)
                durability.durability = this.durability.durability;
            if (this.durability.durabilityBonus != null)
                durability.durabilityBonus = this.durability.durabilityBonus;
            if (this.durability.durabilityMultiplier != null)
                durability.durabilityMultiplier = this.durability.durabilityMultiplier;

            if (this.efficiency != null && item instanceof DiggerItem diggerItem)
                diggerItem.speed = this.efficiency.floatValue();
            if (this.maxStackSize != null)
                item.maxStackSize = this.maxStackSize;
        }
    }

    public static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    public static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    private static final EnumMap<ArmorItem.Type, UUID> ARMOR_MODIFIER_UUID_PER_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), (enumMap) -> {
        enumMap.put(ArmorItem.Type.BOOTS, UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"));
        enumMap.put(ArmorItem.Type.LEGGINGS, UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"));
        enumMap.put(ArmorItem.Type.CHESTPLATE, UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"));
        enumMap.put(ArmorItem.Type.HELMET, UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"));
    });

    public void applyAttributes(ItemAttributeModifierEvent event, ItemStack stack, Multimap<Attribute, AttributeModifier> modifiers) {
        if (!this.item.matchesItem(stack))
            return;
        Multimap<Attribute, AttributeModifier> toAdd = HashMultimap.create();
        Multimap<Attribute, AttributeModifier> toRemove = HashMultimap.create();
        for (var entry : modifiers.entries()) {
            if (this.baseAttackDamage != null && event.getSlotType() == EquipmentSlot.MAINHAND) {
                double materialAd = 0d;
                if (stack.getItem() instanceof TieredItem tieredItem)
                    materialAd = tieredItem.getTier().getAttackDamageBonus();
                if (this.baseAttackDamage + materialAd > 0d) {
                    toAdd.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", this.baseAttackDamage + materialAd, AttributeModifier.Operation.ADDITION));
                }
                if (entry.getValue().getId().equals(BASE_ATTACK_DAMAGE_UUID) && entry.getKey().equals(Attributes.ATTACK_DAMAGE))
                    toRemove.put(entry.getKey(), entry.getValue());
            }
            if (this.baseAttackSpeed != null && event.getSlotType() == EquipmentSlot.MAINHAND) {
                if (this.baseAttackSpeed > 0d)
                    toAdd.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -(4d - this.baseAttackSpeed), AttributeModifier.Operation.ADDITION));
                if (entry.getValue().getId().equals(BASE_ATTACK_SPEED_UUID) && entry.getKey().equals(Attributes.ATTACK_SPEED))
                    toRemove.put(entry.getKey(), entry.getValue());
            }
            if (this.baseArmor != null && stack.getItem() instanceof ArmorItem armorItem) {
                if (this.baseArmor > 0d)
                    toAdd.put(Attributes.ARMOR, new AttributeModifier(ARMOR_MODIFIER_UUID_PER_TYPE.get(armorItem.getType()), "Armor modifier", this.baseArmor, AttributeModifier.Operation.ADDITION));
                if (entry.getValue().getId().equals(ARMOR_MODIFIER_UUID_PER_TYPE.get(armorItem.getType())) && entry.getKey().equals(Attributes.ARMOR))
                    toRemove.put(entry.getKey(), entry.getValue());
            }
            if (this.baseArmorToughness != null && stack.getItem() instanceof ArmorItem armorItem) {
                if (this.baseArmorToughness > 0d)
                    toAdd.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ARMOR_MODIFIER_UUID_PER_TYPE.get(armorItem.getType()), "Armor toughness", this.baseArmorToughness, AttributeModifier.Operation.ADDITION));
                if (entry.getValue().getId().equals(ARMOR_MODIFIER_UUID_PER_TYPE.get(armorItem.getType())) && entry.getKey().equals(Attributes.ARMOR_TOUGHNESS))
                    toRemove.put(entry.getKey(), entry.getValue());
            }
            if (this.baseKnockbackResistance != null && stack.getItem() instanceof ArmorItem armorItem) {
                if (this.baseKnockbackResistance > 0d)
                    toAdd.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ARMOR_MODIFIER_UUID_PER_TYPE.get(armorItem.getType()), "Armor knockback resistance", this.baseKnockbackResistance, AttributeModifier.Operation.ADDITION));
                if (entry.getValue().getId().equals(ARMOR_MODIFIER_UUID_PER_TYPE.get(armorItem.getType())) && entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE))
                    toRemove.put(entry.getKey(), entry.getValue());
            }
            if (this.baseRegeneratingAbsorption != null && stack.getItem() instanceof ArmorItem armorItem) {
                if (this.baseRegeneratingAbsorption > 0d)
                    toAdd.put(RegeneratingAbsorption.ATTRIBUTE.get(), new AttributeModifier(ARMOR_MODIFIER_UUID_PER_TYPE.get(armorItem.getType()), "Armor Regenerating Absorption", this.baseRegeneratingAbsorption, AttributeModifier.Operation.ADDITION));
                if (entry.getValue().getId().equals(ARMOR_MODIFIER_UUID_PER_TYPE.get(armorItem.getType())) && entry.getKey().equals(RegeneratingAbsorption.ATTRIBUTE.get()))
                    toRemove.put(entry.getKey(), entry.getValue());
            }
            if (this.baseRegenAbsorptionSpeed != null && stack.getItem() instanceof ArmorItem armorItem) {
                if (this.baseRegenAbsorptionSpeed > 0d)
                    toAdd.put(RegeneratingAbsorption.SPEED_ATTRIBUTE.get(), new AttributeModifier(ARMOR_MODIFIER_UUID_PER_TYPE.get(armorItem.getType()), "Armor Regenerating Absorption Speed", this.baseRegenAbsorptionSpeed, AttributeModifier.Operation.ADDITION));
                if (entry.getValue().getId().equals(ARMOR_MODIFIER_UUID_PER_TYPE.get(armorItem.getType())) && entry.getKey().equals(RegeneratingAbsorption.SPEED_ATTRIBUTE.get()))
                    toRemove.put(entry.getKey(), entry.getValue());
            }
            if (this.movementSpeedPenalty != null && stack.getItem() instanceof ArmorItem armorItem) {
                if (this.movementSpeedPenalty > 0d)
                    toAdd.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(ARMOR_MODIFIER_UUID_PER_TYPE.get(armorItem.getType()), "Armor Movement Speed Reduction", getSpeedReductionPerArmor(-this.movementSpeedPenalty, armorItem), AttributeModifier.Operation.MULTIPLY_BASE));
            }
        }

        //Try to remove original modifiers first
        if (event.getItemStack().is(ItemStats.REMOVE_ORIGINAL_MODIFIERS_TAG)) {
            Multimap<Attribute, AttributeModifier> originalModifiers = event.getOriginalModifiers();
            toRemove.putAll(originalModifiers);
        }

        if (this.modifiers != null) {
            for (SerializableAttributeModifier attributeModifier : this.modifiers) {
                if (event.getSlotType() != attributeModifier.slot)
                    continue;
                AttributeModifier modifier = new AttributeModifier(attributeModifier.uuid, attributeModifier.name, attributeModifier.amount, attributeModifier.operation);
                toAdd.put(attributeModifier.attribute.get(), modifier);
            }
        }
        toRemove.forEach(event::removeModifier);
        toAdd.forEach(event::addModifier);
    }

    private static double getSpeedReductionPerArmor(double totalReduction, ArmorItem item) {
        return switch (item.getEquipmentSlot()) {
            case HEAD -> totalReduction * 0.2d;
            case CHEST -> totalReduction * 0.35d;
            case LEGS -> totalReduction * 0.3d;
            default -> totalReduction * 0.15d;
        };
    }

    public static final Type LIST_TYPE = new TypeToken<ArrayList<ItemStatistics>>() {}.getType();

    public static class Serializer implements JsonDeserializer<ItemStatistics>, JsonSerializer<ItemStatistics> {
        @Override
        public ItemStatistics deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            IdTagMatcher item = context.deserialize(jObject.get("item"), IdTagMatcher.class);
            Integer maxStackSize = ITRGsonHelper.getAsNullableInt(jObject, "max_stack");
            Integer durability = ITRGsonHelper.getAsNullableInt(jObject, "durability");
            Float durabilityBonus = ITRGsonHelper.getAsNullableFloat(jObject, "durability_bonus");
            Float durabilityMultiplier = ITRGsonHelper.getAsNullableFloat(jObject, "durability_multiplier");
            Double efficiency = ITRGsonHelper.getAsNullableDouble(jObject, "efficiency");
            Integer enchantability = ITRGsonHelper.getAsNullableInt(jObject, "enchantability");
            Double baseAttackDamage = ITRGsonHelper.getAsNullableDouble(jObject, "attack_damage");
            Double baseAttackSpeed = ITRGsonHelper.getAsNullableDouble(jObject, "attack_speed");
            Double baseArmor = ITRGsonHelper.getAsNullableDouble(jObject, "armor");
            Double baseToughness = ITRGsonHelper.getAsNullableDouble(jObject, "armor_toughness");
            Double regeneratingAbsorption = ITRGsonHelper.getAsNullableDouble(jObject, "regenerating_absorption");
            Double regeneratingAbsorptionSpeed = ITRGsonHelper.getAsNullableDouble(jObject, "regenerating_absorption_speed");
            Double baseKnockbackResistance = ITRGsonHelper.getAsNullableDouble(jObject, "knockback_resistance");
            Double movementSpeedPenalty = ITRGsonHelper.getAsNullableDouble(jObject, "movement_speed_penalty");
            List<SerializableAttributeModifier> modifiers = null;
            if (jObject.has("modifiers"))
                modifiers = context.deserialize(jObject.get("modifiers"), SerializableAttributeModifier.LIST_TYPE);
            return new ItemStatistics(item, maxStackSize, durability, durabilityMultiplier, efficiency, enchantability, baseAttackDamage, baseAttackSpeed, baseArmor, baseToughness, baseKnockbackResistance, regeneratingAbsorption, regeneratingAbsorptionSpeed, movementSpeedPenalty, modifiers);
        }

        @Override
        public JsonElement serialize(ItemStatistics src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            JsonElement item = context.serialize(src.item);
            jObject.add("item", item);
            if (src.maxStackSize != null)
                jObject.addProperty("max_stack", src.maxStackSize);
            if (src.durability.durability != null)
                jObject.addProperty("durability", src.durability.durability);
            if (src.durability.durabilityBonus != null)
                jObject.addProperty("durability_bonus", src.durability.durabilityBonus);
            if (src.durability.durabilityMultiplier != null)
                jObject.addProperty("durability_multiplier", src.durability.durabilityMultiplier);
            if (src.efficiency != null)
                jObject.addProperty("efficiency", src.efficiency);
            if (src.enchantability != null)
                jObject.addProperty("enchantability", src.enchantability);
            if (src.baseAttackDamage != null)
                jObject.addProperty("attack_damage", src.baseAttackDamage);
            if (src.baseAttackSpeed != null)
                jObject.addProperty("attack_speed", src.baseAttackSpeed);
            if (src.baseArmor != null)
                jObject.addProperty("armor", src.baseArmor);
            if (src.baseArmorToughness != null)
                jObject.addProperty("armor_toughness", src.baseArmorToughness);
            if (src.baseKnockbackResistance != null)
                jObject.addProperty("knockback_resistance", src.baseKnockbackResistance);
            if (src.baseRegeneratingAbsorption != null)
                jObject.addProperty("regenerating_absorption", src.baseRegeneratingAbsorption);
            if (src.baseRegenAbsorptionSpeed != null)
                jObject.addProperty("regenerating_absorption_speed", src.baseRegenAbsorptionSpeed);
            if (src.movementSpeedPenalty != null)
                jObject.addProperty("movement_speed_penalty", src.movementSpeedPenalty);
            if (src.modifiers != null)
                jObject.add("modifiers", context.serialize(src.modifiers, SerializableAttributeModifier.LIST_TYPE));
            return jObject;
        }
    }

    public static ItemStatistics fromNetwork(FriendlyByteBuf byteBuf) {
        String utf = byteBuf.readUtf();
        IdTagMatcher item = IdTagMatcher.parseLine(utf);
        if (item == null)
            throw new NullPointerException("Parsing item from %s for Item Statistics returned null".formatted(utf));
        Integer maxStackSize = byteBuf.readNullable(FriendlyByteBuf::readInt);
        Integer durability = byteBuf.readNullable(FriendlyByteBuf::readInt);
        Integer durabilityBonus = byteBuf.readNullable(FriendlyByteBuf::readInt);
        Float durabilityMultiplier = byteBuf.readNullable(FriendlyByteBuf::readFloat);
        Double efficiency = byteBuf.readNullable(FriendlyByteBuf::readDouble);
        Integer enchantability = byteBuf.readNullable(FriendlyByteBuf::readInt);
        Double baseAttackDamage = byteBuf.readNullable(FriendlyByteBuf::readDouble);
        Double baseAttackSpeed = byteBuf.readNullable(FriendlyByteBuf::readDouble);
        Double baseArmor = byteBuf.readNullable(FriendlyByteBuf::readDouble);
        Double baseToughness = byteBuf.readNullable(FriendlyByteBuf::readDouble);
        Double baseKnockbackResistance = byteBuf.readNullable(FriendlyByteBuf::readDouble);
        Double baseRegeneratingAbsorption = byteBuf.readNullable(FriendlyByteBuf::readDouble);
        Double baseRegeneratingAbsorptionSpeed = byteBuf.readNullable(FriendlyByteBuf::readDouble);
        Double movementSpeedPenalty = byteBuf.readNullable(FriendlyByteBuf::readDouble);
        boolean hasModifiers = byteBuf.readBoolean();
        List<SerializableAttributeModifier> modifiers = null;
        if (hasModifiers) {
            int size = byteBuf.readInt();
            modifiers = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                modifiers.add(SerializableAttributeModifier.fromNetwork(byteBuf));
            }
        }
        return new ItemStatistics(item, maxStackSize, durability, durabilityMultiplier, efficiency, enchantability, baseAttackDamage, baseAttackSpeed, baseArmor, baseToughness, baseKnockbackResistance, baseRegeneratingAbsorption, baseRegeneratingAbsorptionSpeed, movementSpeedPenalty, modifiers);
    }

    public void toNetwork(FriendlyByteBuf byteBuf) {
        byteBuf.writeUtf(this.item.getSerializedName());
        byteBuf.writeNullable(this.maxStackSize, FriendlyByteBuf::writeInt);
        byteBuf.writeNullable(this.durability.durability, FriendlyByteBuf::writeInt);
        byteBuf.writeNullable(this.durability.durabilityBonus, FriendlyByteBuf::writeInt);
        byteBuf.writeNullable(this.durability.durabilityMultiplier, FriendlyByteBuf::writeFloat);
        byteBuf.writeNullable(this.efficiency, FriendlyByteBuf::writeDouble);
        byteBuf.writeNullable(this.enchantability, FriendlyByteBuf::writeInt);
        byteBuf.writeNullable(this.baseAttackDamage, FriendlyByteBuf::writeDouble);
        byteBuf.writeNullable(this.baseAttackSpeed, FriendlyByteBuf::writeDouble);
        byteBuf.writeNullable(this.baseArmor, FriendlyByteBuf::writeDouble);
        byteBuf.writeNullable(this.baseArmorToughness, FriendlyByteBuf::writeDouble);
        byteBuf.writeNullable(this.baseKnockbackResistance, FriendlyByteBuf::writeDouble);
        byteBuf.writeNullable(this.baseRegeneratingAbsorption, FriendlyByteBuf::writeDouble);
        byteBuf.writeNullable(this.baseRegenAbsorptionSpeed, FriendlyByteBuf::writeDouble);
        byteBuf.writeNullable(this.movementSpeedPenalty, FriendlyByteBuf::writeDouble);
        if (this.modifiers != null) {
            byteBuf.writeBoolean(true);
            byteBuf.writeInt(this.modifiers.size());
            for (SerializableAttributeModifier attributeModifier : this.modifiers) {
                attributeModifier.toNetwork(byteBuf);
            }
        }
        else {
            byteBuf.writeBoolean(false);
        }
    }

    public IdTagMatcher item() {
        return item;
    }

    @Nullable
    public Integer maxStackSize() {
        return maxStackSize;
    }

    public Durability durability() {
        return durability;
    }

    @Nullable
    public Double efficiency() {
        return efficiency;
    }

    @Nullable
    public Integer enchantability() {
        return enchantability;
    }

    @Nullable
    public Double baseAttackDamage() {
        return baseAttackDamage;
    }

    @Nullable
    public Double baseAttackSpeed() {
        return baseAttackSpeed;
    }

    @Nullable
    public Double baseArmor() {
        return baseArmor;
    }

    @Nullable
    public Double baseArmorToughness() {
        return baseArmorToughness;
    }

    @Nullable
    public Double baseKnockbackResistance() {
        return baseKnockbackResistance;
    }

    @Nullable
    public Double baseRegeneratingAbsorption() {
        return baseRegeneratingAbsorption;
    }

    @Nullable
    public Double baseRegenAbsorptionSpeed() {
        return baseRegenAbsorptionSpeed;
    }

    @Nullable
    public Double movementSpeedPenalty() {
        return movementSpeedPenalty;
    }

    @Nullable
    public List<SerializableAttributeModifier> modifiers() {
        return modifiers;
    }

    /**
     * @param attribute Use supplier due to default modifiers loading at runtime and modded attributes are not yet registered
     */
    @JsonAdapter(SerializableAttributeModifier.Serializer.class)
    public record SerializableAttributeModifier(UUID uuid, String name, EquipmentSlot slot,
                                                Supplier<Attribute> attribute, double amount,
                                                AttributeModifier.Operation operation) {

        public static final Type LIST_TYPE = new TypeToken<ArrayList<SerializableAttributeModifier>>() {}.getType();
        public static class Serializer implements JsonDeserializer<SerializableAttributeModifier>, JsonSerializer<SerializableAttributeModifier> {
            @Override
            public SerializableAttributeModifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jObject = json.getAsJsonObject();
                String sUUID = GsonHelper.getAsString(jObject, "uuid");
                UUID uuid;
                try {
                    uuid = UUID.fromString(sUUID);
                } catch (Exception ex) {
                    throw new JsonParseException("uuid %s is not valid".formatted(sUUID));
                }
                String name = GsonHelper.getAsString(jObject, "name");
                EquipmentSlot slot = EquipmentSlot.byName(GsonHelper.getAsString(jObject, "slot"));
                String sAttribute = GsonHelper.getAsString(jObject, "attribute");
                Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.tryParse(sAttribute));
                if (attribute == null) {
                    throw new JsonParseException("Invalid attribute: %s".formatted(sAttribute));
                }
                double amount = GsonHelper.getAsDouble(jObject, "amount");
                AttributeModifierOperation operation = context.deserialize(jObject.get("operation"), AttributeModifierOperation.class);
                return new SerializableAttributeModifier(uuid, name, slot, () -> attribute, amount, operation.get());
            }

            @Override
            public JsonElement serialize(SerializableAttributeModifier src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject jObject = new JsonObject();
                jObject.addProperty("uuid", src.uuid.toString());
                jObject.addProperty("name", src.name);
                jObject.addProperty("slot", src.slot.getName());
                jObject.addProperty("attribute", ForgeRegistries.ATTRIBUTES.getKey(src.attribute.get()).toString());
                jObject.addProperty("amount", src.amount);
                jObject.addProperty("operation", AttributeModifierOperation.getNameFromOperation(src.operation));
                return jObject;
            }
        }

        public static SerializableAttributeModifier fromNetwork(FriendlyByteBuf byteBuf) {
            UUID uuid = byteBuf.readUUID();
            String name = byteBuf.readUtf();
            EquipmentSlot slot = EquipmentSlot.byName(byteBuf.readUtf());
            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.tryParse(byteBuf.readUtf()));
            double amount = byteBuf.readDouble();
            AttributeModifierOperation operation = byteBuf.readEnum(AttributeModifierOperation.class);
            return new SerializableAttributeModifier(uuid, name, slot, () -> attribute, amount, operation.get());
        }

        public void toNetwork(FriendlyByteBuf byteBuf) {
            byteBuf.writeUUID(this.uuid);
            byteBuf.writeUtf(this.name);
            byteBuf.writeUtf(this.slot.getName());
            byteBuf.writeUtf(ForgeRegistries.ATTRIBUTES.getKey(this.attribute.get()).toString());
            byteBuf.writeDouble(this.amount);
            byteBuf.writeEnum(this.operation);
        }
    }

    public static final class Durability {
        @Nullable
        public Integer durability;
        @Nullable
        public Integer durabilityBonus;
        @Nullable
        public Float durabilityMultiplier;

        public Durability(@Nullable Integer durability, @Nullable Integer durabilityBonus, @Nullable Float durabilityMultiplier) {
            this.durability = durability;
            this.durabilityBonus = durabilityBonus;
            this.durabilityMultiplier = durabilityMultiplier;
        }

        public void apply(Item item) {
            if (ItemStatsReloadListener.OriginalDurability.containsKey(item))
                item.maxDamage = ItemStatsReloadListener.OriginalDurability.get(item);
            else
                ItemStatsReloadListener.OriginalDurability.put(item, item.maxDamage);
            if (this.durability != null)
                item.maxDamage = this.durability;
            if (this.durabilityBonus != null)
                item.maxDamage += this.durabilityBonus;
            if (this.durabilityMultiplier != null)
                item.maxDamage *= this.durabilityMultiplier;
        }


    }
}
