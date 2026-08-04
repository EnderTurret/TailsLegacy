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

import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.network.BasePlayerDataMapMessage;
import net.enderturret.tailslegacy.common.part.PartsData;

// S → C
@Internal
public record PlayerDataMapMessage(Map<UUID, PartsData> partsDataMap) implements CustomPacketPayload, BasePlayerDataMapMessage {

	public static final Type<PlayerDataMapMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(TailsPlatform.MOD_ID, "bulk_sync_to_client"));

	public static final StreamCodec<ByteBuf, PlayerDataMapMessage> STREAM_CODEC = ByteBufCodecs.stringUtf8(Short.MAX_VALUE)
			.map(PlayerDataMapMessage::decode, PlayerDataMapMessage::encode);

	@Override
	public Type<PlayerDataMapMessage> type() {
		return TYPE;
	}

	private static PlayerDataMapMessage decode(String tailInfoJson) {
		return new PlayerDataMapMessage(BasePlayerDataMapMessage.decodeJson(tailInfoJson));
	}

	private static String encode(PlayerDataMapMessage msg) {
		return BasePlayerDataMapMessage.encodeJson(msg.partsDataMap);
	}

	@Internal
	public static void handle(PlayerDataMapMessage message, IPayloadContext context) {
		BasePlayerDataMapMessage.handle(message.partsDataMap);
	}
}