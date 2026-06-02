/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.gson.client;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.enderturret.tailslegacy.common.client.part.ClientPartInfo;
import net.enderturret.tailslegacy.common.client.part.LocalPartManager;
import net.enderturret.tailslegacy.common.gson.ServerPartInfoSerializer;
import net.enderturret.tailslegacy.common.part.IPartInfo;

/**
 * A serializer for {@link ClientPartInfo}.
 * @author EnderTurret
 * @see LocalPartManager#GSON
 */
public class ClientPartInfoSerializer implements JsonSerializer<IPartInfo>, JsonDeserializer<IPartInfo> {

	@Override
	public IPartInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		final IPartInfo info = ServerPartInfoSerializer.INSTANCE.deserialize(json, typeOfT, context);
		return ClientPartInfo.coerce(info);
	}

	@Override
	public JsonElement serialize(IPartInfo src, Type typeOfSrc, JsonSerializationContext context) {
		return ServerPartInfoSerializer.INSTANCE.serialize(src, typeOfSrc, context);
	}
}