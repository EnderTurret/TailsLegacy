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

import io.netty.buffer.ByteBuf;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.common.network.BaseC2SPlayerDataMessage;
import uk.kihira.tails.common.part.PartsData;
import uk.kihira.tails.forge.common.Tails;

@Internal
public record C2SPlayerDataMessage(PartsData partsData) implements BaseC2SPlayerDataMessage {

	public static C2SPlayerDataMessage decode(FriendlyByteBuf buf) {
		return new C2SPlayerDataMessage(BaseC2SPlayerDataMessage.decodeJson(buf.readUtf(Short.MAX_VALUE)));
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeUtf(BaseC2SPlayerDataMessage.encodeJson(partsData), Short.MAX_VALUE);
	}

	@Internal
	public static void handle(C2SPlayerDataMessage message, Supplier<NetworkEvent.Context> context) {
		if (message.partsData == null) return;

		final ServerPlayer sender = context.get().getSender();
		final UUID uuid = sender.getUUID();

		Tails.PROXY.getPartManager().set(uuid, message.partsData);

		// Tell other clients about the change.
		// TODO: This sends the user's part data to themself, which is an unnecessary packet (they already have this data).
		TailsNetworkManager.get().send(PacketDistributor.ALL.noArg(), new S2CPlayerDataMessage(uuid, message.partsData));

		context.get().setPacketHandled(true);
	}
}