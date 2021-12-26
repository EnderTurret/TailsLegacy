/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.network;

import java.util.List;
import java.util.function.Supplier;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.Tails;

public class LibraryEntriesMessage {

	private List<LibraryEntryData> entries;
	private boolean delete; // Only used when sending to server.

	public LibraryEntriesMessage() {}
	public LibraryEntriesMessage(List<LibraryEntryData> entries, boolean delete) {
		this.entries = entries;
		this.delete = delete;
	}

	public static LibraryEntriesMessage decode(FriendlyByteBuf buf) {
		final String dataJson = buf.readUtf(Short.MAX_VALUE);
		final LibraryEntriesMessage msg = new LibraryEntriesMessage();

		try {
			msg.entries = Tails.GSON.fromJson(dataJson, new TypeToken<List<LibraryEntryData>>() {}.getType());
		} catch (JsonParseException e) {
			Tails.LOGGER.error("Exception parsing library data:", e);
		}

		msg.delete = buf.readBoolean();

		return msg;
	}

	public static void encode(LibraryEntriesMessage msg, FriendlyByteBuf buf) {
		buf.writeUtf(Tails.GSON.toJson(msg.entries, new TypeToken<List<LibraryEntryData>>() {}.getType()), Short.MAX_VALUE);
		buf.writeBoolean(msg.delete);
	}

	public static void handle(LibraryEntriesMessage message, Supplier<NetworkEvent.Context> ctx) {
		if (message.entries != null)
			// Client
			if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
				// Yeah this isn't exactly a nice way of doing this.
				for (LibraryEntryData entry : message.entries)
					entry.remoteEntry = true;

				// We add server entries to the uk.kihira.tails.client
				Tails.PROXY.getLibraryManager().removeRemoteEntries();
				Tails.PROXY.getLibraryManager().addEntries(message.entries);
			}
		// Server
			else {
				if (message.delete)
					//Tails.LOGGER.debug("Removing Library Entries: " + message.entries.size());
					Tails.PROXY.getLibraryManager().libraryEntries.removeAll(message.entries);
				else
					//Tails.LOGGER.debug("Adding Library Entries: " + message.entries.size());
					Tails.PROXY.getLibraryManager().addEntries(message.entries);
				Tails.PROXY.getLibraryManager().saveLibrary();
			}

		ctx.get().setPacketHandled(true);
	}
}