/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.network;

import java.util.UUID;
import java.util.function.Supplier;

import com.google.common.base.Strings;
import com.google.gson.JsonSyntaxException;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.part.PartsData;

public class PlayerDataMessage {

	private UUID uuid;
	private PartsData partsData;

	public PlayerDataMessage() {}
	public PlayerDataMessage(UUID uuid, PartsData partsData) {
		this.uuid = uuid;
		this.partsData = partsData;
	}

	public static PlayerDataMessage decode(PacketBuffer buf) {
		final PlayerDataMessage msg = new PlayerDataMessage();

		msg.uuid = buf.readUniqueId();

		final String tailInfoJson = buf.readString(Short.MAX_VALUE);

		if (!Strings.isNullOrEmpty(tailInfoJson))
			try {
				msg.partsData = Tails.GSON.fromJson(tailInfoJson, PartsData.class);
			} catch (JsonSyntaxException e) {
				Tails.LOGGER.catching(e);
			}
		else msg.partsData = null;

		return msg;
	}

	public static void encode(PlayerDataMessage msg, PacketBuffer buf) {
		buf.writeUniqueId(msg.uuid);
		final String tailInfoJson = msg.partsData == null ? "" : Tails.GSON.toJson(msg.partsData);
		buf.writeString(tailInfoJson, Short.MAX_VALUE);
	}

	public static void handle(PlayerDataMessage message, Supplier<NetworkEvent.Context> ctx) {
		if (message.partsData != null) {
			Tails.PROXY.addPartsData(message.uuid, message.partsData);
			// Tell other clients about the change.
			if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
				Tails.CHANNEL.send(PacketDistributor.ALL.noArg(), new PlayerDataMessage(message.uuid, message.partsData));
		}

		ctx.get().setPacketHandled(true);
	}
}
