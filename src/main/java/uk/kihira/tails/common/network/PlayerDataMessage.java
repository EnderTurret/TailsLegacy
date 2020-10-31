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
import com.mojang.util.UUIDTypeAdapter;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import uk.kihira.tails.common.PartsData;
import uk.kihira.tails.common.Tails;

public class PlayerDataMessage {

	private UUID uuid;
	private PartsData partsData;
	private boolean shouldRemove;

	public PlayerDataMessage() {}
	public PlayerDataMessage(UUID uuid, PartsData partsData, boolean shouldRemove) {
		this.uuid = uuid;
		this.partsData = partsData;
		this.shouldRemove = shouldRemove;
	}

	public static PlayerDataMessage fromBytes(PacketBuffer buf) {
		final PlayerDataMessage msg = new PlayerDataMessage();
		msg.uuid = UUIDTypeAdapter.fromString(buf.readString(Short.MAX_VALUE));
		final String tailInfoJson = buf.readString(Short.MAX_VALUE);
		if (!Strings.isNullOrEmpty(tailInfoJson))
			try {
				msg.partsData = Tails.gson.fromJson(tailInfoJson, PartsData.class);
			} catch (JsonSyntaxException e) {
				Tails.logger.warn(e);
			}
		else msg.partsData = null;
		return msg;
	}

	public static void toBytes(PlayerDataMessage msg, PacketBuffer buf) {
		buf.writeString(UUIDTypeAdapter.fromUUID(msg.uuid), Short.MAX_VALUE);
		final String tailInfoJson = msg.partsData == null ? "" : Tails.gson.toJson(msg.partsData);
		buf.writeString(tailInfoJson, Short.MAX_VALUE);
	}

	public static void onMessage(PlayerDataMessage message, Supplier<NetworkEvent.Context> ctx) {
		if (message.shouldRemove) Tails.proxy.removePartsData(message.uuid);
		else if (message.partsData != null) {
			Tails.proxy.addPartsData(message.uuid, message.partsData);
			//Tell other clients about the change
			if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
				Tails.networkWrapper.send(PacketDistributor.ALL.noArg(), new PlayerDataMessage(message.uuid, message.partsData, false));
		}
		ctx.get().setPacketHandled(true);
	}
}
