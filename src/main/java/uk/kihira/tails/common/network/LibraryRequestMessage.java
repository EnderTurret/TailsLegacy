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
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import uk.kihira.tails.common.Tails;

public class LibraryRequestMessage {

	public static LibraryRequestMessage decode(FriendlyByteBuf buf) {
		return new LibraryRequestMessage();
	}

	public static void encode(LibraryRequestMessage msg, FriendlyByteBuf buf) {}

	public static void handle(LibraryRequestMessage message, Supplier<NetworkEvent.Context> ctx) {
		Tails.CHANNEL.send(PacketDistributor.PLAYER.with(() -> ctx.get().getSender()), new LibraryEntriesMessage(Tails.PROXY.getLibraryManager().libraryEntries, false));
		ctx.get().setPacketHandled(true);
	}
}