package uk.kihira.tails.common_gson;

import java.util.Map;
import java.util.function.Predicate;

import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;

public interface ResourceManagerWrapper {

	public JsonElement getJson(ResourceLocation path);
	public Map<ResourceLocation, JsonElement> listJsonFiles(String prefix, Predicate<ResourceLocation> filter);
}