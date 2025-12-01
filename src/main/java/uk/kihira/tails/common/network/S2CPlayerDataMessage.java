/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.network;

import java.util.UUID;
import java.util.function.Supplier;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.common.base.Strings;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.TailsNetworkManager;
import uk.kihira.tails.common2.part.PartsData;

@Internal
public record S2CPlayerDataMessage(UUID uuid, PartsData partsData) implements CustomPacketPayload {

	public static final Type<S2CPlayerDataMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tails.MOD_ID, "sync_to_client"));

	public static final StreamCodec<FriendlyByteBuf, S2CPlayerDataMessage> STREAM_CODEC = StreamCodec.of(S2CPlayerDataMessage::encode, S2CPlayerDataMessage::decode);

	@Override
	public Type<S2CPlayerDataMessage> type() {
		return TYPE;
	}

	private static S2CPlayerDataMessage decode(FriendlyByteBuf buf) {
		final UUID uuid = buf.readUUID();

		final String tailInfoJson = buf.readUtf(Short.MAX_VALUE);

		if (TailsNetworkManager.DEBUG_NETWORK)
			Tails.LOGGER.info("[S2CPlayerDataMessage] Received {} = {}", uuid, tailInfoJson);

		PartsData partsData = PartsData.EMPTY;

		if (!Strings.isNullOrEmpty(tailInfoJson))
			try {
				partsData = Tails.PROXY.getSidedGson().fromJson(tailInfoJson, PartsData.class);
			} catch (Exception e) {
				Tails.LOGGER.error("Exception decoding player part data:\n{}", tailInfoJson, e);
			}

		return new S2CPlayerDataMessage(uuid, partsData);
	}

	private static void encode(FriendlyByteBuf buf, S2CPlayerDataMessage msg) {
		buf.writeUUID(msg.uuid);
		final String tailInfoJson = msg.partsData == null || msg.partsData.isEmpty() ? "" : Tails.SERVER_GSON.toJson(msg.partsData);
		buf.writeUtf(tailInfoJson, Short.MAX_VALUE);
	}

	@Internal
	public static void handle(S2CPlayerDataMessage message, IPayloadContext context) {
		if (message.partsData != null)
			Tails.PROXY.getPartManager().set(message.uuid, message.partsData);
	}
}
