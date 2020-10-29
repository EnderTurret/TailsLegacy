package uk.kihira.tails.common.network;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import io.netty.buffer.ByteBuf;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.Tails;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class LibraryEntriesMessage {

    private List<LibraryEntryData> entries;
    private boolean delete; //Only used when sending to server

    public LibraryEntriesMessage() {}
    public LibraryEntriesMessage(List<LibraryEntryData> entries, boolean delete) {
        this.entries = entries;
        this.delete = delete;
    }

    public static LibraryEntriesMessage fromBytes(PacketBuffer buf) {
        String dataJson = buf.readString(Short.MAX_VALUE);
        LibraryEntriesMessage msg = new LibraryEntriesMessage();
        try {
            msg.entries = Tails.gson.fromJson(dataJson, new TypeToken<List<LibraryEntryData>>() {}.getType());
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        msg.delete = buf.readBoolean();

        return msg;
    }

    public static void toBytes(LibraryEntriesMessage msg, PacketBuffer buf) {
        buf.writeString(Tails.gson.toJson(msg.entries, new TypeToken<List<LibraryEntryData>>() {}.getType()), Short.MAX_VALUE);
        buf.writeBoolean(msg.delete);
    }

        public static void onMessage(LibraryEntriesMessage message, Supplier<NetworkEvent.Context> ctx) {
            //Client
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                //Yeah this isn't exactly a nice way of doing this.
                for (LibraryEntryData entry : message.entries) {
                    entry.remoteEntry = true;
                }

                //We add server entries to the uk.kihira.tails.client
                Tails.proxy.getLibraryManager().removeRemoteEntries();
                Tails.proxy.getLibraryManager().addEntries(message.entries);
            }
            //Server
            else {
                if (message.delete) {
                    Tails.logger.debug("Removing Library Entries: " + message.entries.size());
                    Tails.proxy.getLibraryManager().libraryEntries.removeAll(message.entries);
                }
                else {
                    Tails.logger.debug("Adding Library Entries: " + message.entries.size());
                    Tails.proxy.getLibraryManager().addEntries(message.entries);
                }
                Tails.proxy.getLibraryManager().saveLibrary();
        }
    }
}
