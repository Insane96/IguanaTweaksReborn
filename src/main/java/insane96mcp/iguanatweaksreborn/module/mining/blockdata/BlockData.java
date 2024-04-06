package insane96mcp.iguanatweaksreborn.module.mining.blockdata;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.iguanatweaksreborn.utils.ITRGsonHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonAdapter(BlockData.Serializer.class)
public class BlockData {

	public Block block;
	private List<BlockState> blockStates = new ArrayList<>();
	@Nullable
	private Float stateHardness;
	@Nullable
	private Boolean stateRequiresCorrectToolForDrops;
	@Nullable
	private NoteBlockInstrument stateNoteBlockInstrument;

	@Nullable
	public Float explosionResistance;
	@Nullable
	public Float friction;
	@Nullable
	public Float speedFactor;
	@Nullable
	public Float jumpFactor;

	public BlockData(Block block, List<BlockState> blockStates, @Nullable Float stateHardness, @Nullable Boolean stateRequiresCorrectToolForDrops, @Nullable NoteBlockInstrument stateNoteBlockInstrument, @Nullable Float explosionResistance, @Nullable Float friction, @Nullable Float speedFactor, @Nullable Float jumpFactor) {
		this.block = block;
		this.blockStates = blockStates;
		this.stateHardness = stateHardness;
		this.stateRequiresCorrectToolForDrops = stateRequiresCorrectToolForDrops;
		this.stateNoteBlockInstrument = stateNoteBlockInstrument;
		this.explosionResistance = explosionResistance;
		this.friction = friction;
		this.speedFactor = speedFactor;
		this.jumpFactor = jumpFactor;
	}

	public BlockData(Block block) {
		this.block = block;
	}

	public void apply(boolean applyingOriginal) {
		BlockData originalData = new BlockData(block);
		this.block.getStateDefinition().getPossibleStates().forEach(blockState -> {
			if (blockStates.isEmpty() || blockStates.contains(blockState)) {
				originalData.blockStates.add(blockState);
				if (this.stateHardness != null) {
					originalData.stateHardness = blockState.destroySpeed;
					blockState.destroySpeed = this.stateHardness;
				}
				if (this.stateRequiresCorrectToolForDrops != null) {
					originalData.stateRequiresCorrectToolForDrops = blockState.requiresCorrectToolForDrops;
					blockState.requiresCorrectToolForDrops = this.stateRequiresCorrectToolForDrops;
				}
				if (this.stateNoteBlockInstrument != null) {
					originalData.stateNoteBlockInstrument = blockState.instrument;
					blockState.instrument = this.stateNoteBlockInstrument;
				}
			}
		});
		if (this.explosionResistance != null) {
			originalData.explosionResistance = this.block.explosionResistance;
			this.block.explosionResistance = this.explosionResistance;
		}
		if (this.friction != null) {
			originalData.friction = this.block.friction;
			this.block.friction = this.friction;
		}
		if (this.speedFactor != null) {
			originalData.speedFactor = this.block.speedFactor;
			this.block.speedFactor = this.speedFactor;
		}
		if (this.jumpFactor != null) {
			originalData.jumpFactor = this.block.jumpFactor;
			this.block.jumpFactor = this.jumpFactor;
		}
		if (!applyingOriginal)
			BlockDataReloadListener.ORIGINAL_DATA.add(originalData);
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<BlockData>>(){}.getType();

	public static class Serializer implements JsonDeserializer<BlockData>, JsonSerializer<BlockData> {
		@Override
		public BlockData deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jObject = json.getAsJsonObject();
			boolean required = GsonHelper.getAsBoolean(jObject, "required", true);
			ResourceLocation blockRL = ResourceLocation.tryParse(jObject.get("block").getAsString());
			if (blockRL == null) {
				if (!required)
					return null;
				else
					throw new JsonParseException("Failed to get block for %s".formatted(jObject.get("block").getAsString()));
			}
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
			Float friction = ITRGsonHelper.getAsNullableFloat(jObject, "friction");
			Float speedFactor = ITRGsonHelper.getAsNullableFloat(jObject, "speed_factor");
			Float jumpFactor = ITRGsonHelper.getAsNullableFloat(jObject, "jump_factor");
			return new BlockData(block, blockStates, hardness, requiresCorrectToolForDrops, instrument, explosionResistance, friction, speedFactor, jumpFactor);
		}

		@Override
		public JsonElement serialize(BlockData src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			throw new NotImplementedException();
			/*JsonObject jObject = new JsonObject();
			if (!src.blockStates.isEmpty()) {
				JsonArray array = new JsonArray();
				for (BlockState state : src.blockStates) {
					array.add("%s=%s".formatted(property.property.getName(), property.value));
				}
			}
			if (src.stateHardness != null)
				jObject.addProperty("hardness", src.stateHardness);
			if (src.explosionResistance != null)
				jObject.addProperty("explosion_resistance", src.explosionResistance);
			return jObject;*/
		}
	}

