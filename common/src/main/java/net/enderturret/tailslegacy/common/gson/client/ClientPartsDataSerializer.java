/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.gson.client;

import java.lang.reflect.Type;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import net.enderturret.tailslegacy.common.client.part.ClientPartsData;
import net.enderturret.tailslegacy.common.gson.PartsDataSerializer;
import net.enderturret.tailslegacy.common.part.PartsData;

@Internal
public class ClientPartsDataSerializer extends PartsDataSerializer {

	@Override
	public PartsData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return ClientPartsData.clone(super.deserialize(json, typeOfT, context));
	}
}