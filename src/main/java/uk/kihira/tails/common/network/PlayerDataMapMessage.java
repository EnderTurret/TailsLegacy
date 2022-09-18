/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.network;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonSyntaxException;

import net.minecraft.network.FriendlyByteBuf;

import net.minecraftforge.network.NetworkEvent;

import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.part.PartsData;

public record PlayerDataMapMessage(Map<UUID, PartsData> partsDataMap) {

	private static final Type PART_DATA_MAP_TYPE = new TypeToken<Map<UUID,PartsData>>() {}.getType();

	public static PlayerDataMapMessage decode(FriendlyByteBuf buf) {
		final String tailInfoJson = buf.readUtf(Short.MAX_VALUE);

		if (Tails.DEBUG_NETWORK)
			Tails.LOGGER.info("[PlayerDataMapMessage] Received {}", tailInfoJson);

		Map<UUID, PartsData> partsDataMap = Map.of();

		try {
			partsDataMap = Tails.GSON.fromJson(tailInfoJson, PART_DATA_MAP_TYPE);
		} catch (Exception e) {
			Tails.LOGGER.error("Exception decoding player part data:\n{}", tailInfoJson, e);
		}

		return new PlayerDataMapMessage(partsDataMap);
	}

	public static void encode(PlayerDataMapMessage msg, FriendlyByteBuf buf) {
		buf.writeUtf(Tails.GSON.toJson(msg.partsDataMap), Short.MAX_VALUE);
	}

	public static void handle(PlayerDataMapMessage message, Supplier<NetworkEvent.Context> ctx) {
		if (message.partsDataMap != null)
			for (Map.Entry<UUID, PartsData> entry : message.partsDataMap.entrySet())
				Tails.PROXY.addPartsData(entry.getKey(), entry.getValue());

		ctx.get().setPacketHandled(true);
	}
}