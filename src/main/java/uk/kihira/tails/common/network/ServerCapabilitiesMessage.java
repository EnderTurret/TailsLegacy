package uk.kihira.tails.common.network;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import uk.kihira.tails.common.Tails;

public class ServerCapabilitiesMessage {

	private final boolean library;

	public ServerCapabilitiesMessage(boolean library) {
		this.library = library;
	}

	public static ServerCapabilitiesMessage decode(PacketBuffer buf) {
		return new ServerCapabilitiesMessage(buf.readBoolean());
	}

	public static void encode(ServerCapabilitiesMessage msg, PacketBuffer buf) {
		buf.writeBoolean(msg.library);
	}

	public static void handle(ServerCapabilitiesMessage message, Supplier<NetworkEvent.Context> ctx) {
		Tails.libraryEnabled = message.library;
		ctx.get().setPacketHandled(true);
	}
}