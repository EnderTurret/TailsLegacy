package uk.kihira.tails.common.gson;

import java.util.Map;
import java.util.function.Predicate;

import com.google.gson.JsonElement;

import uk.kihira.tails.common.client.duck.TResourceLocation;

public interface ResourceManagerWrapper {

	public JsonElement getJson(TResourceLocation path);
	public Map<TResourceLocation, JsonElement> listJsonFiles(String prefix, Predicate<TResourceLocation> filter);
}