/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.common.network;

import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.ApiStatus.Internal;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.network.BasePlayerDataMapMessage;
import net.enderturret.tailslegacy.common.part.PartsData;

@Internal
public record S2CBulkPlayerDataMessage(Map<UUID, PartsData> partsDataMap) implements CustomPacketPayload, BasePlayerDataMapMessage {

	public static final Type<S2CBulkPlayerDataMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(TailsPlatform.MOD_ID, "bulk_sync_to_client"));

	public static final StreamCodec<ByteBuf, S2CBulkPlayerDataMessage> STREAM_CODEC = ByteBufCodecs.stringUtf8(Short.MAX_VALUE)
			.map(S2CBulkPlayerDataMessage::decode, S2CBulkPlayerDataMessage::encode);

	@Override
	public Type<S2CBulkPlayerDataMessage> type() {
		return TYPE;
	}

	private static S2CBulkPlayerDataMessage decode(String tailInfoJson) {
		return new S2CBulkPlayerDataMessage(BasePlayerDataMapMessage.decodeJson(tailInfoJson));
	}

	private static String encode(S2CBulkPlayerDataMessage msg) {
		return BasePlayerDataMapMessage.encodeJson(msg.partsDataMap);
	}
}