/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.network;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.Tails;

public record LibraryEntriesMessage(List<LibraryEntryData> entries, boolean delete) {

	public static final Type ENTRY_DATA_LIST = new TypeToken<List<LibraryEntryData>>() {}.getType();

	public static LibraryEntriesMessage decode(FriendlyByteBuf buf) {
		final String dataJson = buf.readUtf(Short.MAX_VALUE);

		final List<LibraryEntryData> entries = new ArrayList<>(0);

		try {
			entries.addAll(Tails.GSON.fromJson(dataJson, ENTRY_DATA_LIST));
		} catch (JsonParseException e) {
			Tails.LOGGER.error("Exception parsing library data:", e);
		}

		final boolean delete = buf.readBoolean();

		return new LibraryEntriesMessage(entries, delete);
	}

	public static void encode(LibraryEntriesMessage msg, FriendlyByteBuf buf) {
		buf.writeUtf(Tails.GSON.toJson(msg.entries, ENTRY_DATA_LIST), Short.MAX_VALUE);
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
			} else { // Server
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