/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.common.network;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.network.FriendlyByteBuf;

import net.minecraftforge.network.NetworkEvent;

import net.enderturret.tailslegacy.common.network.BaseS2CBulkPlayerDataMessage;
import net.enderturret.tailslegacy.common.part.PartsData;

@Internal
public record S2CBulkPlayerDataMessage(Map<UUID, PartsData> partsDataMap) implements BaseS2CBulkPlayerDataMessage {

	public static S2CBulkPlayerDataMessage decode(FriendlyByteBuf buf) {
		return new S2CBulkPlayerDataMessage(BaseS2CBulkPlayerDataMessage.decodeJson(buf.readUtf(Short.MAX_VALUE)));
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeUtf(BaseS2CBulkPlayerDataMessage.encodeJson(partsDataMap), Short.MAX_VALUE);
	}

	@Internal
	public static void handle(S2CBulkPlayerDataMessage message, Supplier<NetworkEvent.Context> context) {
		BaseS2CBulkPlayerDataMessage.handle(message.partsDataMap);
		context.get().setPacketHandled(true);
	}
}