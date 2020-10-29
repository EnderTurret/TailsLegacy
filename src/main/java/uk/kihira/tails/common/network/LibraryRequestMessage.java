package uk.kihira.tails.common.network;

import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import uk.kihira.tails.common.Tails;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class LibraryRequestMessage {

    public static LibraryRequestMessage fromBytes(PacketBuffer buf) {
        return new LibraryRequestMessage();
    }

    public static void toBytes(LibraryRequestMessage msg, PacketBuffer buf) {}

    public static LibraryEntriesMessage onMessage(LibraryRequestMessage message, Supplier<NetworkEvent.Context> ctx) {
            return new LibraryEntriesMessage(Tails.proxy.getLibraryManager().libraryEntries, false);
    }
}
