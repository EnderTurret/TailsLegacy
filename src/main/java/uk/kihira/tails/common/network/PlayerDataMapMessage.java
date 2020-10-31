/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.network;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonSyntaxException;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import uk.kihira.tails.common.PartsData;
import uk.kihira.tails.common.Tails;

public class PlayerDataMapMessage {

	private Map<UUID, PartsData> partsDataMap;

	public PlayerDataMapMessage() {}
	@SuppressWarnings("unchecked")
	public PlayerDataMapMessage(Map partsDataMap) {
		this.partsDataMap = partsDataMap;
	}

	@SuppressWarnings("unchecked")
	public static PlayerDataMapMessage fromBytes(PacketBuffer buf) {
		final String tailInfoJson = buf.readString(Short.MAX_VALUE);
		final PlayerDataMapMessage msg = new PlayerDataMapMessage();
		try {
			msg.partsDataMap = Tails.gson.fromJson(tailInfoJson, new TypeToken<Map<UUID, PartsData>>() {}.getType());
		} catch (JsonSyntaxException e) {
			Tails.logger.catching(e);
		}
		return msg;
	}

	public static void toBytes(PlayerDataMapMessage msg, PacketBuffer buf) {
		final String tailInfoJson = Tails.gson.toJson(msg.partsDataMap);
		buf.writeString(tailInfoJson, Short.MAX_VALUE);
	}

	public static void onMessage(PlayerDataMapMessage message, Supplier<NetworkEvent.Context> ctx) {
		for (Map.Entry<UUID, PartsData> entry : message.partsDataMap.entrySet())
			Tails.proxy.addPartsData(entry.getKey(), entry.getValue());
		ctx.get().setPacketHandled(true);
	}
}
