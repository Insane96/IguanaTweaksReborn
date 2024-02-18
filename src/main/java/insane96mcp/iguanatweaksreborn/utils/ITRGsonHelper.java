package insane96mcp.iguanatweaksreborn.utils;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;

public class ITRGsonHelper {
	/**
	 * Wrapper of GsonHelper.getAsInt, but if there is no member returns null
	 */
	@Nullable
	public static Integer getAsNullableInt(JsonObject jObject, String memberName) {
		if (!jObject.has(memberName))
			return null;
		return GsonHelper.getAsInt(jObject, memberName);
	}
	/**
	 * Wrapper of GsonHelper.getAsDouble, but if there is no member returns null
	 */
	@Nullable
	public static Double getAsNullableDouble(JsonObject jObject, String memberName) {
		if (!jObject.has(memberName))
			return null;
		return GsonHelper.getAsDouble(jObject, memberName);
	}
	/**
	 * Wrapper of GsonHelper.getAsFloat, but if there is no member returns null
	 */
	@Nullable
	public static Float getAsNullableFloat(JsonObject jObject, String memberName) {
		if (!jObject.has(memberName))
			return null;
		return GsonHelper.getAsFloat(jObject, memberName);
	}
	/**
	 * Wrapper of GsonHelper.getAsBoolean, but if there is no member returns null
	 */
	@Nullable
	public static Boolean getAsNullableBoolean(JsonObject jObject, String memberName) {
		if (!jObject.has(memberName))
			return null;
		return GsonHelper.getAsBoolean(jObject, memberName);
	}
}
