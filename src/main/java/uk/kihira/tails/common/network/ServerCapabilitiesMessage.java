/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.network;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import uk.kihira.tails.common.Tails;

public class ServerCapabilitiesMessage {

	private final boolean library;

	public ServerCapabilitiesMessage(boolean library) {
		this.library = library;
	}

	public static ServerCapabilitiesMessage decode(FriendlyByteBuf buf) {
		return new ServerCapabilitiesMessage(buf.readBoolean());
	}

	public static void encode(ServerCapabilitiesMessage msg, FriendlyByteBuf buf) {
		buf.writeBoolean(msg.library);
	}

	public static void handle(ServerCapabilitiesMessage message, Supplier<NetworkEvent.Context> ctx) {
		Tails.libraryEnabled = message.library;
		ctx.get().setPacketHandled(true);
	}
}