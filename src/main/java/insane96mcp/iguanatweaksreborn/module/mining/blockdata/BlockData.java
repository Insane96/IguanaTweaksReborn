package insane96mcp.iguanatweaksreborn.module.mining.blockdata;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.iguanatweaksreborn.utils.ITRGsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonAdapter(BlockData.Serializer.class)
public class BlockData {
	public Block block;

	private final List<BlockState> blockStates;
	@Nullable
	private final Float stateHardness;
	@Nullable
	private final Boolean stateRequiresCorrectToolForDrops;
	@Nullable
	private final NoteBlockInstrument stateNoteBlockInstrument;

	@Nullable
	public Float explosionResistance;
	/*
	 * Block
	 * soundType?
	 * friction
	 * speedFactor
	 * jumpFactor
	 * */

	public BlockData(Block block, List<BlockState> blockStates, @Nullable Float stateHardness, @Nullable Boolean stateRequiresCorrectToolForDrops, @Nullable NoteBlockInstrument stateNoteBlockInstrument, @Nullable Float explosionResistance) {
		this.block = block;
		this.blockStates = blockStates;
		this.stateHardness = stateHardness;
		this.stateRequiresCorrectToolForDrops = stateRequiresCorrectToolForDrops;
		this.stateNoteBlockInstrument = stateNoteBlockInstrument;
		this.explosionResistance = explosionResistance;
	}

	public void apply() {
		this.block.getStateDefinition().getPossibleStates().forEach(blockState -> {
			if (blockStates.isEmpty() || blockStates.contains(blockState)) {
				if (this.stateHardness != null)
					blockState.destroySpeed = this.stateHardness;
				if (this.stateRequiresCorrectToolForDrops != null)
					blockState.requiresCorrectToolForDrops = this.stateRequiresCorrectToolForDrops;
				if (this.stateNoteBlockInstrument != null)
					blockState.instrument = this.stateNoteBlockInstrument;
			}
		});
		if (this.explosionResistance != null)
			this.block.explosionResistance = this.explosionResistance;
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<BlockData>>(){}.getType();

	public static class Serializer implements JsonDeserializer<BlockData>, JsonSerializer<BlockData> {
		@Override
		public BlockData deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jObject = json.getAsJsonObject();
			ResourceLocation blockRL = ResourceLocation.tryParse(jObject.get("block").getAsString());
			if (blockRL == null)
				throw new JsonParseException("Failed to get block for %s".formatted(jObject.get("block").getAsString()));
			Block block = ForgeRegistries.BLOCKS.getValue(blockRL);
			if (block == null)
				throw new JsonParseException("Failed to get block for %s".formatted(jObject.get("block").getAsString()));
			List<BlockState> blockStates = new ArrayList<>();
			if (jObject.has("states")) {
				JsonArray array = jObject.getAsJsonArray("states");
				for (JsonElement element : array) {
					PropertiesAndValues propertyAndValues = PropertiesAndValues.of(block.getStateDefinition(), element.getAsString());
					block.getStateDefinition().getPossibleStates().forEach(blockState -> {
						if (propertyAndValues.match(blockState))
							blockStates.add(blockState);
					});
				}
			}
			Float hardness = ITRGsonHelper.getAsNullableFloat(jObject, "state_hardness");
			Boolean requiresCorrectToolForDrops = ITRGsonHelper.getAsNullableBoolean(jObject, "state_requires_correct_tool_for_drops");
			NoteBlockInstrument instrument = null;
			if (jObject.has("state_instrument")) {
				String stringInstrument = GsonHelper.getAsString(jObject, "state_instrument");
				instrument = Arrays.stream(NoteBlockInstrument.values())
						.filter(noteBlockInstrument -> noteBlockInstrument.getSerializedName().equals(stringInstrument))
						.findFirst()
						.orElseThrow();
			}
			Float explosionResistance = ITRGsonHelper.getAsNullableFloat(jObject, "explosion_resistance");
			return new BlockData(block, blockStates, hardness, requiresCorrectToolForDrops, instrument, explosionResistance);
		}

		@Override
		public JsonElement serialize(BlockData src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jObject = new JsonObject();
			/*if (!src.blockStates.isEmpty()) {
				JsonArray array = new JsonArray();
				for (BlockState state : src.blockStates) {
					array.add("%s=%s".formatted(property.property.getName(), property.value));
				}
			}*/
			if (src.stateHardness != null)
				jObject.addProperty("hardness", src.stateHardness);
			if (src.explosionResistance != null)
				jObject.addProperty("explosion_resistance", src.explosionResistance);
			return jObject;
		}
	}

	//Thanks Random832
	public record PropertyAndValue<T extends Comparable<T>>(Property<T> property, T value) {
		static <T extends Comparable<T>> PropertyAndValue<?> of(StateDefinition definition, String string) {
			String[] split = string.split("=", 2);
			Property<T> prop = (Property<T>) definition.getProperty(split[0]);
			T value = prop.getValue(split[1]).orElseThrow();
			return new PropertyAndValue<>(prop, value);
		}

		boolean match(BlockState state) {
			return state.getValue(property) == value;
		}
	}

	public static class PropertiesAndValues extends ArrayList<PropertyAndValue<?>> {

		public static PropertiesAndValues of(StateDefinition definition, String string) {
			PropertiesAndValues propertiesAndValues = new PropertiesAndValues();
			String[] split = string.split(",");
			for (String s : split) {
				propertiesAndValues.add(PropertyAndValue.of(definition, s));
			}
			return propertiesAndValues;
		}

		public boolean match(BlockState state) {
			for (PropertyAndValue<?> propertyAndValue : this) {
				if (!propertyAndValue.match(state))
					return false;
			}
			return true;
		}
	}
}
