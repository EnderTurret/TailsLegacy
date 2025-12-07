/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.common.network;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.jetbrains.annotations.ApiStatus.Internal;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.network.NetworkEvent;

import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.common.network.BasePlayerDataMapMessage;
import uk.kihira.tails.common.part.PartsData;

// S → C
@Internal
public record PlayerDataMapMessage(Map<UUID, PartsData> partsDataMap) implements BasePlayerDataMapMessage {

	public static PlayerDataMapMessage decode(FriendlyByteBuf buf) {
		return new PlayerDataMapMessage(BasePlayerDataMapMessage.decodeJson(buf.readUtf(Short.MAX_VALUE)));
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeUtf(BasePlayerDataMapMessage.encodeJson(partsDataMap), Short.MAX_VALUE);
	}

	@Internal
	public static void handle(PlayerDataMapMessage message, Supplier<NetworkEvent.Context> context) {
		BasePlayerDataMapMessage.handle(message.partsDataMap);
		context.get().setPacketHandled(true);
	}
}