package insane96mcp.iguanatweaksreborn.utils;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;

public class ITRGsonHelper {
	/**
	 * Wrapper of GsonHelper.getAsInt, but if there is no member returns null
	 */
	@Nullable
	public static Integer getAsInt(JsonObject jObject, String memberName) {
		if (!jObject.has(memberName))
			return null;
		return GsonHelper.getAsInt(jObject, memberName);
	}
	/**
	 * Wrapper of GsonHelper.getAsDouble, but if there is no member returns null
	 */
	@Nullable
	public static Double getAsDouble(JsonObject jObject, String memberName) {
		if (!jObject.has(memberName))
			return null;
		return GsonHelper.getAsDouble(jObject, memberName);
	}
	/**
	 * Wrapper of GsonHelper.getAsFloat, but if there is no member returns null
	 */
	@Nullable
	public static Float getAsFloat(JsonObject jObject, String memberName) {
		if (!jObject.has(memberName))
			return null;
		return GsonHelper.getAsFloat(jObject, memberName);
	}
	/**
	 * Wrapper of GsonHelper.getAsBoolean, but if there is no member returns null
	 */
	@Nullable
	public static Boolean getAsBoolean(JsonObject jObject, String memberName) {
		if (!jObject.has(memberName))
			return null;
		return GsonHelper.getAsBoolean(jObject, memberName);
	}
}
