/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.common.network;

import java.util.UUID;
import java.util.function.Supplier;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.network.NetworkEvent;

import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.common.network.BaseS2CPlayerDataMessage;
import uk.kihira.tails.common.part.PartsData;

@Internal
public record S2CPlayerDataMessage(UUID uuid, PartsData partsData) implements BaseS2CPlayerDataMessage {

	public static S2CPlayerDataMessage decode(FriendlyByteBuf buf) {
		final UUID uuid = buf.readUUID();
		final String tailInfoJson = buf.readUtf(Short.MAX_VALUE);

		return new S2CPlayerDataMessage(uuid, BaseS2CPlayerDataMessage.decodeJson(uuid, tailInfoJson));
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeUUID(uuid);
		buf.writeUtf(BaseS2CPlayerDataMessage.encodeJson(partsData), Short.MAX_VALUE);
	}

	@Internal
	public static void handle(S2CPlayerDataMessage message, Supplier<NetworkEvent.Context> context) {
		BaseS2CPlayerDataMessage.handle(message.uuid, message.partsData);
		context.get().setPacketHandled(true);
	}
}