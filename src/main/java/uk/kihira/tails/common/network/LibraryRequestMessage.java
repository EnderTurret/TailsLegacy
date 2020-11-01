package uk.kihira.tails.common.network;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import uk.kihira.tails.common.Tails;

public class LibraryRequestMessage {

	public static LibraryRequestMessage decode(PacketBuffer buf) {
		return new LibraryRequestMessage();
	}

	public static void encode(LibraryRequestMessage msg, PacketBuffer buf) {}

	public static void handle(LibraryRequestMessage message, Supplier<NetworkEvent.Context> ctx) {
		Tails.CHANNEL.send(PacketDistributor.PLAYER.with(() -> ctx.get().getSender()), new LibraryEntriesMessage(Tails.PROXY.getLibraryManager().libraryEntries, false));
		ctx.get().setPacketHandled(true);
	}
}