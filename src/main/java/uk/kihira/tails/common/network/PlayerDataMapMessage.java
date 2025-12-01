/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.network;

import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.reflect.TypeToken;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.TailsNetworkManager;
import uk.kihira.tails.common2.part.PartsData;

// S → C
@Internal
public record PlayerDataMapMessage(Map<UUID, PartsData> partsDataMap) implements CustomPacketPayload {

	public static final Type<PlayerDataMapMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tails.MOD_ID, "bulk_sync_to_client"));

	public static final StreamCodec<ByteBuf, PlayerDataMapMessage> STREAM_CODEC = ByteBufCodecs.stringUtf8(Short.MAX_VALUE)
			.map(PlayerDataMapMessage::decode, PlayerDataMapMessage::encode);

	@Override
	public Type<PlayerDataMapMessage> type() {
		return TYPE;
	}

	private static final TypeToken<Map<UUID, PartsData>> PART_DATA_MAP_TYPE = new TypeToken<>() {};

	private static PlayerDataMapMessage decode(String tailInfoJson) {
		if (TailsNetworkManager.DEBUG_NETWORK)
			Tails.LOGGER.info("[PlayerDataMapMessage] Received {}", tailInfoJson);

		Map<UUID, PartsData> partsDataMap = Map.of();

		try {
			partsDataMap = Tails.PROXY.getSidedGson().fromJson(tailInfoJson, PART_DATA_MAP_TYPE);
		} catch (Exception e) {
			Tails.LOGGER.error("Exception decoding player part data:\n{}", tailInfoJson, e);
		}

		return new PlayerDataMapMessage(partsDataMap);
	}

	private static String encode(PlayerDataMapMessage msg) {
		return Tails.SERVER_GSON.toJson(msg.partsDataMap);
	}

	@Internal
	public static void handle(PlayerDataMapMessage message, IPayloadContext context) {
		if (message.partsDataMap != null)
			for (Map.Entry<UUID, PartsData> entry : message.partsDataMap.entrySet())
				Tails.PROXY.getPartManager().set(entry.getKey(), entry.getValue());
	}
}