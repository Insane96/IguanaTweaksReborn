package insane96mcp.iguanatweaksreborn.module.mining.blockdata;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.iguanatweaksreborn.utils.ITRGsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@JsonAdapter(BlockData.Serializer.class)
public class BlockData {
	public Block block;
	public List<BlockState> blockStates;
	@Nullable
	public Float hardness;
	@Nullable
	public Float explosionResistance;
	/*
	* State
	* spawnParticlesOnBreak
	* noteblock instrument
	*
	* Block
	* soundType?
	* friction
	* speedFactor
	* jumpFactor
	* */

	public BlockData(Block block, List<BlockState> blockStates, @Nullable Float hardness, @Nullable Float explosionResistance) {
		this.block = block;
		this.blockStates = blockStates;
		this.hardness = hardness;
		this.explosionResistance = explosionResistance;
	}

	public void apply(boolean isClientSide) {
		this.block.getStateDefinition().getPossibleStates().forEach(blockState -> {
			if (blockStates.isEmpty() || blockStates.contains(blockState)) {
				if (this.hardness != null)
					blockState.destroySpeed = this.hardness;
			}
		});
		if (this.explosionResistance != null)
			this.block.explosionResistance = this.explosionResistance;
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<BlockData>>() {}.getType();

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
					PropertyAndValue<?> propertyAndValue = PropertyAndValue.of(block.getStateDefinition(), element.getAsString());
					block.getStateDefinition().getPossibleStates().forEach(blockState -> {
						if (propertyAndValue.match(blockState))
							blockStates.add(blockState);
					});
				}
			}
			Float hardness = ITRGsonHelper.getAsNullableFloat(jObject, "hardness");
			return new BlockData(block, blockStates, hardness);
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
			if (src.hardness != null)
				jObject.addProperty("hardness", src.hardness);
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

		BlockState set(BlockState orig) {
			return orig.setValue(property, value);
		}
	}
}