	public static BlockData fromNetwork(FriendlyByteBuf byteBuf) {
		Block block = byteBuf.readById(BuiltInRegistries.BLOCK);
		List<BlockState> blockStates = new ArrayList<>();
		byte stateCount = byteBuf.readByte();
		for (int i = 0; i < stateCount; i++) {
			byte propertiesCount = byteBuf.readByte();
			for (int p = 0; p < propertiesCount; p++) {
				PropertyAndValue<?> propertyAndValue = PropertyAndValue.of(block.getStateDefinition(), byteBuf.readUtf());
				block.getStateDefinition().getPossibleStates().forEach(blockState -> {
					if (propertyAndValue.match(blockState))
						blockStates.add(blockState);
				});
			}
		}
		Float hardness = byteBuf.readNullable(FriendlyByteBuf::readFloat);
		Boolean requiresCorrectToolForDrops = byteBuf.readNullable(FriendlyByteBuf::readBoolean);
		NoteBlockInstrument noteBlockInstrument = byteBuf.readNullable(buf -> buf.readEnum(NoteBlockInstrument.class));
		Float explosionResistance = byteBuf.readNullable(FriendlyByteBuf::readFloat);
		Float friction = byteBuf.readNullable(FriendlyByteBuf::readFloat);
		Float speedFactor = byteBuf.readNullable(FriendlyByteBuf::readFloat);
		Float jumpFactor = byteBuf.readNullable(FriendlyByteBuf::readFloat);
		return new BlockData(block, blockStates, hardness, requiresCorrectToolForDrops, noteBlockInstrument, explosionResistance, friction, speedFactor, jumpFactor);
	}

	public void toNetwork(FriendlyByteBuf byteBuf) {
		byteBuf.writeId(BuiltInRegistries.BLOCK, this.block);
		byteBuf.writeByte(this.blockStates.size());
		for (BlockState state : this.blockStates)
		{
			byteBuf.writeByte(state.getProperties().size());
			for (Property<?> property : state.getProperties()) {
				byteBuf.writeUtf(property.getName() + "=" + state.getValue(property));
			}
		}
		byteBuf.writeNullable(this.stateHardness, FriendlyByteBuf::writeFloat);
		byteBuf.writeNullable(this.stateRequiresCorrectToolForDrops, FriendlyByteBuf::writeBoolean);
		byteBuf.writeNullable(this.stateNoteBlockInstrument, FriendlyByteBuf::writeEnum);
		byteBuf.writeNullable(this.explosionResistance, FriendlyByteBuf::writeFloat);
		byteBuf.writeNullable(this.friction, FriendlyByteBuf::writeFloat);
		byteBuf.writeNullable(this.speedFactor, FriendlyByteBuf::writeFloat);
		byteBuf.writeNullable(this.jumpFactor, FriendlyByteBuf::writeFloat);
	}

	//Thanks Random832
	public record PropertyAndValue<T extends Comparable<T>>(Property<T> property, T value) {
		static <T extends Comparable<T>> PropertyAndValue<?> of(StateDefinition definition, String string) {
			String[] split = string.split("=", 2);
			Property<T> prop = (Property<T>) definition.getProperty(split[0]);
			if (prop == null)
				throw new NullPointerException("Property %s doesn't belong to %s".formatted(split[0], definition));
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
